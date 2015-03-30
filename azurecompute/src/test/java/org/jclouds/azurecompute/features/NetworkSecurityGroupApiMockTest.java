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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.azurecompute.xml.ListNetworkSecurityGroupsHandlerTest;
import org.jclouds.azurecompute.xml.NetworkSecurityGroupHandlerTest;

@Test(groups = "unit", testName = "NetworkSecurityGroupApiMockTest")
public class NetworkSecurityGroupApiMockTest extends BaseAzureComputeApiMockTest {

   public void list() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/networksecuritygroups.xml"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.list(), ListNetworkSecurityGroupsHandlerTest.expected());
         assertSent(server, "GET", "/services/networking/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void listWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertTrue(api.list().isEmpty());
         assertSent(server, "GET", "/services/networking/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void getFullDetails() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/networksecuritygroupfulldetails.xml"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.getFullDetails("jclouds-NSG"), NetworkSecurityGroupHandlerTest.expectedFull());
         assertSent(server, "GET", "/services/networking/networksecuritygroups/jclouds-NSG?detaillevel=Full");
      } finally {
         server.shutdown();
      }
   }

   public void getFullDetailsWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.getFullDetails("jclouds-NSG"));
         assertSent(server, "GET", "/services/networking/networksecuritygroups/jclouds-NSG?detaillevel=Full");
      } finally {
         server.shutdown();
      }
   }

   public void get() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/networksecuritygroup.xml"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.get("group1"), NetworkSecurityGroupHandlerTest.expected());
         assertSent(server, "GET", "/services/networking/networksecuritygroups/group1");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.get("group1"));
         assertSent(server, "GET", "/services/networking/networksecuritygroups/group1");
      } finally {
         server.shutdown();
      }
   }

   public void getNetworkSecurityGroupAppliedToSubnet() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/networksecuritygroupforsubnet.xml"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.getNetworkSecurityGroupAppliedToSubnet("myvn", "mysubnet"),
                 NetworkSecurityGroupHandlerTest.expectedForSubnet());
         assertSent(server, "GET", "/services/networking/virtualnetwork/myvn/subnets/mysubnet/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void getNetworkSecurityGroupAppliedToSubnetWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.getNetworkSecurityGroupAppliedToSubnet("myvn", "mysubnet"));
         assertSent(server, "GET", "/services/networking/virtualnetwork/myvn/subnets/mysubnet/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void deleteGroup() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.delete("mygroup"), "request-1");
         assertSent(server, "DELETE", "/services/networking/networksecuritygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }

   public void deleteGroupWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.delete("mygroup"));
         assertSent(server, "DELETE", "/services/networking/networksecuritygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }

   public void deleteRule() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.deleteRule("mygroup", "myrule"), "request-1");
         assertSent(server, "DELETE", "/services/networking/networksecuritygroups/mygroup/rules/myrule");
      } finally {
         server.shutdown();
      }
   }

   public void deleteRuleWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.deleteRule("mygroup", "myrule"));
         assertSent(server, "DELETE", "/services/networking/networksecuritygroups/mygroup/rules/myrule");
      } finally {
         server.shutdown();
      }
   }

   public void removeFromSubnet() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.removeFromSubnet("myvn", "mysubnet", "mygroup"), "request-1");
         assertSent(server, "DELETE",
                 "/services/networking/virtualnetwork/myvn/subnets/mysubnet/networksecuritygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }

   public void removeFromSubnetWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertNull(api.removeFromSubnet("myvn", "mysubnet", "mygroup"));
         assertSent(server, "DELETE",
                 "/services/networking/virtualnetwork/myvn/subnets/mysubnet/networksecuritygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }

   public void addToSubnet() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.addToSubnet("myvn", "mysubnet", "mygroup"), "request-1");
         assertSent(server, "POST",
                 "/services/networking/virtualnetwork/myvn/subnets/mysubnet/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void create() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.create(NetworkSecurityGroup.create("mygroup", "sec mygroup", "North Europe", null, null)),
                 "request-1");
         assertSent(server, "POST", "/services/networking/networksecuritygroups");
      } finally {
         server.shutdown();
      }
   }

   public void setRule() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final NetworkSecurityGroupApi api = networkSecurityGroupApi(server);
         assertEquals(api.setRule("mygroup", "myrule", Rule.create(
                 "myrule",
                 Rule.Type.Inbound,
                 "100",
                 Rule.Action.Allow,
                 "192.168.0.2",
                 "*",
                 "192.168.0.1",
                 "80",
                 Rule.Protocol.TCP
         )), "request-1");

         assertSent(server, "PUT", "/services/networking/networksecuritygroups/mygroup/rules/myrule");
      } finally {
         server.shutdown();
      }
   }

   private NetworkSecurityGroupApi networkSecurityGroupApi(final MockWebServer server) {
      return api(server.getUrl("/")).getNetworkSecurityGroupApi();
   }
}
