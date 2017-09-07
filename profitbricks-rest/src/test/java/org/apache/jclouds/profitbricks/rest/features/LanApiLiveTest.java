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
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.CpuFamily;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.IpBlock;
import org.apache.jclouds.profitbricks.rest.domain.Lan;
import org.apache.jclouds.profitbricks.rest.domain.Lan.IpFailover;
import org.apache.jclouds.profitbricks.rest.domain.Location;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "LanApiLiveTest")
public class LanApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Lan testLan;
   private Server testServer;
   private IpBlock testIpBlock;
   private Nic testNic;

   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null) {
         deleteDataCenter(dataCenter.id());
         api.ipBlockApi().delete(testIpBlock.id());
      }
   }

   @Test
   public void testCreateLan() {
      assertNotNull(dataCenter);

      testLan = lanApi().create(
              Lan.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-lan")
              .build());

      assertRequestCompleted(testLan);
      assertNotNull(testLan);
      assertEquals(testLan.properties().name(), "jclouds-lan");
   }

   @Test(dependsOnMethods = "testCreateLan")
   public void testGetLan() {
      Lan lan = lanApi().get(dataCenter.id(), testLan.id());

      assertNotNull(lan);
      assertEquals(lan.id(), testLan.id());
   }

   @Test(dependsOnMethods = "testCreateLan")
   public void testList() {
      List<Lan> lans = lanApi().list(dataCenter.id());

      assertNotNull(lans);
      assertFalse(lans.isEmpty());
      assertTrue(Iterables.any(lans, new Predicate<Lan>() {
         @Override
         public boolean apply(Lan input) {
            return input.id().equals(testLan.id());
         }
      }));
   }

   @Test(dependsOnMethods = "testCreateLan")
   public void testUpdateLan() {
      assertDataCenterAvailable(dataCenter);

      //reserve an ip for the failover group
      testIpBlock = api.ipBlockApi().create(IpBlock.Request.creatingBuilder()
              .properties(IpBlock.PropertiesRequest.create("jclouds ipBlock", Location.US_LAS.getId(), 1)).build());
      assertRequestCompleted(testIpBlock);

      //create the failover lan
      Lan failoverLan = lanApi().create(
              Lan.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("failover-lan")
              .build());
      //creating the server
      testServer = api.serverApi().createServer(
              Server.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-node")
              .cpuFamily(CpuFamily.INTEL_XEON)
              .cores(1)
              .ram(1024)
              .build());

      assertRequestCompleted(testServer);

      List<String> ips = new ArrayList<String>();
      ips.add(testIpBlock.properties().ips().get(0));

      //creating the NIC
      testNic = api.nicApi().create(
              Nic.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .serverId(testServer.id())
              .name("failover-nic")
              .ips(ips)
              .lan(Integer.parseInt(failoverLan.id()))
              .build());

      assertRequestCompleted(testNic);

      List<IpFailover> failovers = new ArrayList<IpFailover>();
      failovers.add(IpFailover.create(testIpBlock.properties().ips().get(0), testNic.id()));

      //update lan with failover group
      Lan updated = api.lanApi().update(
              Lan.Request.updatingBuilder()
              .dataCenterId(failoverLan.dataCenterId())
              .id(failoverLan.id())
              .isPublic(true)
              .ipFailover(failovers)
              .build());

      assertRequestCompleted(updated);
      assertLanAvailable(updated);

      Lan lan = lanApi().get(dataCenter.id(), failoverLan.id());

      assertTrue(lan.properties().isPublic());
      assertNotNull(lan.properties().ipFailover());
   }

   @Test(dependsOnMethods = "testUpdateLan")
   public void testDeleteLan() {
      URI uri = lanApi().delete(testLan.dataCenterId(), testLan.id());
      assertRequestCompleted(uri);
      assertLanRemoved(testLan);
   }

   private void assertLanAvailable(Lan lan) {
      assertPredicate(new Predicate<Lan>() {
         @Override
         public boolean apply(Lan testLan) {
            Lan lan = lanApi().get(testLan.dataCenterId(), testLan.id());

            if (lan == null || lan.metadata() == null) {
               return false;
            }

            return lan.metadata().state() == State.AVAILABLE;
         }
      }, lan);
   }

   private void assertLanRemoved(Lan lan) {
      assertPredicate(new Predicate<Lan>() {
         @Override
         public boolean apply(Lan testLan) {
            return lanApi().get(testLan.dataCenterId(), testLan.id()) == null;
         }
      }, lan);
   }

   private LanApi lanApi() {
      return api.lanApi();
   }

}
