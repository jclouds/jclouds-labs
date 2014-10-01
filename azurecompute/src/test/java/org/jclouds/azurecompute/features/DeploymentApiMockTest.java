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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSType;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.parse.GetDeploymentTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "DeploymentApiMockTest")
public class DeploymentApiMockTest extends BaseAzureComputeApiMockTest {

   public void create() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         DeploymentParams params = DeploymentParams.builder().osType(OSType.LINUX).name("mydeployment")
               .username("username").password("testpwd").size(RoleSize.MEDIUM)
               .sourceImageName("OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd")
               .storageAccount("portalvhds0g7xhnq2x7t21").build();

         assertThat(api.create(params)).isEqualTo("request-1");

         assertSent(server, "POST", "/services/hostedservices/myservice/deployments", "/deploymentparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/deployment.xml"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertThat(api.get("mydeployment")).isEqualTo(GetDeploymentTest.expected());

         assertSent(server, "GET", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }

   public void getWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertThat(api.get("mydeployment")).isNull();

         assertSent(server, "GET", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertThat(api.delete("mydeployment")).isEqualTo("request-1");

         assertSent(server, "DELETE", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         DeploymentApi api = api(server.getUrl("/")).getDeploymentApiForService("myservice");

         assertThat(api.delete("mydeployment")).isNull();

         assertSent(server, "DELETE", "/services/hostedservices/myservice/deployments/mydeployment");
      } finally {
         server.shutdown();
      }
   }
}
