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
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.marconi.v1.internal.BaseMarconiApiLiveTest;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "QueueApiLiveTest", singleThreaded = true)
public class QueueApiLiveTest extends BaseMarconiApiLiveTest {

   public void create() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         boolean success = queueApi.create("jclouds-test");

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "create" })
   public void exists() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         boolean success = queueApi.exists("jclouds-test");

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "exists" })
   public void setMetadata() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         Map<String, String> metadata = ImmutableMap.of("key1", "value1");
         boolean success = queueApi.setMetadata("jclouds-test", metadata);

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "setMetadata" })
   public void getMetadata() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         Map<String, String> metadata = queueApi.getMetadata("jclouds-test");

         assertEquals(metadata.get("key1"), "value1");
      }
   }

   @Test(dependsOnMethods = { "getMetadata" })
   public void getStatsWithoutTotal() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         QueueStats stats = queueApi.getStats("jclouds-test");

         assertEquals(stats.getMessagesStats().getClaimed(), 0);
         assertEquals(stats.getMessagesStats().getFree(), 0);
         assertEquals(stats.getMessagesStats().getTotal(), 0);
         assertFalse(stats.getMessagesStats().getOldest().isPresent());
         assertFalse(stats.getMessagesStats().getNewest().isPresent());
      }
   }

   @Test(dependsOnMethods = { "getStatsWithoutTotal" })
   public void delete() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         boolean success = queueApi.delete("jclouds-test");

         assertTrue(success);
      }
   }

   @Test(dependsOnMethods = { "delete" })
   public void doesNotExist() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         QueueApi queueApi = api.getQueueApiForZone(zoneId);
         boolean success = queueApi.exists("jclouds-test");

         assertFalse(success);
      }
   }
}
