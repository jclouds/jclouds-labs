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

import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Dvd;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerPrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Snapshot;
import org.apache.jclouds.oneandone.rest.ids.ServerPrivateNetworkRef;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ServerOperationsApiLiveTest extends BaseOneAndOneLiveTest {

   private Server currentServer;
   private Server cloneServer;

   private ServerPrivateNetwork currentPrivateNetwork;

   private ServerApi serverApi() {

      return api.serverApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("jclouds operations test");
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      //give time for operations to finish
//        Thread.sleep(10000);
      if (currentServer != null) {
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
      if (cloneServer != null) {
         assertNodeAvailable(cloneServer);
         deleteServer(cloneServer.id());
      }
   }

   @Test(dependsOnMethods = "testCreateClone")
   public void testGetDvd() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Dvd dvd = serverApi().getDvd(currentServer.id());

      assertNotNull(dvd);
   }

   @Test(dependsOnMethods = "testGetDvd")
   public void testLoadDvd() throws InterruptedException {
      assertNodeAvailable(currentServer);
      //TODO: get data from live api
      String dvdId = "81504C620D98BCEBAA5202D145203B4B";
      Server response = serverApi().loadDvd(currentServer.id(), dvdId);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testLoadDvd")
   public void testUnloadDvd() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Server response = serverApi().unloadDvd(currentServer.id());
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAssignPrivateNetwork")
   public void testListPrivateNetwork() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<ServerPrivateNetwork> privateNetworks = serverApi().listPrivateNetworks(currentServer.id());
      currentPrivateNetwork = privateNetworks.get(0);

      assertNotNull(privateNetworks);
      assertFalse(privateNetworks.isEmpty());
      Assert.assertTrue(privateNetworks.size() > 0);
   }

   @Test(dependsOnMethods = "testListPrivateNetwork")
   public void testGetPrivateNetwork() throws InterruptedException {
      assertNodeAvailable(currentServer);
      PrivateNetwork privatenetworkd = serverApi().getPrivateNetwork(currentServer.id(), currentPrivateNetwork.id());
      assertNotNull(privatenetworkd);
   }

   @Test
   public void testAssignPrivateNetwork() throws InterruptedException {
      assertNodeAvailable(currentServer);
      //TODO: replace with live data from api
      String privateNetworkId = "40D2C8D5029BF03F7C9D02D54C9F237D";
      Server response = serverApi().assignPrivateNetwork(currentServer.id(), privateNetworkId);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testGetPrivateNetwork")
   public void testDeletePrivateNetwork() throws InterruptedException {
      assertNodeAvailable(currentServer);
      assertPrivateNetworkAvailable(ServerPrivateNetworkRef.create(currentServer.id(), currentPrivateNetwork.id()));
      assertNodeAvailable(currentServer);

      Server response = serverApi().deletePrivateNetwork(currentServer.id(), currentPrivateNetwork.id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshot() throws InterruptedException {
      List<Snapshot> snapshots = serverApi().listSnapshots(currentServer.id());

      assertNotNull(snapshots);
      assertFalse(snapshots.isEmpty());
      Assert.assertTrue(snapshots.size() > 0);
   }

   @Test(dependsOnMethods = "testListSnapshot")
   public void testRestoreSnapshot() throws InterruptedException {
      assertNodeAvailable(currentServer);
      currentServer = serverApi().get(currentServer.id());
      Server response = serverApi().restoreSnapshot(currentServer.id(), currentServer.Snapshot().id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testDeletePrivateNetwork")
   public void testCreateSnapshot() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Server response = serverApi().createSnapshot(currentServer.id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testListSnapshot")
   public void testDeleteSnapshot() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Thread.sleep(120000);
      currentServer = serverApi().get(currentServer.id());
      Server response = serverApi().deleteSnapshot(currentServer.id(), currentServer.Snapshot().id());

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testDeleteSnapshot")
   public void testCreateClone() throws InterruptedException {
      assertNodeAvailable(currentServer);

      cloneServer = serverApi().clone(currentServer.id(), Server.Clone.create(currentServer.datacenter().id(), "jclouds clone"));

      assertNotNull(cloneServer);
   }
}
