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
import java.util.List;

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ListVirtualNetworkSitesHandlerTest")
public class ListVirtualNetworkSitesHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/virtualnetworksites.xml");
      List<VirtualNetworkSite> result = factory.create(new ListVirtualNetworkSitesHandler(new VirtualNetworkSiteHandler())).parse(is);

      assertEquals(result, expected());
   }

   public static List<VirtualNetworkSite> expected() {
      return ImmutableList.of(
              VirtualNetworkSite.create(
                      "39d0d14b-fc1d-496f-8928-b5a13a6f4b64",
                      "Group Group testDocker",
                      "West Europe",
                      NetworkConfiguration.AddressSpace.create("10.1.0.0/16"),
                      ImmutableList.of(NetworkConfiguration.Subnet.create("Subnet-1", "10.1.0.0/24", null))
              ),
              VirtualNetworkSite.create(
                      "12252126-cffc-4fac-8ba4-afa7150a8d4a",
                      "Group Group-1 dockertest",
                      "West Europe",
                      NetworkConfiguration.AddressSpace.create("10.2.0.0/16"),
                      ImmutableList.of(NetworkConfiguration.Subnet.create("Subnet-1", "10.2.0.0/24", null))
              )
      );
   }

}
