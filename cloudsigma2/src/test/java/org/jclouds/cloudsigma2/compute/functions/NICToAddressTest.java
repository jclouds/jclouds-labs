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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.collect.ImmutableList;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.FirewallRule;
import org.jclouds.cloudsigma2.domain.IP;
import org.jclouds.cloudsigma2.domain.IPConfiguration;
import org.jclouds.cloudsigma2.domain.IPConfigurationType;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.net.URI;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "NICToAddressTest")
public class NICToAddressTest {

   private List<NIC> input;
   private List<String> expected;

   @BeforeMethod
   public void setUp() throws Exception {
      input = ImmutableList.of(
            new NIC.Builder()
                  .firewallPolicy(new FirewallPolicy.Builder()
                        .name("firewall")
                        .rules(ImmutableList.of(new FirewallRule.Builder()
                              .sourcePort("22")
                              .destinationPort("123")
                              .sourceIp("1.2.3.4")
                              .destinationIp("11.22.33.44")
                              .build()))
                        .build())
                  .build(),
            new NIC.Builder()
                  .vlan(new VLANInfo.Builder()
                        .uuid("a21a4e59-b133-487a-ad7b-16b41ac38e9b")
                        .resourceUri(new URI("/api/2.0/vlans/a21a4e59-b133-487a-ad7b-16b41ac38e9b/"))
                        .build())
                  .build(),
            new NIC.Builder()
                  .ipV4Configuration(new IPConfiguration.Builder()
                        .configurationType(IPConfigurationType.STATIC)
                        .ip(new IP.Builder()
                              .uuid("1.2.3.4")
                              .resourceUri(new URI("api/2.0/ips/1.2.3.4/"))
                              .build())
                        .build())
                  .build(),
            new NIC.Builder()
                  .ipV6Configuration(new IPConfiguration.Builder()
                        .configurationType(IPConfigurationType.STATIC)
                        .ip(new IP.Builder()
                              .uuid("2001:0db8:0000:0000:0000:ff00:0042:8329")
                              .resourceUri(new URI("api/2.0/ips/2001:0db8:0000:0000:0000:ff00:0042:8329/"))
                              .build())
                        .build())
                  .build());

      expected = Lists.newArrayList(null, null, "1.2.3.4", "2001:0db8:0000:0000:0000:ff00:0042:8329");
   }

   public void testConvertNICs() {
      NICToAddress function = new NICToAddress();
      for (int i = 0; i < input.size() - 1; i++) {
         assertEquals(function.apply(input.get(i)), expected.get(i));
      }
   }
}
