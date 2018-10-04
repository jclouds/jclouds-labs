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

import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Test(groups = "unit")
public class NatRuleParseTest extends BaseDimensionDataCloudControlParseTest<NatRule> {

   @Override
   public String resource() {
      return "/natRule.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public NatRule expected() {
      return NatRule.builder().networkDomainId("484174a2-ae74-4658-9e56-50fc90e086cf")
            .createTime(parseDate("2015-03-06T13:45:10.000Z")).internalIp("10.0.0.16").externalIp("165.180.12.19")
            .state(State.NORMAL).id("2169a38e-5692-497e-a22a-701a838a6539").datacenterId("NA9").build();
   }
}
