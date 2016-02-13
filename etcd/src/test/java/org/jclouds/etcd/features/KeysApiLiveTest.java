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
package org.jclouds.etcd.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.etcd.BaseEtcdApiLiveTest;
import org.jclouds.etcd.domain.keys.Key;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;

@Test(groups = "live", testName = "KeysApiLiveTest")
public class KeysApiLiveTest extends BaseEtcdApiLiveTest {

   private String key;
   private String value;

   @BeforeClass
   protected void init() {
      key = randomString();
      value = randomString();
   }

   @Test
   public void testCreateKeyWithTTL() {
      String localKey = randomString();
      String localValue = randomString();
      Key createdKey = api().createKey(localKey, localValue, 1);
      assertNotNull(createdKey);
      assertNotNull(createdKey.node().expiration());
      assertTrue(createdKey.node().ttl() == 1);

      try {
         Thread.sleep(3000);
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      }

      createdKey = api().getKey(localKey);
      assertNull(createdKey);
   }

   @Test
   public void testCreateKey() {
      Key createdKey = api().createKey(key, value);
      assertNotNull(createdKey);
      assertTrue(createdKey.action().equals("set"));
      assertTrue(createdKey.node().value().equals(value));
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testGetKey() {
      Key getKey = api().getKey(key);
      assertNotNull(getKey);
      assertTrue(getKey.action().equals("get"));
      assertTrue(getKey.node().value().equals(value));
   }

   @Test(dependsOnMethods = "testGetKey", alwaysRun = true)
   public void testDeleteKey() {
      Key deletedKey = api().deleteKey(key);
      assertNotNull(deletedKey);
      assertTrue(deletedKey.action().equals("delete"));
      assertTrue(deletedKey.prevNode().value().equals(value));
   }

   @Test
   public void testGetNonExistentKey() {
      Key deletedKey = api().getKey(randomString());
      assertNull(deletedKey);
   }

   @Test
   public void testDeleteNonExistentKey() {
      Key deletedKey = api().deleteKey(randomString());
      assertNull(deletedKey);
   }

   private KeysApi api() {
      return api.keysApi();
   }
}
