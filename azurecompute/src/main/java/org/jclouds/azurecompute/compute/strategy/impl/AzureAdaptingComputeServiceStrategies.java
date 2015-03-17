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
package org.jclouds.azurecompute.compute.strategy.impl;

import com.google.common.base.Function;
import java.util.Map;
import javax.inject.Inject;
import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule.AddDefaultCredentialsToImage;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.compute.strategy.impl.AdaptingComputeServiceStrategies;
import org.jclouds.domain.Credentials;

public class AzureAdaptingComputeServiceStrategies
        extends AdaptingComputeServiceStrategies<Deployment, RoleSize, OSImage, Location> {

   private final AzureComputeServiceAdapter client;

   private final Function<Deployment, NodeMetadata> nodeMetadataAdapter;

   @Inject
   public AzureAdaptingComputeServiceStrategies(
           final Map<String, Credentials> credentialStore,
           final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate,
           final ComputeServiceAdapter<Deployment, RoleSize, OSImage, Location> client,
           final Function<Deployment, NodeMetadata> nodeMetadataAdapter,
           final Function<OSImage, Image> imageAdapter,
           final AddDefaultCredentialsToImage addDefaultCredentialsToImage) {

      super(credentialStore,
              prioritizeCredentialsFromTemplate,
              client,
              nodeMetadataAdapter,
              imageAdapter,
              addDefaultCredentialsToImage);

      this.client = (AzureComputeServiceAdapter) client;
      this.nodeMetadataAdapter = nodeMetadataAdapter;
   }

   @Override
   public NodeMetadata destroyNode(final String id) {
      final Deployment node = client.internalDestroyNode(id);
      return node == null
              ? null
              : nodeMetadataAdapter.apply(node);
   }

}
