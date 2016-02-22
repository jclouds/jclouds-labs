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
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerApiMockTest", singleThreaded = true)
public class ServerApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testGetList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/list.json"))
      );
      
      List<Server> list = serverApi().getList("datacenter-id");
      
      assertNotNull(list);
      assertEquals(list.size(), 5);
      assertEquals(list.get(0).properties().name(), "docker001");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers");
   }
   
   @Test
   public void testGetListWithDepth() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/list-depth-5.json"))
      );
      
      List<Server> list = serverApi().getList("datacenter-id", new DepthOptions().depth(5));
      
      assertNotNull(list);
      assertEquals(list.size(), 4);
      assertEquals(list.get(0).properties().name(), "kube-lb");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers?depth=5");
   }

   @Test
   public void testGetServer() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/server/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Server server = serverApi().getServer("datacenter-id", "some-id");
      
      assertNotNull(server);
      assertEquals(server.properties().name(), "docker001");
      
      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "GET", "/datacenters/datacenter-id/servers/some-id");
   }
   
   @Test
   public void testGetServerWithDepth() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/server/get-depth-5.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Server server = serverApi().getServer("datacenter-id", "some-id", new DepthOptions().depth(5));
      
      assertNotNull(server);
      assertEquals(server.properties().name(), "kube-lb");
      
      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "GET", "/datacenters/datacenter-id/servers/some-id?depth=5");
   }
   
   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/get.json"))
      );
      
      Server server = serverApi().createServer(
        Server.Request.creatingBuilder()
        .dataCenterId("datacenter-id")
        .name("jclouds-node")
        .cores(1)
        .ram(1024)
        .build());

      assertNotNull(server);
      assertNotNull(server.id());
      
      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "POST", "/rest/datacenters/datacenter-id/servers", 
              "{\"properties\": {\"name\": \"jclouds-node\", \"cores\": 1, \"ram\": 1024}}"
      );
   }
   
   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/get.json"))
      );
      
      api.serverApi().updateServer(
              Server.Request.updatingBuilder()
              .id("some-id")
              .dataCenterId("datacenter-id")
              .name("apache-node")
              .ram(1024 * 2)
              .cores(2)
              .build());
            
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/rest/datacenters/datacenter-id/servers/some-id", "{\"name\": \"apache-node\", \"ram\": 2048, \"cores\": 2}");
   }
   
   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(response204());
      
      serverApi().deleteServer("datacenter-id", "some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/some-id");
   }

   @Test
   public void testStopServer() throws InterruptedException {
      server.enqueue(response204());
      serverApi().stopServer("datacenter-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/servers/some-id/stop");
   }

   @Test
   public void testStartServer() throws InterruptedException {
      server.enqueue(response204());
      serverApi().startServer("datacenter-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/servers/some-id/start");
   }

   @Test
   public void testRebootServer() throws InterruptedException {
      server.enqueue(response204());
      serverApi().rebootServer("datacenter-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/servers/some-id/reboot");
   }
   
   @Test
   public void testListVolumes() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/volumes.json"))
      );
      
      List<Volume> volumes = serverApi().listAttachedVolumes("datacenter-id", "some-id");
      assertEquals(volumes.size(), 2);
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/some-id/volumes");
   }
   
   @Test
   public void testAttachVolume() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/volume.json"))
      );
      
      Volume volume = serverApi().attachVolume(Server.Request.attachVolumeBuilder()
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .volumeId("volume-id")
              .build()
      );
      
      assertEquals(volume.properties().name(), "Storage");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/rest/datacenters/datacenter-id/servers/server-id/volumes", "{\"id\": \"volume-id\"}");
   }
   
   @Test
   public void testGetVolume() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/volume.json"))
      );
      
      Volume volume = serverApi().getVolume("datacenter-id", "server-id", "volume-id");
      
      assertEquals(volume.properties().name(), "Storage");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/volumes/volume-id");
   }
   
   @Test
   public void testDetachVolume() throws InterruptedException {
      server.enqueue(response204());
      
      serverApi().detachVolume("datacenter-id", "server-id", "image-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/volumes/image-id");
   }
   
   @Test
   public void testListCdroms() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/cdroms.json"))
      );
      
      List<Image> cdroms = serverApi().listAttachedCdroms("datacenter-id", "some-id");
      assertEquals(cdroms.size(), 1);
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/some-id/cdroms");
   }

   @Test
   public void testAttachCdrom() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/image.json"))
      );
      serverApi().attachCdrom(Server.Request.attachCdromBuilder()
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .imageId("image-id")
              .build()
      );
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/rest/datacenters/datacenter-id/servers/server-id/cdroms", "{\"id\": \"image-id\"}");
   }
   
   @Test
   public void testGetCdrom() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/server/cdrom.json"))
      );
      
      Image cdrom = serverApi().getCdrom("datacenter-id", "server-id", "cdrom-id");
      
      assertEquals(cdrom.properties().name(), "ubuntu-15.10-server-amd64.iso");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/cdroms/cdrom-id");
   }

   @Test
   public void testDettachCdrom() throws InterruptedException {
      server.enqueue(response204());
      
      serverApi().detachCdrom("datacenter-id", "server-id", "image-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/cdroms/image-id");
   }
      
   
   private ServerApi serverApi() {
      return api.serverApi();
   }
   
}
