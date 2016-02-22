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
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.LicenceType;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

@Test(groups = "live", testName = "VolumeApiLiveTest")
public class VolumeApiLiveTest extends BaseProfitBricksLiveTest {

   DataCenter dataCenter;
   Volume testVolume;
   Snapshot testSnapshot;

   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null)
         deleteDataCenter(dataCenter.id());
   }

   @Test
   public void testCreateVolume() {
      assertNotNull(dataCenter);

      testVolume = volumeApi().createVolume(
              Volume.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-volume")
              .size(3)
              .licenceType(LicenceType.LINUX)
              .build());

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

      assertVolumeAvailable(testVolume);
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

      assertSnapshotAvailable(testSnapshot);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testRestoreSnapshot() {
      volumeApi().restoreSnapshot(
         Volume.Request.restoreSnapshotBuilder()
            .dataCenterId(testVolume.dataCenterId())
            .volumeId(testVolume.id())
            .snapshotId(testSnapshot.id())
            .build()
      );
      assertVolumeAvailable(testVolume);
   }

   @Test(dependsOnMethods = "testRestoreSnapshot")
   public void testDeleteVolume() {
      volumeApi().deleteVolume(testVolume.dataCenterId(), testVolume.id());
      assertVolumeRemoved(testVolume);
   }

   private void assertVolumeAvailable(Volume volume) {
      assertRandom(new Predicate<VolumeRef>() {
         @Override
         public boolean apply(VolumeRef volumeRef) {
            Volume volume = volumeApi().getVolume(volumeRef.dataCenterId(), volumeRef.volumeId());

            if (volume == null || volume.metadata() == null)
               return false;

            return volume.metadata().state() == State.AVAILABLE;
         }
      }, VolumeRef.create(volume.dataCenterId(), volume.id()));
   }

   private void assertVolumeRemoved(Volume volume) {
      assertRandom(new Predicate<VolumeRef>() {
         @Override
         public boolean apply(VolumeRef volumeRef) {
            return volumeApi().getVolume(volumeRef.dataCenterId(), volumeRef.volumeId()) == null;
         }
      }, VolumeRef.create(volume.dataCenterId(), volume.id()));
   }

   private void assertSnapshotAvailable(Snapshot snapshot) {
      assertRandom(new Predicate<String>() {
         @Override
         public boolean apply(String id) {
            Snapshot snapshot = api.snapshotApi().getSnapshot(id);

            if (snapshot == null || snapshot.metadata() == null)
               return false;

            return snapshot.metadata().state() == State.AVAILABLE;
         }
      }, snapshot.id());
   }

   private VolumeApi volumeApi() {
      return api.volumeApi();
   }

}
