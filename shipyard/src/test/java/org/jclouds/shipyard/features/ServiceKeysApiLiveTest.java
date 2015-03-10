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
package org.jclouds.shipyard.features;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import org.jclouds.shipyard.domain.servicekeys.ServiceKey;
import org.jclouds.shipyard.internal.BaseShipyardApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServiceKeysApiLiveTest", singleThreaded = true)
public class ServiceKeysApiLiveTest extends BaseShipyardApiLiveTest {

   private final String serviceKeyDescription = "ShipyardJCloudsLiveTest";
   private String serviceKey = null;
   
   @AfterClass (alwaysRun = true)
   protected void tearDown() {
      assertNotNull(serviceKey, "Expected serviceKey to be set but was not");
      boolean removed = api().deleteServiceKey(serviceKey);
      assertTrue(removed);
   }
   
   public void testCreateServiceKey() throws Exception {
     ServiceKey possibleServiceKey = api().createServiceKey(serviceKeyDescription);
     assertNotNull(possibleServiceKey, "Did not successfully create ServiceKey");
     assertTrue(possibleServiceKey.description().equals(serviceKeyDescription), "ServiceKey description returned from Shipyard did not match expected value");
     serviceKey = possibleServiceKey.key();
   }
   
   @Test (dependsOnMethods = "testCreateServiceKey")
   public void testListServiceKeys() throws Exception {
      List<ServiceKey> possibleServiceKeys = api().listServiceKeys();
      assertNotNull(possibleServiceKeys, "possibleServiceKeys was not set");
      assertTrue(possibleServiceKeys.size() > 0, "Expected at least 1 ServiceKey but list was empty");
      boolean serviceKeyFound = false;
      for (ServiceKey possibleKey : possibleServiceKeys) {
         if (possibleKey.key().equals(serviceKey)) {
            serviceKeyFound = true;
         }
      }
      assertTrue(serviceKeyFound, "Expected but could not find ServiceKey amongst " + possibleServiceKeys.size() + " found");
   }
   
   public void testRemoveNonExistentServiceKey() throws Exception {
      boolean removed = api().deleteServiceKey(UUID.randomUUID().toString().replaceAll("-", ""));
      assertFalse(removed);
   }
   
   private ServiceKeysApi api() {
      return api.serviceKeysApi();
   }
}
