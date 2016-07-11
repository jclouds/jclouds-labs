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
import org.apache.jclouds.oneandone.rest.domain.FixedInstanceHardware;
import org.apache.jclouds.oneandone.rest.domain.Hardware;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.Image;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Status;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.Types.ServerAction;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseOneAndOneLiveTest {

   private List<Server> servers;
   private Server currentServer;
   private Server fixedInstanceServer;
   private HardwareFlavour currentFlavour;
   private Hdd currentHdd;
   private Image currentImage;

   private ServerApi serverApi() {

      return api.serverApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("jclouds test");
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      //turn on currentServer in order to be able to delete
      assertNodeAvailable(currentServer);
      turnOnServer(currentServer.id());

      if (fixedInstanceServer != null) {

         //delete fixed instance server once ready 
         assertNodeAvailable(fixedInstanceServer);
         deleteServer(fixedInstanceServer.id());
      }
      if (currentServer != null) {
         //delete currentserver once ready
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
   }

   @Test(dependsOnMethods = "testListHardwareFlavours")
   public void testCreateFixedInstanceServer() {

      Server.CreateFixedInstanceServer request = Server.CreateFixedInstanceServer.builder()
              .name("java test fixed instance")
              .description("testing with jclouds")
              .hardware(FixedInstanceHardware.create(currentFlavour.id()))
              .applianceId("7C5FA1D21B98DE39D7516333AAB7DA54")
              .password("Test123!")
              .powerOn(Boolean.TRUE).build();
      fixedInstanceServer = serverApi().createFixedInstanceServer(request);

      assertNotNull(fixedInstanceServer);
      assertNotNull(fixedInstanceServer.id());
      assertEquals(currentFlavour.hardware().vcore(), fixedInstanceServer.hardware().vcore());
      assertEquals(currentFlavour.hardware().coresPerProcessor(), fixedInstanceServer.hardware().coresPerProcessor());
      assertEquals(currentFlavour.hardware().ram(), fixedInstanceServer.hardware().ram());

   }

   @Test
   public void testList() {
      servers = serverApi().list();

      assertNotNull(servers);
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testList")
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<Server> serversWithQuery = serverApi().list(options);

      assertNotNull(serversWithQuery);
      Assert.assertTrue(serversWithQuery.size() > 0);
   }

   @Test(dependsOnMethods = "testListWithOption")
   public void testGetServer() {
      Server result = serverApi().get(currentServer.id());

      assertNotNull(result);
      assertEquals(result.id(), currentServer.id());
   }

   @Test(dependsOnMethods = "testList")
   public void testListHardwareFlavours() {
      List<HardwareFlavour> flavours = serverApi().listHardwareFlavours();
      currentFlavour = flavours.get(1);
      assertNotNull(flavours);
      assertFalse(flavours.isEmpty());
      Assert.assertTrue(flavours.size() > 0);
   }

   @Test(dependsOnMethods = "testListHardwareFlavours")
   public void testGetHardwareFlavour() {
      HardwareFlavour flavours = serverApi().getHardwareFlavour(currentFlavour.id());

      assertNotNull(flavours);
   }

   @Test(dependsOnMethods = "testList")
   public void testGetServerStatus() {
      Status status = serverApi().getStatus(currentServer.id());

      assertNotNull(status);
   }

   @Test(dependsOnMethods = "testGetServerStatus")
   public void testGetServerHardware() {
      Hardware hardware = serverApi().getHardware(currentServer.id());

      assertNotNull(hardware);
   }

   @Test(dependsOnMethods = "testGetServerStatus")
   public void testUpdateServer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      String updatedName = "Updatedjava";
      String updatedDesc = "Updated desc";

      Server updateResult = serverApi().update(currentServer.id(), Server.UpdateServer.create(updatedName, updatedDesc));

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);
      assertEquals(updateResult.description(), updatedDesc);

   }

   @Test(dependsOnMethods = "testUpdateStaus")
   public void testUpdateHardware() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server updateResult = serverApi().updateHardware(currentServer.id(), Hardware.UpdateHardware.create(4, 2, 6));

      assertNotNull(updateResult);
   }

   @Test(dependsOnMethods = "testAddHdds")
   public void testListHardwareHdds() throws InterruptedException {
      assertNodeAvailable(currentServer);
      //give time for harddisk to be added
//        Thread.sleep(60000);
      List<Hdd> hdds = serverApi().listHdds(currentServer.id());
      for (Hdd hdd : hdds) {
         if (!hdd.isMain()) {
            currentHdd = hdd;
            break;
         }
      }
      assertNotNull(hdds);
      assertFalse(hdds.isEmpty());
      Assert.assertTrue(hdds.size() > 0);
   }

   @Test(dependsOnMethods = "testUpdateHardware")
   public void testAddHdds() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<Hdd.CreateHdd> requestList = new ArrayList<Hdd.CreateHdd>();
      requestList.add(Hdd.CreateHdd.create(20, Boolean.TRUE));
      Hdd.CreateHddList request = Hdd.CreateHddList.create(requestList);
      //double check
      assertNodeAvailable(currentServer);
      Server response = serverApi().addHdd(currentServer.id(), request);

      assertNotNull(response);
      Assert.assertTrue(response.hardware().hdds().size() > 0);
   }

   @Test(dependsOnMethods = "testListHardwareHdds")
   public void testGetHdd() throws InterruptedException {
      Hdd response = serverApi().getHdd(currentServer.id(), currentHdd.id());

      assertNotNull(response);
      assertEquals(response.size(), currentHdd.size());
   }

   @Test(dependsOnMethods = "testGetHdd")
   public void testUpdateHdd() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().updateHdd(currentServer.id(), currentHdd.id(), currentHdd.size() + 20);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testUpdateHdd")
   public void testDeleteHdd() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Hdd hddToDelete = null;
      List<Hdd> hdds = serverApi().listHdds(currentServer.id());
      for (Hdd hdd : hdds) {
         if (!hdd.isMain()) {
            hddToDelete = hdd;
            break;
         }
      }
      if (hddToDelete != null) {
         Server response = serverApi().deleteHdd(currentServer.id(), hddToDelete.id());
         assertNotNull(response);
      }
   }

   @Test(dependsOnMethods = "testDeleteHdd")
   public void testGetImage() throws InterruptedException {
      if (fixedInstanceServer != null) {
         currentImage = serverApi().getImage(fixedInstanceServer.id());

         assertNotNull(currentImage);
      }
   }

   @Test(dependsOnMethods = "testGetImage")
   public void testUpdateImage() throws InterruptedException {
      if (fixedInstanceServer != null) {
         assertNodeAvailable(fixedInstanceServer);

         Server.UpdateServerResponse response = serverApi().updateImage(fixedInstanceServer.id(), Server.UpdateImage.create(currentImage.id(), "Test123!"));

         assertNotNull(response);
      }
   }

   @Test(dependsOnMethods = "testUpdateServer")
   public void testUpdateStaus() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server updateResult = serverApi().updateStatus(currentServer.id(), Server.UpdateStatus.create(ServerAction.POWER_OFF, Types.ServerActionMethod.HARDWARE));
      assertNodeAvailable(currentServer);

      assertNotNull(updateResult);
   }
}
