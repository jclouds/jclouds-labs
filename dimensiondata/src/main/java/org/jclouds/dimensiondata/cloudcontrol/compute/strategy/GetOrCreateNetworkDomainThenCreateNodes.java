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
package org.jclouds.dimensiondata.cloudcontrol.compute.strategy;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.firstNonNull;
import static java.lang.String.format;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_NETWORK_DOMAIN_NAME;
import static org.jclouds.dimensiondata.cloudcontrol.compute.options.DimensionDataCloudControlTemplateOptions.DEFAULT_VLAN_NAME;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.NETWORK_DOMAIN_NORMAL_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.VLAN_NORMAL_PREDICATE;

@Singleton
public class GetOrCreateNetworkDomainThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final DimensionDataCloudControlApi api;
   private final ComputeServiceConstants.Timeouts timeouts;
   private final Predicate<String> networkDomainNormalPredicate;
   private final Predicate<String> vlanNormalPredicate;

   @Inject
   protected GetOrCreateNetworkDomainThenCreateNodes(final CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
         final ListNodesStrategy listNodesStrategy, final GroupNamingConvention.Factory namingConvention,
         final ListeningExecutorService userExecutor,
         final CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         final DimensionDataCloudControlApi api, final ComputeServiceConstants.Timeouts timeouts,
         @Named(NETWORK_DOMAIN_NORMAL_PREDICATE) final Predicate<String> networkDomainNormalPredicate,
         @Named(VLAN_NORMAL_PREDICATE) final Predicate<String> vlanNormalPredicate) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.timeouts = timeouts;
      this.networkDomainNormalPredicate = networkDomainNormalPredicate;
      this.vlanNormalPredicate = vlanNormalPredicate;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(final String group, final int count, final Template template,
         final Set<NodeMetadata> goodNodes, final Map<NodeMetadata, Exception> badNodes,
         final Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      final DimensionDataCloudControlTemplateOptions templateOptions = template.getOptions()
            .as(DimensionDataCloudControlTemplateOptions.class);

      String networkDomainName = firstNonNull(templateOptions.getNetworkDomainName(), DEFAULT_NETWORK_DOMAIN_NAME);
      String vlanName = firstNonNull(
            templateOptions.getNetworks().isEmpty() ? null : templateOptions.getNetworks().iterator().next(),
            DEFAULT_VLAN_NAME);
      templateOptions.networkDomainName(networkDomainName);
      String networkDomainId = tryCreateOrGetExistingNetworkDomainId(template.getLocation().getId(), networkDomainName);
      String vlanId = tryCreateOrGetExistingVlanId(networkDomainId, vlanName, templateOptions);
      templateOptions.networks(vlanName);
      return super
            .execute(group, count, new TemplateWithNetworkIds(template, networkDomainId, vlanId), goodNodes, badNodes,
                  customizationResponses);
   }

   private String tryCreateOrGetExistingNetworkDomainId(final String datacenterId, final String networkDomainName) {
      String networkDomainId = getExistingNetworkDomainId(datacenterId, networkDomainName).orNull();
      if (networkDomainId != null) {
         logger.debug("Found a suitable existing network domain %s", networkDomainId);
      } else {
         networkDomainId = deployNeworkDomain(datacenterId, networkDomainName);
      }
      return networkDomainId;
   }

   private String deployNeworkDomain(final String datacenter, final String networkDomainName) {
      logger.debug("Creating a network domain '%s' in Datacenter '%s' ...", networkDomainName, datacenter);
      String networkDomainId = api.getNetworkApi()
            .deployNetworkDomain(datacenter, networkDomainName, "network domain created by jclouds",
                  NetworkDomain.Type.ESSENTIALS.name());
      String message = format("networkDomain(%s) is not ready within %d ms.", networkDomainId, timeouts.nodeRunning);

      if (!networkDomainNormalPredicate.apply(networkDomainId)) {
         throw new IllegalStateException(message);
      }
      return networkDomainId;
   }

   private Optional<String> getExistingNetworkDomainId(final String datacenterId, final String networkDomainName) {
      Optional<NetworkDomain> networkDomainOptional = api.getNetworkApi()
            .listNetworkDomainsWithDatacenterIdAndName(datacenterId, networkDomainName).concat().first();
      if (networkDomainOptional.isPresent()) {
         return Optional.of(networkDomainOptional.get().id());
      } else {
         return Optional.<String>absent();
      }
   }

   private String tryCreateOrGetExistingVlanId(final String networkDomainId, final String vlanName,
         final DimensionDataCloudControlTemplateOptions templateOptions) {

      String vlanId = getExistingVlan(networkDomainId, vlanName).orNull();
      if (vlanId != null) {
         logger.debug("Found a suitable existing vlan %s", vlanId);
      } else {
         vlanId = deployVlan(networkDomainId, vlanName, templateOptions);
      }
      return vlanId;

   }

   private String deployVlan(final String networkDomainId, final String vlanName,
         final DimensionDataCloudControlTemplateOptions templateOptions) {
      logger.debug("Creating a vlan %s in network domain '%s' ...", vlanName, networkDomainId);
      String defaultPrivateIPv4BaseAddress = firstNonNull(templateOptions.getDefaultPrivateIPv4BaseAddress(),
            DimensionDataCloudControlTemplateOptions.DEFAULT_PRIVATE_IPV4_BASE_ADDRESS);
      Integer defaultPrivateIPv4PrefixSize = firstNonNull(templateOptions.getDefaultPrivateIPv4PrefixSize(),
            DimensionDataCloudControlTemplateOptions.DEFAULT_PRIVATE_IPV4_PREFIX_SIZE);

      String vlanId = api.getNetworkApi()
            .deployVlan(networkDomainId, vlanName, "vlan created by jclouds", defaultPrivateIPv4BaseAddress,
                  defaultPrivateIPv4PrefixSize);
      if (!vlanNormalPredicate.apply(vlanId)) {
         String message = format("vlan(%s) is not ready within %d ms.", vlanId, timeouts.nodeRunning);
         throw new IllegalStateException(message);
      }
      return vlanId;
   }

   private Optional<String> getExistingVlan(final String networkDomainId, final String vlanName) {
      FluentIterable<Vlan> vlans = api.getNetworkApi().listVlans(networkDomainId).concat();
      final Optional<Vlan> vlan = vlans.firstMatch(new Predicate<Vlan>() {
         @Override
         public boolean apply(final Vlan input) {
            return input.name().equals(vlanName);
         }
      });

      if (vlan.isPresent()) {
         return Optional.of(vlan.get().id());
      } else {
         return Optional.absent();
      }
   }
}

