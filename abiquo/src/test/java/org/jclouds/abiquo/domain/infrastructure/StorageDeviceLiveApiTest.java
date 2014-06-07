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
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 */
@Test(groups = "api", testName = "StorageDeviceLiveApiTest")
public class StorageDeviceLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      env.storageDevice.setName("Updated storage device");
      env.storageDevice.update();

      // Recover the updated storage device
      StorageDeviceDto updated = env.infrastructureApi.getStorageDevice(env.datacenter.unwrap(),
            env.storageDevice.getId());

      assertEquals(updated.getName(), "Updated storage device");
   }

   public void testListStorageDevices() {
      Iterable<StorageDevice> storageDevices = env.datacenter.listStorageDevices();
      assertEquals(size(storageDevices), 1);

      storageDevices = filter(env.datacenter.listStorageDevices(), name(env.storageDevice.getName()));
      assertEquals(size(storageDevices), 1);

      storageDevices = filter(env.datacenter.listStorageDevices(), name(env.storageDevice.getName() + "FAIL"));
      assertEquals(size(storageDevices), 0);
   }

   private static Predicate<StorageDevice> name(final String name) {
      return new Predicate<StorageDevice>() {
         @Override
         public boolean apply(StorageDevice input) {
            return input.getName().equals(name);
         }
      };
   }

}
