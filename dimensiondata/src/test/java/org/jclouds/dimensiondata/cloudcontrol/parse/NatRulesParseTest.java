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
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRules;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Test(groups = "unit")
public class NatRulesParseTest extends BaseDimensionDataCloudControlParseTest<NatRules> {

   @Override
   public String resource() {
      return "/natRules.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public NatRules expected() {
      List<NatRule> natRules = ImmutableList.of(NatRule.builder().id("2187a636-7ebb-49a1-a2ff-5d617f496dce")
            .createTime(parseDate("2015-03-06T13:43:45.000Z")).state("NORMAL").externalIp("165.180.12.18")
            .internalIp("10.0.0.15").networkDomainId("484174a2-ae74-4658-9e56-50fc90e086cf").datacenterId("NA9")
            .build(), NatRule.builder().id("2169a38e-5692-497e-a22a-701a838a6539")
            .createTime(parseDate("2015-03-06T13:45:10.000Z")).state("NORMAL").externalIp("165.180.12.19")
            .internalIp("10.0.0.16").networkDomainId("484174a2-ae74-4658-9e56-50fc90e086cf").datacenterId("NA9")
            .build());
      return new NatRules(natRules, 1, 2, 2, 250);
   }
}
