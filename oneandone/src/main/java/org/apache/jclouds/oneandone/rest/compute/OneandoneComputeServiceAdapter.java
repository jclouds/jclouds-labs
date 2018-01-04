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
package org.apache.jclouds.oneandone.rest.compute;

import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicate;
import static com.google.common.base.Strings.isNullOrEmpty;
import com.google.common.base.Throwables;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.compute.strategy.CleanupResources;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_PREDICATE_SERVER;
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.FirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import static org.jclouds.compute.util.ComputeServiceUtils.getPortRangesFromList;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.PasswordGenerator;

@Singleton
public class OneandoneComputeServiceAdapter implements ComputeServiceAdapter<Server, HardwareFlavour, SingleServerAppliance, DataCenter> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CleanupResources cleanupResources;
   private final OneAndOneApi api;
   private final Predicate<Server> waitServerUntilAvailable;
   private final PasswordGenerator.Config passwordGenerator;

   @Inject
   OneandoneComputeServiceAdapter(OneAndOneApi api, CleanupResources cleanupResources,
           @Named(POLL_PREDICATE_SERVER) Predicate<Server> waitServerUntilAvailable,
           PasswordGenerator.Config passwordGenerator) {
      this.api = api;
      this.cleanupResources = cleanupResources;
      this.waitServerUntilAvailable = waitServerUntilAvailable;
      this.passwordGenerator = passwordGenerator;
   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {

      final String dataCenterId = template.getLocation().getId();
      Hardware hardware = template.getHardware();
      TemplateOptions options = template.getOptions();
      Server updateServer = null;

      final String loginUser = isNullOrEmpty(options.getLoginUser()) ? "root" : options.getLoginUser();
      final String password = options.hasLoginPassword() ? options.getLoginPassword() : passwordGenerator.generate();
      final String privateKey = options.hasLoginPrivateKey() ? options.getPrivateKey() : null;
      final org.jclouds.compute.domain.Image image = template.getImage();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      //prepare hdds to provision
      List<? extends Volume> volumes = hardware.getVolumes();
      List<Hdd.CreateHdd> hdds = new ArrayList<Hdd.CreateHdd>();

      for (final Volume volume : volumes) {
         try {
            //check if the bootable device has enough size to run the appliance(image).
            float minHddSize = volume.getSize();
            if (volume.isBootDevice()) {
               SingleServerAppliance appliance = api.serverApplianceApi().get(image.getId());
               if (appliance.minHddSize() > volume.getSize()) {
                  minHddSize = appliance.minHddSize();
               }
            }
            Hdd.CreateHdd hdd = Hdd.CreateHdd.create(minHddSize, volume.isBootDevice());
            hdds.add(hdd);
         } catch (Exception ex) {
            throw Throwables.propagate(ex);

         }
      }

      // provision server
      Server server = null;
      Double cores = ComputeServiceUtils.getCores(hardware);
      Double ram = (double) hardware.getRam();
      if (ram < 1024) {
         ram = 0.5;
      } else {
         ram = ram / 1024;
      }

      try {
         org.apache.jclouds.oneandone.rest.domain.Hardware.CreateHardware hardwareRequest
                 = org.apache.jclouds.oneandone.rest.domain.Hardware.CreateHardware.create(cores, 1, ram, hdds);
         final Server.CreateServer serverRequest = Server.CreateServer.builder()
                 .name(name)
                 .description(name)
                 .hardware(hardwareRequest)
                 .rsaKey(options.getPublicKey())
                 .password(privateKey == null ? password : null)
                 .applianceId(image.getId())
                 .dataCenterId(dataCenterId)
                 .powerOn(Boolean.TRUE).build();

         logger.trace("<< provisioning server '%s'", serverRequest);

         server = api.serverApi().create(serverRequest);

         waitServerUntilAvailable.apply(server);

         updateServer = api.serverApi().get(server.id());

         Map<Integer, Integer> portsRange = getPortRangesFromList(inboundPorts);
         List<FirewallPolicy.Rule.CreatePayload> rules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();

         for (Map.Entry<Integer, Integer> range : portsRange.entrySet()) {
            FirewallPolicy.Rule.CreatePayload rule = FirewallPolicy.Rule.CreatePayload.builder()
                    .portFrom(range.getKey())
                    .portTo(range.getValue())
                    .protocol(Types.RuleProtocol.TCP)
                    .build();
            rules.add(rule);
         }
         if (inboundPorts.length > 0) {
            FirewallPolicy rule = api.firewallPolicyApi().create(FirewallPolicy.CreateFirewallPolicy.create(server.name() + " firewall policy", "desc", rules));
            api.serverApi().addFirewallPolicy(updateServer.id(), updateServer.ips().get(0).id(), rule.id());
            waitServerUntilAvailable.apply(server);
         }

         logger.trace(">> provisioning complete for server. returned id='%s'", server.id());

      } catch (Exception ex) {
         logger.error(ex, ">> failed to provision server. rollbacking..");
         if (server != null) {
            destroyNode(server.id());
         }
         throw Throwables.propagate(ex);
      }

      LoginCredentials serverCredentials = LoginCredentials.builder()
              .user(loginUser)
              .password(password)
              .privateKey(privateKey)
              .build();

      return new NodeAndInitialCredentials<Server>(updateServer, updateServer.id(), serverCredentials);
   }

   @Override
   public List<HardwareFlavour> listHardwareProfiles() {
      return api.serverApi().listHardwareFlavours();
   }

   @Override
   public Iterable<SingleServerAppliance> listImages() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, null, null);
      List<ServerAppliance> list = api.serverApplianceApi().list(options);
      List<SingleServerAppliance> results = new ArrayList<SingleServerAppliance>();
      for (ServerAppliance appliance : list) {
         List<SingleServerAppliance.AvailableDataCenters> availableDatacenters = new ArrayList<SingleServerAppliance.AvailableDataCenters>();
         for (String dcId : appliance.availableDataCenters()) {
            availableDatacenters.add(SingleServerAppliance.AvailableDataCenters.create(dcId, ""));
         }
         results.add(SingleServerAppliance.builder()
                 .id(appliance.id())
                 .name(appliance.name())
                 .availableDataCenters(availableDatacenters)
                 .osInstallationBase(appliance.osInstallationBase())
                 .osFamily(appliance.osFamily())
                 .os(appliance.os())
                 .osVersion(appliance.osVersion())
                 .osArchitecture(appliance.osArchitecture())
                 .osImageType(appliance.osImageType())
                 .minHddSize(appliance.minHddSize())
                 .type(appliance.type())
                 .state(appliance.state())
                 .version(appliance.version())
                 .categories(appliance.categories())
                 .eulaUrl(appliance.eulaUrl())
                 .build());
      }
      return results;
   }

   @Override
   public SingleServerAppliance getImage(String id) {
      // try search images
      logger.trace("<< searching for image with id=%s", id);
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, id, null);
      try {
         List<ServerAppliance> list = api.serverApplianceApi().list(options);
         if (list.size() > 0) {
            ServerAppliance appliance = list.get(0);
            List<SingleServerAppliance.AvailableDataCenters> availableDatacenters = new ArrayList<SingleServerAppliance.AvailableDataCenters>();
            for (String dcId : appliance.availableDataCenters()) {
               availableDatacenters.add(SingleServerAppliance.AvailableDataCenters.create(dcId, ""));
            }
            SingleServerAppliance image = SingleServerAppliance.create(appliance.id(), appliance.name(), availableDatacenters, appliance.osInstallationBase(),
                    appliance.osFamily(), appliance.os(), appliance.osVersion(), appliance.osArchitecture(), appliance.osImageType(), appliance.minHddSize(),
                    appliance.type(), appliance.state(), appliance.version(), appliance.categories(), appliance.eulaUrl());
            logger.trace(">> found image [%s].", image.name());
            return image;
         }
      } catch (Exception ex) {
         throw new ResourceNotFoundException("No image with id '" + id + "' was found");
      }
      throw new ResourceNotFoundException("No image with id '" + id + "' was found");
   }

   @Override
   public Iterable<DataCenter> listLocations() {
      return api.dataCenterApi().list();
   }

   @Override
   public Server getNode(String id) {
      return api.serverApi().get(id);
   }

   @Override
   public void destroyNode(String id) {
      checkState(cleanupResources.cleanupNode(id), "server(%s) and its resources still there after deleting!?", id);
   }

   @Override
   public void rebootNode(String id) {
      waitServerUntilAvailable.apply(getNode(id));
      api.serverApi().updateStatus(id, Server.UpdateStatus.create(Types.ServerAction.REBOOT, Types.ServerActionMethod.HARDWARE));
   }

   @Override
   public void resumeNode(String id) {
      api.serverApi().updateStatus(id, Server.UpdateStatus.create(Types.ServerAction.POWER_ON, Types.ServerActionMethod.HARDWARE));
   }

   @Override
   public void suspendNode(String id) {
      waitServerUntilAvailable.apply(getNode(id));
      api.serverApi().updateStatus(id, Server.UpdateStatus.create(Types.ServerAction.POWER_OFF, Types.ServerActionMethod.HARDWARE));
   }

   @Override
   public Iterable<Server> listNodes() {
      return api.serverApi().list();
   }

   @Override
   public Iterable<Server> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Server>() {
         @Override
         public boolean apply(Server server) {
            return contains(ids, server);
         }
      });
   }

}
