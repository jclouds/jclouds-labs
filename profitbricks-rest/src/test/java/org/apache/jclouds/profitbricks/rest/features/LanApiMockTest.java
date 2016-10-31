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
import org.apache.jclouds.profitbricks.rest.domain.Lan;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "LanApiMockTest", singleThreaded = true)
public class LanApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/lan/list.json"))
      );
      
      List<Lan> list = lanApi().list("datacenter-id");
      
      assertNotNull(list);
      assertEquals(list.size(), 4);
      assertEquals(list.get(0).properties().name(), "Ex lan 1");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans");
   }
   
   
   @Test
   public void testListWithDepth() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/lan/list.json"))
      );
      
      List<Lan> list = lanApi().list("datacenter-id", new DepthOptions().depth(2));
      
      assertNotNull(list);
      assertEquals(list.size(), 4);
      assertEquals(list.get(0).properties().name(), "Ex lan 1");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans?depth=2");
   }
   
   @Test
   public void testListWith404() throws InterruptedException {
      server.enqueue(response404());
      List<Lan> list = lanApi().list("datacenter-id");
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans");
   }
   
   @Test
   public void testListWith404WithDepth() throws InterruptedException {
      server.enqueue(response404());
      List<Lan> list = lanApi().list("datacenter-id", new DepthOptions().depth(1));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans?depth=1");
   }
    
   @Test
   public void testGetLan() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/lan/get.json"));
      response.setHeader("Content-Type", "application/json");
      
      server.enqueue(response);
      
      Lan lan = lanApi().get("datacenter-id", "some-id");
      
      assertNotNull(lan);
      assertEquals(lan.properties().name(), "Ex lan");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans/some-id");
   }
   
   public void testGetLanWith404() throws InterruptedException {
      server.enqueue(response404());

      Lan lan = lanApi().get("datacenter-id", "some-id");
      
      assertEquals(lan, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans/some-id");
   }
    
   @Test
   public void testGetLanWithDepth() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/lan/get.json"));
      response.setHeader("Content-Type", "application/json");
      
      server.enqueue(response);
      
      Lan lan = lanApi().get("datacenter-id", "some-id", new DepthOptions().depth(2));
      
      assertNotNull(lan);
      assertEquals(lan.properties().name(), "Ex lan");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans/some-id?depth=2");
   }
   
   public void testGetLanWith404WithDepth() throws InterruptedException {
      server.enqueue(response404());

      Lan lan = lanApi().get("datacenter-id", "some-id", new DepthOptions().depth(2));
      
      assertEquals(lan, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/lans/some-id?depth=2");
   }
   
   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/lan/get.json"))
      );
      
      Lan lan = lanApi().create(
              Lan.Request.creatingBuilder()
              .dataCenterId("datacenter-id")
              .name("jclouds-lan")
              .build());

      assertNotNull(lan);
      assertNotNull(lan.id());
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/lans", 
              "{\"properties\": {\"name\": \"jclouds-lan\"}}"
      );
   }
   
   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/lan/get.json"))
      );
      
      api.lanApi().update(
              Lan.Request.updatingBuilder()
              .id("some-id")
              .dataCenterId("datacenter-id")
              .isPublic(false)
              .build());
            
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/datacenters/datacenter-id/lans/some-id", "{\"public\": false}");
   }
   
   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody("")
      );
      
      lanApi().delete("datacenter-id", "some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/lans/some-id");
   }
   
   @Test
   public void testDeleteWith404() throws InterruptedException {
      server.enqueue(response404());

      lanApi().delete("datacenter-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/lans/some-id");
   }
        
   private LanApi lanApi() {
      return api.lanApi();
   }
   
}
