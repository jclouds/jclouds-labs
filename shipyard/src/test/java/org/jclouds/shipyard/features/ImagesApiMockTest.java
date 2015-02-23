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

import java.net.URI;

import org.jclouds.http.HttpResponseException;
import org.jclouds.shipyard.ShipyardApi;
import org.jclouds.shipyard.internal.BaseShipyardMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.shipyard.features.ImagesApi} class.
 */
@Test(groups = "unit", testName = "ImagesApiMockTest")
public class ImagesApiMockTest extends BaseShipyardMockTest {

   public void testGetAllImages() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().
                     setResponseCode(200).
                     setBody(payloadFromResource("/images.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ImagesApi api = shipyardApi.imagesApi();
      try {
         assertEquals(api.listImages(URI.create("")).size(), 1);
         assertSentIgnoreServiceKey(server, "GET", "/images/json");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testGetNonExistentDockerDaemon() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().
                     setResponseCode(404).
                     setBody(payloadFromResource("/images.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ImagesApi api = shipyardApi.imagesApi();
      try {
         assertEquals(api.listImages(URI.create("http://test-jclouds-ship:9999")).size(), 0);
         assertSentIgnoreServiceKey(server, "GET", "/images/json");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
}
