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
package org.jclouds.aliyun.ecs.compute;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.compute.strategy.CleanupResources;
import org.jclouds.aliyun.ecs.domain.AvailableResource;
import org.jclouds.aliyun.ecs.domain.AvailableZone;
import org.jclouds.aliyun.ecs.domain.Image;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.InstanceRequest;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.aliyun.ecs.domain.Region;
import org.jclouds.aliyun.ecs.domain.SupportedResource;
import org.jclouds.aliyun.ecs.domain.options.CreateInstanceOptions;
import org.jclouds.aliyun.ecs.domain.options.ListImagesOptions;
import org.jclouds.aliyun.ecs.domain.options.ListInstancesOptions;
import org.jclouds.aliyun.ecs.domain.options.TagOptions;
import org.jclouds.aliyun.ecs.domain.regionscoped.ImageInRegion;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.aliyun.ecs.compute.options.ECSServiceTemplateOptions;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId.fromSlashEncoded;
import static org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId.slashEncodeRegionAndId;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

/**
 * defines the connection between the {@link ECSComputeServiceApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class ECSComputeServiceAdapter implements ComputeServiceAdapter<Instance, InstanceType, ImageInRegion, Region> {

   private final ECSComputeServiceApi api;
   private final Predicate<String> instanceSuspendedPredicate;

   private final Supplier<Set<String>> regionIds;
   private final CleanupResources cleanupResources;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   ECSComputeServiceAdapter(ECSComputeServiceApi api,
                            @Named(TIMEOUT_NODE_SUSPENDED) Predicate<String> instanceSuspendedPredicate,
                            @org.jclouds.location.Region Supplier<Set<String>> regionIds,
                            CleanupResources cleanupResources) {
      this.api = api;
      this.instanceSuspendedPredicate = instanceSuspendedPredicate;
      this.regionIds = regionIds;
      this.cleanupResources = cleanupResources;
   }

   @Override
   public NodeAndInitialCredentials<Instance> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      String instanceType = template.getHardware().getId();
      String regionId = template.getLocation().getId();
      String imageId = template.getImage().getId();

      ECSServiceTemplateOptions templateOptions = template.getOptions().as(ECSServiceTemplateOptions.class);

      String keyPairName = templateOptions.getKeyPairName();
      String securityGroupId = Iterables.getOnlyElement(templateOptions.getGroups());
      String vSwitchId = templateOptions.getVSwitchId();
      Instance.InternetChargeType internetChargeType = Instance.InternetChargeType.fromValue(templateOptions.getInternetChargeType());
      int internetMaxBandwidthOut = templateOptions.getInternetMaxBandwidthOut();
      String instanceChargeType = templateOptions.getInstanceChargeType();

      Map<String, String> tags = ComputeServiceUtils.metadataAndTagsAsValuesOfEmptyString(templateOptions);
      tags = new ImmutableMap.Builder()
              .putAll(tags)
              .put(vSwitchId, "")
              .build();
      TagOptions tagOptions = TagOptions.Builder.tags(tags);

      InstanceRequest instanceRequest = api.instanceApi().create(regionId, RegionAndId.fromSlashEncoded(imageId).id(), securityGroupId, name, instanceType,
              CreateInstanceOptions.Builder
                      .vSwitchId(vSwitchId)
                      .internetChargeType(internetChargeType.toString())
                      .internetMaxBandwidthOut(internetMaxBandwidthOut)
                      .instanceChargeType(instanceChargeType)
                      .instanceName(name)
                      .keyPairName(keyPairName)
                      .tagOptions(tagOptions)
      );

      String regionAndInstanceId = slashEncodeRegionAndId(regionId, instanceRequest.getInstanceId());
      if (!instanceSuspendedPredicate.apply(regionAndInstanceId)) {
         final String message = format("Instance %s was not created correctly. The associated resources created for it will be destroyed", instanceRequest.getInstanceId());
         logger.warn(message);
         cleanupResources.cleanupNode(RegionAndId.create(regionId, instanceRequest.getInstanceId()));
         cleanupResources.cleanupSecurityGroupIfOrphaned(regionId, securityGroupId);
      }

      api.instanceApi().allocatePublicIpAddress(regionId, instanceRequest.getInstanceId());
      api.instanceApi().powerOn(instanceRequest.getInstanceId());
      Instance instance = Iterables.get(api.instanceApi().list(regionId, ListInstancesOptions.Builder.instanceIds(instanceRequest.getInstanceId())), 0);

      // Safe to pass null credentials here, as jclouds will default populate
      // the node with the default credentials from the image, or the ones in
      // the options, if provided.
      return new NodeAndInitialCredentials(instance,
              slashEncodeRegionAndId(regionId, instanceRequest.getInstanceId()), null);
   }

   @Override
   public Iterable<InstanceType> listHardwareProfiles() {
      final ImmutableSet.Builder<String> instanceTypeIdsBuilder = ImmutableSet.builder();
      for (String regionId : getAvailableLocationNames()) {
         instanceTypeIdsBuilder.addAll(getInstanceTypeIds(regionId));
      }
      final Set<String> ids = instanceTypeIdsBuilder.build();

      List<InstanceType> instanceTypes = FluentIterable.from(api.instanceApi().listTypes())
              .filter(new Predicate<InstanceType>() {
                 @Override
                 public boolean apply(@Nullable InstanceType input) {
                    return contains(ids, input.id());
                 }
              }).toList();

      return instanceTypes;
   }

   private List<String> getInstanceTypeIds(String regionId) {
      List<String> instanceTypeIds = Lists.newArrayList();
      for (AvailableZone availableZone : api.instanceApi().listInstanceTypesByAvailableZone(regionId)) {
         for (AvailableResource availableResource : availableZone.availableResources().get("AvailableResource")) {
            for (SupportedResource supportedResource : availableResource.supportedResources()
                    .get("SupportedResource")) {
               if (SupportedResource.Status.AVAILABLE == supportedResource.status()) {
                  instanceTypeIds.add(supportedResource.value());
               }
            }
         }
      }
      return instanceTypeIds;
   }

   @Override
   public Iterable<ImageInRegion> listImages() {
      final ImmutableList.Builder<ImageInRegion> imagesInRegion = ImmutableList.builder();

      for (final String regionId : getAvailableLocationNames()) {
               imagesInRegion.addAll(api.imageApi().list(regionId).concat()
                       .transform(new Function<Image, ImageInRegion>() {
                           @Override
                           public ImageInRegion apply(Image image) {
                              return ImageInRegion.create(regionId, image);
                           }
                        })
               );
      }
      return imagesInRegion.build();
   }

   @Override
   public ImageInRegion getImage(final String id) {
      RegionAndId regionAndId = fromSlashEncoded(id);
      Image image = api.imageApi().list(regionAndId.regionId(), ListImagesOptions.Builder.imageIds(regionAndId.id()))
              .firstMatch(Predicates.<Image>notNull())
              .orNull();
      if (image == null) return null;
      return ImageInRegion.create(regionAndId.regionId(), image);
   }

   @Override
   public Iterable<Region> listLocations() {
      return FluentIterable.from(api.regionAndZoneApi().describeRegions()).filter(new Predicate<Region>() {
         @Override
         public boolean apply(Region region) {
            return regionIds.get().isEmpty() ? true : regionIds.get().contains(region.id());
         }
      }).toList();
   }

   @Override
   public Instance getNode(final String id) {
      RegionAndId regionAndId = fromSlashEncoded(id);
      return api.instanceApi().list(regionAndId.regionId(),
              ListInstancesOptions.Builder.instanceIds(regionAndId.id()))
              .firstMatch(Predicates.<Instance>notNull())
              .orNull();
   }

   @Override
   public void destroyNode(String id) {
      checkState(cleanupResources.cleanupNode(RegionAndId.fromSlashEncoded(id)), "server(%s) and its resources still there after deleting!?", id);
   }

   @Override
   public void rebootNode(String id) {
      api.instanceApi().reboot(id);
   }

   @Override
   public void resumeNode(String id) {
      api.instanceApi().powerOn(id);
   }

   @Override
   public void suspendNode(String id) {
      api.instanceApi().powerOff(id);
   }

   @Override
   public Iterable<Instance> listNodes() {
      final ImmutableList.Builder<Instance> instances = ImmutableList.builder();
      for (String regionId : getAvailableLocationNames()) {
         instances.addAll(api.instanceApi().list(regionId).concat());
      }
      return instances.build();
   }

   @Override
   public Iterable<Instance> listNodesByIds(final Iterable<String> ids) {

      final ImmutableList.Builder<Instance> instancesBuilder = ImmutableList.builder();
      for (String regionId : getAvailableLocationNames()) {
         instancesBuilder.addAll(api.instanceApi().list(regionId, ListInstancesOptions.Builder.instanceIds(Iterables.toArray(ids, String.class))));
      }
      return instancesBuilder.build();
   }

   private List<String> getAvailableLocationNames() {
      return newArrayList(
              Iterables.transform(listLocations(), new Function<Region, String>() {
                 @Override
                 public String apply(Region location) {
                    return location.id();
                 }
              }));
   }

}
