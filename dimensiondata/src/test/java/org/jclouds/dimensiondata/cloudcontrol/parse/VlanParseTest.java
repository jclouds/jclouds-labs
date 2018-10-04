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

import org.jclouds.dimensiondata.cloudcontrol.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Test(groups = "unit")
public class VlanParseTest extends BaseDimensionDataCloudControlParseTest<Vlan> {

   @Override
   public String resource() {
      return "/vlan.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Vlan expected() {
      return Vlan.builder()
            .networkDomain(NetworkDomain.builder().id("690de302-bb80-49c6-b401-8c02bbefb945").name("test").build())
            .name("vlan1").description("").privateIpv4Range(IpRange.create("10.0.0.0", 24))
            .ipv4GatewayAddress("10.0.0.1").ipv6Range(IpRange.create("2607:f480:111:1575:0:0:0:0", 64))
            .ipv6GatewayAddress("2607:f480:111:1575:0:0:0:1").createTime(parseDate("2016-03-11T10:41:19.000Z"))
            .state(State.NORMAL).datacenterId("NA9").id("6b25b02e-d3a2-4e69-8ca7-9bab605deebd").build();
   }
}
