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
import static org.testng.Assert.assertEquals;

import java.util.logging.Logger;

import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.CloudServiceProperties;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.logging.Level;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;

@Test(groups = "live", testName = "DeploymentApiLiveTest", singleThreaded = true)
public class DeploymentApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD_SERVICE = String.format("%s%d-%s",
           System.getProperty("user.name"), RAND, DeploymentApiLiveTest.class.getSimpleName()).toLowerCase();

   private static final String DEPLOYMENT = String.format("%s%d-%s",
           System.getProperty("user.name"), RAND, DeploymentApiLiveTest.class.getSimpleName()).toLowerCase();

   private Predicate<Deployment> deploymentCreated;

   private Predicate<Deployment> deploymentGone;

   private Deployment deployment;

   private CloudService cloudService;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      cloudService = getOrCreateCloudService(CLOUD_SERVICE, LOCATION);

      deploymentCreated = retry(new Predicate<Deployment>() {

         @Override
         public boolean apply(final Deployment input) {
            return api().get(input.name()).status() == Deployment.Status.RUNNING;
         }
      }, 600, 5, 5, SECONDS);

      deploymentGone = retry(new Predicate<Deployment>() {

         @Override
         public boolean apply(final Deployment input) {
            return api().get(input.name()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   public void testCreate() {
      final DeploymentParams params = DeploymentParams.builder()
              .name(DEPLOYMENT)
              .os(OSImage.Type.LINUX)
              .sourceImageName(DeploymentApiLiveTest.IMAGE_NAME)
              .mediaLink(AzureComputeServiceAdapter.createMediaLink(storageService.serviceName(), DEPLOYMENT))
              .username("test")
              .password("supersecurePassword1!")
              .size(RoleSize.Type.BASIC_A2)
              .subnetName(Iterables.get(virtualNetworkSite.subnets(), 0).name())
              .virtualNetworkName(virtualNetworkSite.name())
              .externalEndpoint(DeploymentParams.ExternalEndpoint.inboundTcpToLocalPort(22, 22))
              .build();
      final String requestId = api().create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);

      deployment = api().get(DEPLOYMENT);
      assertNotNull(deployment);
      assertTrue(deploymentCreated.apply(deployment), deployment.toString());
      assertThat(deployment.name()).isEqualTo(DEPLOYMENT);
      assertThat(deployment.status()).isEqualTo(Deployment.Status.RUNNING);
      assertThat(deployment.label()).isEqualTo(DEPLOYMENT);
      assertThat(deployment.slot()).isEqualTo(Deployment.Slot.PRODUCTION);
      assertThat(deployment.roleList().size()).isEqualTo(1);
      assertThat(deployment.roleInstanceList().size()).isEqualTo(1);
      assertThat(deployment.virtualNetworkName()).isEqualTo(virtualNetworkSite.name());

   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      Deployment foundDeployment = api().get(deployment.name());
      assertThat(foundDeployment).isEqualToComparingFieldByField(deployment);
   }

   // Test CloudServiceProperties with a deployment
   @Test(dependsOnMethods = "testCreate")
   public void testGetProperties() {
      CloudServiceProperties cloudServiceProperties = api.getCloudServiceApi().getProperties(cloudService.name());
      assertNotNull(cloudServiceProperties);
      assertEquals(cloudServiceProperties.serviceName(), CLOUD_SERVICE);

      Deployment deployment = cloudServiceProperties.deployments().get(0);
      checkDeployment(deployment);
   }

   @Test(dependsOnMethods = "testGet")
   public void testDelete() {
      final List<Role> roles = api.getDeploymentApiForService(cloudService.name()).get(DEPLOYMENT).roleList();

      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api().delete(deployment.name());
         }
      }.apply(deployment.name()));

      assertTrue(deploymentGone.apply(deployment), deployment.toString());
      Logger.getAnonymousLogger().log(Level.INFO, "deployment deleted: {0}", deployment);

      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getCloudServiceApi().delete(cloudService.name());
         }
      }.apply(cloudService.name()));

      for (Role r : roles) {
         final Role.OSVirtualHardDisk disk = r.osVirtualHardDisk();
         if (disk != null) {
            assertTrue(new ConflictManagementPredicate(api) {

               @Override
               protected String operation() {
                  return api.getDiskApi().delete(disk.diskName());
               }
            }.apply(disk.diskName()));
         }
      }
   }

   private void checkDeployment(Deployment deployment) {
      assertNotNull(deployment);
      assertNotNull(deployment.name(), "Name cannot be Null for Deployment" + deployment);
      assertTrue(deployment.roleList().size() > 0, "There should be atleast 1 Virtual machine for a deployment  ");
      assertNotNull(deployment.label(), "Label cannot be Null for Deployment" + deployment);

      Deployment.Slot slot = deployment.slot();
      assertTrue((slot == Deployment.Slot.PRODUCTION) || (slot == Deployment.Slot.STAGING));
      assertEquals(deployment.name(), DEPLOYMENT);
   }

   @Override
   @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDown() {
      super.tearDown();
   }

   private DeploymentApi api() {
      return api.getDeploymentApiForService(cloudService.name());
   }
}
