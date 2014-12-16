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

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import java.util.UUID;

import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkConfiguration;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListVirtualNetworkSitesHandlerTest;
import org.jclouds.azurecompute.xml.NetworkConfigurationHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "VirtualNetworkApiMockTest")
public class VirtualNetworkApiMockTest extends BaseAzureComputeApiMockTest {

   public void testGetNetworkConfiguration() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/networkconfiguration.xml"));

      try {
         VirtualNetworkApi api = virtualNetworkApi(server);

         assertEquals(api.getNetworkConfiguration(), NetworkConfigurationHandlerTest.expected());

         assertSent(server, "GET", "/services/networking/media");
      } finally {
         server.shutdown();
      }
   }

   public void testSet() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         VirtualNetworkApi api = virtualNetworkApi(server);

         assertThat(api.set(NetworkConfiguration.create(
                 VirtualNetworkConfiguration.create(null,
                         ImmutableList.of(NetworkConfiguration.VirtualNetworkSite.create(
                                 UUID.randomUUID().toString(),
                                 "jclouds-virtual-network",
                                 "West Europe",
                                 NetworkConfiguration.AddressSpace.create("10.0.0.0/20"),
                                 ImmutableList.of(NetworkConfiguration.Subnet.create("jclouds-1", "10.0.0.0/23",
                                         null)))))))
                 ).isEqualTo("request-1");

         assertSent(server, "PUT", "/services/networking/media");
      } finally {
         server.shutdown();
      }
   }

   public void testList() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/virtualnetworksites.xml"));

      try {
         VirtualNetworkApi api = virtualNetworkApi(server);

         assertThat(api.list()).containsExactlyElementsOf(ListVirtualNetworkSitesHandlerTest.expected());

         assertSent(server, "GET",
                 "/services/networking/virtualnetwork");
      } finally {
         server.shutdown();
      }
   }


   private VirtualNetworkApi virtualNetworkApi(MockWebServer server) {
      return api(server.getUrl("/")).getVirtualNetworkApi();
   }
}
