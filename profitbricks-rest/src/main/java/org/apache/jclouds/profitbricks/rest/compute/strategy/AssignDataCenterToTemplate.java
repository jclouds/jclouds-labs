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
package org.apache.jclouds.profitbricks.rest.compute.strategy;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import static com.google.common.collect.Iterables.find;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.logging.Logger;

@Beta
@Singleton
public class AssignDataCenterToTemplate extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ProfitBricksApi api;
   private final Predicate<String> waitDcUntilAvailable;

   @Inject
   protected AssignDataCenterToTemplate(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           ProfitBricksApi api, @Named(POLL_PREDICATE_DATACENTER) Predicate<String> waitDcUntilAvailable) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.waitDcUntilAvailable = waitDcUntilAvailable;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, final Template template,
           Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
           Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      logger.info(">> looking for a datacenter in %s", template.getLocation().getId());

      // Try to find an existing datacenter in the selected location
      DataCenter dataCenter = find(api.dataCenterApi().list(), new Predicate<DataCenter>() {
         @Override
         public boolean apply(DataCenter input) {
            // The location field is not populated when getting the list of datacenters
            DataCenter details = api.dataCenterApi().getDataCenter(input.id());
            return details != null && template.getLocation().getId().equals(details.properties().location().getId());
         }
      }, null);

      if (dataCenter == null) {
         String name = namingConvention.create().sharedNameForGroup(group);
         logger.info(">> no datacenter was found. Creating a new one named %s in %s...", name, template.getLocation()
                 .getId());
         dataCenter = api.dataCenterApi().create(name, "desc,,,", template.getLocation().getId());
         waitDcUntilAvailable.apply(dataCenter.id());
      }

      return super.execute(group, count, new TemplateWithDataCenter(template, dataCenter), goodNodes, badNodes,
              customizationResponses);
   }

}
