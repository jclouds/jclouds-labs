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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import java.util.logging.Logger;

import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "DeploymentApiLiveTest", singleThreaded = true)
public class DeploymentApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD_SERVICE = (System.getProperty("user.name") + "cloudservice").toLowerCase();
   private static final String DEPLOYMENT = DeploymentApiLiveTest.class.getSimpleName().toLowerCase();

   private Predicate<Deployment> deploymentCreated;
   private Predicate<Deployment> deploymentGone;

   private Deployment deployment;
   private CloudService cloudService;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      cloudService = getOrCreateCloudService(CLOUD_SERVICE, location);

      deploymentCreated = retry(new Predicate<Deployment>() {
         public boolean apply(Deployment input) {
            return api().get(input.name()).status() == Deployment.Status.RUNNING;
         }
      }, 600, 5, 5, SECONDS);
      deploymentGone = retry(new Predicate<Deployment>() {
         public boolean apply(Deployment input) {
            return api().get(input.name()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   public void testCreate() {
      final DeploymentParams params = DeploymentParams.builder()
              .name(DEPLOYMENT)
              .os(OSImage.Type.LINUX)
              .sourceImageName("b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_1-LTS-amd64-server-20150123-en-us-30GB")
              .mediaLink(AzureComputeServiceAdapter.createMediaLink(storageService.serviceName(), DEPLOYMENT))
              .username("test")
              .password("supersecurePassword1!")
              .size(RoleSize.Type.BASIC_A2)
              .subnetName(Iterables.get(virtualNetworkSite.subnets(), 0).name())
              .virtualNetworkName(virtualNetworkSite.name())
              .externalEndpoint(DeploymentParams.ExternalEndpoint.inboundTcpToLocalPort(22, 22))
              .build();
      String requestId = api().create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);

      deployment = api().get(DEPLOYMENT);
      assertNotNull(deployment);
      assertTrue(deploymentCreated.apply(deployment), deployment.toString());
      assertThat(deployment.name()).isEqualTo(DEPLOYMENT);
      assertThat(deployment.status()).isEqualTo(Deployment.Status.RUNNING);
      assertThat(deployment.label()).isEqualTo(DEPLOYMENT);
      assertThat(deployment.slot()).isEqualTo(Deployment.Slot.PRODUCTION);
      assertThat(deployment.roles().size()).isEqualTo(1);
      assertThat(deployment.roleInstanceList().size()).isEqualTo(1);
      assertThat(deployment.virtualNetworkName()).isEqualTo(virtualNetworkSite.name());

   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      Deployment foundDeployment = api().get(deployment.name());
      assertThat(foundDeployment).isEqualToComparingFieldByField(deployment);
   }

   @Test(dependsOnMethods = "testGet")
   public void testDelete() {
      String requestId = api().delete(deployment.name());
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      assertTrue(deploymentGone.apply(deployment), deployment.toString());
      Logger.getAnonymousLogger().info("deployment deleted: " + deployment);
   }

   @Override @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDown() {
      super.tearDown();
      if (api().get(DEPLOYMENT) != null) {
         String requestId = api().delete(deployment.name());
         operationSucceeded.apply(requestId);
         Logger.getAnonymousLogger().info("deployment deleted: " + deployment);
      }
      if (api.getCloudServiceApi().get(CLOUD_SERVICE) != null) {
         String requestId = api.getCloudServiceApi().delete(CLOUD_SERVICE);
         assertTrue(operationSucceeded.apply(requestId), requestId);
         Logger.getAnonymousLogger().info("cloudservice deleted: " + CLOUD_SERVICE);
      }
   }

   private DeploymentApi api() {
      return api.getDeploymentApiForService(cloudService.name());
   }
}
