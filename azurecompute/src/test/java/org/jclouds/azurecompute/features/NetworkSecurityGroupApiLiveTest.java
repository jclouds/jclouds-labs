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
package org.jclouds.azurecompute.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;

import org.jclouds.azurecompute.util.ConflictManagementPredicate;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "NetworkSecurityGroupApiLiveTest", singleThreaded = true)
public class NetworkSecurityGroupApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String GROUP1 = System.getProperty("user.name") + RAND + "-1";

   private static final String GROUP2 = System.getProperty("user.name") + RAND + "-2";

   @BeforeClass
   public void groupSetup() {
      // ----------------------------
      // Clean before start
      // ----------------------------
      try {
         final NetworkSecurityGroup group = api().
                 getNetworkSecurityGroupAppliedToSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME);

         if (group != null && (group.name().equals(GROUP1) || group.name().equals(GROUP2))) {
            api().removeFromSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME, group.name());
         }
      } catch (Exception e) {
         // ignore
      }

      try {
         if (api().get(GROUP1) != null) {
            operationSucceeded.apply(api.getNetworkSecurityGroupApi().delete(GROUP1));
         }
      } catch (Exception e) {
         // ignore
      }

      try {
         if (api().get(GROUP2) != null) {
            operationSucceeded.apply(api.getNetworkSecurityGroupApi().delete(GROUP2));
         }
      } catch (Exception e) {
         // ignore
      }
      // ----------------------------

      String requestId = api().create(
              NetworkSecurityGroup.create(GROUP1, GROUP1 + " security group", LOCATION, null, null));

      assertTrue(operationSucceeded.apply(requestId), GROUP1);

      requestId = api().create(
              NetworkSecurityGroup.create(GROUP2, GROUP2 + " security group", LOCATION, null, null));

      assertTrue(operationSucceeded.apply(requestId), GROUP2);
   }

   @Test
   public void list() {
      final List<NetworkSecurityGroup> groups = api().list();
      assertFalse(groups.isEmpty());
   }

   @Test
   public void get() {
      final NetworkSecurityGroup group = api().get(GROUP1);
      assertEquals(group.name(), GROUP1);
      assertEquals(group.label(), GROUP1 + " security group");
      assertEquals(group.location(), LOCATION);
      assertTrue(group.state() == NetworkSecurityGroup.State.CREATED);
      assertTrue(group.rules().isEmpty());
   }

   @Test
   public void getFullDetails() {
      final NetworkSecurityGroup group = api().getFullDetails(GROUP2);
      assertEquals(group.name(), GROUP2);
      assertEquals(group.label(), GROUP2 + " security group");
      assertEquals(group.location(), LOCATION);
      assertTrue(group.state() == NetworkSecurityGroup.State.CREATED);
      assertFalse(group.rules().isEmpty());
   }

   @Test
   public void setRule() {
      final String ruleName = "newrule";

      final String requestId = api().setRule(GROUP1, ruleName, Rule.create(
              ruleName,
              Rule.Type.Inbound,
              "100",
              Rule.Action.Allow,
              "INTERNET",
              "*",
              "10.0.0.0/0",
              "*",
              Rule.Protocol.ALL));

      assertTrue(operationSucceeded.apply(requestId), ruleName);

      Rule newrule = null;

      for (Rule rule : api().getFullDetails(GROUP1).rules()) {
         if (ruleName.equals(rule.name())) {
            newrule = rule;
         }
      }

      assertNotNull(newrule);

      assertNull(newrule.isDefault());
      assertEquals(newrule.action(), Rule.Action.Allow);
      assertEquals(newrule.type(), Rule.Type.Inbound);
      assertEquals(newrule.protocol(), Rule.Protocol.ALL);
      assertEquals(newrule.state(), "Active");
   }

   @Test(dependsOnMethods = {"setRule"})
   public void removeRule() {
      final String ruleName = "newrule";

      final String requestId = api().deleteRule(GROUP1, ruleName);
      assertTrue(operationSucceeded.apply(requestId), ruleName);

      Rule newrule = null;

      for (Rule rule : api().getFullDetails(GROUP1).rules()) {
         if (ruleName.equals(rule.name())) {
            newrule = rule;
         }
      }

      assertNull(newrule);
   }

   @Test(dependsOnMethods = {"removeRule"})
   public void addToSubnet() {
      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api().addToSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME, GROUP1);
         }
      }.apply(GROUP1));
   }

   @Test(dependsOnMethods = {"addToSubnet"})
   public void getForSubnet() {
      final NetworkSecurityGroup group = api().
              getNetworkSecurityGroupAppliedToSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME);
      assertEquals(group.state(), NetworkSecurityGroup.State.CREATED);
   }

   @Test(dependsOnMethods = {"getForSubnet"})
   public void removeFromSubnet() {
      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api().removeFromSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME, GROUP1);
         }
      }.apply(GROUP1));
   }

   @AfterClass
   public void cleanup() {
      // no assertion is required: just to be sure to remove for subnet
      final NetworkSecurityGroup group = api().
              getNetworkSecurityGroupAppliedToSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME);

      if (group != null) {
         api().removeFromSubnet(VIRTUAL_NETWORK_NAME, DEFAULT_SUBNET_NAME, group.name());
      }

      String requestId = api.getNetworkSecurityGroupApi().delete(GROUP1);
      assertTrue(operationSucceeded.apply(requestId), GROUP1);

      requestId = api.getNetworkSecurityGroupApi().delete(GROUP2);
      assertTrue(operationSucceeded.apply(requestId), GROUP2);
   }

   private NetworkSecurityGroupApi api() {
      return api.getNetworkSecurityGroupApi();
   }
}
