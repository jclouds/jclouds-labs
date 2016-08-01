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
import org.apache.jclouds.oneandone.rest.domain.SharedStorage;
import org.apache.jclouds.oneandone.rest.domain.SharedStorageAccess;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SharedStorageApiMockTest", singleThreaded = true)
public class SharedStorageApiMockTest extends BaseOneAndOneApiMockTest {

   private SharedStorageApi sharedStorageApi() {
      return api.sharedStorageApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/list.json"))
      );

      List<SharedStorage> sharedStorages = sharedStorageApi().list();

      assertNotNull(sharedStorages);
      assertEquals(sharedStorages.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<SharedStorage> sharedStorages = sharedStorageApi().list();

      assertNotNull(sharedStorages);
      assertEquals(sharedStorages.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<SharedStorage> sharedStorages = sharedStorageApi().list(options);

      assertNotNull(sharedStorages);
      assertEquals(sharedStorages.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<SharedStorage> sharedStorages = sharedStorageApi().list(options);

      assertNotNull(sharedStorages);
      assertEquals(sharedStorages.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages?q=test");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );
      SharedStorage result = sharedStorageApi().get("sharedStorageId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId");
   }

   @Test
   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      SharedStorage result = sharedStorageApi().get("sharedStorageId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );
      SharedStorage response = sharedStorageApi().create(SharedStorage.CreateSharedStorage.builder()
              .name("name")
              .description("desc")
              .size(20)
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/shared_storages", "{\"name\":\"name\",\"description\":\"desc\",\"size\":20}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );
      SharedStorage response = sharedStorageApi().update("sharedStorageId", SharedStorage.UpdateSharedStorage.create("name", "desc", null));
      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/shared_storages/sharedStorageId", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );
      SharedStorage response = sharedStorageApi().delete("sharedStorageId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/shared_storages/sharedStorageId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      SharedStorage storage = sharedStorageApi().delete("sharedStorageId");

      assertEquals(storage, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/shared_storages/sharedStorageId");
   }

   @Test
   public void testListServers() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/servers.list.json"))
      );

      List<SharedStorage.Server> servers = sharedStorageApi().listServers("sharedStorageId");

      assertNotNull(servers);
      assertEquals(servers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId/servers");
   }

   @Test
   public void testListServers404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );

      List<SharedStorage.Server> servers = sharedStorageApi().listServers("sharedStorageId");

      assertNotNull(servers);
      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId/servers");
   }

   @Test
   public void testGetServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/server.get.json"))
      );
      SharedStorage.Server result = sharedStorageApi().getServer("sharedStorageId", "serverId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId/servers/serverId");
   }

   @Test
   public void testGetServer404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      SharedStorage.Server result = sharedStorageApi().getServer("sharedStorageId", "serverId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/sharedStorageId/servers/serverId");
   }

   @Test
   public void testAttachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );

      List<SharedStorage.Server.CreateServer.ServerPayload> servers = new ArrayList<SharedStorage.Server.CreateServer.ServerPayload>();
      SharedStorage.Server.CreateServer.ServerPayload toAdd = SharedStorage.Server.CreateServer.ServerPayload.create("server_id", Types.StorageServerRights.R);
      servers.add(toAdd);
      SharedStorage response = sharedStorageApi().attachServer("sharedStorageId", SharedStorage.Server.CreateServer.create(servers));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/shared_storages/sharedStorageId/servers", "{\"servers\":[{\"id\":\"server_id\",\"rights\":\"R\"}]}");
   }

   @Test
   public void testDetachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/get.json"))
      );
      SharedStorage response = sharedStorageApi().detachServer("sharedStorageId", "serverId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/shared_storages/sharedStorageId/servers/serverId");
   }

   @Test
   public void testListAccessCredentials() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/list.access.json"))
      );

      List<SharedStorageAccess> servers = sharedStorageApi().getAccessCredentials();

      assertNotNull(servers);
      assertEquals(servers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/access");
   }

   @Test
   public void testListAccessCredentials404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );

      List<SharedStorageAccess> servers = sharedStorageApi().getAccessCredentials();

      assertEquals(servers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/shared_storages/access");
   }

   @Test
   public void testChangePassword() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/sharedstorage/list.access.json"))
      );
      List<SharedStorageAccess> response = sharedStorageApi().changePassword(SharedStorageAccess.UpdateSharedStorageAccess.create("password"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/shared_storages/access", "{\"password\":\"password\"}");
   }

}
