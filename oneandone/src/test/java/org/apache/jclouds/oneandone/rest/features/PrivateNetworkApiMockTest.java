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
package org.apache.jclouds.oneandone.rest.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "PrivateNetworkApiMockTest", singleThreaded = true)
public class PrivateNetworkApiMockTest extends BaseOneAndOneApiMockTest {

   private PrivateNetworkApi privateNetworkApi() {
      return api.privateNetworkApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/list.json"))
      );

      List<PrivateNetwork> networks = privateNetworkApi().list();

      assertNotNull(networks);
      assertEquals(networks.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<PrivateNetwork> networks = privateNetworkApi().list();

      assertEquals(networks.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<PrivateNetwork> result = privateNetworkApi().list(options);

      assertNotNull(result);
      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<PrivateNetwork> result = privateNetworkApi().list(options);

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );
      PrivateNetwork result = privateNetworkApi().get("privatenetworkId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      PrivateNetwork result = privateNetworkApi().get("privatenetworkId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );

      PrivateNetwork response = privateNetworkApi().create(PrivateNetwork.CreatePrivateNetwork.builder()
              .name("name")
              .networkAddress("192.168.1.0")
              .subnetMask("255.255.255.0")
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/private_networks", "{\"name\":\"name\",\"network_address\":\"192.168.1.0\",\"subnet_mask\":\"255.255.255.0\"}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );
      PrivateNetwork response = privateNetworkApi().update("privatenetworkId", PrivateNetwork.UpdatePrivateNetwork.builder()
              .name("name")
              .build()
      );

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/private_networks/privatenetworkId", "{\"name\":\"name\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );
      PrivateNetwork response = privateNetworkApi().delete("privatenetworkId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/private_networks/privatenetworkId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      PrivateNetwork response = privateNetworkApi().delete("privatenetworkId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/private_networks/privatenetworkId");
   }

   @Test
   public void testListServers() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/servers.list.json"))
      );

      List<PrivateNetwork.Server> servers = privateNetworkApi().listServers("privatenetworkId");

      assertNotNull(servers);
      assertEquals(servers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId/servers");
   }

   @Test
   public void testListServers404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<PrivateNetwork.Server> servers = privateNetworkApi().listServers("privatenetworkId");

      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId/servers");
   }

   @Test
   public void testGetServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/server.get.json"))
      );
      PrivateNetwork.Server result = privateNetworkApi().getServer("privatenetworkId", "serverId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId/servers/serverId");
   }

   @Test
   public void testGetServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      PrivateNetwork.Server result = privateNetworkApi().getServer("privatenetworkId", "serverId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/private_networks/privatenetworkId/servers/serverId");
   }

   @Test
   public void testAttachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );

      List<String> servers = new ArrayList<String>();
      String toAdd = "server_id";
      servers.add(toAdd);
      PrivateNetwork response = privateNetworkApi().attachServer("privatenetworkId", PrivateNetwork.Server.CreateServer.create(servers));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/private_networks/privatenetworkId/servers", "{\"servers\":[\"server_id\"]}");
   }

   @Test
   public void testDetachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/privatenetwork/get.json"))
      );
      PrivateNetwork response = privateNetworkApi().detachServer("privatenetworkId", "serverId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/private_networks/privatenetworkId/servers/serverId");
   }

   @Test
   public void testDetachServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      PrivateNetwork response = privateNetworkApi().detachServer("privatenetworkId", "serverId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/private_networks/privatenetworkId/servers/serverId");
   }
}
