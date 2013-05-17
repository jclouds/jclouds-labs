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
package org.jclouds.compute.management;


import com.google.common.collect.ImmutableSet;
import org.jclouds.codec.ToLocation;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.codec.ToExecResponse;
import org.jclouds.compute.codec.ToHardware;
import org.jclouds.compute.codec.ToImage;
import org.jclouds.compute.codec.ToNodeMetadata;
import org.jclouds.compute.representations.ExecResponse;
import org.jclouds.compute.representations.NodeMetadata;
import org.jclouds.management.ViewMBean;
import org.jclouds.representations.Location;
import org.jclouds.compute.representations.Hardware;
import org.jclouds.compute.representations.Image;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;

public class ComputeServiceManagement implements ComputeServiceManagementMBean, ViewMBean<ComputeServiceContext> {

   private final ComputeService service;

   public ComputeServiceManagement(ComputeServiceContext context) {
      this.service = context.getComputeService();
   }

   @Override
   public Set<Hardware> listHardwareProfiles() {
      return ImmutableSet.<Hardware>builder()
                         .addAll(transform(service.listHardwareProfiles(), ToHardware.INSTANCE))
                         .build();
   }

   @Override
   public Set<Image> listImages() {
      return ImmutableSet.<Image>builder()
                         .addAll(transform(service.listImages(), ToImage.INSTANCE))
                         .build();
   }

   @Override
   public Image getImage(String id)  {
      return ToImage.INSTANCE.apply(service.getImage(id));
   }

   @Override
   public Set<NodeMetadata> listNodes() {
      return ImmutableSet.<NodeMetadata>builder()
                         .addAll(transform( (Set<org.jclouds.compute.domain.NodeMetadata>) service.listNodes(), ToNodeMetadata.INSTANCE))
                         .build();
   }

   @Override
   public Set<Location> listAssignableLocations() {
      return ImmutableSet.<Location>builder()
                         .addAll(transform(service.listAssignableLocations(), ToLocation.INSTANCE))
                         .build();
   }

   @Override
   public void resumeNode(String id) {
      service.resumeNode(id);
   }

   @Override
   public void suspendNode(String id) {
      service.suspendNode(id);
   }


   @Override
   public void destroyNode(String id) {
      service.destroyNode(id);
   }


   @Override
   public void rebootNode(String id) {
      service.destroyNode(id);
   }

   @Override
   public NodeMetadata getNode(String id) {
      return ToNodeMetadata.INSTANCE.apply(service.getNodeMetadata(id));
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      return ToExecResponse.INSTANCE.apply(service.runScriptOnNode(id, runScript));
   }

   @Override
   public String getType() {
      return "compute";
   }
}
