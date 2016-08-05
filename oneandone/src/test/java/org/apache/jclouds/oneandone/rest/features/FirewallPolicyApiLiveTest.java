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
import org.apache.jclouds.oneandone.rest.domain.FirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "FirewallPolicyApiLiveTest")
public class FirewallPolicyApiLiveTest extends BaseOneAndOneLiveTest {

   private FirewallPolicy currentFirewallPolicy;
   private Server currentServer;
   private List<FirewallPolicy> firewallPolicies;

   private FirewallPolicyApi firewallPolicyApi() {
      return api.firewallPolicyApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("firewallpolicies jclouds server");
      assertNodeAvailable(currentServer);

      List<FirewallPolicy.Rule.CreatePayload> rules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();
      FirewallPolicy.Rule.CreatePayload rule = FirewallPolicy.Rule.CreatePayload.builder()
              .portFrom(80)
              .portTo(80)
              .protocol(Types.RuleProtocol.TCP)
              .source("0.0.0.0")
              .build();
      rules.add(rule);
      currentFirewallPolicy = firewallPolicyApi().create(FirewallPolicy.CreateFirewallPolicy.create("jclouds firewall policy", "desc", rules));
   }

   @Test
   public void testList() {
      firewallPolicies = firewallPolicyApi().list();

      assertNotNull(firewallPolicies);
      assertFalse(firewallPolicies.isEmpty());
      Assert.assertTrue(firewallPolicies.size() > 0);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<FirewallPolicy> imageWithQuery = firewallPolicyApi().list(options);

      assertNotNull(imageWithQuery);
      assertFalse(imageWithQuery.isEmpty());
      Assert.assertTrue(imageWithQuery.size() > 0);
   }

   @Test
   public void testGet() {
      FirewallPolicy result = firewallPolicyApi().get(currentFirewallPolicy.id());

      assertNotNull(result);
      assertEquals(result.id(), currentFirewallPolicy.id());
   }

   @Test
   public void testUpdate() {
      String updatedName = "UpdatedjcloudsPolicy";

      FirewallPolicy updateResult = firewallPolicyApi().update(currentFirewallPolicy.id(), FirewallPolicy.UpdateFirewallPolicy.create(updatedName, null));

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testAssignServerIp() {
      assertNodeAvailable(currentServer);
      currentServer = updateServerStatus(currentServer);
      List<String> ips = new ArrayList<String>();
      ips.add(currentServer.ips().get(0).id());
      FirewallPolicy.ServerIp.CreateServerIp toAdd = FirewallPolicy.ServerIp.CreateServerIp.create(ips);

      FirewallPolicy updateResult = firewallPolicyApi().assignServerIp(currentFirewallPolicy.id(), toAdd);
      assertEquals(updateResult.serverIps().get(0).id(), currentServer.ips().get(0).id());

      assertNotNull(updateResult);

   }

   @Test(dependsOnMethods = "testAssignServerIp")
   public void testListServerIps() {
      List<FirewallPolicy.ServerIp> servers = firewallPolicyApi().listServerIps(currentFirewallPolicy.id());

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testListServerIps")
   public void testServerIpGet() {
      FirewallPolicy.ServerIp result = firewallPolicyApi().getServerIp(currentFirewallPolicy.id(), currentServer.ips().get(0).id());

      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testServerIpGet")
   public void testUnassignServer() {
      FirewallPolicy result = firewallPolicyApi().unassignServerIp(currentFirewallPolicy.id(), currentServer.ips().get(0).id());

      assertNotNull(result);
      assertEquals(result.serverIps().size(), 0);
   }

   @Test(dependsOnMethods = "testUnassignServer")
   public void testAddRules() {
      assertNodeAvailable(currentServer);
      currentServer = updateServerStatus(currentServer);
      List<FirewallPolicy.Rule.CreatePayload> rules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();
      FirewallPolicy.Rule.CreatePayload rule = FirewallPolicy.Rule.CreatePayload.builder()
              .portFrom(4567)
              .portTo(4567)
              .protocol(Types.RuleProtocol.TCP)
              .source("0.0.0.0")
              .build();
      rules.add(rule);
      FirewallPolicy response = firewallPolicyApi().addRules(currentFirewallPolicy.id(), FirewallPolicy.Rule.AddRule.create(rules));
      FirewallPolicy.Rule ruleFromApi = firewallPolicyApi().getRule(currentFirewallPolicy.id(), currentFirewallPolicy.rules().get(0).id());

      assertNotNull(response);
      assertNotNull(ruleFromApi);

   }

   @Test(dependsOnMethods = "testAddRules")
   public void testListRules() {
      List<FirewallPolicy.Rule> servers = firewallPolicyApi().listRules(currentFirewallPolicy.id());

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testAddRules")
   public void testGetRule() {
      FirewallPolicy.Rule result = firewallPolicyApi().getRule(currentFirewallPolicy.id(), currentFirewallPolicy.rules().get(0).id());

      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testGetRule")
   public void testRemoveRule() {
      FirewallPolicy result = firewallPolicyApi().removeRule(currentFirewallPolicy.id(), currentFirewallPolicy.rules().get(0).id());
      FirewallPolicy.Rule ruleFromApi = firewallPolicyApi().getRule(currentFirewallPolicy.id(), currentFirewallPolicy.rules().get(0).id());

      assertNotNull(result);
      assertEquals(ruleFromApi, null);
      assertEquals(result.id(), currentFirewallPolicy.id());
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      assertNodeAvailable(currentServer);
      firewallPolicyApi().delete(currentFirewallPolicy.id());
      assertNodeAvailable(currentServer);
      deleteServer(currentServer.id());
   }
}
