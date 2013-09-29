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

import static com.google.common.base.Preconditions.checkState;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.BulkDeleteResponse;
import org.jclouds.openstack.swift.v1.domain.ExtractArchiveResponse;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

@Test(groups = "live", testName = "BulkApiLiveTest")
public class BulkApiLiveTest extends BaseSwiftApiLiveTest {

   static final int OBJECT_COUNT = 10;

   private String containerName = getClass().getSimpleName();

   public void notPresentWhenDeleting() throws Exception {
      for (String regionId : api.configuredRegions()) {
         BulkDeleteResponse deleteResponse = api.bulkApiInRegion(regionId).bulkDelete(
               ImmutableList.of(UUID.randomUUID().toString()));
         assertEquals(deleteResponse.deleted(), 0);
         assertEquals(deleteResponse.notFound(), 1);
         assertTrue(deleteResponse.errors().isEmpty());
      }
   }

   public void extractArchive() throws Exception {
      for (String regionId : api.configuredRegions()) {
         ExtractArchiveResponse extractResponse = api.bulkApiInRegion(regionId).extractArchive(containerName,
               Payloads.newPayload(tarGz), "tar.gz");
         assertEquals(extractResponse.created(), OBJECT_COUNT);
         assertTrue(extractResponse.errors().isEmpty());
         assertEquals(api.containerApiInRegion(regionId).get(containerName).objectCount(), OBJECT_COUNT);

         // repeat the command
         extractResponse = api.bulkApiInRegion(regionId).extractArchive(containerName, Payloads.newPayload(tarGz), "tar.gz");
         assertEquals(extractResponse.created(), OBJECT_COUNT);
         assertTrue(extractResponse.errors().isEmpty());
      }
   }

   @Test(dependsOnMethods = "extractArchive")
   public void bulkDelete() throws Exception {
      for (String regionId : api.configuredRegions()) {
         BulkDeleteResponse deleteResponse = api.bulkApiInRegion(regionId).bulkDelete(paths);
         assertEquals(deleteResponse.deleted(), OBJECT_COUNT);
         assertEquals(deleteResponse.notFound(), 0);
         assertTrue(deleteResponse.errors().isEmpty());
         assertEquals(api.containerApiInRegion(regionId).get(containerName).objectCount(), 0);
      }
   }

   List<String> paths = Lists.newArrayList();
   byte[] tarGz;

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : api.configuredRegions()) {
         boolean created = api.containerApiInRegion(regionId).createIfAbsent(containerName,
               new CreateContainerOptions());
         if (!created) {
            deleteAllObjectsInContainer(regionId);
         }
      }
      GenericArchive files = ShrinkWrap.create(GenericArchive.class, "files.tar.gz");
      StringAsset content = new StringAsset("foo");
      for (int i = 0; i < OBJECT_COUNT; i++) {
         paths.add(containerName + "/file" + i);
         files.add(content, "/file" + i);
      }
      try {
         tarGz = ByteStreams.toByteArray(files.as(TarGzExporter.class).exportAsInputStream());
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : api.configuredRegions()) {
         deleteAllObjectsInContainer(regionId);
         api.containerApiInRegion(regionId).deleteIfEmpty(containerName);
      }
      super.tearDown();
   }

   void deleteAllObjectsInContainer(String regionId) {
      ImmutableList<String> pathsToDelete = api.objectApiInRegionForContainer(regionId, containerName)
            .list(new ListContainerOptions()).transform(new Function<SwiftObject, String>() {

               public String apply(SwiftObject input) {
                  return containerName + "/" + input.name();
               }

            }).toList();
      if (!pathsToDelete.isEmpty()) {
         BulkDeleteResponse response = api.bulkApiInRegion(regionId).bulkDelete(pathsToDelete);
         checkState(response.errors().isEmpty(), "Errors deleting paths %s: %s", pathsToDelete, response);
      }
   }
}
