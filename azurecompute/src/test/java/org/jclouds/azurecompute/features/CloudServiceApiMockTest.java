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

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.CloudServiceHandlerTest;
import org.jclouds.azurecompute.xml.ListCloudServicesHandlerTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "CloudServiceApiMockTest")
public class CloudServiceApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/hostedservices.xml"));

      try {
         CloudServiceApi api = api(server.getUrl("/")).getCloudServiceApi();

         assertEquals(api.list(), ListCloudServicesHandlerTest.expected());

         assertSent(server, "GET", "/services/hostedservices?embed-detail=true");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/hostedservice.xml"));

      try {
         CloudServiceApi api = api(server.getUrl("/")).getCloudServiceApi();

         assertEquals(api.get("myservice"), CloudServiceHandlerTest.expected());

         assertSent(server, "GET", "/services/hostedservices/myservice?embed-detail=true");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWithLabelInLocation() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         CloudServiceApi api = api(server.getUrl("/")).getCloudServiceApi();

         assertEquals(api.createWithLabelInLocation("myservice", "service mine", "West US"), "request-1");

         assertSent(server, "POST", "/services/hostedservices", "/create_hostedservice_location.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         CloudServiceApi api = api(server.getUrl("/")).getCloudServiceApi();

         assertEquals(api.delete("myservice"), "request-1");

         assertSent(server, "DELETE", "/services/hostedservices/myservice");
      } finally {
         server.shutdown();
      }
   }

}
