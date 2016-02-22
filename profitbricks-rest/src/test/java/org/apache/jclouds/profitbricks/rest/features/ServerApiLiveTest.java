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

import com.google.common.base.Predicate;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseProfitBricksLiveTest {
   
   DataCenter dataCenter;
   Server testServer;
   Image attachedCdrom;
   Volume attachedVolume;
  
   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }
   
   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null)
         deleteDataCenter(dataCenter.id());
   }
     
   @Test
   public void testCreateServer() {
      assertNotNull(dataCenter);
            
      testServer = serverApi().createServer(
              Server.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-node")
              .cores(1)
              .ram(1024)
              .build());

      assertNotNull(testServer);
      assertEquals(testServer.properties().name(), "jclouds-node");
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));
   }
   

   @Test(dependsOnMethods = "testCreateServer")
   public void testGetServer() {
      Server server = serverApi().getServer(dataCenter.id(), testServer.id());

      assertNotNull(server);
      assertEquals(server.id(), testServer.id());
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testList() {
      List<Server> servers = serverApi().getList(dataCenter.id());

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
      assertEquals(servers.size(), 1);
   }
   
   @Test(dependsOnMethods = "testGetServer")
   public void testUpdateServer() {
      assertDataCenterAvailable(dataCenter);
      
      api.serverApi().updateServer(
              Server.Request.updatingBuilder()
              .id(testServer.id())
              .dataCenterId(testServer.dataCenterId())
              .name("apache-node")
              .ram(1024 * 2)
              .cores(2)
              .build());

      assertDataCenterAvailable(dataCenter);

      assertNodeAvailable(ServerRef.create(dataCenter.id(), testServer.id()));
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));
      
      Server server = serverApi().getServer(dataCenter.id(), testServer.id());
      
      assertEquals(server.properties().name(), "apache-node");
   }
   
   @Test(dependsOnMethods = "testUpdateServer")
   public void testStopServer() {
      serverApi().stopServer(testServer.dataCenterId(), testServer.id());
      assertNodeSuspended(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(testServer.dataCenterId(), testServer.id());
      assertEquals(server.properties().vmState(), Server.Status.SHUTOFF);
   }

   @Test(dependsOnMethods = "testStopServer")
   public void testStartServer() {
      serverApi().startServer(testServer.dataCenterId(), testServer.id());
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(testServer.dataCenterId(), testServer.id());
      assertEquals(server.properties().vmState(), Server.Status.RUNNING);
   }
   
   @Test(dependsOnMethods = "testStartServer")
   public void testRebootServer() {
      serverApi().rebootServer(testServer.dataCenterId(), testServer.id());
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(testServer.dataCenterId(), testServer.id());
      assertEquals(server.properties().vmState(), Server.Status.RUNNING);
   }
   
   @Test(dependsOnMethods = "testRebootServer")
   public void testListVolumes() {
      List<Volume> volumes = serverApi().listAttachedVolumes(testServer.dataCenterId(), testServer.id());
      assertTrue(volumes.isEmpty());
   }
   
   @Test(dependsOnMethods = "testListVolumes")
   public void testAttachVolume() {
      
      Volume volume = createVolume(dataCenter);
      
      assertVolumeAvailable(VolumeRef.create(dataCenter.id(), volume.id()));
      
      attachedVolume = serverApi().attachVolume(
         Server.Request.attachVolumeBuilder()
            .dataCenterId(testServer.dataCenterId())
            .serverId(testServer.id())
            .volumeId(volume.id())
            .build()
      );
      
      assertVolumeAttached(testServer, volume.id());
      
      List<Volume> volumes = serverApi().listAttachedVolumes(testServer.dataCenterId(), testServer.id());
      assertEquals(volumes.size(), 1);
   }
   
   @Test(dependsOnMethods = "testAttachVolume")
   public void testGetVolume() {
      Volume volume = serverApi().getVolume(testServer.dataCenterId(), testServer.id(), attachedVolume.id());
      assertEquals(volume.id(), attachedVolume.id());
   }
   
   @Test(dependsOnMethods = "testGetVolume")
   public void testDetachVolume() {
      serverApi().detachVolume(testServer.dataCenterId(), testServer.id(), attachedVolume.id());
      assertVolumeDetached(testServer, attachedVolume.id());
   }   
   
   @Test(dependsOnMethods = "testDetachVolume")
   public void testListCdroms() {
      List<Image> images = serverApi().listAttachedCdroms(testServer.dataCenterId(), testServer.id());
      assertTrue(images.isEmpty());
   }
   
   @Test(dependsOnMethods = "testListCdroms")
   public void testAttachCdrom() {
      attachedCdrom = serverApi().attachCdrom(
         Server.Request.attachCdromBuilder()
            .dataCenterId(testServer.dataCenterId())
            .serverId(testServer.id())
            .imageId("7cb4b3a3-50c3-11e5-b789-52540066fee9")
            .build()
      );
      assertEquals(attachedCdrom.properties().name(), "ubuntu-14.04.3-server-amd64.iso");
      assertCdromAvailable(testServer, attachedCdrom.id());
      
      List<Image> images = serverApi().listAttachedCdroms(testServer.dataCenterId(), testServer.id());
      assertEquals(images.size(), 1);
   }
   
   @Test(dependsOnMethods = "testAttachCdrom")
   public void testRetrieveAttachedCdrom() {
      Image cdrom = serverApi().getCdrom(testServer.dataCenterId(), testServer.id(), attachedCdrom.id());
      assertEquals(cdrom.id(), attachedCdrom.id());
   }
   
   @Test(dependsOnMethods = "testRetrieveAttachedCdrom")
   public void testDetachCdrom() {
      serverApi().detachCdrom(testServer.dataCenterId(), testServer.id(), attachedCdrom.id());
      assertCdromRemoved(testServer, attachedCdrom.id());
   }
   
   @Test(dependsOnMethods = "testDetachCdrom")
   public void testDeleteServer() {
      serverApi().deleteServer(testServer.dataCenterId(), testServer.id());
      assertNodeRemoved(ServerRef.create(dataCenter.id(), testServer.id()));
   }
   
   private ServerApi serverApi() {
      return api.serverApi();
   }
   
   private void assertCdromAvailable(Server server, String cdRomId) {
      assertRandom(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            Image cdrom = serverApi().getCdrom(params[0], params[1], params[2]);
            
            if (cdrom == null || cdrom.metadata() == null)
               return false;
            
            return cdrom.metadata().state() == State.AVAILABLE;
         }
      }, complexId(server.dataCenterId(), server.id(), cdRomId));
   }

   private void assertCdromRemoved(Server server, String cdRomId) {
      assertRandom(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            return serverApi().getCdrom(params[0], params[1], params[2]) == null;
         }
      }, complexId(server.dataCenterId(), server.id(), cdRomId));
   }
   
   private void assertVolumeAttached(Server server, String volumeId) {
      assertRandom(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            Volume volume = serverApi().getVolume(params[0], params[1], params[2]);
            
            if (volume == null || volume.metadata() == null)
               return false;
            
            return volume.metadata().state() == State.AVAILABLE;
         }
      }, complexId(server.dataCenterId(), server.id(), volumeId));
   }

   private void assertVolumeDetached(Server server, String volumeId) {
      assertRandom(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            return serverApi().getVolume(params[0], params[1], params[2]) == null;
         }
      }, complexId(server.dataCenterId(), server.id(), volumeId));
   }
      
}
