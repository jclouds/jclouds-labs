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
import com.google.common.collect.Maps;
import org.jclouds.openstack.marconi.v1.domain.Claim;
import org.jclouds.openstack.marconi.v1.domain.CreateMessage;
import org.jclouds.openstack.marconi.v1.domain.Message;
import org.jclouds.openstack.marconi.v1.domain.MessagesCreated;
import org.jclouds.openstack.marconi.v1.internal.BaseMarconiApiLiveTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ClaimApiLiveTest", singleThreaded = true)
public class ClaimApiLiveTest extends BaseMarconiApiLiveTest {

   private static final UUID CLIENT_ID = UUID.fromString("3381af92-2b9e-11e3-b191-71861300734c");
   private final Map<String, List<String>> claimIds = Maps.newHashMap();

   public void createQueues() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZoneAndClient(zoneId, CLIENT_ID);
         boolean success = queueApi.create("jclouds-test");

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "createQueues" })
   public void createMessages() throws Exception {
      for (String zoneId : zones) {
         MessageApi messageApi = api.getMessageApiForZoneAndClientAndQueue(zoneId, CLIENT_ID, "jclouds-test");

         String json1 = "{\"event\":{\"name\":\"Austin Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage message1 = CreateMessage.builder().ttl(86400).body(json1).build();
         String json2 = "{\"event\":{\"name\":\"SF Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage message2 = CreateMessage.builder().ttl(86400).body(json2).build();
         String json3 = "{\"event\":{\"name\":\"HK Java User Group\",\"attendees\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage message3 = CreateMessage.builder().ttl(86400).body(json3).build();
         List<CreateMessage> messages = ImmutableList.of(message1, message2, message3);

         MessagesCreated messagesCreated = messageApi.create(messages);

         assertNotNull(messagesCreated);
         assertEquals(messagesCreated.getMessageIds().size(), 3);
      }
   }

   @Test(dependsOnMethods = { "createMessages" })
   public void claimMessages() throws Exception {
      for (String zoneId : zones) {
         ClaimApi claimApi = api.getClaimApiForZoneAndClientAndQueue(zoneId, CLIENT_ID, "jclouds-test");

         List<Message> messages = claimApi.claim(300, 200, 2);
         assertEquals(messages.size(), 2);

         claimIds.put(zoneId, new ArrayList<String>());

         for (Message message : messages) {
            claimIds.get(zoneId).add(message.getClaimId().get());

            assertNotNull(message.getId());
            assertTrue(message.getClaimId().isPresent());
            assertEquals(message.getTTL(), 86400);
         }
      }
   }

   @Test(dependsOnMethods = { "claimMessages" })
   public void getClaim() throws Exception {
      for (String zoneId : zones) {
         ClaimApi claimApi = api.getClaimApiForZoneAndClientAndQueue(zoneId, CLIENT_ID, "jclouds-test");

         Claim claim = claimApi.get(claimIds.get(zoneId).get(0));

         assertNotNull(claim.getId());
         assertEquals(claim.getMessages().size(), 2);
         assertEquals(claim.getTTL(), 300);

         for (Message message : claim.getMessages()) {
            assertNotNull(message.getId());
            assertTrue(message.getClaimId().isPresent());
            assertEquals(message.getTTL(), 86400);
         }
      }
   }

   @Test(dependsOnMethods = { "getClaim" })
   public void updateClaim() throws Exception {
      for (String zoneId : zones) {
         ClaimApi claimApi = api.getClaimApiForZoneAndClientAndQueue(zoneId, CLIENT_ID, "jclouds-test");

         boolean success = claimApi.update(claimIds.get(zoneId).get(0), 400);

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "updateClaim" })
   public void releaseClaim() throws Exception {
      for (String zoneId : zones) {
         ClaimApi claimApi = api.getClaimApiForZoneAndClientAndQueue(zoneId, CLIENT_ID, "jclouds-test");

         boolean success = claimApi.release(claimIds.get(zoneId).get(0));

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "releaseClaim" })
   public void delete() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZoneAndClient(zoneId, CLIENT_ID);
         boolean success = queueApi.delete("jclouds-test");

         assertTrue(success);
      }
   }
}
