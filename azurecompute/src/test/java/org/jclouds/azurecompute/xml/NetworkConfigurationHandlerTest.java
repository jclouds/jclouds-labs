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

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "NetworkConfigurationHandlerTest")
public class NetworkConfigurationHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/networkconfiguration.xml");
      NetworkConfiguration result = factory.create(new NetworkConfigurationHandler(new VirtualNetworkConfigurationHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static NetworkConfiguration expected() {
      return NetworkConfiguration.create(
              NetworkConfiguration.VirtualNetworkConfiguration.create(null,
                      ImmutableList.of(NetworkConfiguration.VirtualNetworkSite.create(
                              null,
                              "jclouds-virtual-network",
                              "West Europe",
                              NetworkConfiguration.AddressSpace.create("10.0.0.0/20"),
                              ImmutableList.of(NetworkConfiguration.Subnet.create("jclouds-1", "10.0.0.0/23", null))))
              )
      );
   }
}
