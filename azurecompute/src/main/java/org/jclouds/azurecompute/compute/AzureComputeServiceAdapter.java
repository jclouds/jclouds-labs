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
package org.jclouds.azurecompute.compute;

import javax.inject.Singleton;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;

/**
 * defines the connection between the {@link AzureComputeApi} implementation and the
 * jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<Deployment, RoleSize, OSImage, String> {

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
         String group, String name, Template template) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterable<RoleSize> listHardwareProfiles() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterable<OSImage> listImages() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public OSImage getImage(String id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterable<String> listLocations() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Deployment getNode(String id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void destroyNode(String id) {
      // TODO Auto-generated method stub

   }

   @Override
   public void rebootNode(String id) {
      // TODO Auto-generated method stub
   }

   @Override
   public void resumeNode(String id) {
      // TODO Auto-generated method stub
   }

   @Override
   public void suspendNode(String id) {
      // TODO Auto-generated method stub

   }

   @Override
   public Iterable<Deployment> listNodes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override public Iterable<Deployment> listNodesByIds(Iterable<String> ids) {
      // TODO Auto-generated method stub
      return null;
   }
}
