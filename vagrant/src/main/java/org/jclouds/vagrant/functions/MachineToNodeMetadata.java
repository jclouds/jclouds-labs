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
package org.jclouds.vagrant.functions;

import java.util.Set;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class MachineToNodeMetadata implements Function<VagrantNode, NodeMetadata> {
   private final Location location;
   private final BoxConfig.Factory boxConfigFactory;

   @Inject
   MachineToNodeMetadata(
         @Memoized Supplier<Set<? extends Location>> locations,
         BoxConfig.Factory boxConfigFactory) {
      this.location = Iterables.getOnlyElement(locations.get());
      this.boxConfigFactory = boxConfigFactory;
   }

   @Override
   public NodeMetadata apply(VagrantNode node) {
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder()
            .ids(node.id())
            .name(node.name())
            .group(node.group())
            .imageId(node.image().getId())
            .location(location)
            .hardware(node.hardware())
            .operatingSystem(node.image().getOperatingSystem())
            .hostname(node.name())
            .status(node.machineState())
            .loginPort(getLoginPort(node.image()))
            .privateAddresses(node.networks())
            .publicAddresses(ImmutableList.<String> of())
            .hostname(node.hostname());
      // Credentials fetched from cache from AdaptingComputeServiceStrategies.addLoginCredentials.
      // Cache already initialized just after creating the node.
      return nodeMetadataBuilder.build();
   }

   private int getLoginPort(Image image) {
      BoxConfig config = boxConfigFactory.newInstance(image);
      String port;
      if (image.getOperatingSystem().getFamily() == OsFamily.WINDOWS) {
         port = config.getKey(VagrantConstants.KEY_WINRM_PORT).or("5985");
      } else {
         port = config.getKey(VagrantConstants.KEY_SSH_PORT).or("22");
      }
      return Integer.parseInt(port);
   }

}
