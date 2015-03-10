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
package org.jclouds.shipyard.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpResponseException;
import org.jclouds.shipyard.ShipyardApi;
import org.jclouds.shipyard.domain.servicekeys.ServiceKey;
import org.jclouds.shipyard.internal.BaseShipyardMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.shipyard.features.ServiceKeysApi} class.
 */
@Test(groups = "unit", testName = "ServiceKeysApiMockTest")
public class ServiceKeysApiMockTest extends BaseShipyardMockTest {

   public void testListServiceKeys() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody(payloadFromResource("/servicekeys.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ServiceKeysApi api = shipyardApi.serviceKeysApi();
      try {
         assertEquals(api.listServiceKeys().size(), 2);
         assertSent(server, "GET", "/api/servicekeys");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testCreateServiceKey() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody(payloadFromResource("/servicekey-response.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ServiceKeysApi api = shipyardApi.serviceKeysApi();
      try {
         ServiceKey serviceKey = api.createServiceKey("jclouds-shipyard-testing");
         assertNotNull(serviceKey);
         assertEquals(serviceKey.description(), "jclouds-shipyard-testing");
         assertSent(server, "POST", "/api/servicekeys", new String(payloadFromResource("/servicekey-create.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testDeleteServiceKey() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ServiceKeysApi api = shipyardApi.serviceKeysApi();
      try {
         boolean removed = api.deleteServiceKey("1111222233334444");
         assertTrue(removed);
         assertSent(server, "DELETE", "/api/servicekeys");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testDeleteNonExistentServiceKey() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(500).setBody("service key does not exist"));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ServiceKeysApi api = shipyardApi.serviceKeysApi();
      try {
         Boolean removed = api.deleteServiceKey("NonExistentServiceKey");
         assertFalse(removed);
         assertSent(server, "DELETE", "/api/servicekeys");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testDeleteNonExistentServiceKeyWithErroneousData() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(555).setBody("erroneous data"));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ServiceKeysApi api = shipyardApi.serviceKeysApi();
      try {
         api.deleteServiceKey("NonExistentServiceKey");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
}
