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
import org.apache.jclouds.oneandone.rest.domain.FirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "FirewallPolicyApiMockTest", singleThreaded = true)
public class FirewallPolicyApiMockTest extends BaseOneAndOneApiMockTest {

   private FirewallPolicyApi firewallpolicyApi() {
      return api.firewallPolicyApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/list.json"))
      );

      List<FirewallPolicy> policies = firewallpolicyApi().list();

      assertNotNull(policies);
      assertEquals(policies.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<FirewallPolicy> firewallpolicies = firewallpolicyApi().list();

      assertEquals(firewallpolicies.size(), 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<FirewallPolicy> firewallpolicies = firewallpolicyApi().list(options);

      assertNotNull(firewallpolicies);
      assertEquals(firewallpolicies.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<FirewallPolicy> firewallpolicies = firewallpolicyApi().list(options);

      assertEquals(firewallpolicies.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies?q=test");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );
      FirewallPolicy result = firewallpolicyApi().get("firewallpolicyId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId");
   }

   @Test
   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      FirewallPolicy result = firewallpolicyApi().get("firewallpolicyId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );
      List<FirewallPolicy.Rule.CreatePayload> rules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();
      FirewallPolicy.Rule.CreatePayload rule = FirewallPolicy.Rule.CreatePayload.builder()
              .portFrom(80)
              .portTo(80)
              .protocol(Types.RuleProtocol.TCP)
              .source("source")
              .build();
      rules.add(rule);
      FirewallPolicy response = firewallpolicyApi().create(FirewallPolicy.CreateFirewallPolicy.create("name", "desc", rules));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/firewall_policies", "{\"name\":\"name\",\"description\":\"desc\",\"rules\":[{\"protocol\":\"TCP\",\"port_from\":80,\"port_to\":80,\"source\":\"source\"}]}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );
      FirewallPolicy response = firewallpolicyApi().update("firewallpolicyId", FirewallPolicy.UpdateFirewallPolicy.create("name", "desc"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/firewall_policies/firewallpolicyId", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );
      FirewallPolicy response = firewallpolicyApi().delete("firewallpolicyId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/firewall_policies/firewallpolicyId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      FirewallPolicy response = firewallpolicyApi().delete("firewallpolicyId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/firewall_policies/firewallpolicyId");
   }

   @Test
   public void testListServerIps() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/list.serverips.json"))
      );

      List<FirewallPolicy.ServerIp> serverIps = firewallpolicyApi().listServerIps("firewallpolicyId");

      assertNotNull(serverIps);
      assertEquals(serverIps.size(), 1);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/server_ips");
   }

   @Test
   public void testListServerIps404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );

      List<FirewallPolicy.ServerIp> serverIps = firewallpolicyApi().listServerIps("firewallpolicyId");

      assertEquals(serverIps.size(), 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/server_ips");
   }

   @Test
   public void testGetServerIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.serverip.json"))
      );
      FirewallPolicy.ServerIp result = firewallpolicyApi().getServerIp("firewallpolicyId", "serverIpId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/server_ips/serverIpId");
   }

   @Test
   public void testGetServerIp404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      FirewallPolicy.ServerIp result = firewallpolicyApi().getServerIp("firewallpolicyId", "serverIpId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/server_ips/serverIpId");
   }

   @Test
   public void testAssignServerIp() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );

      List<String> ips = new ArrayList<String>();
      ips.add("ip1");
      FirewallPolicy response = firewallpolicyApi().assignServerIp("firewallpolicyId", FirewallPolicy.ServerIp.CreateServerIp.create(ips));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/firewall_policies/firewallpolicyId/server_ips", "{\"server_ips\":[\"ip1\"]}");
   }

   @Test
   public void testListRules() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/list.rules.json"))
      );

      List<FirewallPolicy.Rule> rules = firewallpolicyApi().listRules("firewallpolicyId");

      assertNotNull(rules);
      assertEquals(rules.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/rules");
   }

   @Test
   public void testListRules404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );

      List<FirewallPolicy.Rule> rules = firewallpolicyApi().listRules("firewallpolicyId");

      assertEquals(rules.size(), 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/rules");
   }

   @Test
   public void testGetRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.rule.json"))
      );
      FirewallPolicy.Rule result = firewallpolicyApi().getRule("firewallpolicyId", "ruleId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/rules/ruleId");
   }

   @Test
   public void testGetRule404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      FirewallPolicy.Rule result = firewallpolicyApi().getRule("firewallpolicyId", "ruleId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/firewall_policies/firewallpolicyId/rules/ruleId");
   }

   @Test
   public void testAddRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );

      List<FirewallPolicy.Rule.CreatePayload> rules = new ArrayList<FirewallPolicy.Rule.CreatePayload>();
      FirewallPolicy.Rule.CreatePayload rule = FirewallPolicy.Rule.CreatePayload.builder()
              .portFrom(80)
              .portTo(80)
              .protocol(Types.RuleProtocol.TCP)
              .source("source")
              .build();
      rules.add(rule);
      FirewallPolicy response = firewallpolicyApi().addRules("firewallpolicyId", FirewallPolicy.Rule.AddRule.create(rules));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/firewall_policies/firewallpolicyId/rules", "{\"rules\":[{\"protocol\":\"TCP\",\"port_from\":80,\"port_to\":80,\"source\":\"source\"}]}");
   }

   @Test
   public void testRemoveRule() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/firewallpolicies/get.json"))
      );
      FirewallPolicy response = firewallpolicyApi().removeRule("firewallpolicyId", "ruleId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/firewall_policies/firewallpolicyId/rules/ruleId");
   }
}
