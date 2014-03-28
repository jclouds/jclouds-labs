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
package org.jclouds.rackspace.cloudbigdata.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TimeZone;

import org.jclouds.rackspace.cloudbigdata.v1.CloudBigDataApi;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Cluster.Status;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateCluster;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateCluster.ClusterType;
import org.jclouds.rackspace.cloudbigdata.v1.internal.BaseCloudBigDataApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests ProfileApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test
public class ClusterApiMockTest extends BaseCloudBigDataApiMockTest {

   public void testCreateCluster() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/cluster_create_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");

         CreateCluster createCluster = CreateCluster.builder()
               .name("slice")
               .clusterType(ClusterType.HADOOP_HDP1_1.name())
               .flavorId("4fba3bca-7c76-11e2-b737-beeffa00040e")
               .nodeCount(5)
               .postInitScript(new URI("http://example.com/configure_cluster.sh"))
               .build();

         Cluster cluster = api.create(createCluster);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/clusters", "/cluster_create_request.json");

         /*
          * Check response
          */
         assertNotNull(cluster);
         assertEquals(cluster.getId(), "db478fc1-2d86-4597-8010-cbe787bbbc41");
         TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
         assertEquals(cluster.getCreated().toString(), "Thu Dec 27 10:10:10 GMT+00:00 2012");
         assertEquals(cluster.getUpdated().toString(), "Fri Dec 27 10:10:10 GMT+00:00 2013");
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_1.name());
         assertEquals(cluster.getFlavorId(), "4fba3bca-7c76-11e2-b737-beeffa00040e");
         assertEquals(cluster.getNodeCount(), 5);
         assertEquals(cluster.getPostInitScriptStatus().toString(), "PENDING");
         assertEquals(cluster.getProgress(), 0.0F);
         assertEquals(cluster.getStatus(), Status.BUILDING);
         assertEquals(cluster.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/1234/clusters/db478fc1-2d86-4597-8010-cbe787bbbc41"));
      } finally {
         server.shutdown();
      }
   }

   public void testCreateClusterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/cluster_create_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");

         CreateCluster createCluster = CreateCluster.builder()
               .name("slice")
               .clusterType(ClusterType.HADOOP_HDP1_1.name())
               .flavorId("4fba3bca-7c76-11e2-b737-beeffa00040e")
               .nodeCount(5)
               .postInitScript(new URI("http://example.com/configure_cluster.sh"))
               .build();

         Cluster cluster = api.create(createCluster);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/clusters", "/cluster_create_request.json");

         /*
          * Check response
          */
         assertNull(cluster);
      } finally {
         server.shutdown();
      }
   }

   public void testGetCluster() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/cluster_get_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.get("5");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/clusters/5");

         /*
          * Check response
          */
         assertNotNull(cluster);
         assertEquals(cluster.getId(), "db478fc1-2d86-4597-8010-cbe787bbbc41");
         TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
         assertEquals(cluster.getCreated().toString(), "Thu Dec 27 10:10:10 GMT+00:00 2012");
         assertEquals(cluster.getUpdated().toString(), "Fri Dec 27 10:10:10 GMT+00:00 2013");
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_1.name());
         assertEquals(cluster.getFlavorId(), "4fba3bca-7c76-11e2-b737-beeffa00040e");
         assertEquals(cluster.getNodeCount(), 5);
         assertEquals(cluster.getPostInitScriptStatus().toString(), "PENDING");
         assertEquals(cluster.getProgress(), 1.0F);
         assertEquals(cluster.getStatus(), Status.ACTIVE);
         assertEquals(cluster.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/1234/clusters/db478fc1-2d86-4597-8010-cbe787bbbc41"));
      } finally {
         server.shutdown();
      }
   }

   public void testGetClusterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/cluster_get_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.get("5");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/clusters/5");

         /*
          * Check response
          */
         assertNull(cluster);
      } finally {
         server.shutdown();
      }
   }

   public void testListClusters() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/cluster_list_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         FluentIterable<Cluster> clusters = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/clusters");

         /*
          * Check response
          */
         assertNotNull(clusters);
         assertEquals(clusters.size(), 2);
         Cluster cluster = clusters.get(0);
         assertEquals(cluster.getId(), "db478fc1-2d86-4597-8010-cbe787bbbc41");
         TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
         assertEquals(cluster.getCreated().toString(), "Thu Dec 27 10:10:10 GMT+00:00 2012");
         assertEquals(cluster.getUpdated().toString(), "Fri Dec 27 10:10:10 GMT+00:00 2013");
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_1.name());
         assertEquals(cluster.getFlavorId(), "4fba3bca-7c76-11e2-b737-beeffa00040e");
         assertEquals(cluster.getNodeCount(), 5);
         assertEquals(cluster.getPostInitScriptStatus().toString(), "SUCCEEDED");
         assertEquals(cluster.getProgress(), 1.0F);
         assertEquals(cluster.getStatus(), Status.ACTIVE);
         assertEquals(cluster.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/1234/clusters/db478fc1-2d86-4597-8010-cbe787bbbc41"));
      } finally {
         server.shutdown();
      }
   }

   public void testListClustersFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/cluster_list_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         FluentIterable<Cluster> clusters = api.list();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/clusters");

         /*
          * Check response
          */
         assertNotNull(clusters);
         assertEquals(clusters.size(), 0);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteCluster() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/cluster_delete_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.delete("5");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/clusters/5");

         /*
          * Check response
          */
         assertNotNull(cluster);
         assertEquals(cluster.getId(), "db478fc1-2d86-4597-8010-cbe787bbbc41");
         TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
         assertEquals(cluster.getCreated().toString(), "Thu Dec 27 10:10:10 GMT+00:00 2012");
         assertEquals(cluster.getUpdated().toString(), "Fri Dec 27 10:10:10 GMT+00:00 2013");
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_1.name());
         assertEquals(cluster.getFlavorId(), "4fba3bca-7c76-11e2-b737-beeffa00040e");
         assertEquals(cluster.getNodeCount(), 5);
         assertEquals(cluster.getPostInitScriptStatus(), null);
         assertEquals(cluster.getProgress(), 0.0F);
         assertEquals(cluster.getStatus(), Status.DELETING);
         assertEquals(cluster.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/1234/clusters/db478fc1-2d86-4597-8010-cbe787bbbc41"));
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteClusterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/cluster_delete_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.delete("5");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1.0/888888/clusters/5");

         /*
          * Check response
          */
         assertNull(cluster);
      } finally {
         server.shutdown();
      }
   }

   public void testResizeCluster() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/cluster_resize_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.resize("5", 10);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/clusters/5/action", "/cluster_resize_request.json");

         /*
          * Check response
          */
         assertNotNull(cluster);
         assertEquals(cluster.getId(), "db478fc1-2d86-4597-8010-cbe787bbbc41");
         TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
         assertEquals(cluster.getCreated().toString(), "Thu Dec 27 10:10:10 GMT+00:00 2012");
         assertEquals(cluster.getUpdated().toString(), "Fri Dec 27 10:10:10 GMT+00:00 2013");
         assertEquals(cluster.getName(), "slice");
         assertEquals(cluster.getClusterType(), ClusterType.HADOOP_HDP1_1.name());
         assertEquals(cluster.getFlavorId(), "4fba3bca-7c76-11e2-b737-beeffa00040e");
         assertEquals(cluster.getNodeCount(), 10);
         assertEquals(cluster.getPostInitScriptStatus().toString(), "PENDING");
         assertEquals(cluster.getProgress(), 0.5F);
         assertEquals(cluster.getStatus(), Status.UPDATING);
         assertEquals(cluster.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/1234/clusters/db478fc1-2d86-4597-8010-cbe787bbbc41"));
      } finally {
         server.shutdown();
      }
   }

   public void testResizeClusterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/cluster_resize_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ClusterApi api = cbdApi.getClusterApiForZone("ORD");         

         Cluster cluster = api.resize("5", 10);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/clusters/5/action", "/cluster_resize_request.json");

         /*
          * Check response
          */
         assertNull(cluster);
      } finally {
         server.shutdown();
      }
   }
}
