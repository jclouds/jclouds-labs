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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import org.jclouds.etcd.EtcdApi;
import org.jclouds.etcd.domain.miscellaneous.Version;
import org.jclouds.etcd.internal.BaseEtcdMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.etcd.features.MiscellaneousApi} class.
 */
@Test(groups = "unit", testName = "MiscellaneousApiMockTest")
public class MiscellaneousApiMockTest extends BaseEtcdMockTest {

   private final String versionRegex = "^\\d+\\.\\d+\\.\\d+$";

   public void testGetVersion() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/version.json")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      MiscellaneousApi api = etcdJavaApi.miscellaneousApi();
      try {
         Version version = api.version();
         assertNotNull(version);
         assertTrue(version.etcdServer().matches(versionRegex));
         assertTrue(version.etcdCluster().matches(versionRegex));
         assertSent(server, "GET", "/version");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetHealth() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/health.json")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      MiscellaneousApi api = etcdJavaApi.miscellaneousApi();
      try {
         boolean health = api.health();
         assertTrue(health);
         assertSent(server, "GET", "/health");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetBadHealth() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/health-bad.json")).setResponseCode(503));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      MiscellaneousApi api = etcdJavaApi.miscellaneousApi();
      try {
         boolean health = api.health();
         assertFalse(health);
         assertSent(server, "GET", "/health");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetMetrics() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/metrics.txt")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      MiscellaneousApi api = etcdJavaApi.miscellaneousApi();
      try {
         String metrics = api.metrics();
         assertNotNull(metrics);
         assertSentAcceptText(server, "GET", "/metrics");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

}
