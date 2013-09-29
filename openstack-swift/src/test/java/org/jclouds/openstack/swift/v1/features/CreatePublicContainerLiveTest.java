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
package org.jclouds.openstack.swift.v1.features;

import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.openstack.swift.v1.options.CreateContainerOptions.Builder.anybodyRead;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "CreatePublicContainerLiveTest")
public class CreatePublicContainerLiveTest extends BaseSwiftApiLiveTest {

   private String name = getClass().getSimpleName();
   private String containerName = getClass().getSimpleName() + "Container";

   public void anybodyReadObjectUri() throws Exception {
      for (String regionId : api.configuredRegions()) {
         api.containerApiInRegion(regionId).createIfAbsent(containerName, anybodyRead());
         api.containerApiInRegion(regionId).get(containerName);

         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         objectApi.replace(name, newStringPayload("swifty"), ImmutableMap.<String, String> of());

         InputStream publicStream = objectApi.get(name, new GetOptions()).uri().toURL().openStream();

         assertEquals(Strings2.toStringAndClose(publicStream), "swifty");
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : api.configuredRegions()) {
         api.objectApiInRegionForContainer(regionId, containerName).delete(name);
         api.containerApiInRegion(regionId).deleteIfEmpty(containerName);
      }
      super.tearDown();
   }
}
