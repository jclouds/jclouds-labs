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
package org.jclouds.digitalocean.features;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live tests for the {@link ImageApi} class.
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseDigitalOceanLiveTest {

   private Image snapshot;
   private Image snapshotUsingSlug;
   private DropletCreation droplet;
   private DropletCreation dropletUsingSlug;

   @Override
   protected void initialize() {
      super.initialize();
      initializeImageSizeAndRegion();
   }

   @AfterClass
   public void cleanup() {
      try {
         if (droplet != null) {
            api.getDropletApi().destroy(droplet.getId(), true);
         }
         if (dropletUsingSlug != null) {
            api.getDropletApi().destroy(dropletUsingSlug.getId(), true);
         }
      } finally {
         if (snapshot != null) {
            api.getImageApi().delete(snapshot.getId());
            assertFalse(tryFind(api.getImageApi().list(), byName(snapshot.getName())).isPresent(),
                  "Snapshot should not exist after delete");
         }
         if (snapshotUsingSlug != null) {
            api.getImageApi().delete(snapshotUsingSlug.getId());
            assertFalse(tryFind(api.getImageApi().list(), byName(snapshotUsingSlug.getName())).isPresent(),
                  "Snapshot should not exist after delete");
         }
      }
   }

   public void testGetImage() {
      assertNotNull(api.getImageApi().get(defaultImage.getId()), "The image should not be null");
   }

   public void testGetImageBySlug() {
      assertNotNull(api.getImageApi().get(defaultImage.getSlug()), "The image should not be null");
   }

   public void testGetImageNotFound() {
      assertNull(api.getImageApi().get(-1));
   }

   public void testTransferImage() {
      droplet = api.getDropletApi().create("imagetransferdroplet", defaultImage.getId(), defaultSize.getId(),
            defaultRegion.getId());

      assertTrue(droplet.getId() > 0, "Created droplet id should be > 0");
      assertTrue(droplet.getEventId() > 0, "Droplet creation event id should be > 0");

      waitForEvent(droplet.getEventId());
      int powerOffEvent = api.getDropletApi().powerOff(droplet.getId());
      waitForEvent(powerOffEvent);

      int snapshotEvent = api.getDropletApi().snapshot(droplet.getId(), "imagetransfersnapshot");
      waitForEvent(snapshotEvent);

      snapshot = find(api.getImageApi().list(), byName("imagetransfersnapshot"));

      Region newRegion = regions.get(1);
      int transferEvent = api.getImageApi().transfer(snapshot.getId(), newRegion.getId());
      assertTrue(transferEvent > 0, "Transfer event id should be > 0");
      waitForEvent(transferEvent);
   }

   public void testTransferImageUsingSlug() {
      dropletUsingSlug = api.getDropletApi().create("imagetransferdropletusingslug", defaultImage.getSlug(),
            defaultSize.getSlug(), defaultRegion.getSlug());

      assertTrue(dropletUsingSlug.getId() > 0, "Created droplet id should be > 0");
      assertTrue(dropletUsingSlug.getEventId() > 0, "Droplet creation event id should be > 0");

      waitForEvent(dropletUsingSlug.getEventId());
      int powerOffEvent = api.getDropletApi().powerOff(dropletUsingSlug.getId());
      waitForEvent(powerOffEvent);

      int snapshotEvent = api.getDropletApi().snapshot(dropletUsingSlug.getId(), "imagetransfersnapshotusingslug");
      waitForEvent(snapshotEvent);

      snapshotUsingSlug = find(api.getImageApi().list(), byName("imagetransfersnapshotusingslug"));

      Region newRegion = regions.get(1);
      int transferEvent = api.getImageApi().transfer(snapshotUsingSlug.getId(), newRegion.getId());
      assertTrue(transferEvent > 0, "Transfer event id should be > 0");
      waitForEvent(transferEvent);
   }

   private static Predicate<Image> byName(final String name) {
      return new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return input.getName().equals(name);
         }
      };
   }
}
