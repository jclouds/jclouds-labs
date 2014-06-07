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
package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 */
@Test(groups = "api", testName = "StoragePoolLiveApiTest")
public class StoragePoolLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testGetDevice() {
      StorageDevice device = env.storagePool.getStorageDevice();
      assertNotNull(device);
      assertEquals(device.getId(), env.storageDevice.getId());
   }

   public void testUpdate() {
      try {
         Tier tier3 = find(env.datacenter.listTiers(), new Predicate<Tier>() {
            @Override
            public boolean apply(Tier input) {
               return input.getName().equals("Default Tier 3");
            }
         });
         env.storagePool.setTier(tier3);
         env.storagePool.update();

         assertEquals(env.storagePool.getTier().getName(), "Default Tier 3");
      } finally {
         // Restore the original tier
         env.storagePool.setTier(env.tier);
         env.storagePool.update();
         assertEquals(env.storagePool.getTier().getId(), env.tier.getId());
      }
   }

   public void testListStoragePool() {
      Iterable<StoragePool> storagePools = env.storageDevice.listStoragePools();
      assertEquals(size(storagePools), 1);

      storagePools = filter(env.storageDevice.listStoragePools(), name(env.storagePool.getName()));
      assertEquals(size(storagePools), 1);

      storagePools = filter(env.storageDevice.listStoragePools(), name(env.storagePool.getName() + "FAIL"));
      assertEquals(size(storagePools), 0);
   }

   public void testRefreshStoragePool() {
      env.storagePool.refresh();
   }

   private static Predicate<StoragePool> name(final String name) {
      return new Predicate<StoragePool>() {
         @Override
         public boolean apply(StoragePool input) {
            return input.getName().equals(name);
         }
      };
   }

}
