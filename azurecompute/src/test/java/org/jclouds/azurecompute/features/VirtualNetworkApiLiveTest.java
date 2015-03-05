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
import static org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest.VIRTUAL_NETWORK_NAME;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;

import com.google.common.base.Predicates;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.azurecompute.AzureTestUtils;
import org.jclouds.azurecompute.util.ConflictManagementPredicate;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@Test(groups = "live", testName = "VirtualNetworkApiLiveTest", singleThreaded = true)
public class VirtualNetworkApiLiveTest extends BaseAzureComputeApiLiveTest {

   @BeforeSuite
   @Override
   public void setup() {
      super.setup();

      final List<VirtualNetworkSite> virtualNetworkSites = Lists.newArrayList(Iterables.filter(
              AzureTestUtils.getVirtualNetworkSite(api),
              new AzureTestUtils.SameVirtualNetworkSiteNamePredicate(VIRTUAL_NETWORK_NAME)));

      final NetworkConfiguration.AddressSpace addressSpace = NetworkConfiguration.AddressSpace.create(
              DEFAULT_ADDRESS_SPACE);

      final ImmutableList<NetworkConfiguration.Subnet> subnets = ImmutableList.of(NetworkConfiguration.Subnet.create(
              DEFAULT_SUBNET_NAME, DEFAULT_SUBNET_ADDRESS_SPACE, null));

      final NetworkConfiguration networkConfiguration = api().getNetworkConfiguration();
      assertThat(networkConfiguration.virtualNetworkConfiguration().dns()).isEqualTo(
              networkConfiguration.virtualNetworkConfiguration().dns());

      assertThat(virtualNetworkSites.size()).isEqualTo(1);
      assertThat(virtualNetworkSites.get(0).name()).isEqualTo(VIRTUAL_NETWORK_NAME);
      assertThat(virtualNetworkSites.get(0).location()).isEqualTo(LOCATION);
      assertThat(virtualNetworkSites.get(0).addressSpace()).isEqualTo(addressSpace);
      assertThat(virtualNetworkSites.get(0).subnets()).isEqualTo(subnets);
   }

   @AfterSuite
   @Override
   protected void tearDown() {
      super.tearDown();

      final List<VirtualNetworkSite> virtualNetworkSites = Lists.newArrayList(Iterables.filter(api.
              getVirtualNetworkApi().list(),
              Predicates.not(new AzureTestUtils.SameVirtualNetworkSiteNamePredicate(VIRTUAL_NETWORK_NAME))));

      retry(new ConflictManagementPredicate(operationSucceeded) {

         @Override
         protected String operation() {
            return api.getVirtualNetworkApi().set(NetworkConfiguration.create(
                    NetworkConfiguration.VirtualNetworkConfiguration.create(null, virtualNetworkSites)));
         }
      }, 600, 30, 30, SECONDS).apply(VIRTUAL_NETWORK_NAME);
   }

   @Test
   public void testList() {
      for (VirtualNetworkSite vns : api().list()) {
         checkVirtualNetworkSite(vns);
      }
   }

   private void checkVirtualNetworkSite(VirtualNetworkSite virtualNetworkSite) {
      assertNotNull(virtualNetworkSite.name(), "Name cannot be null for a VirtualNetworkSite.");
      assertNotNull(virtualNetworkSite.addressSpace(), "AddressSpace cannot be null for: " + virtualNetworkSite);
      assertNotNull(virtualNetworkSite.subnets(), "Subnets cannot be null for: " + virtualNetworkSite);
   }

   @Test
   public void testGetNetworkConfiguration() {
      final NetworkConfiguration networkConfiguration = api().getNetworkConfiguration();
      assertThat(networkConfiguration).isNotNull();
      for (VirtualNetworkSite vns : networkConfiguration.virtualNetworkConfiguration().virtualNetworkSites()) {
         assertThat(vns.name()).isNotEqualTo("not-existing");
      }
   }

   private VirtualNetworkApi api() {
      return api.getVirtualNetworkApi();
   }

}
