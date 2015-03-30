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
package org.jclouds.azurecompute.compute.strategy;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.String.format;
import static org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.options.AzureComputeTemplateOptions;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

@Singleton
public class GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes
        extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private static final String DEFAULT_STORAGE_ACCOUNT_PREFIX = "jclouds";

   private static final String DEFAULT_STORAGE_SERVICE_TYPE = "Standard_GRS";

   private static final String DEFAULT_VIRTUAL_NETWORK_NAME = "jclouds-virtual-network";

   private static final String DEFAULT_ADDRESS_SPACE_ADDRESS_PREFIX = "10.0.0.0/20";

   private static final String DEFAULT_SUBNET_NAME = "jclouds-1";

   private static final String DEFAULT_SUBNET_ADDRESS_PREFIX = "10.0.0.0/23";

   private final AzureComputeApi api;

   private final Predicate<String> operationSucceededPredicate;

   private final AzureComputeConstants azureComputeConstants;

   @Inject
   protected GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named("jclouds.user-threads") ListeningExecutorService userExecutor,
           Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           AzureComputeApi api,
           Predicate<String> operationSucceededPredicate,
           AzureComputeConstants azureComputeConstants) {

      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);

      this.api = api;
      this.operationSucceededPredicate = operationSucceededPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   protected ListenableFuture<AtomicReference<NodeMetadata>> createNodeInGroupWithNameAndTemplate(
           final String group, final String name, final Template template) {

      return super.createNodeInGroupWithNameAndTemplate(group, name, template);
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(
           final String group, final int count, final Template template,
           final Set<NodeMetadata> goodNodes, final Map<NodeMetadata, Exception> badNodes,
           final Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      final AzureComputeTemplateOptions templateOptions = template.getOptions().as(AzureComputeTemplateOptions.class);
      final String storageAccountName = templateOptions.getStorageAccountName().
              or(generateStorageServiceName(DEFAULT_STORAGE_ACCOUNT_PREFIX));
      final String location = template.getLocation().getId();
      final String storageAccountType = templateOptions.getStorageAccountType().or(DEFAULT_STORAGE_SERVICE_TYPE);
      final String virtualNetworkName = templateOptions.getVirtualNetworkName().or(DEFAULT_VIRTUAL_NETWORK_NAME);
      final String subnetName = templateOptions.getSubnetName().or(DEFAULT_SUBNET_NAME);
      final String addressSpaceAddressPrefix = templateOptions.getAddressSpaceAddressPrefix().
              or(DEFAULT_ADDRESS_SPACE_ADDRESS_PREFIX);
      final String subnetAddressPrefix = templateOptions.getSubnetAddressPrefix().or(DEFAULT_SUBNET_ADDRESS_PREFIX);
      final Set<String> networkSecurityGroupNames = templateOptions.getGroups().isEmpty() ? Sets.<String>newHashSet() : templateOptions.getGroups();

      // get or create storage service
      final StorageService storageService = tryFindExistingStorageServiceAccountOrCreate(
              api, location, storageAccountName, storageAccountType);
      templateOptions.storageAccountName(storageService.serviceName());

      // check existence or create virtual network
      checkExistingVirtualNetworkNamedOrCreate(
              virtualNetworkName, location, subnetName, addressSpaceAddressPrefix, subnetAddressPrefix);
      templateOptions.virtualNetworkName(virtualNetworkName);
      templateOptions.subnetName(subnetName);

      // add network security group to the subnet
      if (!networkSecurityGroupNames.isEmpty()) {
         String networkSecurityGroupName = Iterables.get(networkSecurityGroupNames, 0);
         logger.warn("Only network security group '%s' will be applied to subnet '%s'.",
                 networkSecurityGroupName, subnetName);
         final NetworkSecurityGroup networkSecurityGroupAppliedToSubnet = api.getNetworkSecurityGroupApi().
                 getNetworkSecurityGroupAppliedToSubnet(virtualNetworkName, subnetName);
         if (networkSecurityGroupAppliedToSubnet != null) {
            if (!networkSecurityGroupAppliedToSubnet.name().equals(networkSecurityGroupName)) {
               logger.debug("Removing a networkSecurityGroup %s is already applied to subnet '%s' ...",
                       networkSecurityGroupName, subnetName);
               // remove existing nsg from subnet
               String removeFromSubnetRequestId = api.getNetworkSecurityGroupApi().
                       removeFromSubnet(virtualNetworkName, subnetName, networkSecurityGroupAppliedToSubnet.name());
               if (!operationSucceededPredicate.apply(removeFromSubnetRequestId)) {
                  final String warnMessage = format(
                          "Remove existing networkSecurityGroup(%s) to subnet(%s) has not been completed "
                          + "within %sms.", networkSecurityGroupName, subnetName,
                          azureComputeConstants.operationTimeout());
                  logger.warn(warnMessage);
                  final String illegalStateExceptionMessage = format(
                          "%s. Please, try by increasing `%s` and try again",
                          AzureComputeProperties.OPERATION_TIMEOUT, warnMessage);
                  throw new IllegalStateException(illegalStateExceptionMessage);
               }
            }
         }
         // add nsg to subnet
         logger.debug("Adding a networkSecurityGroup %s is already applied to subnet '%s' of virtual network %s ...",
                 networkSecurityGroupName, subnetName, virtualNetworkName);
         final String addToSubnetId = api.getNetworkSecurityGroupApi().addToSubnet(virtualNetworkName, subnetName,
                 networkSecurityGroupName);
         if (!operationSucceededPredicate.apply(addToSubnetId)) {
            final String warnMessage = format("Add networkSecurityGroup(%s) to subnet(%s) has not been completed "
                    + "within %sms.", networkSecurityGroupName, subnetName, azureComputeConstants.operationTimeout());
            logger.warn(warnMessage);
            final String illegalStateExceptionMessage = format("%s. Please, try by increasing `%s` and try again",
                    AzureComputeProperties.OPERATION_TIMEOUT, warnMessage);
            throw new IllegalStateException(illegalStateExceptionMessage);
         }
      }
      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Tries to find a storage service account whose name matches the regex DEFAULT_STORAGE_ACCOUNT_PREFIX+"[a-z]{10}" in
    * the location, otherwise it creates a new storage service account with name and type in the location
    */
   private StorageService tryFindExistingStorageServiceAccountOrCreate(
           final AzureComputeApi api, final String location, final String name, final String type) {

      final List<StorageService> storageServices = api.getStorageAccountApi().list();
      logger.debug("Looking for a suitable existing storage account ...");

      @SuppressWarnings("unchecked")
      final Predicate<StorageService> storageServicePredicate = and(notNull(),
              new SameLocationAndCreatedStorageServicePredicate(location), new Predicate<StorageService>() {
                 @Override
                 public boolean apply(final StorageService input) {
                    return input.serviceName().matches(format("^%s[a-z]{10}$", DEFAULT_STORAGE_ACCOUNT_PREFIX));
                 }
              });
      final Optional<StorageService> storageServiceOptional = tryFind(storageServices, storageServicePredicate);
      if (storageServiceOptional.isPresent()) {
         final StorageService storageService = storageServiceOptional.get();
         logger.debug("Found a suitable existing storage service account '%s'", storageService);
         return storageService;
      } else {
         // create
         if (!checkAvailability(name)) {
            logger.warn("The storage service account name %s is not available", name);
            throw new IllegalStateException(format("Can't create a valid storage account with name %s. "
                    + "Please, try by choosing a different `storageAccountName` in templateOptions and try again", name));
         }
         logger.debug("Creating a storage service account '%s' in location '%s' ...", name, location);
         final String createStorateServiceRequestId = api.getStorageAccountApi().create(
                 CreateStorageServiceParams.builder()
                 .serviceName(name)
                 .label(name)
                 .location(location)
                 .accountType(StorageService.AccountType.valueOf(type))
                 .build());
         if (!operationSucceededPredicate.apply(createStorateServiceRequestId)) {
            final String warnMessage = format("Create storage service account has not been completed within %sms.",
                    azureComputeConstants.operationTimeout());
            logger.warn(warnMessage);
            final String illegalStateExceptionMessage = format("%s. Please, try by increasing `%s` and try again",
                    AzureComputeProperties.OPERATION_TIMEOUT, warnMessage);
            throw new IllegalStateException(illegalStateExceptionMessage);
         }
         return api.getStorageAccountApi().get(name);
      }
   }

   private void checkExistingVirtualNetworkNamedOrCreate(
           final String virtualNetworkName, final String location, final String subnetName,
           final String addressSpaceAddressPrefix, final String subnetAddressPrefix) {

      logger.debug("Looking for a virtual network named '%s' ...", virtualNetworkName);
      final Optional<NetworkConfiguration.VirtualNetworkSite> networkSiteOptional
              = getVirtualNetworkNamed(virtualNetworkName);
      if (networkSiteOptional.isPresent()) {
         return;
      }
      final NetworkConfiguration networkConfiguration = NetworkConfiguration.create(
              NetworkConfiguration.VirtualNetworkConfiguration.create(null,
                      ImmutableList.of(NetworkConfiguration.VirtualNetworkSite.create(
                                      UUID.randomUUID().toString(),
                                      virtualNetworkName,
                                      location,
                                      NetworkConfiguration.AddressSpace.create(addressSpaceAddressPrefix),
                                      ImmutableList.of(NetworkConfiguration.Subnet.create(
                                                      subnetName, subnetAddressPrefix, null))))
              )
      );
      logger.debug("Creating a virtual network with configuration '%s' ...", networkConfiguration);
      final String setNetworkConfigurationRequestId = api.getVirtualNetworkApi().set(networkConfiguration);
      if (!operationSucceededPredicate.apply(setNetworkConfigurationRequestId)) {
         final String warnMessage = format("Network configuration (%s) has not been completed within %sms.",
                 networkConfiguration, azureComputeConstants.operationTimeout());
         logger.warn(warnMessage);
         final String illegalStateExceptionMessage = format("%s. Please, try by increasing `%s` and try again",
                 AzureComputeProperties.OPERATION_TIMEOUT, warnMessage);
         throw new IllegalStateException(illegalStateExceptionMessage);
      }
   }

   private Optional<NetworkConfiguration.VirtualNetworkSite> getVirtualNetworkNamed(final String virtualNetworkName) {
      return FluentIterable.from(api.getVirtualNetworkApi().list())
              .filter(new Predicate<NetworkConfiguration.VirtualNetworkSite>() {
                 @Override
                 public boolean apply(final NetworkConfiguration.VirtualNetworkSite input) {
                    return input.name().equals(virtualNetworkName);
                 }
              })
              .first();
   }

   private boolean checkAvailability(final String name) {
      return api.getStorageAccountApi().isAvailable(name).result();
   }

   private static String generateStorageServiceName(final String prefix) {
      String characters = "abcdefghijklmnopqrstuvwxyz";
      StringBuilder builder = new StringBuilder();
      builder.append(prefix);
      int charactersLength = characters.length();
      for (int i = 0; i < 10; i++) {
         double index = Math.random() * charactersLength;
         builder.append(characters.charAt((int) index));
      }
      return builder.toString();
   }

   private static class SameLocationAndCreatedStorageServicePredicate implements Predicate<StorageService> {

      private final String location;

      public SameLocationAndCreatedStorageServicePredicate(final String location) {
         this.location = location;
      }

      @Override
      public boolean apply(final StorageService input) {
         return input.storageServiceProperties().location().equals(location)
                 && input.storageServiceProperties().status().equals("Created");
      }
   }
}
