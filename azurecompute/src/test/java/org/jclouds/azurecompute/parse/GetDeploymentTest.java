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
package org.jclouds.azurecompute.parse;

import java.io.InputStream;
import java.net.URI;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentSlot;
import org.jclouds.azurecompute.domain.DeploymentStatus;
import org.jclouds.azurecompute.domain.InstanceStatus;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.xml.DeploymentHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "GetDeploymentTest")
public class GetDeploymentTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/deployment.xml");

      Deployment expected = expected();

      DeploymentHandler handler = injector.getInstance(DeploymentHandler.class);
      Deployment result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public static Deployment expected() {
      return Deployment.builder()
         .deploymentName("neotysss")
         .deploymentSlot(DeploymentSlot.PRODUCTION)
         .deploymentStatus(DeploymentStatus.RUNNING)
         .deploymentLabel("neotysss")
         .deploymentURL(URI.create("http://neotysss.cloudapp.net/"))
         .roleName("neotysss")
         .instanceName("neotysss")
         .instanceStatus(InstanceStatus.READY_ROLE)
         .instanceSize(RoleSize.MEDIUM)
         .privateIpAddress("10.59.244.162")
         .publicIpAddress("168.63.27.148")
         .build();
   }

}
