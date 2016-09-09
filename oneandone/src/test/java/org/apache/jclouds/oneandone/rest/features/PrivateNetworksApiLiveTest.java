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
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.ids.ServerPrivateNetworkRef;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "PrivateNetworksApiLiveTest")
public class PrivateNetworksApiLiveTest extends BaseOneAndOneLiveTest {

   private PrivateNetwork currentPrivateNetwork;
   private List<PrivateNetwork> privateNetworks;
   private Server currentServer;

   private PrivateNetworkApi privateNetworkApi() {

      return api.privateNetworkApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("privatenetwork jclouds server");
      assertNodeAvailable(currentServer);
      currentPrivateNetwork = privateNetworkApi().create(PrivateNetwork.CreatePrivateNetwork.builder()
              .name("jclouds privatenetwork")
              .networkAddress("192.168.1.0")
              .subnetMask("255.255.255.0")
              .build());
   }

   @Test
   public void testList() {
      privateNetworks = privateNetworkApi().list();

      Assert.assertTrue(privateNetworks.size() > 0);
   }

   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<PrivateNetwork> resultWithQuery = privateNetworkApi().list(options);

      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   public void testGet() {
      PrivateNetwork result = privateNetworkApi().get(currentPrivateNetwork.id());

      assertEquals(result.id(), currentPrivateNetwork.id());
   }

   public void testUpdate() throws InterruptedException {
      String updatedName = "updatejclouds PN";

      PrivateNetwork updateResult = privateNetworkApi().update(currentPrivateNetwork.id(), PrivateNetwork.UpdatePrivateNetwork.builder()
              .name(updatedName)
              .build());

      assertEquals(updateResult.name(), updatedName);

   }

   @Test(dependsOnMethods = "testUpdate")
   public void testAttachServer() throws InterruptedException {
      List<String> servers = new ArrayList<String>();
      String toAdd = currentServer.id();
      servers.add(toAdd);

      PrivateNetwork updateResult = privateNetworkApi().attachServer(currentPrivateNetwork.id(), PrivateNetwork.Server.CreateServer.create(servers));

      assertNotNull(updateResult);

   }

   @Test(dependsOnMethods = "testAttachServer")
   public void testListServers() {
      assertPrivateNetworkAvailable(ServerPrivateNetworkRef.create(currentServer.id(), currentPrivateNetwork.id()));
      List<PrivateNetwork.Server> servers = privateNetworkApi().listServers(currentPrivateNetwork.id());

      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testAttachServer")
   public void testServerGet() {
      PrivateNetwork.Server result = privateNetworkApi().getServer(currentPrivateNetwork.id(), currentServer.id());

      assertEquals(result.id(), currentServer.id());
   }

   @Test(dependsOnMethods = "testServerGet")
   public void testDetachServer() {
      turnOFFServer(currentServer.id());
      assertNodeAvailable(currentServer);
      assertPrivateNetworkAvailable(ServerPrivateNetworkRef.create(currentServer.id(), currentPrivateNetwork.id()));
      PrivateNetwork result = privateNetworkApi().detachServer(currentPrivateNetwork.id(), currentServer.id());

      assertEquals(result.id(), currentPrivateNetwork.id());
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      if (currentPrivateNetwork != null && currentServer != null) {
         assertPrivateNetworkAvailable(ServerPrivateNetworkRef.create(currentServer.id(), currentPrivateNetwork.id()));
         privateNetworkApi().delete(currentPrivateNetwork.id());
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
   }

}
