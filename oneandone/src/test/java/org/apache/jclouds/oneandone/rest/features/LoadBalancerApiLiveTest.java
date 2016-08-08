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
import org.apache.jclouds.oneandone.rest.domain.LoadBalancer;
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

@Test(groups = "live", testName = "LoadBalancerApiLiveTest")
public class LoadBalancerApiLiveTest extends BaseOneAndOneLiveTest {

   private LoadBalancer currentLoadBalancer;
   private Server currentServer;
   private List<LoadBalancer> loadBalancers;

   private LoadBalancerApi loadBalancerApi() {
      return api.loadBalancerApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("loadbalancers jclouds server");
      assertNodeAvailable(currentServer);

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
   }

   @Test
   public void testList() {
      loadBalancers = loadBalancerApi().list();

      assertNotNull(loadBalancers);
      assertFalse(loadBalancers.isEmpty());
      Assert.assertTrue(loadBalancers.size() > 0);
   }

   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<LoadBalancer> resultWithQuery = loadBalancerApi().list(options);

      assertNotNull(resultWithQuery);
      assertFalse(resultWithQuery.isEmpty());
      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   public void testGet() {
      LoadBalancer result = loadBalancerApi().get(currentLoadBalancer.id());

      assertNotNull(result);
      assertEquals(result.id(), currentLoadBalancer.id());
   }

   @Test(dependsOnMethods = "testGet")
   public void testUpdate() throws InterruptedException {
      String updatedName = "UpdatedjcloudsPolicy";

      LoadBalancer updateResult = loadBalancerApi().update(currentLoadBalancer.id(), LoadBalancer.UpdateLoadBalancer.builder()
              .name(updatedName)
              .description("description")
              .healthCheckInterval(40)
              .healthCheckTest(Types.HealthCheckTestTypes.TCP)
              .method(Types.LoadBalancerMethod.ROUND_ROBIN)
              .persistence(Boolean.TRUE)
              .persistenceTime(1200)
              .build());

      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);

   }

   @Test(dependsOnMethods = "testGet")
   public void testAssignServerIp() throws InterruptedException {
      assertNodeAvailable(currentServer);
      currentServer = updateServerStatus(currentServer);
      List<String> ips = new ArrayList<String>();
      ips.add(currentServer.ips().get(0).id());
      LoadBalancer.ServerIp.CreateServerIp toAdd = LoadBalancer.ServerIp.CreateServerIp.create(ips);

      LoadBalancer updateResult = loadBalancerApi().assignServerIp(currentLoadBalancer.id(), toAdd);
      assertEquals(updateResult.serverIps().get(0).id(), currentServer.ips().get(0).id());
   }

   @Test(dependsOnMethods = "testAssignServerIp")
   public void testListServerIps() {
      List<LoadBalancer.ServerIp> servers = loadBalancerApi().listServerIps(currentLoadBalancer.id());

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testListServerIps")
   public void testServerIpGet() {
      LoadBalancer.ServerIp result = loadBalancerApi().getServerIp(currentLoadBalancer.id(), currentServer.ips().get(0).id());

      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testServerIpGet")
   public void testUnassignServer() {
      LoadBalancer result = loadBalancerApi().unassignServerIp(currentLoadBalancer.id(), currentServer.ips().get(0).id());

      assertNotNull(result);
      assertEquals(result.serverIps().size(), 0);
   }

   @Test(dependsOnMethods = "testServerIpGet")
   public void testAddRules() throws InterruptedException {
      assertNodeAvailable(currentServer);
      currentServer = updateServerStatus(currentServer);
      List<LoadBalancer.Rule.CreatePayload> rules = new ArrayList<LoadBalancer.Rule.CreatePayload>();
      LoadBalancer.Rule.CreatePayload rule = LoadBalancer.Rule.CreatePayload.builder()
              .portBalancer(4668)
              .portServer(4765)
              .protocol(Types.RuleProtocol.TCP)
              .source("0.0.0.0")
              .build();
      rules.add(rule);
      LoadBalancer response = loadBalancerApi().addRules(currentLoadBalancer.id(), LoadBalancer.Rule.AddRule.create(rules));

      LoadBalancer.Rule ruleFromApi = loadBalancerApi().getRule(currentLoadBalancer.id(), currentLoadBalancer.rules().get(0).id());
      assertNotNull(response);
      assertNotNull(ruleFromApi);

   }

   @Test(dependsOnMethods = "testAddRules")
   public void testListRules() {
      List<LoadBalancer.Rule> servers = loadBalancerApi().listRules(currentLoadBalancer.id());

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
      Assert.assertTrue(servers.size() > 0);
   }

   @Test(dependsOnMethods = "testListRules")
   public void testGetRule() {
      LoadBalancer.Rule result = loadBalancerApi().getRule(currentLoadBalancer.id(), currentLoadBalancer.rules().get(0).id());

      assertNotNull(result);
   }

   @Test(dependsOnMethods = "testGetRule")
   public void testRemoveRule() {
      LoadBalancer result = loadBalancerApi().removeRule(currentLoadBalancer.id(), currentLoadBalancer.rules().get(0).id());

      assertNodeAvailable(currentServer);
      LoadBalancer.Rule ruleFromApi = loadBalancerApi().getRule(currentLoadBalancer.id(), currentLoadBalancer.rules().get(0).id());

      assertNotNull(result);
      //the API returns an object with null values
      assertEquals(ruleFromApi, LoadBalancer.Rule.create(null, null, null, null, null));
      assertEquals(result.id(), currentLoadBalancer.id());
   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      if (currentLoadBalancer != null) {
         loadBalancerApi().delete(currentLoadBalancer.id());
      }
      assertNodeAvailable(currentServer);
      deleteServer(currentServer.id());
   }
}
