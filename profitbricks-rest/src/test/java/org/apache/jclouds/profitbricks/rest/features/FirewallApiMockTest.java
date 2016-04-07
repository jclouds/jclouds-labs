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

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "FirewallApiMockTest", singleThreaded = true)
public class FirewallApiMockTest extends BaseProfitBricksApiMockTest {
   
   @Test
   public void testGeRuleList() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/firewall/list.json"))
      );
      
      List<FirewallRule> list = firewallApi().list("datacenter-id", "server-id", "nic-id");
      
      assertNotNull(list);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).properties().name(), "apache-firewall");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules");
   }
   
   @Test
   public void testGeRuleListWithDepth() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/firewall/list.json"))
      );
      
      List<FirewallRule> list = firewallApi().list("datacenter-id", "server-id", "nic-id", new DepthOptions().depth(3));
      
      assertNotNull(list);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).properties().name(), "apache-firewall");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules?depth=3");
   }

   @Test
   public void testGetRuleListWith404() throws InterruptedException {
      server.enqueue(response404());
      List<FirewallRule> list = firewallApi().list("datacenter-id", "server-id", "nic-id");
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules");
   }
   
   @Test
   public void testGetRuleListWith404AndDepth() throws InterruptedException {
      server.enqueue(response404());
      List<FirewallRule> list = firewallApi().list("datacenter-id", "server-id", "nic-id", new DepthOptions().depth(1));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules?depth=1");
   }
    
   @Test
   public void testGetFirewallRule() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/firewall/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      FirewallRule firewallRule = firewallApi().get("datacenter-id", "server-id", "nic-id", "some-id");
      
      assertNotNull(firewallRule);
      assertEquals(firewallRule.properties().name(), "apache-firewall");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id");
   }
   
   public void testGetFirewallRuleWithDepth() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/firewall/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");
      
      server.enqueue(response);
      
      FirewallRule firewallRule = firewallApi().get("datacenter-id", "server-id", "nic-id", "some-id", new DepthOptions().depth(3));
      
      assertNotNull(firewallRule);
      assertEquals(firewallRule.properties().name(), "apache-firewall");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id?depth=3");
   }
   
   public void testGetFirewallRuleWith404() throws InterruptedException {
      server.enqueue(response404());

      FirewallRule firewallRule = firewallApi().get("datacenter-id", "server-id", "nic-id", "some-id");
      
      assertEquals(firewallRule, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id");
   }
   
   public void testGetFirewallRuleWith404AndDepth() throws InterruptedException {
      server.enqueue(response404());

      FirewallRule firewallRule = firewallApi().get("datacenter-id", "server-id", "nic-id", "some-id", new DepthOptions().depth(2));
      
      assertEquals(firewallRule, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id?depth=2");
   }
   
   @Test
   public void testCreateRule() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/firewall/get.json"))
      );
      
      FirewallRule firewallRule = firewallApi().create(
              FirewallRule.Request.creatingBuilder()
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .nicId("nic-id")
              .name("jclouds-firewall")
              .protocol(FirewallRule.Protocol.TCP)
              .portRangeStart(20)
              .portRangeEnd(80)
              .build());

      assertNotNull(firewallRule);
      assertNotNull(firewallRule.id());
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules", 
              "{\"properties\": {\"name\": \"jclouds-firewall\", \"protocol\": \"TCP\", \"portRangeStart\": 20, \"portRangeEnd\": 80}}"
      );
   }
   
   @Test
   public void testUpdateRule() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody(stringFromResource("/firewall/get.json"))
      );
      
      api.firewallApi().update(
              FirewallRule.Request.updatingBuilder()
              .id("some-id")
              .dataCenterId("datacenter-id")
              .serverId("server-id")
              .nicId("nic-id")
              .name("apache-firewall")
              .build());
            
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id", "{\"name\": \"apache-firewall\"}");
   }
   
   @Test
   public void testDeleteRule() throws InterruptedException {
      server.enqueue(
         new MockResponse().setBody("")
      );
      
      firewallApi().delete("datacenter-id", "server-id", "nic-id", "some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id");
   }
   
   @Test
   public void testDeleteRuleWith404() throws InterruptedException {
      server.enqueue(response404());

      firewallApi().delete("datacenter-id", "server-id", "nic-id", "some-id");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules/some-id");
   }
           
   private FirewallApi firewallApi() {
      return api.firewallApi();
   }
   
}
