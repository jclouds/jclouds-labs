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
package org.jclouds.azurecompute.internal;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import static org.testng.Assert.assertTrue;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.azurecompute.AzureTestUtils;
import org.jclouds.azurecompute.AzureTestUtils.SameVirtualNetworkSiteNamePredicate;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.AddressSpace;
import org.jclouds.azurecompute.domain.NetworkConfiguration.Subnet;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkConfiguration;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class BaseAzureComputeApiLiveTest extends AbstractAzureComputeApiLiveTest {

   public static final String DEFAULT_ADDRESS_SPACE = "10.0.0.0/20";

   public static final String DEFAULT_SUBNET_ADDRESS_SPACE = "10.0.0.0/23";

   public static final String VIRTUAL_NETWORK_NAME = "jclouds-virtual-network-live-test";

   public static final String DEFAULT_SUBNET_NAME = "jclouds-1";

   public static final String LOCATION = "West Europe";

   public static final String IMAGE_NAME
           = "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_1-LTS-amd64-server-20150123-en-us-30GB";

   protected StorageService storageService;

   protected VirtualNetworkSite virtualNetworkSite;

   private String storageServiceName = null;

   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase();
      }
      return storageServiceName;
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      operationSucceeded = new ConflictManagementPredicate(api, 600, 5, 5, SECONDS);
      virtualNetworkSite = getOrCreateVirtualNetworkSite(VIRTUAL_NETWORK_NAME, LOCATION);

      final CreateStorageServiceParams params = CreateStorageServiceParams.builder().
              serviceName(getStorageServiceName()).
              label(getStorageServiceName()).
              location(LOCATION).
              accountType(StorageService.AccountType.Standard_LRS).
              build();
      storageService = getOrCreateStorageService(getStorageServiceName(), params);
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();

      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getStorageAccountApi().delete(getStorageServiceName());
         }
      }.apply(getStorageServiceName()));
   }

   protected CloudService getOrCreateCloudService(final String cloudServiceName, final String location) {
      CloudService cloudService = api.getCloudServiceApi().get(cloudServiceName);
      if (cloudService != null) {
         return cloudService;
      }

      String requestId = api.getCloudServiceApi().createWithLabelInLocation(
              cloudServiceName, cloudServiceName, location);

      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
      cloudService = api.getCloudServiceApi().get(cloudServiceName);
      Logger.getAnonymousLogger().log(Level.INFO, "created cloudService: {0}", cloudService);
      return cloudService;
   }

   protected Deployment getOrCreateDeployment(final String serviceName, final DeploymentParams params) {
      Deployment deployment = api.getDeploymentApiForService(serviceName).get(params.name());
      if (deployment != null) {
         return deployment;
      }

      assertTrue(new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getDeploymentApiForService(serviceName).create(params);
         }
      }.apply(getStorageServiceName()));

      deployment = api.getDeploymentApiForService(serviceName).get(params.name());

      Logger.getAnonymousLogger().log(Level.INFO, "created deployment: {0}", deployment);
      return deployment;
   }

   protected StorageService getOrCreateStorageService(String storageServiceName, CreateStorageServiceParams params) {
      StorageService ss = api.getStorageAccountApi().get(storageServiceName);
      if (ss != null) {
         return ss;
      }
      String requestId = api.getStorageAccountApi().create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().log(Level.INFO, "operation succeeded: {0}", requestId);
      ss = api.getStorageAccountApi().get(storageServiceName);

      Logger.getAnonymousLogger().log(Level.INFO, "created storageService: {0}", ss);
      return ss;
   }

   protected VirtualNetworkSite getOrCreateVirtualNetworkSite(final String virtualNetworkSiteName, String location) {
      final List<VirtualNetworkSite> current = AzureTestUtils.getVirtualNetworkSite(api);

      final Optional<VirtualNetworkSite> optionalVirtualNetworkSite = tryFind(
              current,
              new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));

      if (optionalVirtualNetworkSite.isPresent()) {
         return optionalVirtualNetworkSite.get();
      }

      current.add(VirtualNetworkSite.create(UUID.randomUUID().toString(),
              virtualNetworkSiteName,
              location,
              AddressSpace.create(DEFAULT_ADDRESS_SPACE),
              ImmutableList.of(Subnet.create(DEFAULT_SUBNET_NAME, DEFAULT_SUBNET_ADDRESS_SPACE, null))));

      final NetworkConfiguration networkConfiguration
              = NetworkConfiguration.create(VirtualNetworkConfiguration.create(null, current));

      VirtualNetworkSite vns;
      try {
         vns = find(
                 api.getVirtualNetworkApi().getNetworkConfiguration().virtualNetworkConfiguration().
                 virtualNetworkSites(),
                 new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));
      } catch (Exception e) {
         assertTrue(new ConflictManagementPredicate(api) {

            @Override
            protected String operation() {
               return api.getVirtualNetworkApi().set(networkConfiguration);
            }
         }.apply(virtualNetworkSiteName));

         vns = find(
                 api.getVirtualNetworkApi().getNetworkConfiguration().virtualNetworkConfiguration().
                 virtualNetworkSites(),
                 new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));

         Logger.getAnonymousLogger().log(Level.INFO, "created virtualNetworkSite: {0}", vns);
      }
      return vns;
   }
}
