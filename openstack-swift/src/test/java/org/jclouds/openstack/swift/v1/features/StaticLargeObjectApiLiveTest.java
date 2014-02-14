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

import static java.lang.String.format;
import static org.jclouds.io.Payloads.newPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "StaticLargeObjectApiLiveTest")
public class StaticLargeObjectApiLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {

   private String name = getClass().getSimpleName();
   private String containerName = getClass().getSimpleName() + "Container";
   private byte[] megOf1s;
   private byte[] megOf2s;

   public void testNotPresentWhenDeleting() throws Exception {
      for (String regionId : regions) {
         api.staticLargeObjectApiInRegionForContainer(regionId, containerName).delete(UUID.randomUUID().toString());
      }
   }

   public void testReplaceManifest() throws Exception {
      for (String regionId : regions) {
         ObjectApi objectApi = api.objectApiInRegionForContainer(regionId, containerName);
         String etag1s = objectApi.replace(name + "/1", newPayload(megOf1s), ImmutableMap.<String, String> of());
         assertMegabyteAndETagMatches(regionId, name + "/1", etag1s);

         String etag2s = objectApi.replace(name + "/2", newPayload(megOf2s), ImmutableMap.<String, String> of());
         assertMegabyteAndETagMatches(regionId, name + "/2", etag2s);

         List<Segment> segments = ImmutableList.<Segment> builder()
               .add(Segment.builder()
                           .path(format("%s/%s/1", containerName, name)).etag(etag1s).sizeBytes(1024 * 1024)
                           .build())
               .add(Segment.builder()
                           .path(format("%s/%s/2", containerName, name)).etag(etag2s).sizeBytes(1024 * 1024)
                           .build())
               .build();

         String etagOfEtags = api.staticLargeObjectApiInRegionForContainer(regionId, containerName).replaceManifest(
               name, segments, ImmutableMap.of("myfoo", "Bar"));

         assertNotNull(etagOfEtags);

         SwiftObject bigObject = api.objectApiInRegionForContainer(regionId, containerName).head(name);
         assertEquals(bigObject.getEtag(), etagOfEtags);
         assertEquals(bigObject.getPayload().getContentMetadata().getContentLength(), new Long(2 * 1024 * 1024));
         assertEquals(bigObject.getMetadata(), ImmutableMap.of("myfoo", "Bar"));

         // segments are visible
         assertEquals(api.containerApiInRegion(regionId).get(containerName).getObjectCount(), 3);
      }
   }

   @Test(dependsOnMethods = "testReplaceManifest")
   public void testDelete() throws Exception {
      for (String regionId : regions) {
         api.staticLargeObjectApiInRegionForContainer(regionId, containerName).delete(name);
         assertEquals(api.containerApiInRegion(regionId).get(containerName).getObjectCount(), 0);
      }
   }

   protected void assertMegabyteAndETagMatches(String regionId, String name, String etag1s) {
      SwiftObject object1s = api.objectApiInRegionForContainer(regionId, containerName).head(name);
      assertEquals(object1s.getEtag(), etag1s);
      assertEquals(object1s.getPayload().getContentMetadata().getContentLength(), new Long(1024 * 1024));
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         boolean created = api.containerApiInRegion(regionId).createIfAbsent(containerName,
               CreateContainerOptions.NONE);
         if (!created) {
            deleteAllObjectsInContainer(regionId, containerName);
         }
      }
      megOf1s = new byte[1024 * 1024];
      megOf2s = new byte[1024 * 1024];
      for (int i = 0; i < 1024 * 1024; i++) {
         megOf1s[i] = 1;
         megOf2s[i] = 2;
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         deleteAllObjectsInContainer(regionId, containerName);
         api.containerApiInRegion(regionId).deleteIfEmpty(containerName);
      }
      super.tearDown();
   }
}
