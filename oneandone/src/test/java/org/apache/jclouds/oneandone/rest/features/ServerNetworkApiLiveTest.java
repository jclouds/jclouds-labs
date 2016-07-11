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
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerFirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.ServerIp;
import org.apache.jclouds.oneandone.rest.domain.ServerLoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.Types;
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
   private ServerFirewallPolicy currentPolicy;
   private ServerLoadBalancer currentBalancer;

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("jclouds network test");
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      deleteIp();
      //give time for operations to finish
      if (currentServer != null) {
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
   }

   private ServerApi serverApi() {

      return api.serverApi();
   }

   private void deleteIp() throws InterruptedException {
      assertNodeAvailable(currentServer);

      Server response = serverApi().deleteIp(currentServer.id(), currentIP.id());

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

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddIpFirewallPolicy")
   public void testListipFirewallPolicies() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<ServerFirewallPolicy> policies = serverApi().listIpFirewallPolicies(currentServer.id(), currentIP.id());
      currentPolicy = policies.get(0);
      assertNotNull(policies);
      assertFalse(policies.isEmpty());
      Assert.assertTrue(policies.size() > 0);
   }

   @Test(dependsOnMethods = "testListips")
   public void testAddIpFirewallPolicy() throws InterruptedException {
      assertNodeAvailable(currentServer);
      //TODO:: replace with live api data
      String firewallPolicyId = "34A7E423DA3253E6D38563ED06F1041F";

      Server response = serverApi().addFirewallPolicy(currentServer.id(), currentIP.id(), firewallPolicyId);
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testListipFirewallPolicies")
   public void testDeleteIpFirewallPolicy() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Server response = serverApi().deleteIpFirewallPolicy(currentServer.id(), currentIP.id());
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testAddIpLoadBalancer")
   public void testListipLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      List<ServerLoadBalancer> balancers = serverApi().listIpLoadBalancer(currentServer.id(), currentIP.id());
      assertNotNull(balancers);
      assertFalse(balancers.isEmpty());
   }

   @Test(dependsOnMethods = "testAddIp")
   public void testAddIpLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      //TODO:: replace with live api data
      String loadBalancerId = "13C3F75BA55AF28B8B2B4E508786F48B";

      Server response = serverApi().addIpLoadBalancer(currentServer.id(), currentIP.id(), loadBalancerId);

      List<ServerLoadBalancer> balancers = serverApi().listIpLoadBalancer(currentServer.id(), currentIP.id());
      currentBalancer = balancers.get(0);

      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testListipLoadBalancer")
   public void testDeleteIpLoadBalancer() throws InterruptedException {
      assertNodeAvailable(currentServer);
      Server response = serverApi().deleteIpLoadBalancer(currentServer.id(), currentIP.id(), currentBalancer.id());
      assertNotNull(response);
   }

}
