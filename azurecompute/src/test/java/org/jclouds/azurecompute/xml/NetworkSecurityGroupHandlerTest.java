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
package org.jclouds.azurecompute.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "NetworkSecurityGroupHandlerTest")
public class NetworkSecurityGroupHandlerTest extends BaseHandlerTest {

   public void testFull() {
      final InputStream is = getClass().getResourceAsStream("/networksecuritygroupfulldetails.xml");
      final NetworkSecurityGroup result = factory.create(new NetworkSecurityGroupHandler()).parse(is);
      assertEquals(result, expectedFull());
   }

   public void test() {
      final InputStream is = getClass().getResourceAsStream("/networksecuritygroup.xml");
      final NetworkSecurityGroup result = factory.create(new NetworkSecurityGroupHandler()).parse(is);
      assertEquals(result, expected());
   }

   public void testForSubnet() {
      final InputStream is = getClass().getResourceAsStream("/networksecuritygroupforsubnet.xml");
      final NetworkSecurityGroup result = factory.create(new NetworkSecurityGroupHandler()).parse(is);
      assertEquals(result, expectedForSubnet());
   }

   public static NetworkSecurityGroup expected() {
      return NetworkSecurityGroup.create("group1", "sec group 1", "West Europe", null, null);
   }

   public static NetworkSecurityGroup expectedForSubnet() {
      return NetworkSecurityGroup.create("group1", null, null, NetworkSecurityGroup.State.CREATED, null);
   }

   public static NetworkSecurityGroup expectedFull() {
      return NetworkSecurityGroup.create( //
              "jclouds-NSG", // name
              "jclouds-NSG", // label
              "West Europe", // location
              null, // Network Security Group state
              ImmutableList.of(
                      Rule.create("tcp_10-20", // name
                              Rule.Type.Inbound, // type
                              "100", // priority
                              Rule.Action.Allow, // action
                              "INTERNET", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "10-20", // destinationPortRange
                              Rule.Protocol.TCP, // protocol
                              "Active", // state
                              null // isDefault
                      ),
                      Rule.create("ALLOW VNET INBOUND", // name
                              Rule.Type.Inbound, // type
                              "65000", // priority
                              Rule.Action.Allow, // action
                              "VIRTUAL_NETWORK", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "VIRTUAL_NETWORK", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW VNET OUTBOUND", // name
                              Rule.Type.Outbound, // type
                              "65000", // priority
                              Rule.Action.Allow, // action
                              "VIRTUAL_NETWORK", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "VIRTUAL_NETWORK", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW AZURE LOAD BALANCER INBOUND", // name
                              Rule.Type.Inbound, // type
                              "65001", // priority
                              Rule.Action.Allow, // action
                              "AZURE_LOADBALANCER", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW INTERNET OUTBOUND", // name
                              Rule.Type.Outbound, // type
                              "65001", // priority
                              Rule.Action.Allow, // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "INTERNET", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("DENY ALL OUTBOUND", // name
                              Rule.Type.Outbound, // type
                              "65500", // priority
                              Rule.Action.Deny, // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("DENY ALL INBOUND", // name
                              Rule.Type.Inbound, // type
                              "65500", // priority
                              Rule.Action.Deny, // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              Rule.Protocol.ALL, // protocol
                              "Active", // state
                              true // isDefault
                      )
              ) // rules
      );
   }
}
