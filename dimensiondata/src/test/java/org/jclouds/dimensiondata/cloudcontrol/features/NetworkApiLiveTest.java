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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontrol.domain.IpRange;
import org.jclouds.dimensiondata.cloudcontrol.domain.Placement;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.jclouds.rest.ResourceAlreadyExistsException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.jclouds.dimensiondata.cloudcontrol.features.NetworkApiMockTest.DEFAULT_ACTION;
import static org.jclouds.dimensiondata.cloudcontrol.features.NetworkApiMockTest.DEFAULT_IP_VERSION;
import static org.jclouds.dimensiondata.cloudcontrol.utils.DimensionDataCloudControlResponseUtils.generateFirewallRuleName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "NetworkApiLiveTest", singleThreaded = true)
public class NetworkApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {

   private String networkDomainId;
   private String networkDomainName;
   private String vlanId;
   private String portListId;
   private String firewallRuleId;

   private List<String> firewallRuleIds;

   @BeforeClass
   public void init() {
      firewallRuleIds = Lists.newArrayList();
   }

   @Test(dependsOnMethods = "testDeployVlan")
   public void testCreatePortList() {
      portListId = api()
            .createPortList(networkDomainId, this.getClass().getCanonicalName(), this.getClass().getCanonicalName(),
                  ImmutableList.of(FirewallRuleTarget.Port.create(22, null)), Lists.<String>newArrayList());
      assertNotNull(portListId);
   }

   @Test(dependsOnMethods = "testCreatePortList")
   public void testGetPortList() {
      FirewallRuleTarget.PortList portList = api().getPortList(portListId);
      assertNotNull(portList);
      assertTrue(portList.port().get(0).begin() == 22);
   }

   @Test(dependsOnMethods = "testGetPortList")
   public void testCreateFirewallRuleWithPortList() {
      firewallRuleId = api().createFirewallRule(networkDomainId, generateFirewallRuleName("server-id"), DEFAULT_ACTION,
            DEFAULT_IP_VERSION, DEFAULT_PROTOCOL, FirewallRuleTarget.builder().ip(IpRange.create("ANY", null)).build(),
            FirewallRuleTarget.builder().ip(IpRange.create("ANY", null)).portListId(portListId).build(), Boolean.TRUE,
            Placement.builder().position("LAST").build());
      firewallRuleIds.add(firewallRuleId);
   }

   @Test(dependsOnMethods = "testCreateFirewallRuleWithPortList")
   public void testListFirewallRules() {
      PagedIterable<FirewallRule> firewallRulesList = api().listFirewallRules(networkDomainId);
      assertFalse(firewallRulesList.isEmpty());
      assertEquals(firewallRulesList.last().get().first().get().networkDomainId(), networkDomainId);
   }

   @Test(dependsOnMethods = "testListFirewallRules")
   public void testDeleteFirewallRule() {
      api().deleteFirewallRule(firewallRuleId);
      FirewallRule rule = findById(api().listFirewallRules(networkDomainId).concat().toList(), firewallRuleId);
      assertNull(rule);
   }

   @Test(dependsOnMethods = "testDeployNetworkDomain")
   public void testDeployVlan() {
      vlanId = api().deployVlan(networkDomainId, NetworkApiLiveTest.class.getSimpleName() + new Date().getTime(),
            NetworkApiLiveTest.class.getSimpleName() + new Date().getTime(), DEFAULT_PRIVATE_IPV4_BASE_ADDRESS,
            DEFAULT_PRIVATE_IPV4_PREFIX_SIZE);
      assertNotNull(vlanId);
      assertTrue(vlanNormalPredicate.apply(vlanId), "vlan is not in a NORMAL state after timeout");
   }

   @Test
   public void testDeployNetworkDomain() {
      networkDomainName = NetworkApiLiveTest.class.getSimpleName() + new Date().getTime();
      networkDomainId = api().deployNetworkDomain(datacenters.iterator().next(), networkDomainName,
            NetworkApiLiveTest.class.getSimpleName() + new Date().getTime() + "description", "ESSENTIALS");
      assertNotNull(networkDomainId);
      assertTrue(networkDomainNormalPredicate.apply(networkDomainId),
            "network domain is not in a NORMAL state after timeout");
   }

   @Test(expectedExceptions = ResourceAlreadyExistsException.class)
   public void testDeploySameNetworkDomain() {
      api().deployNetworkDomain(datacenters.iterator().next(), networkDomainName, networkDomainName, "ESSENTIALS");
   }

   @AfterClass
   public void tearDown() {
      if (!firewallRuleIds.isEmpty()) {
         for (String firewallRuleId : firewallRuleIds) {
            api().deleteFirewallRule(firewallRuleId);
         }
      }
      if (portListId != null) {
         api().deletePortList(portListId);
      }
      if (vlanId != null) {
         api().deleteVlan(vlanId);
         assertTrue(vlanDeletedPredicate.apply(vlanId), "vlan is not in a DELETED state after timeout");
      }
      if (networkDomainId != null) {
         api().deleteNetworkDomain(networkDomainId);
         assertTrue(networkDomainDeletedPredicate.apply(networkDomainId),
               "network domain is not in a DELETED state after timeout");
      }
   }

   private NetworkApi api() {
      return api.getNetworkApi();
   }

   private FirewallRule findById(List<FirewallRule> collection, String id) {
      for (FirewallRule rule : collection) {
         if (rule.id().equals(id)) {
            return rule;
         }
      }
      return null;
   }
}
