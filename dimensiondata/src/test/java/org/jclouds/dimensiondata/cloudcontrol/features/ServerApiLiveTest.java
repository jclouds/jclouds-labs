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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CloneServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ServerApiLiveTest", singleThreaded = true)
public class ServerApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {

   private String serverId;
   private String cloneImageId;
   private final String deployedServerName = ServerApiLiveTest.class.getSimpleName() + System.currentTimeMillis();

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testListServers() {
      List<Server> servers = api().listServers().concat().toList();
      assertNotNull(servers);
      boolean foundDeployedServer = false;
      for (Server s : servers) {
         assertNotNull(s);
         if (s.name().equals(deployedServerName)) {
            foundDeployedServer = true;
         }
      }
      assertTrue(foundDeployedServer, "Did not find deployed server " + deployedServerName);
   }

   @Test
   public void testDeployAndStartServer() {
      Boolean started = Boolean.TRUE;
      NetworkInfo networkInfo = NetworkInfo
            .create(NETWORK_DOMAIN_ID, NIC.builder().vlanId(VLAN_ID).build(), Lists.<NIC>newArrayList());
      List<Disk> disks = ImmutableList.of(Disk.builder().scsiId(0).speed("STANDARD").build());
      serverId = api().deployServer(deployedServerName, IMAGE_ID, started, networkInfo, "P$$ssWwrrdGoDd!", disks, null);
      assertNotNull(serverId);
      assertTrue(serverStartedPredicate.apply(serverId), "server did not start after timeout");
      assertTrue(serverNormalPredicate.apply(serverId), "server was not NORMAL after timeout");
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testReconfigureServer() {
      api().reconfigureServer(serverId, 4, CpuSpeed.HIGHPERFORMANCE.name(), 1);
      assertTrue(serverNormalPredicate.apply(serverId), "server was not NORMAL after timeout");
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testRebootServer() {
      api().rebootServer(serverId);
      assertTrue(serverNormalPredicate.apply(serverId), "server was not NORMAL after timeout");
      assertTrue(vmtoolsRunningPredicate.apply(serverId), "server vm tools not running after timeout");
   }

   @Test(dependsOnMethods = "testRebootServer")
   public void testPowerOffServer() {
      api().powerOffServer(serverId);
      assertTrue(serverStoppedPredicate.apply(serverId), "server did not power off after timeout");
   }

   @Test(dependsOnMethods = "testPowerOffServer")
   public void testStartServer() {
      api().startServer(serverId);
      assertTrue(serverStartedPredicate.apply(serverId), "server did not start after timeout");
      assertTrue(vmtoolsRunningPredicate.apply(serverId), "server vm tools not running after timeout");
   }

   @Test(dependsOnMethods = "testStartServer")
   public void testShutdownServer() {
      api().shutdownServer(serverId);
      assertTrue(serverStoppedPredicate.apply(serverId), "server did not shutdown after timeout");
   }

   @Test(dependsOnMethods = "testShutdownServer")
   public void testCloneServer() {
      CloneServerOptions options = CloneServerOptions.builder().clusterId("").description("")
            .guestOsCustomization(false).build();
      cloneImageId = api().cloneServer(serverId, "ServerApiLiveTest-" + System.currentTimeMillis(), options);
      assertNotNull(cloneImageId);
      assertTrue(serverNormalPredicate.apply(serverId), "server was not NORMAL after timeout");
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteServer() {
      if (serverId != null) {
         api().deleteServer(serverId);
         assertTrue(serverDeletedPredicate.apply(serverId), "server was not DELETED after timeout");
      }
   }

   private ServerApi api() {
      return api.getServerApi();
   }

}
