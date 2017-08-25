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

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.compute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecompute.compute.predicates.StorageServicePredicates;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.domain.StorageService;
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
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

@Singleton
public class GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes
        extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private static final String DEFAULT_STORAGE_ACCOUNT_PREFIX = "jclouds";
   private static final String DEFAULT_STORAGE_SERVICE_TYPE = "Standard_GRS";

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
      final String location = template.getLocation().getId();
      final String storageAccountName = templateOptions.getStorageAccountName();
      final String storageAccountType = firstNonNull(templateOptions.getStorageAccountType(), DEFAULT_STORAGE_SERVICE_TYPE);
      final String virtualNetworkName = templateOptions.getVirtualNetworkName();

      final StorageService storageService;
      if (storageAccountName != null) {
         if (api.getStorageAccountApi().get(storageAccountName) == null) {
            String message = String.format("storageAccountName %s specified via AzureComputeTemplateOptions doesn't exist", storageAccountName);
            logger.error(message);
            throw new IllegalStateException(message);
         }
      } else { // get suitable or create storage service
         storageService = tryFindExistingStorageServiceAccountOrCreate(api, location, generateStorageServiceName(DEFAULT_STORAGE_ACCOUNT_PREFIX), storageAccountType);
         templateOptions.storageAccountName(storageService.serviceName());
      }

      if (virtualNetworkName != null && templateOptions.getSubnetNames().isEmpty()) {
         String message = "AzureComputeTemplateOption.subnetNames must not be empty, if AzureComputeTemplateOption.virtualNetworkName is defined.";
         logger.warn(message);
         throw new IllegalArgumentException(message);
      }

      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Tries to find a storage service account whose name matches the regex DEFAULT_STORAGE_ACCOUNT_PREFIX+"[a-z]{10}" in
    * the location, otherwise it creates a new storage service account with name and type in the location
    */
   private StorageService tryFindExistingStorageServiceAccountOrCreate(
           final AzureComputeApi api, final String location, final String storageAccountName, final String type) {

      final List<StorageService> storageServices = api.getStorageAccountApi().list();
      logger.debug("Looking for a suitable existing storage account ...");

      final Predicate<StorageService> storageServicePredicate = and(
              notNull(),
              StorageServicePredicates.sameLocation(location),
              StorageServicePredicates.status(StorageService.Status.Created),
              StorageServicePredicates.matchesName(DEFAULT_STORAGE_ACCOUNT_PREFIX)
      );

      final Optional<StorageService> storageServiceOptional = tryFind(storageServices, storageServicePredicate);
      if (storageServiceOptional.isPresent()) {
         final StorageService storageService = storageServiceOptional.get();
         logger.debug("Found a suitable existing storage service account '%s'", storageService);
         return storageService;
      } else {
         // create
         if (!checkAvailability(storageAccountName)) {
            logger.warn("The storage service account name %s is not available", storageAccountName);
            throw new IllegalStateException(format("Can't create a valid storage account with name %s. "
                    + "Please, try by choosing a different `storageAccountName` in templateOptions and try again", storageAccountName));
         }
         logger.debug("Creating a storage service account '%s' in location '%s' ...", storageAccountName, location);
         final String createStorageServiceRequestId = api.getStorageAccountApi().create(
                 CreateStorageServiceParams.builder()
                 .serviceName(storageAccountName)
                 .label(storageAccountName)
                 .location(location)
                 .accountType(StorageService.AccountType.valueOf(type))
                 .build());
         if (!operationSucceededPredicate.apply(createStorageServiceRequestId)) {
            final String warnMessage = format("Create storage service account has not been completed within %sms.",
                    azureComputeConstants.operationTimeout());
            logger.warn(warnMessage);
            final String illegalStateExceptionMessage = format("%s. Please, try by increasing `%s` and try again",
                    warnMessage, AzureComputeProperties.OPERATION_TIMEOUT);
            throw new IllegalStateException(illegalStateExceptionMessage);
         }
         return api.getStorageAccountApi().get(storageAccountName);
      }
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

}
