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
package org.apache.jclouds.profitbricks.rest.features;

import com.google.common.base.Predicate;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.LicenceType;
import static org.apache.jclouds.profitbricks.rest.domain.Location.US_LAS;
import org.apache.jclouds.profitbricks.rest.domain.ProvisioningState;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.domain.VolumeType;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "VolumeApiLiveTest")
public class VolumeApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Volume testVolume;
   private Snapshot testSnapshot;

   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null) {
         deleteDataCenter(dataCenter.id());
      }
   }

   @Test
   public void testCreateVolume() {
      assertNotNull(dataCenter);

      List<Image> images = api.imageApi().getList(new DepthOptions().depth(5));

      Image testImage = null;

      for (Image image : images) {
         if (image.metadata().state() == State.AVAILABLE
                 && image.properties().isPublic()
                 && image.properties().imageType() == Image.Type.HDD
                 && image.properties().location() == US_LAS
                 && image.properties().licenceType() == LicenceType.LINUX
                 && (testImage == null || testImage.properties().size() > image.properties().size())) {
            testImage = image;
         }
      }

      HashSet<String> sshKeys = new HashSet<String>();
      sshKeys.add("hQGOEJeFL91EG3+l9TtRbWNjzhDVHeLuL3NWee6bekA=");

      testVolume = volumeApi().createVolume(
              Volume.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-volume")
              .size(4)
              .licenceType(LicenceType.LINUX)
              .type(VolumeType.SSD)
              .image(testImage.id())
              .sshKeys(sshKeys)
              .build());

      assertRequestCompleted(testVolume);
      assertNotNull(testVolume);
      assertEquals(testVolume.properties().name(), "jclouds-volume");
      assertVolumeAvailable(testVolume);
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testGetVolume() {
      Volume volume = volumeApi().getVolume(dataCenter.id(), testVolume.id());

      assertNotNull(volume);
      assertEquals(volume.id(), testVolume.id());
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testList() {
      List<Volume> volumes = volumeApi().getList(dataCenter.id());

      assertNotNull(volumes);
      assertFalse(volumes.isEmpty());
      assertEquals(volumes.size(), 1);
   }

   @Test(dependsOnMethods = "testGetVolume")
   public void testUpdateVolume() {
      assertDataCenterAvailable(dataCenter);

      Volume volume = api.volumeApi().updateVolume(
              Volume.Request.updatingBuilder()
              .dataCenterId(testVolume.dataCenterId())
              .id(testVolume.id())
              .name("apache-volume")
              .build());

      assertRequestCompleted(volume);
      assertVolumeAvailable(volume);
      assertEquals(volume.properties().name(), "apache-volume");
   }

   @Test(dependsOnMethods = "testUpdateVolume")
   public void testCreateSnapshot() {
      testSnapshot = volumeApi().createSnapshot(
              Volume.Request.createSnapshotBuilder()
              .dataCenterId(testVolume.dataCenterId())
              .volumeId(testVolume.id())
              .name("test-snapshot")
              .description("snapshot desc...")
              .build());

      assertRequestCompleted(testSnapshot);
      assertSnapshotAvailable(testSnapshot);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testRestoreSnapshot() {
      URI uri = volumeApi().restoreSnapshot(
              Volume.Request.restoreSnapshotBuilder()
              .dataCenterId(testVolume.dataCenterId())
              .volumeId(testVolume.id())
              .snapshotId(testSnapshot.id())
              .build()
      );
      assertRequestCompleted(uri);
      assertVolumeAvailable(testVolume);
   }

   @Test(dependsOnMethods = "testRestoreSnapshot")
   public void testDeleteVolume() {
      URI uri = volumeApi().deleteVolume(testVolume.dataCenterId(), testVolume.id());
      assertRequestCompleted(uri);
      assertVolumeRemoved(testVolume);
      api.snapshotApi().delete(testSnapshot.id());
   }

   private void assertVolumeAvailable(Volume volume) {
      assertPredicate(new Predicate<VolumeRef>() {
         @Override
         public boolean apply(VolumeRef volumeRef) {
            Volume volume = volumeApi().getVolume(volumeRef.dataCenterId(), volumeRef.volumeId());

            if (volume == null || volume.metadata() == null) {
               return false;
            }

            return volume.metadata().state() == State.AVAILABLE;
         }
      }, VolumeRef.create(volume.dataCenterId(), volume.id()));
   }

   private void assertVolumeRemoved(Volume volume) {
      assertPredicate(new Predicate<VolumeRef>() {
         @Override
         public boolean apply(VolumeRef volumeRef) {
            return volumeApi().getVolume(volumeRef.dataCenterId(), volumeRef.volumeId()) == null;
         }
      }, VolumeRef.create(volume.dataCenterId(), volume.id()));
   }

   private void assertSnapshotAvailable(Snapshot snapshot) {
      assertPredicate(new Predicate<String>() {
         @Override
         public boolean apply(String id) {
            Snapshot snapshot = api.snapshotApi().get(id);

            if (snapshot == null || snapshot.metadata() == null) {
               return false;
            }

            return snapshot.metadata().state() == ProvisioningState.AVAILABLE;
         }
      }, snapshot.id());
   }

   private VolumeApi volumeApi() {
      return api.volumeApi();
   }

}
