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

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import org.jclouds.etcd.EtcdApi;
import org.jclouds.etcd.EtcdApiMetadata;
import org.jclouds.etcd.domain.statistics.Leader;
import org.jclouds.etcd.domain.statistics.Self;
import org.jclouds.etcd.domain.statistics.Store;
import org.jclouds.etcd.internal.BaseEtcdMockTest;

import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.etcd.features.StatisticsApi} class.
 */
@Test(groups = "unit", testName = "StatisticsApiMockTest")
public class StatisticsApiMockTest extends BaseEtcdMockTest {

   public void testGetLeader() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/leader.json")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      StatisticsApi api = etcdJavaApi.statisticsApi();
      try {
         Leader leader = api.leader();
         assertNotNull(leader);
         assertTrue(leader.followers().size() == 2);
         assertTrue(leader.leader().equals("924e2e83e93f2560"));
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/stats/leader");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetSelf() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/self.json")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      StatisticsApi api = etcdJavaApi.statisticsApi();
      try {
         Self self = api.self();
         assertNotNull(self);
         assertTrue(self.leaderInfo().leader().equals("924e2e83e93f2560"));
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/stats/self");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }

   public void testGetStore() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/store.json")).setResponseCode(200));
      EtcdApi etcdJavaApi = api(server.getUrl("/"));
      StatisticsApi api = etcdJavaApi.statisticsApi();
      try {
         Store store = api.store();
         assertNotNull(store);
         assertTrue(store.getsSuccess() == 75);
         assertSent(server, "GET", "/" + EtcdApiMetadata.API_VERSION + "/stats/store");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
   }
}
