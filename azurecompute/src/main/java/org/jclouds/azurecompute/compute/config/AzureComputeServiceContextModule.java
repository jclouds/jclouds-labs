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
package org.jclouds.azurecompute.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.config.AzureComputeProperties.TCP_RULE_FORMAT;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.compute.extensions.AzureComputeSecurityGroupExtension;
import org.jclouds.azurecompute.compute.functions.DeploymentToNodeMetadata;
import org.jclouds.azurecompute.compute.functions.LocationToLocation;
import org.jclouds.azurecompute.compute.functions.OSImageToImage;
import org.jclouds.azurecompute.compute.functions.RoleSizeToHardware;
import org.jclouds.azurecompute.compute.strategy.GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes;
import org.jclouds.azurecompute.compute.strategy.UseNodeCredentialsButOverrideFromTemplate;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Operation;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.options.AzureComputeTemplateOptions;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.util.Predicates2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class AzureComputeServiceContextModule
      extends ComputeServiceAdapterContextModule<Deployment, RoleSize, OSImage, Location> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Deployment, RoleSize, OSImage, Location>>() {
      }).to(AzureComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<OSImage, org.jclouds.compute.domain.Image>>() {
      }).to(OSImageToImage.class);
      bind(new TypeLiteral<Function<RoleSize, Hardware>>() {
      }).to(RoleSizeToHardware.class);
      bind(new TypeLiteral<Function<Deployment, NodeMetadata>>() {
      }).to(DeploymentToNodeMetadata.class);

      bind(PrioritizeCredentialsFromTemplate.class).to(UseNodeCredentialsButOverrideFromTemplate.class);
      bind(new TypeLiteral<Function<Location, org.jclouds.domain.Location>>() {
      }).to(LocationToLocation.class);

      bind(TemplateOptions.class).to(AzureComputeTemplateOptions.class);

      bind(new TypeLiteral<SecurityGroupExtension>() {}).to(AzureComputeSecurityGroupExtension.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes.class);

      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<Deployment, RoleSize, OSImage, Location>(){});
   }

   @Override
   protected Optional<SecurityGroupExtension> provideSecurityGroupExtension(Injector i) {
      return Optional.of(i.getInstance(SecurityGroupExtension.class));
   }

   @Provides
   @Singleton
   protected Predicate<String> provideOperationSucceededPredicate(final AzureComputeApi api, AzureComputeConstants azureComputeConstants) {
      return Predicates2.retry(new OperationSucceededPredicate(api),
              azureComputeConstants.operationTimeout(), azureComputeConstants.operationPollInitialPeriod(),
              azureComputeConstants.operationPollMaxPeriod());
   }

   @VisibleForTesting
   static class OperationSucceededPredicate implements Predicate<String> {

      private final AzureComputeApi api;

      public OperationSucceededPredicate(AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(String input) {
         Operation operation = api.getOperationApi().get(input);
         switch (operation.status()) {
            case SUCCEEDED:
               return true;
            case IN_PROGRESS:
               return false;
            case FAILED:
               return false;
            case UNRECOGNIZED:
               return false;
            default:
               throw new IllegalStateException("Operation is in invalid status: " + operation.status().name());
         }
      }

   }

   @Singleton
   public static class AzureComputeConstants {
      @Named(OPERATION_TIMEOUT)
      @Inject
      private String operationTimeoutProperty;

      @Named(OPERATION_POLL_INITIAL_PERIOD)
      @Inject
      private String operationPollInitialPeriodProperty;

      @Named(OPERATION_POLL_MAX_PERIOD)
      @Inject
      private String operationPollMaxPeriodProperty;

      @Named(TCP_RULE_FORMAT)
      @Inject
      private String tcpRuleFormatProperty;

      public Long operationTimeout() {
         return Long.parseLong(operationTimeoutProperty);
      }

      public Integer operationPollInitialPeriod() {
         return Integer.parseInt(operationPollInitialPeriodProperty);
      }

      public Integer operationPollMaxPeriod() {
         return Integer.parseInt(operationPollMaxPeriodProperty);
      }

      public String tcpRuleFormat() {
         return tcpRuleFormatProperty;
      }
   }

}
