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
package org.jclouds.azurecompute.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.StorageServiceKeys;
import org.jclouds.azurecompute.domain.UpdateStorageServiceParams;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "StorageAccountApiLiveTest")
public class StorageAccountApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String NAME = String.format("%3.24s",
           RAND + StorageAccountApiLiveTest.class.getSimpleName().toLowerCase());

   private void check(final StorageService storage) {
      assertNotNull(storage.url());
      assertNotNull(storage.serviceName());
      assertNotNull(storage.storageServiceProperties());
      assertNotNull(storage.storageServiceProperties().accountType());
      assertFalse(storage.storageServiceProperties().endpoints().isEmpty());
      assertNotNull(storage.storageServiceProperties().creationTime());
   }

   public void testList() {
      for (StorageService storage : api().list()) {
         check(storage);
      }
   }

   public void testIsAvailable() {
      assertTrue(api().isAvailable(NAME).result());
   }

   @Test(dependsOnMethods = "testIsAvailable")
   public void testCreate() {
      final CreateStorageServiceParams params = CreateStorageServiceParams.builder().
              serviceName(NAME).
              description("description").
              label("label").
              location(LOCATION).
              extendedProperties(ImmutableMap.of("property_name", "property_value")).
              accountType(StorageService.AccountType.Standard_ZRS).
              build();
      final String requestId = api().create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      final StorageService service = api().get(NAME);
      assertNotNull(service);
      assertEquals(service.serviceName(), NAME);
      assertEquals(service.storageServiceProperties().description(), "description");
      assertEquals(service.storageServiceProperties().location(), LOCATION);
      assertEquals(service.storageServiceProperties().label(), "label");
      assertEquals(service.storageServiceProperties().accountType(), StorageService.AccountType.Standard_ZRS);
      assertTrue(service.extendedProperties().containsKey("property_name"));
      assertEquals(service.extendedProperties().get("property_name"), "property_value");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGetKeys() {
      final StorageServiceKeys keys = api().getKeys(NAME);
      assertNotNull(keys);
      assertNotNull(keys.url());
      assertNotNull(keys.primary());
      assertNotNull(keys.secondary());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRegenerateKeys() {
      final String requestId = api().regenerateKeys(NAME, StorageServiceKeys.KeyType.Primary);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdate() {
      final UpdateStorageServiceParams params = UpdateStorageServiceParams.builder().
              extendedProperties(ImmutableMap.of("another_property_name", "another_property_value")).
              build();
      final String requestId = api().update(NAME, params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() {
      final String requestId = api().delete(NAME);
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   private StorageAccountApi api() {
      return api.getStorageAccountApi();
   }
}
