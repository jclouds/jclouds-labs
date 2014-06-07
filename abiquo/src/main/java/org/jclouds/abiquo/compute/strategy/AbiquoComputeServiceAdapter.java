/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.abiquo.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.compute.options.AbiquoTemplateOptions;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplateInVirtualDatacenter;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.ExternalNetwork;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.Network;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.VirtualMachineMonitor;
import org.jclouds.abiquo.predicates.IpPredicates;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;

/**
 * Defines the connection between the {@link AbiquoApi} implementation and the
 * jclouds {@link ComputeService}.
 * 
 * 
 * @see CreateGroupBeforeCreatingNodes
 */
@Singleton
public class AbiquoComputeServiceAdapter
      implements
      ComputeServiceAdapter<VirtualMachine, VirtualMachineTemplateInVirtualDatacenter, VirtualMachineTemplate, VirtualDatacenter> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ApiContext<AbiquoApi> context;

   private final AdministrationService adminService;

   private final CloudService cloudService;

   private final MonitoringService monitoringService;

   private final FindCompatibleVirtualDatacenters compatibleVirtualDatacenters;

   private final Supplier<Map<Integer, Datacenter>> regionMap;

   private final Timeouts timeouts;

   @Inject
   public AbiquoComputeServiceAdapter(final ApiContext<AbiquoApi> context, final AdministrationService adminService,
         final CloudService cloudService, final MonitoringService monitoringService,
         final FindCompatibleVirtualDatacenters compatibleVirtualDatacenters,
         @Memoized final Supplier<Map<Integer, Datacenter>> regionMap, Timeouts timeouts) {
      this.context = checkNotNull(context, "context");
      this.adminService = checkNotNull(adminService, "adminService");
      this.cloudService = checkNotNull(cloudService, "cloudService");
      this.monitoringService = checkNotNull(monitoringService, "monitoringService");
      this.compatibleVirtualDatacenters = checkNotNull(compatibleVirtualDatacenters, "compatibleVirtualDatacenters");
      this.regionMap = checkNotNull(regionMap, "regionMap");
      this.timeouts = checkNotNull(timeouts, "timeouts");
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(final String group,
         final String name, final Template template) {
      checkArgument(template instanceof VirtualApplianceCachingTemplate,
            "A VirtualApplianceCachingTemplate is required");
      return createNodeWithGroupEncodedIntoName(name, VirtualApplianceCachingTemplate.class.cast(template));
   }

   protected NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(final String name,
         final VirtualApplianceCachingTemplate template) {
      AbiquoTemplateOptions options = template.getOptions().as(AbiquoTemplateOptions.class);
      Enterprise enterprise = adminService.getCurrentEnterprise();

      // Get the region where the template is available
      Datacenter datacenter = regionMap.get().get(Integer.valueOf(template.getImage().getLocation().getId()));

      // Load the template
      VirtualMachineTemplate virtualMachineTemplate = enterprise.getTemplateInRepository(datacenter,
            Integer.valueOf(template.getImage().getId()));

      Integer overrideCores = options.getOverrideCores();
      Integer overrideRam = options.getOverrideRam();

      VirtualMachine vm = VirtualMachine.builder(context, template.getVirtualAppliance(), virtualMachineTemplate) //
            .nameLabel(name) //
            .cpu(overrideCores != null ? overrideCores : totalCores(template.getHardware())) //
            .ram(overrideRam != null ? overrideRam : template.getHardware().getRam()) //
            .password(options.getVncPassword()) // Can be null
            .build();

      vm.save();

      configureNetworking(vm, template, datacenter, options);

      // This is an async operation, but jclouds already waits until the node is
      // RUNNING, so there is no need to block here
      vm.deploy();

      return new NodeAndInitialCredentials<VirtualMachine>(vm, vm.getId().toString(), null);
   }

   @Override
   public Iterable<VirtualMachineTemplateInVirtualDatacenter> listHardwareProfiles() {
      // In Abiquo, images are scoped to a region (physical datacenter), and
      // hardware profiles are scoped to a zone (a virtual datacenter in the
      // region, with a concrete virtualization technology)

      return concat(transform(listImages(),
            new Function<VirtualMachineTemplate, Iterable<VirtualMachineTemplateInVirtualDatacenter>>() {
               @Override
               public Iterable<VirtualMachineTemplateInVirtualDatacenter> apply(final VirtualMachineTemplate template) {
                  Iterable<VirtualDatacenter> compatibleZones = compatibleVirtualDatacenters.execute(template);

                  return transform(compatibleZones,
                        new Function<VirtualDatacenter, VirtualMachineTemplateInVirtualDatacenter>() {
                           @Override
                           public VirtualMachineTemplateInVirtualDatacenter apply(final VirtualDatacenter vdc) {
                              return new VirtualMachineTemplateInVirtualDatacenter(template, vdc);
                           }
                        });
               }
            }));
   }

   @Override
   public Iterable<VirtualMachineTemplate> listImages() {
      Enterprise enterprise = adminService.getCurrentEnterprise();
      return enterprise.listTemplates();
   }

   @Override
   public VirtualMachineTemplate getImage(final String id) {
      return find(listImages(), new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(VirtualMachineTemplate input) {
            return input.getId().equals(id);
         }
      }, null);
   }

   @Override
   public Iterable<VirtualDatacenter> listLocations() {
      return cloudService.listVirtualDatacenters();
   }

   @Override
   public VirtualMachine getNode(final String id) {
      return find(cloudService.listVirtualMachines(), vmId(id), null);
   }

   @Override
   public void destroyNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.undeploy(true);
      monitor.awaitCompletionUndeploy(timeouts.nodeTerminated, TimeUnit.MILLISECONDS, vm);
      vm.delete();
   }

   @Override
   public void rebootNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.reboot();
      monitor.awaitState(timeouts.nodeRunning, TimeUnit.MILLISECONDS, VirtualMachineState.ON, vm);
   }

   @Override
   public void resumeNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.changeState(VirtualMachineState.ON);
      monitor.awaitState(timeouts.nodeRunning, TimeUnit.MILLISECONDS, VirtualMachineState.ON, vm);
   }

   @Override
   public void suspendNode(final String id) {
      VirtualMachineMonitor monitor = monitoringService.getVirtualMachineMonitor();
      VirtualMachine vm = getNode(id);
      vm.changeState(VirtualMachineState.PAUSED);
      monitor.awaitState(timeouts.nodeSuspended, TimeUnit.MILLISECONDS, VirtualMachineState.PAUSED, vm);
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      return cloudService.listVirtualMachines();
   }

   @Override
   public Iterable<VirtualMachine> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualMachine>() {

         @Override
         public boolean apply(VirtualMachine machine) {
            return contains(ids, Integer.toString(machine.getId()));
         }
      });
   }

   /**
    * Configures the networking for the created virtual machine.
    * <ul>
    * <li>If the template options have been configured with a set of network
    * identifiers, jclouds will assign the virtual machine one IP address of
    * each network.</li>
    * <li>If no network ids have been defined, jclouds will try to assign a
    * public IP address.</li>
    * <li>If no public IP addresses are available in the user account, then an
    * IP address in the virtual datacenter's default network will be assigned.</li>
    * </ul>
    */
   private void configureNetworking(VirtualMachine vm, VirtualApplianceCachingTemplate template, Datacenter datacenter,
         TemplateOptions options) {

      if (!options.getNetworks().isEmpty()) {
         ImmutableList.Builder<Ip<?, ?>> ips = ImmutableList.<Ip<?, ?>> builder();

         Enterprise enterprise = adminService.getCurrentEnterprise();
         Iterable<ExternalNetwork> externalNetworks = enterprise.listExternalNetworks(datacenter);

         for (String networkId : options.getNetworks()) {
            Network<? extends Ip<?, ?>> network = template.getVirtualDatacenter().getPrivateNetwork(
                  Ints.tryParse(networkId));

            if (network == null) {
               // If the given network is not a private network, it should be an
               // external one
               final Integer id = Ints.tryParse(networkId);
               network = find(externalNetworks, new Predicate<Network<?>>() {
                  @Override
                  public boolean apply(final Network<?> input) {
                     return Integer.valueOf(id).equals(input.getId());
                  }
               }, null);
            }

            checkArgument(network != null, "No network was found with id: %s", networkId);

            Iterable<? extends Ip<?, ?>> unusedIps = network.listUnusedIps();
            checkArgument(!isEmpty(unusedIps), "There are no available ips in network: %s", networkId);

            // Get the first available ip
            Ip<?, ?> availableIp = get(unusedIps, 0);
            logger.debug(">> Found available ip: %s", availableIp);
            ips.add(availableIp);
         }

         // Assign all ips to the virtual machine
         vm.setNics(ips.build());
      } else {
         Optional<PublicIp> publicIp = tryFind(template.getVirtualDatacenter().listPurchasedPublicIps(),
               IpPredicates.<PublicIp> notUsed());
         if (publicIp.isPresent()) {
            logger.debug(">> Found available public ip %s", publicIp.get().getIp());
            vm.setNics(ImmutableList.<Ip<?, ?>> of(publicIp.get()));
         } else {
            logger.debug(">> No available public ip found. Using a private ip");
         }
      }
   }

   private static Predicate<VirtualMachine> vmId(final String id) {
      return new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(final VirtualMachine input) {
            return Integer.valueOf(id).equals(input.getId());
         }
      };
   }

   private static int totalCores(final Hardware hardware) {
      double cores = 0;
      for (Processor processor : hardware.getProcessors()) {
         cores += processor.getCores();
      }
      return Double.valueOf(cores).intValue();
   }

}
