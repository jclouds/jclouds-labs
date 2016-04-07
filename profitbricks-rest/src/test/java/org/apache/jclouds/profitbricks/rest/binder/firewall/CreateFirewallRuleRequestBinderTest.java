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
package org.apache.jclouds.profitbricks.rest.binder.firewall;

import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BinderTestBase;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CreateFirewallRuleRequestBinderTest")
public class CreateFirewallRuleRequestBinderTest extends BinderTestBase {
   
   @Test
   public void testUpdatePayload() {
            
      CreateFirewallRuleRequestBinder binder = injector.getInstance(CreateFirewallRuleRequestBinder.class);
      
      FirewallRule.Request.CreatePayload payload = FirewallRule.Request.creatingBuilder()
         .dataCenterId("datacenter-id")
         .serverId("server-id")
         .nicId("nic-id")
         .name("jclouds-firewall")
         .protocol(FirewallRule.Protocol.TCP)
         .portRangeStart(1)
         .portRangeEnd(600)
         .build();
              
      String actual = binder.createPayload(payload);

      HttpRequest request = binder.createRequest(
         HttpRequest.builder().method("POST").endpoint("http://test.com").build(), 
         actual
      );
      
      assertEquals(request.getEndpoint().getPath(), "/rest/v2/datacenters/datacenter-id/servers/server-id/nics/nic-id/firewallrules");
      assertNotNull(actual, "Binder returned null payload");
      
      Json json = injector.getInstance(Json.class);
      
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put("name", "jclouds-firewall");
      properties.put("protocol", FirewallRule.Protocol.TCP);
      properties.put("portRangeStart", 1);
      properties.put("portRangeEnd", 600);
      
      HashMap<String, Object> expectedPayload = new HashMap<String, Object>();
      expectedPayload.put("properties", properties);
      
      assertEquals(actual, json.toJson(expectedPayload));
   }

}
