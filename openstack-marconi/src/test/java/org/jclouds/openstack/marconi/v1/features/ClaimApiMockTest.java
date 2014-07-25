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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.marconi.v1.domain.Claim;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ClaimApiMockTest extends BaseOpenStackMockTest<MarconiApi> {
   private static final UUID CLIENT_ID = UUID.fromString("3381af92-2b9e-11e3-b191-71861300734c");

   public void claimMessages() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"HK Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 1997, \"href\": \"/v1/queues/jclouds-test/messages/52a645633ac24e6f0be88d44?claim_id=52a64d30ef913e6d05e7f786\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 981, \"href\": \"/v1/queues/jclouds-test/messages/52a6495bef913e6d195dcffe?claim_id=52a64d30ef913e6d05e7f786\", \"ttl\": 86400}]"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         ClaimApi claimApi = api.getClaimApi("DFW", CLIENT_ID, "jclouds-test");

         List<Message> messages = claimApi.claim(300, 200, 2);

         assertEquals(messages.size(), 2);
         assertEquals(messages.get(0).getId(), "52a645633ac24e6f0be88d44");
         assertEquals(messages.get(0).getClaimId().get(), "52a64d30ef913e6d05e7f786");
         assertEquals(messages.get(0).getTTL(), 86400);
         assertEquals(messages.get(1).getId(), "52a6495bef913e6d195dcffe");
         assertEquals(messages.get(1).getClaimId().get(), "52a64d30ef913e6d05e7f786");
         assertEquals(messages.get(1).getTTL(), 86400);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "POST /v1/123123/queues/jclouds-test/claims?limit=2 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void getClaim() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(201).setBody("{\"age\": 209, \"href\": \"/v1/queues/jclouds-test/claims/52a8d23eb04a584f1bbd4f47\", \"messages\": [{\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"SF Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 12182, \"href\": \"/v1/queues/jclouds-test/messages/52a8a379b04a584f2ec2bc3e?claim_id=52a8d23eb04a584f1bbd4f47\", \"ttl\": 86400}, {\"body\": \"{\\\"event\\\":{\\\"name\\\":\\\"Austin Java User Group\\\",\\\"attendees\\\":[\\\"bob\\\",\\\"jim\\\",\\\"sally\\\"]}}\", \"age\": 12182, \"href\": \"/v1/queues/jclouds-test/messages/52a8a379b04a584f2ec2bc3f?claim_id=52a8d23eb04a584f1bbd4f47\", \"ttl\": 86400}], \"ttl\": 300}"));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         ClaimApi claimApi = api.getClaimApi("DFW", CLIENT_ID, "jclouds-test");

         Claim claim = claimApi.get("52a8d23eb04a584f1bbd4f47");

         assertEquals(claim.getMessages().size(), 2);
         assertEquals(claim.getId(), "52a8d23eb04a584f1bbd4f47");
         assertEquals(claim.getAge(), 209);
         assertEquals(claim.getTTL(), 300);

         assertEquals(claim.getMessages().get(0).getId(), "52a8a379b04a584f2ec2bc3e");
         assertEquals(claim.getMessages().get(0).getClaimId().get(), "52a8d23eb04a584f1bbd4f47");
         assertEquals(claim.getMessages().get(0).getAge(), 12182);
         assertEquals(claim.getMessages().get(0).getTTL(), 86400);

         assertEquals(claim.getMessages().get(1).getId(), "52a8a379b04a584f2ec2bc3f");
         assertEquals(claim.getMessages().get(1).getClaimId().get(), "52a8d23eb04a584f1bbd4f47");
         assertEquals(claim.getMessages().get(1).getAge(), 12182);
         assertEquals(claim.getMessages().get(1).getTTL(), 86400);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "GET /v1/123123/queues/jclouds-test/claims/52a8d23eb04a584f1bbd4f47 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   /**
    * Disabled due to PATCH with an output (body content) is not supported over HTTP.
    *
    * See https://issues.apache.org/jira/browse/JCLOUDS-405
    */
   @Test
   public void updateClaim() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         ClaimApi claimApi = api.getClaimApi("DFW", CLIENT_ID, "jclouds-test");

         boolean success = claimApi.update("52a8d23eb04a584f1bbd4f47", 400);

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "PATCH /v1/123123/queues/jclouds-test/claims/52a8d23eb04a584f1bbd4f47 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }

   public void releaseClaim() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         MarconiApi api = api(server.getUrl("/").toString(), "openstack-marconi");
         ClaimApi claimApi = api.getClaimApi("DFW", CLIENT_ID, "jclouds-test");

         boolean success = claimApi.release("52a8d23eb04a584f1bbd4f47");

         assertTrue(success);

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(), "DELETE /v1/123123/queues/jclouds-test/claims/52a8d23eb04a584f1bbd4f47 HTTP/1.1");
      }
      finally {
         server.shutdown();
      }
   }
}
