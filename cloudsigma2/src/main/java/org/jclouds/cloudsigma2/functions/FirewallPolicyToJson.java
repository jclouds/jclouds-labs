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

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.FirewallRule;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Singleton;

@Singleton
public class FirewallPolicyToJson implements Function<FirewallPolicy, JsonObject> {
   @Override
   public JsonObject apply(@Nullable FirewallPolicy input) {
      JsonObject firewallObject = new JsonObject();

      if (input.getName() != null) {
         firewallObject.addProperty("name", input.getName());
      }

      if (input.getMeta() != null) {
         firewallObject.add("meta", new JsonParser().parse(new Gson().toJson(input.getMeta())));
      }

      if (input.getRules() != null) {
         JsonArray rulesArray = new JsonArray();

         for (FirewallRule rule : input.getRules()) {
            JsonObject ruleObject = new JsonObject();

            if (rule.getAction() != null) {
               ruleObject.addProperty("action", rule.getAction().value());
            }

            if (rule.getComment() != null) {
               ruleObject.addProperty("comment", rule.getComment());
            }

            if (rule.getDirection() != null) {
               ruleObject.addProperty("direction", rule.getDirection().value());
            }

            if (rule.getDestinationIp() != null) {
               ruleObject.addProperty("dst_ip", rule.getDestinationIp());
            }

            if (rule.getDestinationPort() != null) {
               ruleObject.addProperty("dst_port", rule.getDestinationPort());
            }

            if (rule.getIpProtocol() != null) {
               ruleObject.addProperty("ip_proto", rule.getIpProtocol().toString());
            }

            if (rule.getSourceIp() != null) {
               ruleObject.addProperty("src_ip", rule.getSourceIp());
            }

            if (rule.getSourcePort() != null) {
               ruleObject.addProperty("src_port", rule.getSourcePort());
            }

            rulesArray.add(ruleObject);
         }

         firewallObject.add("rules", rulesArray);
      }
      return firewallObject;
   }
}
