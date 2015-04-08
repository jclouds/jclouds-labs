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

import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.domain.ReservedIPAddressParams;
import org.jclouds.azurecompute.xml.ListReservedIPAddressHandlerTest;
import org.jclouds.azurecompute.xml.ReservedIPAddressHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "ReservedIPAddressApiMockTest")
public class ReservedIPAddressApiMockTest extends BaseAzureComputeApiMockTest {

   public void listWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/listreservedipaddress.xml"));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertEquals(api.list(), ListReservedIPAddressHandlerTest.expected());
         assertSent(server, "GET", "/services/networking/reservedips");
      } finally {
         server.shutdown();
      }
   }

   public void listWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertTrue(api.list().isEmpty());
         assertSent(server, "GET", "/services/networking/reservedips");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/reservedipaddress.xml"));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertEquals(api.get("myreservedip"), ReservedIPAddressHandlerTest.expected());
         assertSent(server, "GET", "/services/networking/reservedips/myreservedip");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertNull(api.get("myreservedip"));
         assertSent(server, "GET", "/services/networking/reservedips/myreservedip");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertEquals(api.delete("myreservedip"), "request-1");
         assertSent(server, "DELETE", "/services/networking/reservedips/myreservedip");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();
         assertNull(api.delete("myreservedip"));
         assertSent(server, "DELETE", "/services/networking/reservedips/myreservedip");
      } finally {
         server.shutdown();
      }
   }

   public void create() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final ReservedIPAddressApi api = api(server.getUrl("/")).getReservedIPAddressApi();

         final ReservedIPAddressParams params = ReservedIPAddressParams.builder().
                 name("myreservedip").
                 label("myreservedip label").
                 location("West Europe").build();

         assertEquals(api.create(params), "request-1");
         assertSent(server, "POST", "/services/networking/reservedips", "/reservedipaddressparams.xml");
      } finally {
         server.shutdown();
      }
   }
}
