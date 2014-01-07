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
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "AccountApiLiveTest")
public class AccountApiLiveTest extends BaseSwiftApiLiveTest {

   public void get() throws Exception {
      for (String regionId : regions) {
         AccountApi accountApi = api.accountApiInRegion(regionId);
         Account account = accountApi.get();

         assertNotNull(account);
         assertTrue(account.containerCount() >= 0);
         assertTrue(account.objectCount() >= 0);
         assertTrue(account.bytesUsed() >= 0);
      }
   }

   public void updateMetadata() throws Exception {
      for (String regionId : regions) {
         AccountApi accountApi = api.accountApiInRegion(regionId);

         Map<String, String> meta = ImmutableMap.of("MyAdd1", "foo", "MyAdd2", "bar");

         assertTrue(accountApi.updateMetadata(meta));

         accountHasMetadata(accountApi, meta);
      }
   }

   public void deleteMetadata() throws Exception {
      for (String regionId : regions) {
         AccountApi accountApi = api.accountApiInRegion(regionId);

         Map<String, String> meta = ImmutableMap.of("MyDelete1", "foo", "MyDelete2", "bar");

         assertTrue(accountApi.updateMetadata(meta));
         accountHasMetadata(accountApi, meta);

         assertTrue(accountApi.deleteMetadata(meta));
         Account account = accountApi.get();
         for (Entry<String, String> entry : meta.entrySet()) {
            // note keys are returned in lower-case!
            assertFalse(account.metadata().containsKey(entry.getKey().toLowerCase()));
         }
      }
   }

   static void accountHasMetadata(AccountApi accountApi, Map<String, String> meta) {
      Account account = accountApi.get();
      for (Entry<String, String> entry : meta.entrySet()) {
         // note keys are returned in lower-case!
         assertEquals(account.metadata().get(entry.getKey().toLowerCase()), entry.getValue(), //
               account + " didn't have metadata: " + entry);
      }
   }
}
