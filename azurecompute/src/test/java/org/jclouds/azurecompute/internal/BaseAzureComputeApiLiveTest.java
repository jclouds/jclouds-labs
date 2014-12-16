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
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.domain.NetworkConfiguration.AddressSpace;
import static org.jclouds.azurecompute.domain.NetworkConfiguration.Subnet;
import static org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertTrue;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkConfiguration;
import org.jclouds.azurecompute.domain.Operation;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.StorageServiceParams;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import com.google.common.collect.Lists;

public class BaseAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   private static final String STORAGE_SERVICE = (System.getProperty("user.name") + "4jcloudsstorageaccount").toLowerCase().substring(0, 24);
   private static final String VIRTUAL_NETWORK = (System.getProperty("user.name") + "-jclouds-virtual-network").toLowerCase();

   private static final String DEFAULT_ADDRESS_SPACE_ADDRESS_PREFIX = "10.0.0.0/20";
   private static final String DEFAULT_SUBNET_NAME = "jclouds-1";
   private static final String DEFAULT_SUBNET_ADDRESS_PREFIX = "10.0.0.0/23";

   protected String location;
   protected StorageService storageService;

   protected Predicate<String> operationSucceeded;
   protected VirtualNetworkSite virtualNetworkSite;

   public BaseAzureComputeApiLiveTest() {
      provider = "azurecompute";
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      operationSucceeded = retry(new Predicate<String>() {
         public boolean apply(String input) {
            return api.getOperationApi().get(input).status() == Operation.Status.SUCCEEDED;
         }
      }, 600, 5, 5, SECONDS);
      // TODO: filter locations on those who have compute
      location = get(api.getLocationApi().list(), 0).name();

      StorageServiceParams params = StorageServiceParams.builder()
              .name(STORAGE_SERVICE)
              .label(STORAGE_SERVICE)
              .location(location)
              .accountType(StorageServiceParams.Type.Standard_GRS)
              .build();
      virtualNetworkSite = getOrCreateVirtualNetworkSite(VIRTUAL_NETWORK, location);
      storageService = getOrCreateStorageService(STORAGE_SERVICE, params);
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();
      // TODO fix remove storage account: it can't be removed as it contains disks!
      /*
      if (api.getStorageAccountApi().get(subscriptionId, STORAGE_SERVICE) != null) {
         String requestId = api.getStorageAccountApi().delete(STORAGE_SERVICE);
         assertTrue(operationSucceeded.apply(requestId), requestId);
         Logger.getAnonymousLogger().info("storageService deleted: " + STORAGE_SERVICE);
      }
      */
      List<VirtualNetworkSite> virtualNetworkSites = Lists.newArrayList(Iterables.filter(api.getVirtualNetworkApi().list(),
              Predicates.not(new SameVirtualNetworkSiteNamePredicate(VIRTUAL_NETWORK))));

      String requestId = api.getVirtualNetworkApi().set(NetworkConfiguration.create(VirtualNetworkConfiguration.create
              (null, virtualNetworkSites)));
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   protected CloudService getOrCreateCloudService(String cloudServiceName, String location) {
      CloudService cloudService = api.getCloudServiceApi().get(cloudServiceName);
      if (cloudService != null) return cloudService;

      String requestId = api.getCloudServiceApi().createWithLabelInLocation(cloudServiceName, cloudServiceName, location);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);
      cloudService = api.getCloudServiceApi().get(cloudServiceName);
      Logger.getAnonymousLogger().info("created cloudService: " + cloudService);
      return cloudService;
   }

   protected Deployment getOrCreateDeployment(String serviceName, DeploymentParams params) {
      Deployment deployment = api.getDeploymentApiForService(serviceName).get(params.name());
      if (deployment != null) return deployment;

      String requestId = api.getDeploymentApiForService(serviceName).create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);
      deployment = api.getDeploymentApiForService(serviceName).get(params.name());

      Logger.getAnonymousLogger().info("created deployment: " + deployment);
      return deployment;
   }

   protected StorageService getOrCreateStorageService(String storageServiceName, StorageServiceParams params) {
      StorageService storageService = api.getStorageAccountApi().get(storageServiceName);
      if (storageService != null) return storageService;
      String requestId = api.getStorageAccountApi().create(params);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);
      storageService = api.getStorageAccountApi().get(storageServiceName);

      Logger.getAnonymousLogger().info("created storageService: " + storageService);
      return storageService;
   }

   protected VirtualNetworkSite getOrCreateVirtualNetworkSite(final String virtualNetworkSiteName, String location) {
      Optional<VirtualNetworkSite> optionalVirtualNetworkSite = tryFind(api.getVirtualNetworkApi().list(),
              new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));
      if (optionalVirtualNetworkSite.isPresent()) return optionalVirtualNetworkSite.get();

      final NetworkConfiguration networkConfiguration = NetworkConfiguration.create(
              VirtualNetworkConfiguration.create(null,
                      ImmutableList.of(VirtualNetworkSite.create(
                              UUID.randomUUID().toString(),
                              virtualNetworkSiteName,
                              location,
                              AddressSpace.create(DEFAULT_ADDRESS_SPACE_ADDRESS_PREFIX),
                              ImmutableList.of(Subnet.create(DEFAULT_SUBNET_NAME, DEFAULT_SUBNET_ADDRESS_PREFIX, null))))
              )
      );
      String requestId = api.getVirtualNetworkApi().set(networkConfiguration);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);
      VirtualNetworkSite virtualNetworkSite = find(api.getVirtualNetworkApi().list(), new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));

      Logger.getAnonymousLogger().info("created virtualNetworkSite: " + virtualNetworkSite);
      return virtualNetworkSite;
   }

   private static class SameVirtualNetworkSiteNamePredicate implements Predicate<VirtualNetworkSite> {
      private final String virtualNetworkSiteName;

      public SameVirtualNetworkSiteNamePredicate(String virtualNetworkSiteName) {
         this.virtualNetworkSiteName = virtualNetworkSiteName;
      }

      @Override
      public boolean apply(VirtualNetworkSite input) {
         return input.name().equals(virtualNetworkSiteName);
      }
   }
}
