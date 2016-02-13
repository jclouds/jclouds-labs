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

import org.jclouds.etcd.EtcdApi;
import org.jclouds.etcd.EtcdApiMetadata;
import org.jclouds.etcd.domain.keys.Key;
import org.jclouds.etcd.internal.BaseEtcdMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.etcd.features.KeysApi} class.
 */
@Test(groups = "unit", testName = "KeysApiMockTest")
public class KeysApiMockTest extends BaseEtcdMockTest {

   public void testCreateKey() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/keys-create.json")).setResponseCode(201));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key createdKey = api.createKey("hello", "world");
         assertNotNull(createdKey);
         assertTrue(createdKey.node().key().equals("/hello"));
         assertTrue(createdKey.node().value().equals("world"));
         assertSentWithFormData(server, "PUT", "/" + EtcdApiMetadata.API_VERSION + "/keys/hello", "value=world");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testCreateKeyWithTTL() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/keys-create-ttl.json")).setResponseCode(201));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key createdKey = api.createKey("hello", "world", 5);
         assertNotNull(createdKey);
         assertNotNull(createdKey.node().expiration());
         assertTrue(createdKey.node().ttl() == 5);
         assertTrue(createdKey.node().key().equals("/hello"));
         assertTrue(createdKey.node().value().equals("world"));
         assertSentWithFormData(server, "PUT", "/" + EtcdApiMetadata.API_VERSION + "/keys/hello", "value=world&ttl=5");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testGetKey() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/keys-get.json")).setResponseCode(200));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key foundKey = api.getKey("hello");
         assertNotNull(foundKey);
         assertTrue(foundKey.node().key().equals("/hello"));
         assertTrue(foundKey.node().value().equals("world"));
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/keys/hello");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testGetNonExistentKey() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/keys-get-delete-nonexistent.json")).setResponseCode(404));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key nonExistentKey = api.getKey("NonExistentKeyToGet");
         assertNull(nonExistentKey);
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/keys/NonExistentKeyToGet");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testDeleteKey() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/keys-delete.json")).setResponseCode(200));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key deletedKey = api.deleteKey("hello");
         assertTrue(deletedKey.prevNode().key().equals("/hello"));
         assertTrue(deletedKey.prevNode().value().equals("world"));
         assertSent(server, "DELETE", "/" + EtcdApiMetadata.API_VERSION + "/keys/hello");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNonExistentKey() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/keys-get-delete-nonexistent.json")).setResponseCode(404));
      EtcdApi etcdApi = api(server.getUrl("/"));
      KeysApi api = etcdApi.keysApi();
      try {
         Key nonExistentKey = api.deleteKey("NonExistentKeyToDelete");
         assertNull(nonExistentKey);
         assertSent(server, "DELETE", "/" + EtcdApiMetadata.API_VERSION + "/keys/NonExistentKeyToDelete");
      } finally {
         etcdApi.close();
         server.shutdown();
      }
   }
}
