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
import org.apache.jclouds.oneandone.rest.domain.LoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerFirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.ServerIp;
import org.apache.jclouds.oneandone.rest.domain.ServerLoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.ids.ServerIpRef;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ServerNetworkApiLiveTest extends BaseOneAndOneLiveTest {

   private Server currentServer;
   private ServerIp currentIP;
   private ServerLoadBalancer currentServerBalancer;
   private LoadBalancer currentLoadBalancer;
   private FirewallPolicy currentFirewallPolicy;

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("jclouds network test");
      List<LoadBalancer.Rule.CreatePayload> rules = new ArrayList<LoadBalancer.Rule.CreatePayload>();
      LoadBalancer.Rule.CreatePayload rule = LoadBalancer.Rule.CreatePayload.builder()
            .portBalancer(4567)
            .portServer(4567)
            .protocol(Types.RuleProtocol.TCP)
            .source("0.0.0.0")
            .build();
      rules.add(rule);
      currentLoadBalancer = loadBalancerApi().create(LoadBalancer.CreateLoadBalancer.builder()
            .name("jclouds loadbalancer")
            .rules(rules)
            .description("description")
            .healthCheckInterval(1)
            .healthCheckPath("path")
            .healthCheckTest(Types.HealthCheckTestTypes.TCP)
            .method(Types.LoadBalancerMethod.ROUND_ROBIN)
            .persistence(Boolean.TRUE)
            .persistenceTime(200)
            .build());

      List<FirewallPolicy.Rule.CreatePayload> fwrules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();
      FirewallPolicy.Rule.CreatePayload fwrule = FirewallPolicy.Rule.CreatePayload.builder()
            .port("80")
            .action(Types.FirewallRuleAction.ALLOW)
            .protocol(Types.RuleProtocol.TCP)
            .source("0.0.0.0")
            .build();
      fwrules.add(fwrule);
      currentFirewallPolicy = firewallPolicyApi().create(FirewallPolicy.CreateFirewallPolicy.create("jclouds firewall policy", "desc", fwrules));
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      deleteIp();
      if (currentServer != null) {
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
         assertNodeRemoved(currentServer);
         loadBalancerApi().delete(currentLoadBalancer.id());
         firewallPolicyApi().delete(currentFirewallPolicy.id());
      }
   }

   private ServerApi serverApi() {

      return api.serverApi();
   }

   private LoadBalancerApi loadBalancerApi() {

      return api.loadBalancerApi();
   }

   private FirewallPolicyApi firewallPolicyApi() {
      return api.firewallPolicyApi();
   }

   private void deleteIp() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().deleteIp(currentServer.id(), currentIP.id());
      assertNodeAvailable(currentServer);
      assertServerIPRemoved(ServerIpRef.create(currentServer.id(), currentIP.id()));
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddIp")
   public void testListips() throws InterruptedException {
      List<ServerIp> ips = serverApi().listIps(currentServer.id());
      currentIP = ips.get(0);
      assertNotNull(ips);
      Assert.assertTrue(ips.size() > 0);
   }

   @Test
   public void testAddIp() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().addIp(currentServer.id(), Types.IPType.IPV4);
      assertNodeAvailable(currentServer);
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddIpFirewallPolicy")
   public void testListipFirewallPolicies() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<ServerFirewallPolicy> policies = serverApi().listIpFirewallPolicies(currentServer.id(), currentIP.id());
      assertNotNull(policies);
      assertFalse(policies.isEmpty());
      Assert.assertTrue(policies.size() > 0);
   }

   @Test(dependsOnMethods = "testListips")
   public void testAddIpFirewallPolicy() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().addFirewallPolicy(currentServer.id(), currentIP.id(), currentFirewallPolicy.id());
      assertNodeAvailable(currentServer);
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddIpLoadBalancer")
   public void testListipLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<ServerLoadBalancer> balancers = serverApi().listIpLoadBalancer(currentServer.id(), currentIP.id());
      assertNotNull(balancers);
      assertFalse(balancers.isEmpty());
   }

   @Test(dependsOnMethods = "testListipFirewallPolicies")
   public void testAddIpLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().addIpLoadBalancer(currentServer.id(), currentIP.id(), currentLoadBalancer.id());

      List<ServerLoadBalancer> balancers = serverApi().listIpLoadBalancer(currentServer.id(), currentIP.id());
      currentServerBalancer = balancers.get(0);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testListipLoadBalancer")
   public void testDeleteIpLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Server response = serverApi().deleteIpLoadBalancer(currentServer.id(), currentIP.id(), currentServerBalancer.id());
      assertNotNull(response);
   }

}
