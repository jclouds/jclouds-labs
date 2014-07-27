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
package org.jclouds.openstack.swift.v1.blobstore;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "live")
public class RegionScopedBlobStoreContextLiveTest extends BaseBlobStoreIntegrationTest {

   public RegionScopedBlobStoreContextLiveTest() {
      provider = "openstack-swift";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      return props;
   }

   @Test
   public void regionsAreNotEmpty() {
      assertFalse(RegionScopedBlobStoreContext.class.cast(view).getConfiguredRegions().isEmpty());
   }

   @Test
   public void locationsMatch() {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         Set<? extends Location> locations = ctx.getBlobStore(regionId).listAssignableLocations();
         assertEquals(locations.size(), 1, "expected one region " + regionId + " " + locations);
         Location location = locations.iterator().next();
         assertEquals(location.getId(), regionId, "region id " + regionId + " didn't match getId(): " + location);
      }
   }

   @Test
   public void tryList() throws InterruptedException, ExecutionException {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         assertEquals(ctx.getAsyncBlobStore(regionId).list().get(), ctx.getBlobStore(regionId).list());
      }
   }

   @Test
   public void trySign() throws InterruptedException, ExecutionException {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      for (String regionId : ctx.getConfiguredRegions()) {
         BlobStore region = ctx.getBlobStore(regionId);
         PageSet<? extends StorageMetadata> containers = region.list();
         if (containers.isEmpty()) {
            continue;
         }
         String containerName = Iterables.getLast(containers).getName();
         PageSet<? extends StorageMetadata> blobs = region.list(containerName);
         if (blobs.isEmpty()) {
            continue;
         }
         String blobName = Iterables.getLast(blobs).getName();
         HttpRequest request = ctx.getSigner(regionId).signGetBlob(containerName, blobName);
         assertNotNull(request, "regionId=" + regionId + ", container=" + containerName + ", blob=" + blobName);
      }
   }
}
