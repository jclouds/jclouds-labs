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

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.LoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadBalancerApiMockTest", singleThreaded = true)
public class LoadBalancerApiMockTest extends BaseOneAndOneApiMockTest {

   private LoadBalancerApi loadBalancerApi() {
      return api.loadBalancerApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/list.json"))
      );

      List<LoadBalancer> loadbalancers = loadBalancerApi().list();

      assertEquals(loadbalancers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<LoadBalancer> loadbalancers = loadBalancerApi().list();

      assertEquals(loadbalancers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<LoadBalancer> loadbalancers = loadBalancerApi().list(options);

      assertEquals(loadbalancers.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<LoadBalancer> loadbalancers = loadBalancerApi().list(options);

      assertEquals(loadbalancers.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      LoadBalancer result = loadBalancerApi().get("loadbalancerId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      LoadBalancer result = loadBalancerApi().get("loadbalancerId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      List<LoadBalancer.Rule.CreatePayload> rules = new ArrayList<LoadBalancer.Rule.CreatePayload>();
      LoadBalancer.Rule.CreatePayload rule = LoadBalancer.Rule.CreatePayload.builder()
              .portBalancer(80)
              .portServer(80)
              .protocol(Types.RuleProtocol.TCP)
              .source("source")
              .build();
      rules.add(rule);
      LoadBalancer response = loadBalancerApi().create(LoadBalancer.CreateLoadBalancer.builder()
              .rules(rules)
              .description("description")
              .name("name")
              .healthCheckInterval(1)
              .healthCheckPath("path")
              .healthCheckTest(Types.HealthCheckTestTypes.HTTP)
              .method(Types.LoadBalancerMethod.ROUND_ROBIN)
              .persistence(Boolean.TRUE)
              .persistenceTime(200)
              .rules(rules)
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/load_balancers", "{\"name\":\"name\","
              + "\"description\":\"description\",\"health_check_test\":\"HTTP\",\"health_check_interval\":1,"
              + "\"health_check_path\":\"path\",\"persistence\":true,\"persistence_time\":200,"
              + "\"method\":\"ROUND_ROBIN\",\"rules\":[{\"protocol\":\"TCP\",\"port_balancer\":80,\"port_server\":80,\"source\":\"source\"}]}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      LoadBalancer response = loadBalancerApi().update("loadbalancerId", LoadBalancer.UpdateLoadBalancer.builder()
              .description("description")
              .name("name")
              .healthCheckInterval(1)
              .healthCheckTest(Types.HealthCheckTestTypes.HTTP)
              .method(Types.LoadBalancerMethod.ROUND_ROBIN)
              .persistence(Boolean.TRUE)
              .persistenceTime(200)
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/load_balancers/loadbalancerId", "{\"name\":\"name\",\"description\":\"description\","
              + "\"health_check_test\":\"HTTP\",\"health_check_interval\":1,\"persistence\":true,\"persistence_time\":200,\"method\":\"ROUND_ROBIN\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      LoadBalancer response = loadBalancerApi().delete("loadbalancerId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/load_balancers/loadbalancerId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      LoadBalancer response = loadBalancerApi().delete("loadbalancerId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/load_balancers/loadbalancerId");
   }

   @Test
   public void testListServerIps() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/list.serverips.json"))
      );

      List<LoadBalancer.ServerIp> serverIps = loadBalancerApi().listServerIps("loadbalancerId");

      assertNotNull(serverIps);
      assertEquals(serverIps.size(), 1);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/server_ips");
   }

   @Test
   public void testListServerIps404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<LoadBalancer.ServerIp> serverIps = loadBalancerApi().listServerIps("loadbalancerId");

      assertEquals(serverIps.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/server_ips");
   }

   @Test
   public void testGetServerIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.serverip.json"))
      );
      LoadBalancer.ServerIp result = loadBalancerApi().getServerIp("loadbalancerId", "serverIpId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/server_ips/serverIpId");
   }

   @Test
   public void testGetServerIp404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      LoadBalancer.ServerIp result = loadBalancerApi().getServerIp("loadbalancerId", "serverIpId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/server_ips/serverIpId");
   }

   @Test
   public void testAssignServerIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );

      List<String> ips = new ArrayList<String>();
      ips.add("ip1");
      LoadBalancer response = loadBalancerApi().assignServerIp("loadbalancerId", LoadBalancer.ServerIp.CreateServerIp.create(ips));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/load_balancers/loadbalancerId/server_ips", "{\"server_ips\":[\"ip1\"]}");
   }

   @Test
   public void testUnassignServerIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      LoadBalancer response = loadBalancerApi().unassignServerIp("loadbalancerId", "serverIpId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/load_balancers/loadbalancerId/server_ips/serverIpId");
   }

   @Test
   public void testListRules() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/list.rules.json"))
      );

      List<LoadBalancer.Rule> rules = loadBalancerApi().listRules("loadbalancerId");

      assertNotNull(rules);
      assertEquals(rules.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/rules");
   }

   @Test
   public void testListRules404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<LoadBalancer.Rule> rules = loadBalancerApi().listRules("loadbalancerId");

      assertEquals(rules.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/rules");
   }

   @Test
   public void testGetRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.rule.json"))
      );
      LoadBalancer.Rule result = loadBalancerApi().getRule("loadbalancerId", "ruleId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/rules/ruleId");
   }

   @Test
   public void testGetRule404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      LoadBalancer.Rule result = loadBalancerApi().getRule("loadbalancerId", "ruleId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/load_balancers/loadbalancerId/rules/ruleId");
   }

   @Test
   public void testAddRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );

      List<LoadBalancer.Rule.CreatePayload> rules = new ArrayList<LoadBalancer.Rule.CreatePayload>();
      LoadBalancer.Rule.CreatePayload rule = LoadBalancer.Rule.CreatePayload.builder()
              .portBalancer(80)
              .portServer(80)
              .protocol(Types.RuleProtocol.TCP)
              .source("source")
              .build();
      rules.add(rule);
      LoadBalancer response = loadBalancerApi().addRules("loadbalancerId", LoadBalancer.Rule.AddRule.create(rules));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/load_balancers/loadbalancerId/rules", "{\"rules\":[{\"protocol\":\"TCP\",\"port_balancer\":80,\"port_server\":80,\"source\":\"source\"}]}");
   }

   @Test
   public void testRemoveRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/loadbalancer/get.json"))
      );
      LoadBalancer response = loadBalancerApi().removeRule("loadbalancerId", "ruleId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/load_balancers/loadbalancerId/rules/ruleId");
   }
}
