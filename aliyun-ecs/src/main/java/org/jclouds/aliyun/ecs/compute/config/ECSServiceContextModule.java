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
package org.jclouds.aliyun.ecs.compute.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.compute.ECSComputeService;
import org.jclouds.aliyun.ecs.compute.ECSComputeServiceAdapter;
import org.jclouds.aliyun.ecs.compute.functions.ImageInRegionToImage;
import org.jclouds.aliyun.ecs.compute.functions.InstanceStatusToStatus;
import org.jclouds.aliyun.ecs.compute.functions.InstanceToNodeMetadata;
import org.jclouds.aliyun.ecs.compute.functions.InstanceTypeToHardware;
import org.jclouds.aliyun.ecs.compute.functions.RegionToLocation;
import org.jclouds.aliyun.ecs.compute.strategy.CreateResourcesThenCreateNodes;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.InstanceStatus;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.aliyun.ecs.domain.Region;
import org.jclouds.aliyun.ecs.domain.regionscoped.ImageInRegion;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.aliyun.ecs.compute.options.ECSServiceTemplateOptions;
import org.jclouds.aliyun.ecs.predicates.InstanceStatusPredicate;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatement;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.domain.Location;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

public class ECSServiceContextModule extends ComputeServiceAdapterContextModule<Instance, InstanceType, ImageInRegion, Region> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<Instance, InstanceType, ImageInRegion, Region>>() {
      }).to(ECSComputeServiceAdapter.class);
      bind(ComputeService.class).to(ECSComputeService.class);

      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<Function<InstanceType, Hardware>>() {
      }).to(InstanceTypeToHardware.class);
      bind(new TypeLiteral<Function<ImageInRegion, org.jclouds.compute.domain.Image>>() {
      }).to(ImageInRegionToImage.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<Instance.Status, NodeMetadata.Status>>() {
      }).to(InstanceStatusToStatus.class);
      install(new LocationsFromComputeServiceAdapterModule<Instance, InstanceType, ImageInRegion, Region>() {
      });
      bind(TemplateOptions.class).to(ECSServiceTemplateOptions.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(CreateResourcesThenCreateNodes.class);
      bind(NodeAndTemplateOptionsToStatement.class).to(NodeAndTemplateOptionsToStatementWithoutPublicKey.class);
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<String> provideInstanceRunningPredicate(final ECSComputeServiceApi api,
                                                               ComputeServiceConstants.Timeouts timeouts, ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new InstanceInStatusPredicate(api, InstanceStatus.Status.RUNNING), timeouts.nodeRunning,
              pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<String> provideInstanceSuspendedPredicate(final ECSComputeServiceApi api,
                                                                 ComputeServiceConstants.Timeouts timeouts, ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new InstanceInStatusPredicate(api, InstanceStatus.Status.STOPPED), timeouts.nodeSuspended,
              pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<String> provideInstanceTerminatedPredicate(final ECSComputeServiceApi api,
                                                                  ComputeServiceConstants.Timeouts timeouts, ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new InstanceTerminatedPredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class InstanceInStatusPredicate implements Predicate<String> {

      private final ECSComputeServiceApi api;
      private final InstanceStatus.Status desiredStatus;

      public InstanceInStatusPredicate(ECSComputeServiceApi api, InstanceStatus.Status desiredStatus) {
         this.api = checkNotNull(api, "api must not be null");
         this.desiredStatus = checkNotNull(desiredStatus, "instance status must not be null");
      }

      @Override
      public boolean apply(String id) {
         checkNotNull(id, "id");
         RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
         String regionId = regionAndId.regionId();
         String instanceId = regionAndId.id();
         InstanceStatus instanceStatus = api.instanceApi().listInstanceStatus(regionId)
                 .concat()
                 .firstMatch(new InstanceStatusPredicate(instanceId))
                 .orNull();
         return instanceStatus != null && desiredStatus == instanceStatus.status();
      }
   }

   @VisibleForTesting
   static class InstanceTerminatedPredicate implements Predicate<String> {

      private final ECSComputeServiceApi api;

      public InstanceTerminatedPredicate(ECSComputeServiceApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(String id) {
         checkNotNull(id, "id");
         RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
         String regionId = regionAndId.regionId();
         final String instanceId = regionAndId.id();
         InstanceStatus instanceStatus = api.instanceApi().listInstanceStatus(regionId)
                 .concat()
                 .firstMatch(new InstanceStatusPredicate(instanceId))
                 .orNull();
         return instanceStatus == null;
      }

   }

}
