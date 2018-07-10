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

import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.BlockStorage;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


@Test(groups = "live", testName = "BlockStorageApiLiveTest")
public class BlockStorageApiLiveTest extends BaseOneAndOneLiveTest {

   private BlockStorage currentBlockStorage;
   private Server currentServer;
   private List<BlockStorage> blockStorages;

   private BlockStorageApi blockStorageApi() {

      return api.blockStorageApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("blockstorage jclouds server2");
      assertNodeAvailable(currentServer);
      currentBlockStorage = blockStorageApi().create(BlockStorage.CreateBlockStorage.builder()
              .name("jcloudsBlockStorage2")
              .description("description")
              .size(20)
              .datacenterId("908DC2072407C94C8054610AD5A53B8C")
              .build());
      assertNotNull(currentBlockStorage);
   }

   @Test
   public void testList() {
      blockStorages = blockStorageApi().list();

      assertNotNull(blockStorages);
      Assert.assertTrue(blockStorages.size() > 0);
   }

   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jcloudsBlockStorage", null);
      List<BlockStorage> blockStoragesWithQuery = blockStorageApi().list(options);

      assertNotNull(blockStoragesWithQuery);
      Assert.assertTrue(blockStoragesWithQuery.size() > 0);
   }

   public void testGet() {
      BlockStorage result = blockStorageApi().get(currentBlockStorage.id());

      assertNotNull(result);
      assertEquals(result.id(), currentBlockStorage.id());
   }

   @Test
   public void testUpdate() throws InterruptedException {
      String updatedName = "jcloudsBlockStorage";

      BlockStorage updateResult = blockStorageApi().update(currentBlockStorage.id(), BlockStorage.UpdateBlockStorage.create(updatedName, "desc"));

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);

   }

   @Test(dependsOnMethods = "testUpdate")
   public void testAttachBlockStorage() throws InterruptedException {
      BlockStorage.Server.AttachServer attachServer = BlockStorage.Server.AttachServer.create(currentServer.id());
      BlockStorage attachResult = blockStorageApi().attachServer(currentBlockStorage.id(), attachServer);

      assertNotNull(attachResult);
   }

   @Test(dependsOnMethods = "testAttachBlockStorage")
   public void testDetachServer() {
      assertNodeAvailable(currentServer);
      assertBlockStorageAvailable(currentBlockStorage);
      BlockStorage result = blockStorageApi().detachServer(currentBlockStorage.id());

      assertNotNull(result);
      assertEquals(result.id(), currentBlockStorage.id());
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      assertNodeAvailable(currentServer);
      assertBlockStorageAvailable(currentBlockStorage);
      blockStorageApi().delete(currentBlockStorage.id());
      assertNodeAvailable(currentServer);
      deleteServer(currentServer.id());
   }

}
