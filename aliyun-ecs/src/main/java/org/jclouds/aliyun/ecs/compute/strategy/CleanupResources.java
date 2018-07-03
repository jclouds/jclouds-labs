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
package org.jclouds.aliyun.ecs.compute.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.domain.InstanceStatus;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.aliyun.ecs.predicates.InstanceStatusPredicate;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

/**
 * This utility takes care of cleaning up all the resources created to deploy the node
 *
 * Specifically, it tries to delete the security group created for the group of nodes.
 * In case a VPC_PREFIX and a vSwitch were created for the node, it tries to remove them
 */
@Singleton
public class CleanupResources {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ECSComputeServiceApi api;
   private final Predicate<String> instanceSuspendedPredicate;
   private final Predicate<String> instanceTerminatedPredicate;

   @Inject
   public CleanupResources(ECSComputeServiceApi api,
                           @Named(TIMEOUT_NODE_SUSPENDED) Predicate<String> instanceSuspendedPredicate,
                           @Named(TIMEOUT_NODE_TERMINATED) Predicate<String> instanceTerminatedPredicate
   ) {
      this.api = api;
      this.instanceSuspendedPredicate = instanceSuspendedPredicate;
      this.instanceTerminatedPredicate = instanceTerminatedPredicate;
   }

   /**
    * @param regionAndId
    * @return whether the node and its resources have been deleted
    */
   public boolean cleanupNode(final RegionAndId regionAndId) {
      String instanceId = regionAndId.id();
      InstanceStatus instanceStatus = Iterables.tryFind(api.instanceApi().listInstanceStatus(regionAndId.regionId()).concat(),
              new InstanceStatusPredicate(instanceId)).orNull();
      if (instanceStatus == null) return true;
      if (InstanceStatus.Status.STOPPED != instanceStatus.status()) {
         logger.debug(">> powering off %s ...", RegionAndId.slashEncodeRegionAndId(regionAndId));
         api.instanceApi().powerOff(instanceId);
         instanceSuspendedPredicate.apply(RegionAndId.slashEncodeRegionAndId(regionAndId));
      }
      logger.debug(">> destroying %s ...", RegionAndId.slashEncodeRegionAndId(regionAndId));
      api.instanceApi().delete(instanceId);
      return instanceTerminatedPredicate.apply(RegionAndId.slashEncodeRegionAndId(regionAndId));
   }

   public List<SecurityGroup> findOrphanedSecurityGroups(final String regionId, final String group) {
      return api.securityGroupApi().list(regionId).concat().filter(new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(@Nullable SecurityGroup input) {
            List<Tag> actual = input.tags().entrySet().iterator().next().getValue();
            List<Tag> expected = ImmutableList.of(
                    Tag.create(Tag.DEFAULT_OWNER_KEY, Tag.DEFAULT_OWNER_VALUE), Tag.create(Tag.GROUP, group)
            );
            return actual.containsAll(expected) && expected.containsAll(actual);
         }
      }).toList();
   }

   public boolean cleanupSecurityGroupIfOrphaned(final String regionId, String securityGroupId) {
      return api.securityGroupApi().delete(regionId, securityGroupId) != null;
   }

   public boolean cleanupVSwitchIfOrphaned(final String regionId, String vSwitchId) {
      return api.vSwitchApi().delete(regionId, vSwitchId) != null;
   }

   public boolean cleanupVPCIfOrphaned(final String regionId, String vpcId) {
      return api.vpcApi().delete(regionId, vpcId) != null;
   }

}
