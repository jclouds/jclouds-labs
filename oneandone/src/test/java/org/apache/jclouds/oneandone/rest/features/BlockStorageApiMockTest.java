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
import org.apache.jclouds.oneandone.rest.domain.BlockStorage;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "BlockStorageApiMockTest", singleThreaded = true)
public class BlockStorageApiMockTest extends BaseOneAndOneApiMockTest {

   private BlockStorageApi blockStorageApi() {
      return api.blockStorageApi();
   }

   public void testList() throws InterruptedException {
      server.enqueue(
            new MockResponse().setBody(stringFromResource("/blockstorage/list.json"))
      );

      List<BlockStorage> blockStorages = blockStorageApi().list();

      assertNotNull(blockStorages);
      assertEquals(blockStorages.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages");
   }

   public void testList404() throws InterruptedException {
      server.enqueue(
            new MockResponse().setResponseCode(404));

      List<BlockStorage> blockStorages = blockStorageApi().list();

      assertNotNull(blockStorages);
      assertEquals(blockStorages.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages");
   }

   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/list.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<BlockStorage> blockStorages = blockStorageApi().list(options);

      assertNotNull(blockStorages);
      assertEquals(blockStorages.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages?q=New");
   }

   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<BlockStorage> blockStorages = blockStorageApi().list(options);

      assertNotNull(blockStorages);
      assertEquals(blockStorages.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages?q=test");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );
      BlockStorage result = blockStorageApi().get("blockStorageId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages/blockStorageId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      BlockStorage result = blockStorageApi().get("blockStorageId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/block_storages/blockStorageId");
   }

   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );
      BlockStorage response = blockStorageApi().create(BlockStorage.CreateBlockStorage.builder()
              .name("name")
              .description("desc")
              .size(20)
              .datacenterId("datacenter-id")
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/block_storages", "{\"name\":\"name\",\"description\":\"desc\",\"size\":20,\"datacenter_id\":\"datacenter-id\"}");
   }

   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );
      BlockStorage response = blockStorageApi().update("blockStorageId", BlockStorage.UpdateBlockStorage.create("name", "desc"));
      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/block_storages/blockStorageId", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );
      blockStorageApi().delete("blockStorageId");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/block_storages/blockStorageId");
   }

   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      blockStorageApi().delete("blockStorageId");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/block_storages/blockStorageId");
   }

   public void testAttachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );

      BlockStorage.Server.AttachServer attachServer = BlockStorage.Server.AttachServer.create("serverId");
      BlockStorage response = blockStorageApi().attachServer("blockStorageId", attachServer);

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/block_storages/blockStorageId/server", "{\"server_id\":\"serverId\"}");
   }

   public void testDetachServer() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/blockstorage/get.json"))
      );
      BlockStorage response = blockStorageApi().detachServer("blockStorageId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/block_storages/blockStorageId/server");
   }

}
