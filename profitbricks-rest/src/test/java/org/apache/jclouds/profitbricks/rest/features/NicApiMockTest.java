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
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NicApiMockTest", singleThreaded = true)
public class NicApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testGetList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/nic/list.json"))
      );
      
      List<Nic> list = nicApi().list("datacenter-id", "server-id");
      
      assertNotNull(list);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).properties().name(), "Test Nic");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics");
   }
   
   @Test
   public void testGetListWith404() throws InterruptedException {
      server.enqueue(response404());
      List<Nic> list = nicApi().list("datacenter-id", "server-id", new DepthOptions().depth(1));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics?depth=1");
   }
    
   @Test
   public void testGetNic() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/nic/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      Nic nic = nicApi().get("datacenter-id", "server-id", "some-id");
      
      assertNotNull(nic);
      assertEquals(nic.properties().name(), "test nic");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/some-id");
   }
   
   public void testGetNicWith404() throws InterruptedException {
      server.enqueue(response404());

      Nic nic = nicApi().get("datacenter-id", "server-id", "some-id");
      
      assertEquals(nic, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/some-id");
   }
   
   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/nic/get.json"))
      );
      
      Nic nic = nicApi().create(
              Nic.Request.creatingBuilder()
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .name("jclouds-nic")
              .lan(1)
              .build());

      assertNotNull(nic);
      assertNotNull(nic.id());
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/servers/server-id/nics", 
              "{\"properties\": {\"name\": \"jclouds-nic\", \"lan\": 1}}"
      );
   }
   
   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/nic/get.json"))
      );
      
      api.nicApi().update(
              Nic.Request.updatingBuilder()
              .id("some-id")
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .name("apache-nic")
              .build());
            
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/datacenters/datacenter-id/servers/server-id/nics/some-id", "{\"name\": \"apache-nic\"}");
   }
   
   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody("")
      );
      
      nicApi().delete("datacenter-id", "server-id", "some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/nics/some-id");
   }
   
   @Test
   public void testDeleteWith404() throws InterruptedException {
      server.enqueue(response404());

      nicApi().delete("datacenter-id", "server-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/nics/some-id");
   }
           
   private NicApi nicApi() {
      return api.nicApi();
   }
   
}
