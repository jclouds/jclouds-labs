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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.net.URL;
import org.jclouds.azurecompute.domain.Availability;
import org.jclouds.azurecompute.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.StorageServiceKeys;
import org.jclouds.azurecompute.domain.UpdateStorageServiceParams;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListStorageServiceHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "StorageAccountApiMockTest")
public class StorageAccountApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/storageservices.xml"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.list(), ListStorageServiceHandlerTest.expected());

         assertSent(server, "GET", "/services/storageservices");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertTrue(api.list().isEmpty());

         assertSent(server, "GET", "/services/storageservices");
      } finally {
         server.shutdown();
      }
   }

   public void testCreate() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         final CreateStorageServiceParams params = CreateStorageServiceParams.builder().
                 serviceName("name-of-storage-account").
                 description("description-of-storage-account").
                 label("base64-encoded-label").
                 location("location-of-storage-account").
                 extendedProperties(ImmutableMap.of("property_name", "property_value")).
                 accountType(StorageService.AccountType.Premium_LRS).
                 build();

         assertEquals(api.create(params), "request-1");

         assertSent(server, "POST", "/services/storageservices", "/createstorageserviceparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testIsAvailable() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/isavailablestorageservice.xml"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.isAvailable("serviceName"),
                 Availability.create(false, "The storage account named 'serviceName' is already taken."));

         assertSent(server, "GET", "/services/storageservices/operations/isavailable/serviceName");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/storageservices.xml"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.get("serviceName"), ListStorageServiceHandlerTest.expected().get(0));

         assertSent(server, "GET", "/services/storageservices/serviceName");
      } finally {
         server.shutdown();
      }
   }

   public void testNullGet() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertNull(api.get("serviceName"));

         assertSent(server, "GET", "/services/storageservices/serviceName");
      } finally {
         server.shutdown();
      }
   }

   public void testGetKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/storageaccountkeys.xml"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.getKeys("serviceName"), StorageServiceKeys.create(
                 new URL("https://management.core.windows.net/subscriptionid/services/storageservices/serviceName"),
                 "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
                 "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

         assertSent(server, "GET", "/services/storageservices/serviceName/keys");
      } finally {
         server.shutdown();
      }
   }

   public void testNullGetKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertNull(api.getKeys("serviceName"));

         assertSent(server, "GET", "/services/storageservices/serviceName/keys");
      } finally {
         server.shutdown();
      }
   }

   public void testRegenerateKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.regenerateKeys("serviceName", StorageServiceKeys.KeyType.Primary), "request-1");

         assertSent(server, "POST", "/services/storageservices/serviceName/keys?action=regenerate",
                 "/storageaccountregeneratekeys.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdate() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         final UpdateStorageServiceParams params = UpdateStorageServiceParams.builder().
                 description("description-of-storage-account").
                 label("base64-encoded-label").
                 extendedProperties(ImmutableMap.of("property_name", "property_value")).
                 customDomains(ImmutableList.of(
                                 UpdateStorageServiceParams.CustomDomain.create("name-of-custom-domain", false))).
                 accountType(UpdateStorageServiceParams.AccountType.Standard_GRS).
                 build();

         assertEquals(api.update("serviceName", params), "request-1");

         assertSent(server, "PUT", "/services/storageservices/serviceName", "/updatestorageserviceparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi();

         assertEquals(api.delete("serviceName"), "request-1");

         assertSent(server, "DELETE", "/services/storageservices/serviceName");
      } finally {
         server.shutdown();
      }
   }
}
