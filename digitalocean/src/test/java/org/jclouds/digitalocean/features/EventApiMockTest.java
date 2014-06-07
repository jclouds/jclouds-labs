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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.domain.Event.Status;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link EventApi} class.
 */
@Test(groups = "unit", testName = "EventApiMockTest")
public class EventApiMockTest extends BaseDigitalOceanMockTest {

   public void testGetEvent() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/event.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      EventApi eventApi = api.getEventApi();

      try {
         Event event = eventApi.get(7499);

         assertRequestHasCommonFields(server.takeRequest(), "/events/7499");
         assertNotNull(event);
         assertEquals(event.getStatus(), Status.DONE);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetPendingEvent() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/event-pending.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      EventApi eventApi = api.getEventApi();

      try {
         Event event = eventApi.get(7499);

         assertRequestHasCommonFields(server.takeRequest(), "/events/7499");
         assertNotNull(event);
         assertEquals(event.getStatus(), Status.PENDING);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetUnexistingEvent() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      EventApi eventApi = api.getEventApi();

      try {
         Event event = eventApi.get(7499);

         assertRequestHasCommonFields(server.takeRequest(), "/events/7499");
         assertNull(event);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
