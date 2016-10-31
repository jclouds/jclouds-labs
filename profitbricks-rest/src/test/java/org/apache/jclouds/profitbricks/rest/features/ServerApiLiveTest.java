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
import java.net.URI;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.CpuFamily;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.LicenceType;
import static org.apache.jclouds.profitbricks.rest.domain.Location.US_LAS;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Server testServer;
   private Image attachedCdrom;
   private Volume attachedVolume;

   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null) {
         deleteDataCenter(dataCenter.id());
      }
   }

   @Test
   public void testCreateServer() {
      assertNotNull(dataCenter);

      testServer = serverApi().createServer(
              Server.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-node")
              .cpuFamily(CpuFamily.INTEL_XEON)
              .cores(1)
              .ram(1024)
              .build());

      assertRequestCompleted(testServer);
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
      
      Server updated = api.serverApi().updateServer(
              Server.Request.updatingBuilder()
              .id(testServer.id())
              .dataCenterId(testServer.dataCenterId())
              .name("apache-node")
              .ram(1024 * 2)
              .cores(2)
              .build());

      assertRequestCompleted(updated);
      assertNodeAvailable(ServerRef.create(dataCenter.id(), testServer.id()));
      assertDataCenterAvailable(dataCenter);
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(dataCenter.id(), testServer.id());

      assertEquals(server.properties().name(), "apache-node");
   }

   @Test(dependsOnMethods = "testUpdateServer")
   public void testStopServer() {
      URI uri = serverApi().stopServer(testServer.dataCenterId(), testServer.id());
      assertRequestCompleted(uri);
      assertNodeSuspended(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(testServer.dataCenterId(), testServer.id());
      assertEquals(server.properties().vmState(), Server.Status.SHUTOFF);
   }

   @Test(dependsOnMethods = "testStopServer")
   public void testStartServer() {
      URI uri = serverApi().startServer(testServer.dataCenterId(), testServer.id());
      assertRequestCompleted(uri);
      assertNodeRunning(ServerRef.create(dataCenter.id(), testServer.id()));

      Server server = serverApi().getServer(testServer.dataCenterId(), testServer.id());
      assertEquals(server.properties().vmState(), Server.Status.RUNNING);
   }

   @Test(dependsOnMethods = "testStartServer")
   public void testRebootServer() {
      URI uri = serverApi().rebootServer(testServer.dataCenterId(), testServer.id());
      assertRequestCompleted(uri);
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

      assertRequestCompleted(attachedVolume);
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
      URI uri = serverApi().detachVolume(testServer.dataCenterId(), testServer.id(), attachedVolume.id());
      assertRequestCompleted(uri);
      assertVolumeDetached(testServer, attachedVolume.id());
   }

   @Test(dependsOnMethods = "testDetachVolume")
   public void testListCdroms() {
      List<Image> images = serverApi().listAttachedCdroms(testServer.dataCenterId(), testServer.id());
      assertTrue(images.isEmpty());
   }

   @Test(dependsOnMethods = "testListCdroms")
   public void testAttachCdrom() {

      List<Image> images = api.imageApi().getList(new DepthOptions().depth(5));

      Image testImage = null;

      for (Image image : images) {
         if (image.metadata().state() == State.AVAILABLE
                 && image.properties().isPublic()
                 && image.properties().imageType() == Image.Type.CDROM
                 && image.properties().location() == US_LAS
                 && image.properties().licenceType() == LicenceType.LINUX
                 && (testImage == null || testImage.properties().size() > image.properties().size())) {
            testImage = image;
         }
      }
      attachedCdrom = serverApi().attachCdrom(
              Server.Request.attachCdromBuilder()
              .dataCenterId(testServer.dataCenterId())
              .serverId(testServer.id())
              .imageId(testImage.id())
              .build()
      );
      assertRequestCompleted(attachedCdrom);
      assertEquals(attachedCdrom.properties().name(), testImage.properties().name());
      assertCdromAvailable(testServer, attachedCdrom.id());

      List<Image> attachedimages = serverApi().listAttachedCdroms(testServer.dataCenterId(), testServer.id());
      assertEquals(attachedimages.size(), 1);
   }

   @Test(dependsOnMethods = "testAttachCdrom")
   public void testRetrieveAttachedCdrom() {
      Image cdrom = serverApi().getCdrom(testServer.dataCenterId(), testServer.id(), attachedCdrom.id());
      assertEquals(cdrom.id(), attachedCdrom.id());
   }

   @Test(dependsOnMethods = "testRetrieveAttachedCdrom")
   public void testDetachCdrom() {
      URI uri = serverApi().detachCdrom(testServer.dataCenterId(), testServer.id(), attachedCdrom.id());
      assertRequestCompleted(uri);
      assertCdromRemoved(testServer, attachedCdrom.id());
   }

   @Test(dependsOnMethods = "testDetachCdrom")
   public void testDeleteServer() {
      URI uri = serverApi().deleteServer(testServer.dataCenterId(), testServer.id());
      assertRequestCompleted(uri);
      assertNodeRemoved(ServerRef.create(dataCenter.id(), testServer.id()));
   }

   private ServerApi serverApi() {
      return api.serverApi();
   }

   private void assertCdromAvailable(Server server, String cdRomId) {
      assertPredicate(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            Image cdrom = serverApi().getCdrom(params[0], params[1], params[2]);

            if (cdrom == null || cdrom.metadata() == null) {
               return false;
            }

            return cdrom.metadata().state() == State.AVAILABLE;
         }
      }, complexId(server.dataCenterId(), server.id(), cdRomId));
   }

   private void assertCdromRemoved(Server server, String cdRomId) {
      assertPredicate(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            return serverApi().getCdrom(params[0], params[1], params[2]) == null;
         }
      }, complexId(server.dataCenterId(), server.id(), cdRomId));
   }

   private void assertVolumeAttached(Server server, String volumeId) {
      assertPredicate(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            Volume volume = serverApi().getVolume(params[0], params[1], params[2]);

            if (volume == null || volume.metadata() == null) {
               return false;
            }

            return volume.metadata().state() == State.AVAILABLE;
         }
      }, complexId(server.dataCenterId(), server.id(), volumeId));
   }

   private void assertVolumeDetached(Server server, String volumeId) {
      assertPredicate(new Predicate<String>() {
         @Override
         public boolean apply(String args) {
            String[] params = args.split(",");
            return serverApi().getVolume(params[0], params[1], params[2]) == null;
         }
      }, complexId(server.dataCenterId(), server.id(), volumeId));
   }

}
