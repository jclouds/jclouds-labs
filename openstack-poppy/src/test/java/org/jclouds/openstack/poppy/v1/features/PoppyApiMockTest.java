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
package org.jclouds.openstack.poppy.v1.features;

import static org.assertj.core.api.Assertions.assertThat;

import org.jclouds.openstack.poppy.v1.PoppyApi;
import org.jclouds.openstack.poppy.v1.internal.BasePoppyApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code PoppyApi}
 */
@Test(groups = "unit", testName = "PoppyApiMockTest")
public class PoppyApiMockTest extends BasePoppyApiMockTest {

   public void testPing() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         boolean online = poppyApi.ping();

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/ping");

         assertThat(online).isTrue();
      } finally {
         server.shutdown();
      }
   }

   public void testPingFailOn500() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      // queue the initial 500 that forces the retry handler
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(500)));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(500)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         boolean online = poppyApi.ping();

         assertThat(server.getRequestCount()).isEqualTo(3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/ping");

         assertThat(online).isFalse();
      } finally {
         server.shutdown();
      }
   }

}
