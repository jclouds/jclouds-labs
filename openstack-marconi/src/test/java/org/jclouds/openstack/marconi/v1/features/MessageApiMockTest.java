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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.marconi.v1.domain.CreateMessage;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.marconi.v1.domain.MessageStream;
import org.jclouds.openstack.marconi.v1.domain.MessagesCreated;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.jclouds.openstack.marconi.v1.options.StreamMessagesOptions.Builder.limit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Everett Toews
 */
@Test
public class MessageApiMockTest extends BaseOpenStackMockTest<MarconiApi> {
   private static final UUID CLIENT_ID = UUID.fromString("3381af92-2b9e-11e3-b191-71861300734c");

   public void createMessage() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(201).setBody("{\"partial\": false, \"resources\": [\"/v1/queues/jclouds-test/messages/526550ecef913e655ff84db8\"]}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         String json1 = "{\"event\":{\"name\":\"Edmonton Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage createMessage1 = CreateMessage.builder().ttl(120).body(json1).build();
         List<CreateMessage> createMessages = ImmutableList.of(createMessage1);

         MessagesCreated messagesCreated = messageApi.create(createMessages);

         assertNotNull(messagesCreated);
         assertEquals(messagesCreated.getMessageIds().size(), 1);
         assertEquals(messagesCreated.getMessageIds().get(0), "526550ecef913e655ff84db8");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "POST /v1/123123/queues/jclouds-test/messages HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void createMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(201).setBody("{\"partial\": false, \"resources\": [\"/v1/queues/jclouds-test/messages/5265540ef4919b655da1760a\", \"/v1/queues/jclouds-test/messages/5265540ef4919b655da1760b\", \"/v1/queues/jclouds-test/messages/5265540ef4919b655da1760c\"]}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         String json1 = "{\"event\":{\"name\":\"Austin Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage createMessage1 = CreateMessage.builder().ttl(120).body(json1).build();
         String json2 = "{\"event\":{\"name\":\"SF Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage createMessage2 = CreateMessage.builder().ttl(120).body(json2).build();
         String json3 = "{\"event\":{\"name\":\"HK Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage createMessage3 = CreateMessage.builder().ttl(120).body(json3).build();
         List<CreateMessage> createMessages = ImmutableList.of(createMessage1, createMessage2, createMessage3);

         MessagesCreated messagesCreated = messageApi.create(createMessages);

         assertNotNull(messagesCreated);
         assertEquals(messagesCreated.getMessageIds().size(), 3);
         assertTrue(messagesCreated.getMessageIds().contains("5265540ef4919b655da1760a"));
         assertTrue(messagesCreated.getMessageIds().contains("5265540ef4919b655da1760b"));
         assertTrue(messagesCreated.getMessageIds().contains("5265540ef4919b655da1760c"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "POST /v1/123123/queues/jclouds-test/messages HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void streamZeroPagesOfMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         MessageStream messageStream = messageApi.stream();

         assertTrue(Iterables.isEmpty(messageStream));
         assertFalse(messageStream.nextMarker().isPresent());

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void streamOnePageOfMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\": [{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7353, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba1\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7353, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba2\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7353, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba3\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7342, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc558e\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7342, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc558f\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 7342, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc5590\", \"ttl\": 86400}], \"links\": [{\"href\": \"/v1/queues/jclouds-test/messages?marker=4512\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         MessageStream messageStream = messageApi.stream();

         while(messageStream.nextMarker().isPresent()) {
            assertEquals(Iterables.size(messageStream), 6);

            messageStream = messageApi.stream(messageStream.nextStreamOptions());
         }

         assertFalse(messageStream.nextMarker().isPresent());

         assertEquals(server.getRequestCount(), 3);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?marker=4512 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void streamManyPagesOfMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\": [{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8082, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba1\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8082, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba2\", \"ttl\": 86400}], \"links\": [{\"href\": \"/v1/queues/jclouds-test/messages?marker=4508&limit=2\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\": [{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8082, \"href\": \"/v1/queues/jclouds-test/messages/526ec635b04a5866dbe31ba3\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8071, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc558e\", \"ttl\": 86400}], \"links\": [{\"href\": \"/v1/queues/jclouds-test/messages?marker=4510&limit=2\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"messages\": [{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8071, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc558f\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 8071, \"href\": \"/v1/queues/jclouds-test/messages/526ec640f4919b69a7bc5590\", \"ttl\": 86400}], \"links\": [{\"href\": \"/v1/queues/jclouds-test/messages?marker=4512&limit=2\", \"rel\": \"next\"}]}"));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         MessageStream messageStream = messageApi.stream(limit(2));

         while(messageStream.nextMarker().isPresent()) {
            assertEquals(Iterables.size(messageStream), 2);

            messageStream = messageApi.stream(messageStream.nextStreamOptions());
         }

         assertFalse(messageStream.nextMarker().isPresent());

         assertEquals(server.getRequestCount(), 5);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?limit=2 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?marker=4508&limit=2 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?marker=4510&limit=2 HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?marker=4512&limit=2 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void listMessagesByIds() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 1596, \"href\": \"/v1/queues/jclouds-test/messages/messages/52928896b04a584f24883227\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 1596, \"href\": \"/v1/queues/jclouds-test/messages/messages/52928896b04a584f24883228\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 1596, \"href\": \"/v1/queues/jclouds-test/messages/messages/52928896b04a584f24883229\", \"ttl\": 86400}]"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");
         List<String> ids = ImmutableList.of("52928896b04a584f24883227", "52928896b04a584f24883228", "52928896b04a584f24883229");

         List<Message> messages = messageApi.list(ids);

         assertEquals(messages.size(), 3);

         for (Message message: messages) {
            assertNotNull(message.getId());
            assertNotNull(message.getBody());
            assertEquals(message.getAge(), 1596);
            assertEquals(message.getTTL(), 86400);
         }

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages?ids=52928896b04a584f24883227,52928896b04a584f24883228,52928896b04a584f24883229 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void getMessage() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Edmonton Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 266, \"href\": \"/v1/queues/jclouds-test/messages/5292b30cef913e6d026f4dec\", \"ttl\": 86400}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         Message message = messageApi.get("5292b30cef913e6d026f4dec");

         assertEquals(message.getId(), "5292b30cef913e6d026f4dec");
         assertEquals(message.getBody(), "{\"event\":{\"name\":\"Edmonton Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}");
         assertEquals(message.getAge(), 266);
         assertEquals(message.getTTL(), 86400);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/messages/5292b30cef913e6d026f4dec HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void deleteMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");
         List<String> ids = ImmutableList.of("52936b8a3ac24e6ef4c067dd", "5292b30cef913e6d026f4dec");

         boolean success = messageApi.delete(ids);

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "DELETE /v1/123123/queues/jclouds-test/messages?ids=52936b8a3ac24e6ef4c067dd,5292b30cef913e6d026f4dec HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void deleteMessageByClaimId() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         MessageApi messageApi = api.getMessageApiForZoneAndQueue("DFW", CLIENT_ID, "jclouds-test");

         boolean success = messageApi.deleteByClaim("52936b8a3ac24e6ef4c067dd", "5292b30cef913e6d026f4dec");

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "DELETE /v1/123123/queues/jclouds-test/messages/52936b8a3ac24e6ef4c067dd?claim_id=5292b30cef913e6d026f4dec HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }
}
