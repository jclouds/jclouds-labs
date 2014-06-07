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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.tryFind;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.features.services.CloudService;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.rest.ApiContext;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Creates the group before concurrently creating the nodes, to avoid creating
 * more than one group with the same name.
 */
@Singleton
public class CreateGroupBeforeCreatingNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   protected final ApiContext<AbiquoApi> context;

   protected final CloudService cloudService;

   @Inject
   protected CreateGroupBeforeCreatingNodes(
         CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
         ListNodesStrategy listNodesStrategy,
         GroupNamingConvention.Factory namingConvention,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         ApiContext<AbiquoApi> context, CloudService cloudService) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.context = checkNotNull(context, "context must not be null");
      this.cloudService = checkNotNull(cloudService, "cloudService must not be null");
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(final String group, int count, Template template,
         Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
         Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      // Get the zone where the template will be deployed
      Integer locationId = Integer.valueOf(template.getHardware().getLocation().getId());
      VirtualDatacenter vdc = cloudService.getVirtualDatacenter(locationId);

      // Check if it already exists a group with the given name
      Iterable<VirtualAppliance> existingGroups = vdc.listVirtualAppliances();
      Optional<VirtualAppliance> vapp = tryFind(existingGroups, new Predicate<VirtualAppliance>() {
         @Override
         public boolean apply(VirtualAppliance input) {
            return input.getName().equals(group);
         }
      });

      // Create the group if still does not exist
      VirtualAppliance newVapp = null;
      if (!vapp.isPresent()) {
         logger.debug(">> Creating group %s", group);
         newVapp = VirtualAppliance.builder(context, vdc).name(group).build();
         newVapp.save();
         logger.debug("<< group(%s) created", newVapp.getId());
      } else {
         logger.debug(">> Using existing group(%s)", vapp.get().getId());
      }

      VirtualApplianceCachingTemplate abiquoTemplate = VirtualApplianceCachingTemplate //
            .from(template) //
            .withVirtualDatacenter(vdc) //
            .withVirtualAppliance(vapp.or(newVapp)) //
            .build();

      return super.execute(group, count, abiquoTemplate, goodNodes, badNodes, customizationResponses);
   }

}
