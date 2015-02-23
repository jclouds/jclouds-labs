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

import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.jclouds.http.HttpResponseException;
import org.jclouds.shipyard.ShipyardApi;
import org.jclouds.shipyard.domain.engines.AddEngine;
import org.jclouds.shipyard.domain.engines.EngineSettingsInfo;
import org.jclouds.shipyard.internal.BaseShipyardMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Mock tests for the {@link org.jclouds.shipyard.features.EnginesApi} class.
 */
@Test(groups = "unit", testName = "EnginesApiMockTest")
public class EnginesApiMockTest extends BaseShipyardMockTest {

   public void testGetEngine() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/engine.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      EnginesApi api = shipyardApi.enginesApi();
      try {
         String specificEngineID = "e2059d20-e9df-44f3-8a9b-1bf2321b4eae";
         assertEquals(api.getEngine(specificEngineID).id(), specificEngineID);
         assertSent(server, "GET", "/api/engines/" + specificEngineID);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testGetAllEngines() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/engines.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      EnginesApi api = shipyardApi.enginesApi();
      try {
         assertEquals(api.listEngines().size(), 1);
         assertSent(server, "GET", "/api/engines");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testAddEngine() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      EnginesApi api = shipyardApi.enginesApi();
      try {
         AddEngine addEngine = AddEngine.create("local", 
                                 "", 
                                 "", 
                                 "", 
                                 EngineSettingsInfo.create("1234", 
                                       "http://localhost:2375", 
                                       1, 
                                       1024, 
                                       Lists.newArrayList("shipyard-test")));
         
         api.addEngine(addEngine);
         assertSent(server, "POST", "/api/engines", new String(payloadFromResource("/engine-add.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testAddNonExistentEngine() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(500));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      EnginesApi api = shipyardApi.enginesApi();
      try {
         AddEngine addEngine = AddEngine.create("local", 
                                 "", 
                                 "", 
                                 "", 
                                 EngineSettingsInfo.create("9999", 
                                       "http://shipyard.failure.com:9999", 
                                       1, 
                                       1024, 
                                       Lists.newArrayList("shipyard-faiure")));
         
         api.addEngine(addEngine);
         assertSent(server, "POST", "/api/engines", new String(payloadFromResource("/engine-add.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testRemoveEngine() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      EnginesApi api = shipyardApi.enginesApi();
      try {
         String specificEngineID = "e2059d20-e9df-44f3-8a9b-1bf2321b4eae";
         api.removeEngine(specificEngineID);
         assertSent(server, "DELETE", "/api/engines/" + specificEngineID);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testRemoveNonExistentEngine() throws Exception {
      //TODO
   }
}
