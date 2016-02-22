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

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.LicenceType;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VolumeApiMockTest", singleThreaded = true)
public class VolumeApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testGetList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/volume/list.json"))
      );
      
      List<Volume> list = volumeApi().getList("datacenter-id");
      
      assertNotNull(list);
      assertEquals(list.size(), 6);
      assertEquals(list.get(0).properties().name(), "Docker Registry Volume");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/volumes");
   }
    
   @Test
   public void testGetVolume() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/volume/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Volume volume = volumeApi().getVolume("datacenter-id", "some-id");
      
      assertNotNull(volume);
      assertEquals(volume.properties().name(), "Docker Registry Volume");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/volumes/some-id");
   }
      
   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/volume/get.json"))
      );
      
      Volume volume = volumeApi().createVolume(
              Volume.Request.creatingBuilder()
              .dataCenterId("datacenter-id")
              .name("jclouds-volume")
              .size(3)
              .licenceType(LicenceType.LINUX)
              .build());

      assertNotNull(volume);
      assertNotNull(volume.id());
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/rest/datacenters/datacenter-id/volumes", 
              "{\"properties\": {\"name\": \"jclouds-volume\", \"size\": 3, \"licenceType\": \"LINUX\"}}"
      );
   }
   
   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/volume/get.json"))
      );
      
      api.volumeApi().updateVolume(
              Volume.Request.updatingBuilder()
              .id("some-id")
              .dataCenterId("datacenter-id")
              .name("apache-volume")
              .build());
            
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/rest/datacenters/datacenter-id/volumes/some-id", "{\"name\": \"apache-volume\"}");
   }
   
   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody("")
      );
      
      volumeApi().deleteVolume("datacenter-id", "some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/volumes/some-id");
   }
   
   @Test
   public void testCreateSnapshot() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/volume/snapshot.json"))
      );
      
      volumeApi().createSnapshot(
         Volume.Request.createSnapshotBuilder()
            .dataCenterId("datacenter-id")
            .volumeId("volume-id")
            .name("test snapshot")
            .build()
      );
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/rest/datacenters/datacenter-id/volumes/volume-id/create-snapshot");
   }
   
   @Test
   public void testRestoreSnapshot() throws InterruptedException {
      
      server.enqueue(response204());
      
      volumeApi().restoreSnapshot(
         Volume.Request.restoreSnapshotBuilder()
            .dataCenterId("datacenter-id")
            .volumeId("volume-id")
            .snapshotId("snapshot-id")
            .build()
      );
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/rest/datacenters/datacenter-id/volumes/volume-id/restore-snapshot");
   }
        
   private VolumeApi volumeApi() {
      return api.volumeApi();
   }
   
}
