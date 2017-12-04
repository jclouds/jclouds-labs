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
package org.jclouds.dimensiondata.cloudcontrol.parse;

import com.google.common.collect.ImmutableList;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRules;
import org.jclouds.dimensiondata.cloudcontrol.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Test(groups = "unit")
public class FirewallRulesParseTest extends BaseDimensionDataCloudControlParseTest<FirewallRules> {

   @Override
   public String resource() {
      return "/firewallRules.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public FirewallRules expected() {
      List<FirewallRule> firewallRules = ImmutableList
            .of(FirewallRule.builder().id("1aa3d0ce-d95d-4296-8338-9717e0d37ff9")
                  .name("CCDEFAULT.BlockOutboundMailIPv6Secure").state(State.NORMAL).action("DROP").ipVersion("IPV6")
                  .protocol("TCP").source(FirewallRuleTarget.builder().ip(IpRange.create("ANY", null)).build())
                  .destination(FirewallRuleTarget.builder().ip(IpRange.create("ANY", null))
                        .port(FirewallRuleTarget.Port.create(587, null)).build()).ruleType("DEFAULT_RULE")
                  .networkDomainId("484174a2-ae74-4658-9e56-50fc90e086cf").enabled(Boolean.TRUE).datacenterId("NA9")
                  .build());
      return new FirewallRules(firewallRules, 1, 2, 2, 250);
   }
}
