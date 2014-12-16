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
import static org.jclouds.azurecompute.domain.Deployment.InstanceStatus.READY_ROLE;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertTrue;
import java.util.logging.Logger;

import org.jclouds.azurecompute.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.Deployment.RoleInstance;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest", singleThreaded = true)
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String CLOUD_SERVICE = (System.getProperty("user.name") + "-jclouds-cloudService").toLowerCase();
   private static final String DEPLOYMENT = DeploymentApiLiveTest.class.getSimpleName().toLowerCase();

   private String roleName;
   private Predicate<String> roleInstanceReady;
   private Predicate<String> roleInstanceStopped;
   private CloudService cloudService;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      cloudService = getOrCreateCloudService(CLOUD_SERVICE, location);

      roleInstanceReady = retry(new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            RoleInstance roleInstance = getFirstRoleInstanceInDeployment(input);
            return roleInstance != null && roleInstance.instanceStatus() == READY_ROLE;
         }
      }, 600, 5, 5, SECONDS);

      roleInstanceStopped = retry(new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            RoleInstance roleInstance = getFirstRoleInstanceInDeployment(input);
            return roleInstance != null && roleInstance.instanceStatus() == Deployment.InstanceStatus.STOPPED_VM;
         }
      }, 600, 5, 5, SECONDS);

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
      getOrCreateDeployment(cloudService.name(), params);
      RoleInstance roleInstance = getFirstRoleInstanceInDeployment(DEPLOYMENT);
      assertTrue(roleInstanceReady.apply(DEPLOYMENT), roleInstance.toString());
      roleName = roleInstance.roleName();
   }

   public void testUpdate() {
      Role role = api().getRole(roleName);
      String requestId = api().updateRole(roleName,
              Role.create(
                      role.roleName(),
                      role.roleType(),
                      role.vmImage(),
                      role.mediaLocation(),
                      role.configurationSets(),
                      role.resourceExtensionReferences(),
                      role.availabilitySetName(),
                      role.dataVirtualHardDisks(),
                      role.osVirtualHardDisk(),
                      role.roleSize(),
                      role.provisionGuestAgent(),
                      role.defaultWinRmCertificateThumbprint()));
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testShutdown() {
      String requestId = api().shutdown(roleName);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      RoleInstance roleInstance = getFirstRoleInstanceInDeployment(DEPLOYMENT);
      assertTrue(roleInstanceStopped.apply(DEPLOYMENT), roleInstance.toString());
      Logger.getAnonymousLogger().info("roleInstance stopped: " + roleInstance);
   }

   @Test(dependsOnMethods = "testShutdown")
   public void testStart() {
      String requestId = api().start(roleName);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      RoleInstance roleInstance = getFirstRoleInstanceInDeployment(DEPLOYMENT);
      assertTrue(roleInstanceReady.apply(DEPLOYMENT), roleInstance.toString());
      Logger.getAnonymousLogger().info("roleInstance started: " + roleInstance);
   }

   @Test(dependsOnMethods = "testStart")
   public void testRestart() {
      String requestId = api().restart(roleName);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      RoleInstance roleInstance = getFirstRoleInstanceInDeployment(DEPLOYMENT);
      assertTrue(roleInstanceReady.apply(DEPLOYMENT), roleInstance.toString());
      Logger.getAnonymousLogger().info("roleInstance restarted: " + roleInstance);
   }

   @AfterClass(alwaysRun = true)
   public void cleanup() {
      if (api.getDeploymentApiForService(cloudService.name()).get(DEPLOYMENT) != null) {
         String requestId = api.getDeploymentApiForService(cloudService.name()).delete(DEPLOYMENT);
         operationSucceeded.apply(requestId);
         Logger.getAnonymousLogger().info("deployment deleted: " + DEPLOYMENT);
      }
   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApiForDeploymentInService(DEPLOYMENT, cloudService.name());
   }

   private RoleInstance getFirstRoleInstanceInDeployment(String deployment) {
      return Iterables.getOnlyElement(api.getDeploymentApiForService(cloudService.name()).get(deployment).roleInstanceList());
   }

}
