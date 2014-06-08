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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.marconi.v1.domain.Queue;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.marconi.v1.domain.Queues;
import org.jclouds.openstack.marconi.v1.options.ListQueuesOptions;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.jclouds.openstack.marconi.v1.options.ListQueuesOptions.Builder.limit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test
public class QueueApiMockTest extends BaseOpenStackMockTest<MarconiApi> {
   private static final UUID CLIENT_ID = UUID.fromString("3381af92-2b9e-11e3-b191-71861300734c");

   public void createQueue() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         boolean success = queueApi.create("jclouds-test");

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
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         boolean success = queueApi.delete("jclouds-test");

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
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         boolean success = queueApi.exists("jclouds-test");

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
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         boolean success = queueApi.exists("jclouds-blerg");

         assertFalse(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-blerg HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void listZeroPagesOfQueues() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         List<Queue> queues = queueApi.list(false).concat().toList();

         assertTrue(queues.isEmpty());

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?detailed=false HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void listOnePageOfQueues() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"queues\": [{\"href\": \"/v1/queues/jclouds-test\", \"name\": \"jclouds-test\"}], \"links\": [{\"href\": \"/v1/queues?detailed=false&marker=jclouds-test\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         List<Queue> queues = queueApi.list(false).concat().toList();

         assertEquals(queues.size(), 1);
         assertEquals(Iterables.getOnlyElement(queues).getName(), "jclouds-test");
         assertFalse(Iterables.getOnlyElement(queues).getMetadata().isPresent());

         assertEquals(server.getRequestCount(), 3);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?detailed=false HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?detailed=false&marker=jclouds-test HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void listOnePageOfQueuesFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         FluentIterable<Queue> queues = queueApi.list(false).concat();

         assertTrue(queues.isEmpty(), "Expecting empty queues but was " + queues.toString());
      }
      finally {
         server.shutdown();
      }
   }

   public void listPagedIterableCollectionQueuesFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         Queues queues = queueApi.list(ListQueuesOptions.NONE);

         assertTrue(queues.isEmpty(), "Expecting empty queues but was " + queues.toString());
      }
      finally {
         server.shutdown();
      }
   }

   public void listManyPagesOfQueues() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"queues\": [{\"href\": \"/v1/queues/jclouds-test-1\", \"name\": \"jclouds-test-1\"}, {\"href\": \"/v1/queues/jclouds-test-10\", \"name\": \"jclouds-test-10\"}, {\"href\": \"/v1/queues/jclouds-test-11\", \"name\": \"jclouds-test-11\"}, {\"href\": \"/v1/queues/jclouds-test-12\", \"name\": \"jclouds-test-12\"}, {\"href\": \"/v1/queues/jclouds-test-2\", \"name\": \"jclouds-test-2\"}, {\"href\": \"/v1/queues/jclouds-test-3\", \"name\": \"jclouds-test-3\"}, {\"href\": \"/v1/queues/jclouds-test-4\", \"name\": \"jclouds-test-4\"}, {\"href\": \"/v1/queues/jclouds-test-5\", \"name\": \"jclouds-test-5\"}, {\"href\": \"/v1/queues/jclouds-test-6\", \"name\": \"jclouds-test-6\"}, {\"href\": \"/v1/queues/jclouds-test-7\", \"name\": \"jclouds-test-7\"}], \"links\": [{\"href\": \"/v1/queues?detailed=false&marker=jclouds-test-7\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"queues\": [{\"href\": \"/v1/queues/jclouds-test-8\", \"name\": \"jclouds-test-8\"}, {\"href\": \"/v1/queues/jclouds-test-9\", \"name\": \"jclouds-test-9\"}], \"links\": [{\"href\": \"/v1/queues?marker=jclouds-test-9&detailed=false\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         List<Queue> queues = queueApi.list(false).concat().toList();

         assertEquals(queues.size(), 12);

         for (Queue queue : queues) {
            assertNotNull(queue.getName());
            assertFalse(queue.getMetadata().isPresent());
         }

         assertEquals(server.getRequestCount(), 4);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?detailed=false HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?detailed=false&marker=jclouds-test-7 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?marker=jclouds-test-9&detailed=false HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void listManyPagesOfQueuesManually() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"queues\": [{\"href\": \"/v1/queues/jclouds-test-1\", \"name\": \"jclouds-test-1\"}, {\"href\": \"/v1/queues/jclouds-test-10\", \"name\": \"jclouds-test-10\"}, {\"href\": \"/v1/queues/jclouds-test-11\", \"name\": \"jclouds-test-11\"}, {\"href\": \"/v1/queues/jclouds-test-12\", \"name\": \"jclouds-test-12\"}, {\"href\": \"/v1/queues/jclouds-test-2\", \"name\": \"jclouds-test-2\"}, {\"href\": \"/v1/queues/jclouds-test-3\", \"name\": \"jclouds-test-3\"}], \"links\": [{\"href\": \"/v1/queues?marker=jclouds-test-3&limit=6\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"queues\": [{\"href\": \"/v1/queues/jclouds-test-4\", \"name\": \"jclouds-test-4\"}, {\"href\": \"/v1/queues/jclouds-test-5\", \"name\": \"jclouds-test-5\"}, {\"href\": \"/v1/queues/jclouds-test-6\", \"name\": \"jclouds-test-6\"}, {\"href\": \"/v1/queues/jclouds-test-7\", \"name\": \"jclouds-test-7\"}, {\"href\": \"/v1/queues/jclouds-test-8\", \"name\": \"jclouds-test-8\"}, {\"href\": \"/v1/queues/jclouds-test-9\", \"name\": \"jclouds-test-9\"}], \"links\": [{\"href\": \"/v1/queues?marker=jclouds-test-9&limit=6\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);

         Queues queues = queueApi.list(limit(6));

         while (queues.nextMarker().isPresent()) {
            assertEquals(queues.size(), 6);

            for (Queue queue : queues) {
               assertNotNull(queue.getName());
               assertFalse(queue.getMetadata().isPresent());
            }

            queues = queueApi.list(queues.nextListQueuesOptions());
         }

         assertEquals(server.getRequestCount(), 4);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?limit=6 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?marker=jclouds-test-3&limit=6 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues?marker=jclouds-test-9&limit=6 HTTP/1.1");
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
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         Map<String, String> metadata = ImmutableMap.of("key1", "value1");
         boolean success = queueApi.setMetadata("jclouds-test", metadata);

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
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         Map<String, String> metadata = queueApi.getMetadata("jclouds-test");

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
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\":{\"claimed\":0,\"total\":0,\"free\":0}}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         QueueStats stats = queueApi.getStats("jclouds-test");

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
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\": {\"claimed\": 0, \"oldest\": {\"age\": 0, \"href\": \"/v1/queues/jclouds-test/messages/526558b3f4919b655feba3a7\", \"created\": \"2013-10-21T16:39:15Z\"}, \"total\": 4, \"newest\": {\"age\": 0, \"href\": \"/v1/queues/jclouds-test/messages/526558b33ac24e663fc545e7\", \"created\": \"2013-10-21T16:39:15Z\"}, \"free\": 4}}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         QueueApi queueApi = api.getQueueApiForZoneAndClient("DFW", CLIENT_ID);
         QueueStats stats = queueApi.getStats("jclouds-test");

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
