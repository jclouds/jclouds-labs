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
package org.jclouds.cloudsigma2.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.FirewallAction;
import org.jclouds.cloudsigma2.domain.FirewallDirection;
import org.jclouds.cloudsigma2.domain.FirewallIpProtocol;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.FirewallRule;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;

@Test(groups = "unit")
public class FirewallPolicyToJsonTest {

   private static final FirewallPolicyToJson FIREWALL_POLICY_TO_JSON = Guice
         .createInjector()
         .getInstance(FirewallPolicyToJson.class);

   private FirewallPolicy input;
   private JsonObject expected;

   @BeforeMethod
   public void setUp() throws Exception {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "test firewall policy");
      meta.put("test_key_1", "test_value_1");
      meta.put("test_key_2", "test_value_2");

      input = new FirewallPolicy.Builder()
            .meta(meta)
            .name("My awesome policy")
            .resourceUri(new URI("/api/2.0/fwpolicies/cf8479b4-c98b-46c8-ab9c-108bb00c8218/"))
            .rules(ImmutableList.of(
                  new FirewallRule.Builder()
                        .action(FirewallAction.DROP)
                        .comment("Drop traffic from the VM to IP address 23.0.0.0/32")
                        .direction(FirewallDirection.OUT)
                        .destinationIp("23.0.0.0/32")
                        .build()
                  , new FirewallRule.Builder()
                  .action(FirewallAction.ACCEPT)
                  .comment("Allow SSH traffic to the VM from our office in Dubai")
                  .direction(FirewallDirection.IN)
                  .destinationPort("22")
                  .ipProtocol(FirewallIpProtocol.TCP)
                  .sourceIp("172.66.32.0/24")
                  .build()
                  , new FirewallRule.Builder()
                  .action(FirewallAction.DROP)
                  .comment("Drop all other SSH traffic to the VM")
                  .direction(FirewallDirection.IN)
                  .destinationPort("22")
                  .ipProtocol(FirewallIpProtocol.TCP)
                  .build()
                  , new FirewallRule.Builder()
                  .action(FirewallAction.DROP)
                  .comment("Drop all UDP traffic to the VM, not originating from 172.66.32.55")
                  .direction(FirewallDirection.IN)
                  .ipProtocol(FirewallIpProtocol.UDP)
                  .sourceIp("!172.66.32.55/32")
                  .build()
                  , new FirewallRule.Builder()
                  .action(FirewallAction.DROP)
                  .comment("Drop any traffic, to the VM with destination port not between 1-1024")
                  .direction(FirewallDirection.IN)
                  .destinationPort("!1:1024")
                  .ipProtocol(FirewallIpProtocol.TCP)
                  .build()
            ))
            .build();

      expected = new JsonObject();

      expected.addProperty("name", "My awesome policy");

      JsonObject metaObject = new JsonObject();
      metaObject.addProperty("description", "test firewall policy");
      metaObject.addProperty("test_key_1", "test_value_1");
      metaObject.addProperty("test_key_2", "test_value_2");

      expected.add("meta", metaObject);

      JsonObject rule1Object = new JsonObject();
      rule1Object.addProperty("action", "drop");
      rule1Object.addProperty("comment", "Drop traffic from the VM to IP address 23.0.0.0/32");
      rule1Object.addProperty("direction", "out");
      rule1Object.addProperty("dst_ip", "23.0.0.0/32");
      JsonObject rule2Object = new JsonObject();
      rule2Object.addProperty("action", "accept");
      rule2Object.addProperty("comment", "Allow SSH traffic to the VM from our office in Dubai");
      rule2Object.addProperty("direction", "in");
      rule2Object.addProperty("dst_port", "22");
      rule2Object.addProperty("ip_proto", "tcp");
      rule2Object.addProperty("src_ip", "172.66.32.0/24");
      JsonObject rule3Object = new JsonObject();
      rule3Object.addProperty("action", "drop");
      rule3Object.addProperty("comment", "Drop all other SSH traffic to the VM");
      rule3Object.addProperty("direction", "in");
      rule3Object.addProperty("dst_port", "22");
      rule3Object.addProperty("ip_proto", "tcp");
      JsonObject rule4Object = new JsonObject();
      rule4Object.addProperty("action", "drop");
      rule4Object.addProperty("comment", "Drop all UDP traffic to the VM, not originating from 172.66.32.55");
      rule4Object.addProperty("direction", "in");
      rule4Object.addProperty("ip_proto", "udp");
      rule4Object.addProperty("src_ip", "!172.66.32.55/32");
      JsonObject rule5Object = new JsonObject();
      rule5Object.addProperty("action", "drop");
      rule5Object.addProperty("comment", "Drop any traffic, to the VM with destination port not between 1-1024");
      rule5Object.addProperty("direction", "in");
      rule5Object.addProperty("dst_port", "!1:1024");
      rule5Object.addProperty("ip_proto", "tcp");

      JsonArray rulesArray = new JsonArray();
      rulesArray.add(rule1Object);
      rulesArray.add(rule2Object);
      rulesArray.add(rule3Object);
      rulesArray.add(rule4Object);
      rulesArray.add(rule5Object);

      expected.add("rules", rulesArray);
   }

   public void test() {
      Assert.assertEquals(FIREWALL_POLICY_TO_JSON.apply(input), expected);
   }
}
