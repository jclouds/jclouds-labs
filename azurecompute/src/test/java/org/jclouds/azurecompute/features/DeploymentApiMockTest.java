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
package org.jclouds.azurecompute.features;

import static org.jclouds.azurecompute.domain.DeploymentParams.ExternalEndpoint.inboundTcpToLocalPort;
import static org.jclouds.azurecompute.domain.DeploymentParams.ExternalEndpoint.inboundUdpToLocalPort;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.DeploymentHandlerTest;
import org.jclouds.azurecompute.xml.ListOSImagesHandlerTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "DeploymentApiMockTest")
public class DeploymentApiMockTest extends BaseAzureComputeApiMockTest {

   public void testCreateLinux() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         OSImage OSImage = ListOSImagesHandlerTest.expected().get(5); // Centos

         DeploymentParams params = DeploymentParams.builder()
                 .name("mydeployment")
                 .size(RoleSize.Type.MEDIUM)
                 .sourceImageName(OSImage.name()).mediaLink(URI.create("https://mydeployment.blob.core.windows.net/vhds/disk-mydeployment.vhd")).os(OSImage.os())
                 .username("username").password("testpwd")
                 .virtualNetworkName("my-virtualNetworkName")
                 .externalEndpoint(inboundTcpToLocalPort(80, 8080))
                 .externalEndpoint(inboundUdpToLocalPort(53, 53)).build();

         assertEquals(api.create(params), "request-1");

         assertSent(server, "POST", "/services/hostedservices/myservice/deployments", "/deploymentparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWindows() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         OSImage OSImage = ListOSImagesHandlerTest.expected().get(1); // Windows

         DeploymentParams params = DeploymentParams.builder()
                 .name("mydeployment")
                 .size(RoleSize.Type.MEDIUM)
                 .sourceImageName(OSImage.name()).mediaLink(OSImage.mediaLink()).os(OSImage.os())
                 .username("username").password("testpwd")
                 .virtualNetworkName("my-virtualNetworkName")
                 .externalEndpoint(inboundTcpToLocalPort(80, 8080))
                 .externalEndpoint(inboundUdpToLocalPort(53, 53)).build();

         assertEquals(api.create(params), "request-1");

         assertSent(server, "POST", "/services/hostedservices/myservice/deployments", "/deploymentparams-windows.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/deployment.xml"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertEquals(api.get("mydeployment"), DeploymentHandlerTest.expected());

         assertSent(server, "GET", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertEquals(api.delete("mydeployment"), "request-1");

         assertSent(server, "DELETE", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }

}
