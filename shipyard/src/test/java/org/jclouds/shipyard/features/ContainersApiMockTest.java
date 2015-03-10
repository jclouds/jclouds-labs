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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.util.List;

import org.jclouds.shipyard.ShipyardApi;
import org.jclouds.shipyard.domain.containers.ContainerInfo;
import org.jclouds.shipyard.domain.containers.DeployContainer;
import org.jclouds.shipyard.internal.BaseShipyardMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.shipyard.features.ContainersApi} class.
 */
@Test(groups = "unit", testName = "ContainersApiMockTest")
public class ContainersApiMockTest extends BaseShipyardMockTest {

   
   public void testListContainers() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/containers.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();
      try {
         assertEquals(api.listContainers().size(), 1);
         assertSent(server, "GET", "/api/containers");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testGetContainer() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/container.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();
      String containerId = "e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2";
      try {
         assertEquals(api.getContainer(containerId).id(), containerId);
         assertSent(server, "GET", "/api/containers/" + containerId);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testDeployContainer() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(201).setBody(payloadFromResource("/container-deploy-response.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();

      DeployContainer deployContainer = DeployContainer.create("nkatsaros/atlassian-stash:3.5", 
                                                         "atlassian-stash", 
                                                         8, 
                                                         8096, 
                                                         null,
                                                         null, 
                                                         null, 
                                                         Lists.newArrayList("localhost"), 
                                                         null, 
                                                         ImmutableMap.of("STASH_HOME", "/var/atlassian/stash", "STASH_VERSION", "3.5.0"), 
                                                         null, 
                                                         null, 
                                                         null);
      try {
         List<ContainerInfo> container = api.deployContainer(deployContainer);
         assertNotNull(container);
         assertEquals(container.get(0).id(), "e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2");
         assertSent(server, "POST", "/api/containers", new String(payloadFromResource("/container-deploy.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testDeleteContainer() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();
      String containerId = "e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2";
      try {
         api.deleteContainer(containerId);
         assertSent(server, "DELETE", "/api/containers/" + containerId);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testStopContainer() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();
      String containerId = "e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2";
      try {
         api.stopContainer(containerId);
         assertSent(server, "GET", "/api/containers/" + containerId + "/stop");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testStartOrRestartContainer() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      ContainersApi api = shipyardApi.containersApi();
      String containerId = "e2f6784b75ed8768e83b7ec46ca8ef784941f6ce4c53231023804277965da1d2";
      try {
         api.restartContainer(containerId);
         assertSent(server, "GET", "/api/containers/" + containerId + "/restart");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
}
