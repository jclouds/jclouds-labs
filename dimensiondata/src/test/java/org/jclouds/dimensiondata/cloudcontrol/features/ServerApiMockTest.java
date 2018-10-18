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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CloneServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CreateServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.jclouds.http.Uris;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.util.List;

import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters.Builder.datacenterId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link ServerApi} class.
 */
@Test(groups = "unit", testName = "ServerApiMockTest", singleThreaded = true)
public class ServerApiMockTest extends BaseAccountAwareCloudControlMockTest {

   public void testDeployServerReturnsUnexpectedError() throws InterruptedException {
      server.enqueue(responseUnexpectedError());

      NetworkInfo networkInfo = NetworkInfo
            .create("networkDomainId", NIC.builder().vlanId("vlanId").build(), Lists.<NIC>newArrayList());
      try {
         serverApi().deployServer(ServerApiMockTest.class.getSimpleName(), "imageId", true, networkInfo,
               "administratorPassword");
         failBecauseExceptionWasNotThrown(ResourceNotFoundException.class);
      } catch (ResourceNotFoundException e) {
         assertNotNull(e);
         assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/deployServer");
      }
   }

   public void testDeployServerWithSpecificCpu() throws InterruptedException {
      server.enqueue(jsonResponse("/deployServerResponse.json"));

      NetworkInfo networkInfo = NetworkInfo
            .create("networkDomainId", NIC.builder().vlanId("vlanId").build(), Lists.<NIC>newArrayList());

      CreateServerOptions createServerOptions = CreateServerOptions.builder()
            .cpu(CPU.builder().count(1).speed("HIGHPERFORMANCE").coresPerSocket(2).build()).build();
      final String serverId = serverApi()
            .deployServer(ServerApiMockTest.class.getSimpleName(), "imageId", true, networkInfo,
                  "administratorPassword", Lists.<Disk>newArrayList(), createServerOptions);
      assertEquals(serverId, "7b62aae5-bdbe-4595-b58d-c78f95db2a7f");
      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/deployServer");
      assertBodyContains(recordedRequest, "\"cpu\":{\"count\":1,\"speed\":\"HIGHPERFORMANCE\",\"coresPerSocket\":2}");
   }

   public void testClone() throws Exception {
      server.enqueue(jsonResponse("/cloneServerResponse.json"));
      CloneServerOptions cloneServerOptions = CloneServerOptions.builder().description("description")
            .clusterId("EU6-02").guestOsCustomization(true).build();
      final String imageId = serverApi()
            .cloneServer("9ed47330-5561-11e5-8c14-b8ca3a5d9ef8", "serverNewImageName", cloneServerOptions);
      assertEquals(imageId, "3389ffe8-c3fc-11e3-b29c-001517c4643e");

      RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/cloneServer");

      assertBodyContains(recordedRequest,
            "{\"id\":\"9ed47330-5561-11e5-8c14-b8ca3a5d9ef8\",\"imageName\":\"serverNewImageName\","
                  + "\"description\":\"description\",\"clusterId\":\"EU6-02\",\"guestOsCustomization\":true}");
   }

   public void testListServers() throws Exception {
      server.enqueue(jsonResponse("/servers.json"));
      List<Server> servers = serverApi().listServers().concat().toList();
      Uris.UriBuilder uriBuilder = getListServerUriBuilder();
      assertSent(GET, uriBuilder.toString());
      assertEquals(servers.size(), 1);
      for (Server s : servers) {
         assertNotNull(s);
      }
   }

   public void testListServersWithDatacenterFiltering() throws Exception {
      server.enqueue(jsonResponse("/servers.json"));
      List<Server> servers = serverApi().listServers(datacenterId(datacenters)).toList();
      Uris.UriBuilder uriBuilder = addZonesToUriBuilder("datacenterId", getListServerUriBuilder());
      assertSent(GET, uriBuilder.toString());
      assertEquals(servers.size(), 1);
      for (Server s : servers) {
         assertNotNull(s);
      }
   }

   private Uris.UriBuilder getListServerUriBuilder() {
      return Uris.uriBuilder("/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/server");
   }

   public void testListServers_404() throws Exception {
      server.enqueue(response404());
      assertTrue(serverApi().listServers().concat().isEmpty());
      assertSent(HttpMethod.GET, getListServerUriBuilder().toString());
   }

   public void testGetServer() throws Exception {
      server.enqueue(jsonResponse("/server.json"));
      Server found = serverApi().getServer("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/server/12345");
      assertNotNull(found);
      assertNotNull(found.guest().vmTools());
   }

   public void testGetServer_404() throws Exception {
      server.enqueue(response404());
      serverApi().getServer("12345");
      assertSent(GET, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/server/12345");
   }

   public void testDeleteServer() throws Exception {
      server.enqueue(jsonResponse("/deleteServer.json"));
      serverApi().deleteServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/deleteServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   public void testDeleteServer_404() throws Exception {
      server.enqueue(response404());
      serverApi().deleteServer("12345");
      assertSent(POST, "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/deleteServer");
   }

   public void testPowerOffServer() throws Exception {
      server.enqueue(jsonResponse("/powerOffServer.json"));
      serverApi().powerOffServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/powerOffServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   public void testRebootServer() throws Exception {
      server.enqueue(jsonResponse("/rebootServer.json"));
      serverApi().rebootServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/rebootServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   public void testReconfigureServer() throws Exception {
      server.enqueue(jsonResponse("/reconfigureServer.json"));
      serverApi().reconfigureServer("12345", 2, "STANDARD", 2);
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/reconfigureServer");
      assertBodyContains(recordedRequest,
            "{\"id\":\"12345\",\"cpuCount\":2,\"cpuSpeed\":\"STANDARD\",\"coresPerSocket\":2}");
   }

   public void testShutdownServer() throws Exception {
      server.enqueue(jsonResponse("/rebootServer.json"));
      serverApi().shutdownServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/shutdownServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   public void testStartServer() throws Exception {
      server.enqueue(jsonResponse("/rebootServer.json"));
      serverApi().startServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/startServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   public void testCleanServer() throws Exception {
      server.enqueue(jsonResponse("/cleanServer.json"));
      serverApi().cleanServer("12345");
      final RecordedRequest recordedRequest = assertSent(POST,
            "/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/server/cleanServer");
      assertBodyContains(recordedRequest, "{\"id\":\"12345\"}");
   }

   private ServerApi serverApi() {
      return api.getServerApi();
   }

}
