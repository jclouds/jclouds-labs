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

import static com.google.common.collect.Iterables.tryFind;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Live tests for the {@link DropletApi} class.
 */
@Test(groups = "live", testName = "DropletApiLiveTest")
public class DropletApiLiveTest extends BaseDigitalOceanLiveTest {

   private DropletCreation dropletCreation;
   private DropletCreation dropletCreationUsingSlugs;
   private Droplet droplet;
   private Droplet dropletUsingSlugs;
   private Image snapshot;

   @Override
   protected void initialize() {
      super.initialize();
      initializeImageSizeAndRegion();
   }

   @AfterClass
   public void cleanup() {
      if (droplet != null) {
         int event = api.getDropletApi().destroy(droplet.getId(), true);
         assertTrue(event > 0, "The event id should not be null");
      }
      if (dropletUsingSlugs != null) {
         int event = api.getDropletApi().destroy(dropletUsingSlugs.getId(), true);
         assertTrue(event > 0, "The event id should not be null");
      }
      if (snapshot != null) {
         api.getImageApi().delete(snapshot.getId());
      }
   }

   public void testCreateDroplet() {
      dropletCreation = api.getDropletApi().create("droplettest", defaultImage.getId(), defaultSize.getId(),
            defaultRegion.getId());

      assertTrue(dropletCreation.getId() > 0, "Created droplet id should be > 0");
      assertTrue(dropletCreation.getEventId() > 0, "Droplet creation event id should be > 0");
   }

   public void testCreateDropletUsingSlugs() {
      dropletCreationUsingSlugs = api.getDropletApi().create("droplettestwithslugs", defaultImage.getSlug(),
            defaultSize.getSlug(), defaultRegion.getSlug());

      assertTrue(dropletCreationUsingSlugs.getId() > 0, "Created droplet id should be > 0");
      assertTrue(dropletCreationUsingSlugs.getEventId() > 0, "Droplet creation event id should be > 0");
   }

   @Test(dependsOnMethods = { "testCreateDroplet", "testCreateDropletUsingSlugs" })
   public void testGetDroplet() {
      waitForEvent(dropletCreation.getEventId());
      waitForEvent(dropletCreationUsingSlugs.getEventId());

      droplet = api.getDropletApi().get(dropletCreation.getId());
      dropletUsingSlugs = api.getDropletApi().get(dropletCreationUsingSlugs.getId());

      assertNotNull(droplet, "Created droplet should not be null");
      assertNotNull(dropletUsingSlugs, "Created droplet using slugs should not be null");
   }

   @Test(dependsOnMethods = "testGetDroplet")
   public void testListDroplets() {
      List<Droplet> droplets = api.getDropletApi().list();

      assertFalse(droplets.isEmpty(), "Droplet list should not be empty");
   }

   @Test(dependsOnMethods = "testGetDroplet")
   public void testPowerOffDroplet() {
      int event = api.getDropletApi().powerOff(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerOffDroplet")
   public void testPowerOnDroplet() {
      int event = api.getDropletApi().powerOn(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerOnDroplet")
   public void testRebootDroplet() {
      int event = api.getDropletApi().reboot(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testRebootDroplet")
   public void testPowerCycleDroplet() {
      int event = api.getDropletApi().powerCycle(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerCycleDroplet")
   public void testResetPasswordForDroplet() {
      int event = api.getDropletApi().resetPassword(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testResetPasswordForDroplet")
   public void testRenameDroplet() {
      int event = api.getDropletApi().rename(droplet.getId(), "droplettestupdated");
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
      droplet = api.getDropletApi().get(droplet.getId());
      assertEquals(droplet.getName(), "droplettestupdated", "The renamed droplet should have the new name");
   }

   @Test(dependsOnMethods = "testRenameDroplet")
   public void testRebuildDroplet() {
      int event = api.getDropletApi().rebuild(droplet.getId(), defaultImage.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testRebuildDroplet")
   public void testRestoreDroplet() {
      int event = api.getDropletApi().restore(droplet.getId(), defaultImage.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testRestoreDroplet")
   public void testSnapshotDroplet() {
      // Snapshot requires the droplet to be powered off
      int powerOffEvent = api.getDropletApi().powerOff(droplet.getId());
      assertTrue(powerOffEvent > 0, "The event id should be > 0");
      waitForEvent(powerOffEvent);

      int event = api.getDropletApi().snapshot(droplet.getId(), "testsnapshot");
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);

      Optional<Image> snapshot = tryFind(api.getImageApi().list(), new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return input.getName().equals("testsnapshot");
         }
      });

      assertTrue(snapshot.isPresent(), "The created snapshot should exist in the image list");
      this.snapshot = snapshot.get();
   }

   @Test(dependsOnMethods = "testSnapshotDroplet")
   public void testResizeDroplet() {
      // Resize requires the droplet to be powered off
      int powerOffEvent = api.getDropletApi().powerOff(droplet.getId());
      assertTrue(powerOffEvent > 0, "The event id should be > 0");
      waitForEvent(powerOffEvent);

      Size newSize = sizes.get(1);
      int resizeEvent = api.getDropletApi().resize(droplet.getId(), newSize.getId());
      assertTrue(resizeEvent > 0, "The event id should be > 0");
      waitForEvent(resizeEvent);
   }

   @Test(dependsOnMethods = "testResizeDroplet")
   public void testShutdownDroplet() {
      int event = api.getDropletApi().shutdown(droplet.getId());
      assertTrue(event > 0, "The event id should be > 0");
      waitForEvent(event);
   }
}
