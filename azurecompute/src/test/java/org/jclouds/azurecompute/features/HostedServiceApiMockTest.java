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

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.parse.GetHostedServiceDetailsTest;
import org.jclouds.azurecompute.parse.GetHostedServiceTest;
import org.jclouds.azurecompute.parse.ListHostedServicesTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.azurecompute.options.CreateHostedServiceOptions.Builder.description;

@Test(groups = "unit", testName = "HostedServiceApiMockTest")
public class HostedServiceApiMockTest extends BaseAzureComputeApiMockTest {

   public void listWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/hostedservices.xml"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.list()).containsExactlyElementsOf(ListHostedServicesTest.expected());

         assertSent(server, "GET", "/services/hostedservices");
      } finally {
         server.shutdown();
      }
   }

   public void listWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.list()).isEmpty();

         assertSent(server, "GET", "/services/hostedservices");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/hostedservice.xml"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.get("myservice")).isEqualTo(GetHostedServiceTest.expected());

         assertSent(server, "GET", "/services/hostedservices/myservice");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.get("myservice")).isNull();

         assertSent(server, "GET", "/services/hostedservices/myservice");
      } finally {
         server.shutdown();
      }
   }

   public void getDetailsWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/hostedservice_details.xml"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.getDetails("myservice")).isEqualTo(GetHostedServiceDetailsTest.expected());

         assertSent(server, "GET", "/services/hostedservices/myservice?embed-detail=true");
      } finally {
         server.shutdown();
      }
   }

   public void getDetailsWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.getDetails("myservice")).isNull();

         assertSent(server, "GET", "/services/hostedservices/myservice?embed-detail=true");
      } finally {
         server.shutdown();
      }
   }

   public void createServiceWithLabelInLocation() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.createServiceWithLabelInLocation("myservice", "service mine", "West US"))
               .isEqualTo("request-1");

         assertSent(server, "POST", "/services/hostedservices", "/create_hostedservice_location.xml");
      } finally {
         server.shutdown();
      }
   }

   public void createServiceWithLabelInLocationOptions() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.createServiceWithLabelInLocation("myservice", "service mine", "West US",
               description("my description").extendedProperties(ImmutableMap.of("Role", "Production"))))
               .isEqualTo("request-1");

         assertSent(server, "POST", "/services/hostedservices", "/create_hostedservice_location_options.xml");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.delete("myservice")).isEqualTo("request-1");

         assertSent(server, "DELETE", "/services/hostedservices/myservice");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         HostedServiceApi api = api(server.getUrl("/")).getHostedServiceApi();

         assertThat(api.delete("myservice")).isNull();

         assertSent(server, "DELETE", "/services/hostedservices/myservice");
      } finally {
         server.shutdown();
      }
   }
}
