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

import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.SharedStorage;
import org.apache.jclouds.oneandone.rest.domain.SharedStorageAccess;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SharedStorageApiLiveTest")
public class SharedStorageApiLiveTest extends BaseOneAndOneLiveTest {

   private SharedStorage currentSharedStorage;
   private Server currentServer;
   private List<SharedStorage> sharedStorages;

   private SharedStorageApi sharedStorageApi() {

      return api.sharedStorageApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("sharestorage jclouds server");
      assertNodeAvailable(currentServer);
      currentSharedStorage = sharedStorageApi().create(SharedStorage.CreateSharedStorage.builder()
              .name("jcloudsStorage")
              .description("desc")
              .size(50)
              .build());
      assertNotNull(currentSharedStorage);
   }

   @Test
   public void testList() {
      sharedStorages = sharedStorageApi().list();

      assertNotNull(sharedStorages);
      Assert.assertTrue(sharedStorages.size() > 0);
   }

   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jcloudsStorage", null);
      List<SharedStorage> imageWithQuery = sharedStorageApi().list(options);

      assertNotNull(imageWithQuery);
      Assert.assertTrue(imageWithQuery.size() > 0);
   }

   public void testGet() {
      SharedStorage result = sharedStorageApi().get(currentSharedStorage.id());

      assertNotNull(result);
      assertEquals(result.id(), currentSharedStorage.id());
   }

   @Test
   public void testUpdate() throws InterruptedException {
      String updatedName = "Updatedjava";

      SharedStorage updateResult = sharedStorageApi().update(currentSharedStorage.id(), SharedStorage.UpdateSharedStorage.create(updatedName, "desc", null));

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);

   }

   @Test(dependsOnMethods = "testUpdate")
   public void testAttachServer() throws InterruptedException {

      List<SharedStorage.Server.CreateServer.ServerPayload> servers = new ArrayList<SharedStorage.Server.CreateServer.ServerPayload>();
      SharedStorage.Server.CreateServer.ServerPayload toAdd = SharedStorage.Server.CreateServer.ServerPayload.create(currentServer.id(), Types.StorageServerRights.R);
      servers.add(toAdd);
      SharedStorage updateResult = sharedStorageApi().attachServer(currentSharedStorage.id(), SharedStorage.Server.CreateServer.create(servers));

      assertNotNull(updateResult);

   }

   @Test(dependsOnMethods = "testAttachServer")
   public void testListServers() {
      List<SharedStorage.Server> servers = sharedStorageApi().listServers(currentSharedStorage.id());

      assertNotNull(servers);
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testAttachServer")
   public void testServerGet() {
      SharedStorage.Server result = sharedStorageApi().getServer(currentSharedStorage.id(), currentServer.id());

      assertNotNull(result);
      assertEquals(result.id(), currentServer.id());
   }

   @Test(dependsOnMethods = "testServerGet")
   public void testDetachServer() {
      SharedStorage result = sharedStorageApi().detachServer(currentSharedStorage.id(), currentServer.id());

      assertNotNull(result);
      assertEquals(result.id(), currentSharedStorage.id());
   }

   @Test
   public void testListAccessCredentials() {

      List<SharedStorageAccess> access = sharedStorageApi().getAccessCredentials();

      assertNotNull(access);
      assertTrue(access.size() > 0);
   }

   public void testChangePassword() {
      List<SharedStorageAccess> response = sharedStorageApi().changePassword(SharedStorageAccess.UpdateSharedStorageAccess.create("Test123!"));

      assertNotNull(response);
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      sharedStorageApi().delete(currentSharedStorage.id());
      assertNodeAvailable(currentServer);
      deleteServer(currentServer.id());
   }

}
