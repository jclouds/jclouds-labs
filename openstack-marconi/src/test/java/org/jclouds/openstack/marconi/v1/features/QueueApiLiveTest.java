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
import com.google.common.collect.ImmutableMap;
import org.jclouds.openstack.marconi.v1.domain.CreateMessage;
import org.jclouds.openstack.marconi.v1.domain.Queue;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.marconi.v1.domain.Queues;
import org.jclouds.openstack.marconi.v1.internal.BaseMarconiApiLiveTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.jclouds.openstack.marconi.v1.options.ListQueuesOptions.Builder.limit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "QueueApiLiveTest", singleThreaded = true)
public class QueueApiLiveTest extends BaseMarconiApiLiveTest {
   private static final UUID CLIENT_ID = UUID.fromString("3381af92-2b9e-11e3-b191-71861300734c");

   public void listZeroPagesOfQueues() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         List<Queue> queues = queueApi.list(false).concat().toList();

         assertTrue(queues.isEmpty());
      }
   }

   @Test(dependsOnMethods = { "listZeroPagesOfQueues" })
   public void create() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);

         for (int i=0; i < 6; i++) {
            boolean success = queueApi.create("jclouds-test-" + i);

            assertTrue(success);
         }
      }
   }

   @Test(dependsOnMethods = { "create" })
   public void listOnePageOfQueues() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         List<Queue> queues = queueApi.list(false).concat().toList();

         assertEquals(queues.size(), 6);

         for (Queue queue: queues) {
            assertNotNull(queue.getName());
            assertFalse(queue.getMetadata().isPresent());
         }
      }
   }

   @Test(dependsOnMethods = { "listOnePageOfQueues" })
   public void createMore() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);

         for (int i=6; i < 12; i++) {
            boolean success = queueApi.create("jclouds-test-" + i);

            assertTrue(success);
         }
      }
   }

   @Test(dependsOnMethods = { "createMore" })
   public void listManyPagesOfQueues() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         List<Queue> queues = queueApi.list(false).concat().toList();

         assertEquals(queues.size(), 12);

         for (Queue queue: queues) {
            assertNotNull(queue.getName());
            assertFalse(queue.getMetadata().isPresent());
         }
      }
   }

   @Test(dependsOnMethods = { "listManyPagesOfQueues" })
   public void listManyPagesOfQueuesManually() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);

         Queues queues = queueApi.list(limit(6));

         while(queues.nextMarker().isPresent()) {
            assertEquals(queues.size(), 6);

            for (Queue queue: queues) {
               assertNotNull(queue.getName());
               assertFalse(queue.getMetadata().isPresent());
            }

            queues = queueApi.list(queues.nextListQueuesOptions());
         }
      }
   }

   @Test(dependsOnMethods = { "listManyPagesOfQueuesManually" })
   public void exists() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         boolean success = queueApi.exists("jclouds-test-1");

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "exists" })
   public void setMetadata() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         Map<String, String> metadata = ImmutableMap.of("key1", "value1");
         boolean success = queueApi.setMetadata("jclouds-test-1", metadata);

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "setMetadata" })
   public void listManyPagesOfQueuesWithDetails() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         List<Queue> queues = queueApi.list(true).concat().toList();

         assertEquals(queues.size(), 12);

         for (Queue queue: queues) {
            assertNotNull(queue.getName());
            assertTrue(queue.getMetadata().isPresent());

            if (queue.getName().equals("jclouds-test-1")) {
               assertEquals(queue.getMetadata().get().get("key1"), "value1");
            }
            else {
               assertTrue(queue.getMetadata().get().isEmpty());
            }
         }
      }
   }

   @Test(dependsOnMethods = { "listManyPagesOfQueuesWithDetails" })
   public void getMetadata() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         Map<String, String> metadata = queueApi.getMetadata("jclouds-test-1");

         assertEquals(metadata.get("key1"), "value1");
      }
   }

   @Test(dependsOnMethods = { "getMetadata" })
   public void getStatsWithoutTotal() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         QueueStats stats = queueApi.getStats("jclouds-test-1");

         assertEquals(stats.getMessagesStats().getClaimed(), 0);
         assertEquals(stats.getMessagesStats().getFree(), 0);
         assertEquals(stats.getMessagesStats().getTotal(), 0);
         assertFalse(stats.getMessagesStats().getOldest().isPresent());
         assertFalse(stats.getMessagesStats().getNewest().isPresent());
      }
   }

   @Test(dependsOnMethods = { "getStatsWithoutTotal" })
   public void getStatsWithTotal() throws Exception {
      for (String zoneId : zones) {
         MessageApi messageApi = api.getMessageApiForZoneAndQueue(zoneId, CLIENT_ID, "jclouds-test-1");

         String json1 = "{\"event\":{\"type\":\"hockey\",\"players\":[\"bob\",\"jim\",\"sally\"]}}";
         CreateMessage message1 = CreateMessage.builder().ttl(120).body(json1).build();
         List<CreateMessage> message = ImmutableList.of(message1);

         messageApi.create(message);

         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         QueueStats stats = queueApi.getStats("jclouds-test-1");

         assertEquals(stats.getMessagesStats().getClaimed(), 0);
         assertEquals(stats.getMessagesStats().getFree(), 1);
         assertEquals(stats.getMessagesStats().getTotal(), 1);
         assertTrue(stats.getMessagesStats().getOldest().isPresent());
         assertNotNull(stats.getMessagesStats().getOldest().get().getId());
         assertTrue(stats.getMessagesStats().getNewest().isPresent());
         assertNotNull(stats.getMessagesStats().getNewest().get().getId());
      }
   }

   @Test(dependsOnMethods = { "getStatsWithTotal" })
   public void delete() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);

         for (int i=0; i < 12; i++) {
            boolean success = queueApi.delete("jclouds-test-" + i);

            assertTrue(success);
         }
      }
   }

   @Test(dependsOnMethods = { "delete" })
   public void doesNotExist() throws Exception {
      for (String zoneId : zones) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId, CLIENT_ID);
         boolean success = queueApi.exists("jclouds-test-1");

         assertFalse(success);
      }
   }
}
