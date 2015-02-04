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

   public void test() {
      InputStream is = getClass().getResourceAsStream("/networksecuritygroup.xml");
      NetworkSecurityGroup result = factory.create(new NetworkSecurityGroupHandler()).parse(is);

      assertEquals(result, expected());
   }

   public static NetworkSecurityGroup expected() {
      return NetworkSecurityGroup.create( //
            "jclouds-NSG", // name
            "jclouds-NSG", // label
            "West Europe", // location
              ImmutableList.of(
                      Rule.create("ALLOW VNET INBOUND", // name
                              "Inbound", // type
                              "65000", // priority
                              "Allow", // action
                              "VIRTUAL_NETWORK", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "VIRTUAL_NETWORK", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW VNET OUTBOUND", // name
                              "Outbound", // type
                              "65000", // priority
                              "Allow", // action
                              "VIRTUAL_NETWORK", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "VIRTUAL_NETWORK", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW AZURE LOAD BALANCER INBOUND", // name
                              "Inbound", // type
                              "65001", // priority
                              "Allow", // action
                              "AZURE_LOADBALANCER", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("ALLOW INTERNET OUTBOUND", // name
                              "Outbound", // type
                              "65001", // priority
                              "Allow", // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "INTERNET", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("DENY ALL OUTBOUND", // name
                              "Outbound", // type
                              "65500", // priority
                              "Deny", // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      ),
                      Rule.create("DENY ALL INBOUND", // name
                              "Inbound", // type
                              "65500", // priority
                              "Deny", // action
                              "*", // sourceAddressPrefix
                              "*", // sourcePortRange
                              "*", // destinationAddressPrefix
                              "*", // destinationPortRange
                              "*",  // protocol
                              "Active", // state
                              true // isDefault
                      )
              ) // rules
      );
   }
}
