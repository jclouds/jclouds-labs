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
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link SizesApi} class.
 */
@Test(groups = "unit", testName = "SizesApiMockTest")
public class SizesApiMockTest extends BaseDigitalOceanMockTest {

   public void testListSizes() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/sizes.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      SizesApi sizesApi = api.getSizesApi();

      try {
         List<Size> sizes = sizesApi.list();

         assertRequestHasCommonFields(server.takeRequest(), "/sizes");
         assertEquals(sizes.size(), 4);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
