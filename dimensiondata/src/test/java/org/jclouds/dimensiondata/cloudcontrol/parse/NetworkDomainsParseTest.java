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
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomains;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain.Type.ESSENTIALS;

@Test(groups = "unit")
public class NetworkDomainsParseTest extends BaseDimensionDataCloudControlParseTest<NetworkDomains> {

   @Override
   public String resource() {
      return "/networkDomains.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public NetworkDomains expected() {
      List<NetworkDomain> networkDomains = ImmutableList
            .of(NetworkDomain.builder().id("8e082ed6-c198-4eff-97cb-aeac6f9685d8").datacenterId("NA9").name("test")
                  .description("").state(State.NORMAL).type(ESSENTIALS).snatIpv4Address("168.128.3.44")
                  .createTime(parseDate("2016-03-08T14:39:47.000Z")).build());
      return new NetworkDomains(networkDomains, 1, 5, 5, 250);
   }
}
