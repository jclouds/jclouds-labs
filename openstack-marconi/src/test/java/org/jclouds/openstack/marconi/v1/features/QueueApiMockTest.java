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
package org.jclouds.openstack.marconi.v1.features;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Everett Toews
 */
@Test
public class QueueApiMockTest extends BaseOpenStackMockTest<MarconiApi> {

   public void createQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         boolean success = queueApi.create();

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "PUT /v1/123123/queues/jclouds-test HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void deleteQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         boolean success = queueApi.delete();

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "DELETE /v1/123123/queues/jclouds-test HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void existsQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         boolean success = queueApi.exists();

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void doesNotExistQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-blerg");
         boolean success = queueApi.exists();

         assertFalse(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-blerg HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void setMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         Map<String, String> metadata = ImmutableMap.of("key1", "value1");
         boolean success = queueApi.setMetadata(metadata);

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getRequestLine(), "PUT /v1/123123/queues/jclouds-test/metadata HTTP/1.1");
         assertEquals(request.getUtf8Body(), "{\"key1\":\"value1\"}");
      }
      finally {
         server.shutdown();
      }
   }

   public void getMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"key1\":\"value1\"}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         Map<String, String> metadata = queueApi.getMetadata();

         assertEquals(metadata.get("key1"), "value1");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/metadata HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void getQueueStatsWithoutTotal() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200)
            .setBody("{\"messages\":{\"claimed\":0,\"total\":0,\"free\":0}}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         QueueStats stats = queueApi.getStats();

         assertEquals(stats.getMessagesStats().getClaimed(), 0);
         assertEquals(stats.getMessagesStats().getFree(), 0);
         assertEquals(stats.getMessagesStats().getTotal(), 0);
         assertFalse(stats.getMessagesStats().getOldest().isPresent());
         assertFalse(stats.getMessagesStats().getNewest().isPresent());

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/stats HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void getQueueStatsWithTotal() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200)
            .setBody("{\"messages\": {\"claimed\": 0, \"oldest\": {\"age\": 0, \"href\": \"/v1/queues/jclouds-test/messages/526558b3f4919b655feba3a7\", \"created\": \"2013-10-21T16:39:15Z\"}, \"total\": 4, \"newest\": {\"age\": 0, \"href\": \"/v1/queues/jclouds-test/messages/526558b33ac24e663fc545e7\", \"created\": \"2013-10-21T16:39:15Z\"}, \"free\": 4}}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndQueue("DFW", "jclouds-test");
         QueueStats stats = queueApi.getStats();

         assertEquals(stats.getMessagesStats().getClaimed(), 0);
         assertEquals(stats.getMessagesStats().getFree(), 4);
         assertEquals(stats.getMessagesStats().getTotal(), 4);
         assertTrue(stats.getMessagesStats().getOldest().isPresent());
         assertTrue(stats.getMessagesStats().getOldest().get().getCreated().before(new Date()));
         assertEquals(stats.getMessagesStats().getOldest().get().getAge(), 0);
         assertEquals(stats.getMessagesStats().getOldest().get().getId(), "526558b3f4919b655feba3a7");
         assertTrue(stats.getMessagesStats().getNewest().isPresent());
         assertTrue(stats.getMessagesStats().getNewest().get().getCreated().before(new Date()));
         assertEquals(stats.getMessagesStats().getNewest().get().getAge(), 0);
         assertEquals(stats.getMessagesStats().getNewest().get().getId(), "526558b33ac24e663fc545e7");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/stats HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }
}
