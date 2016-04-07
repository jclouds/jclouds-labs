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
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "FirewallApiLiveTest")
public class FirewallApiLiveTest extends BaseProfitBricksLiveTest {
   
   private DataCenter dataCenter;
   private Server testServer;
   private Nic testNic;
   private FirewallRule testFirewallRule;
  
   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
      assertDataCenterAvailable(dataCenter);
            
      testServer = api.serverApi().createServer(
         Server.Request.creatingBuilder()
            .dataCenterId(dataCenter.id())
            .name("jclouds-node")
            .cores(1)
            .ram(1024)
            .build());
      
      assertNodeAvailable(ServerRef.create(dataCenter.id(), testServer.id()));
            
      testNic = nicApi().create(
              Nic.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .serverId(testServer.id())
              .name("jclouds-nic")
              .lan(1)
              .build());

      assertNicAvailable(testNic);
   }
   
   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null)
         deleteDataCenter(dataCenter.id());
   }
     
   @Test
   public void testCreateFirewallRule() {
      assertNotNull(dataCenter);
            
      testFirewallRule = firewallApi().create(
              FirewallRule.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .serverId(testServer.id())
              .nicId(testNic.id())
              .name("jclouds-firewall")
              .protocol(FirewallRule.Protocol.TCP)
              .portRangeStart(1)
              .portRangeEnd(600)
              .build());

      assertNotNull(testFirewallRule);
      assertEquals(testFirewallRule.properties().name(), "jclouds-firewall");
      assertFirewallRuleAvailable(testFirewallRule);
   }
   

   @Test(dependsOnMethods = "testCreateFirewallRule")
   public void testGetFirewallRule() {
      FirewallRule firewallRule = firewallApi().get(dataCenter.id(), testServer.id(), testNic.id(), testFirewallRule.id());

      assertNotNull(firewallRule);
      assertEquals(firewallRule.id(), testFirewallRule.id());
   }

   @Test(dependsOnMethods = "testCreateFirewallRule")
   public void testListRules() {
      List<FirewallRule> firewalls = firewallApi().list(dataCenter.id(), testServer.id(), testNic.id());

      assertNotNull(firewalls);
      assertFalse(firewalls.isEmpty());
      assertTrue(Iterables.any(firewalls, new Predicate<FirewallRule>() {
         @Override public boolean apply(FirewallRule input) {
            return input.id().equals(testFirewallRule.id());
         }
      }));
   }
   
   @Test(dependsOnMethods = "testCreateFirewallRule")
   public void testUpdateFirewallRule() {
      assertDataCenterAvailable(dataCenter);
      
      firewallApi().update(FirewallRule.Request.updatingBuilder()
              .dataCenterId(testFirewallRule.dataCenterId())
              .serverId(testServer.id())
              .nicId(testNic.id())
              .id(testFirewallRule.id())
              .name("apache-firewall")
              .build());

      assertFirewallRuleAvailable(testFirewallRule);
      
      FirewallRule firewallRule = firewallApi().get(dataCenter.id(), testServer.id(), testNic.id(), testFirewallRule.id());
      
      assertEquals(firewallRule.properties().name(), "apache-firewall");
   }
   

   @Test(dependsOnMethods = "testUpdateFirewallRule")
   public void testDeleteFirewallRule() {
      firewallApi().delete(testFirewallRule.dataCenterId(), testServer.id(), testNic.id(), testFirewallRule.id());
      assertFirewallRuleRemoved(testFirewallRule);
   } 
   
   private void assertFirewallRuleAvailable(FirewallRule firewallRule) {
      assertPredicate(new Predicate<FirewallRule>() {
         @Override
         public boolean apply(FirewallRule testRule) {
            FirewallRule firewallRule = firewallApi().get(testRule.dataCenterId(), testRule.serverId(), testRule.nicId(), testRule.id());
            
            if (firewallRule == null || firewallRule.metadata() == null)
               return false;
            
            return firewallRule.metadata().state() == State.AVAILABLE;
         }
      }, firewallRule);
   }
   
   private void assertFirewallRuleRemoved(FirewallRule firewallRule) {
      assertPredicate(new Predicate<FirewallRule>() {
         @Override
         public boolean apply(FirewallRule testRule) {
            return firewallApi().get(testRule.dataCenterId(), testRule.serverId(), testRule.nicId(), testRule.id()) == null;
         }
      }, firewallRule);
   }
     
   private FirewallApi firewallApi() {
      return api.firewallApi();
   }   
           
   private NicApi nicApi() {
      return api.nicApi();
   }
   
   
}
