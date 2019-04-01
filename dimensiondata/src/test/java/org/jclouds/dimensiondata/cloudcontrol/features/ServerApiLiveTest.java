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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.CustomerImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.Tag;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CloneServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ServerApiLiveTest", singleThreaded = true)
public class ServerApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {

   private String serverId;
   private String cloneImageId;
   private final String deployedServerName = ServerApiLiveTest.class.getSimpleName() + System.currentTimeMillis();
   private String vlanId;
   private String networkDomainId;
   private String imageId;
   private String tagKeyId;
   private String publicIpv4BlockId;
   private PublicIpBlock publicIpBlock;
   private String natRuleId;

   @BeforeClass
   public void init() {
      final String datacenterId = deployNetworkDomain();
      deployVlan();
      findOsImage(datacenterId);
      tagKeyId = createTagKey();
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testListServers() {
      List<Server> servers = api.getServerApi().listServers().concat().toList();
      assertNotNull(servers);
      boolean foundDeployedServer = false;
      for (Server s : servers) {
         assertNotNull(s);
         if (s.name().equals(deployedServerName)) {
            foundDeployedServer = true;
         }
      }
      assertTrue(foundDeployedServer, "Did not find deployed server " + deployedServerName);
   }

   @Test
   public void testDeployAndStartServer() {
      Boolean started = Boolean.TRUE;
      NetworkInfo networkInfo = NetworkInfo
            .create(networkDomainId, NIC.builder().vlanId(vlanId).build(), Lists.<NIC>newArrayList());
      List<Disk> disks = ImmutableList.of(Disk.builder().scsiId(0).speed("STANDARD").build());
      serverId = api.getServerApi()
            .deployServer(deployedServerName, imageId, started, networkInfo, "P$$ssWwrrdGoDd!", disks, null);
      assertNotNull(serverId);
      assertTrue(api.serverStartedPredicate().apply(serverId), "server did not start after timeout");
      assertTrue(api.serverNormalPredicate().apply(serverId), "server was not NORMAL after timeout");
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testReconfigureServer() {
      api.getServerApi().reconfigureServer(serverId, 4, CpuSpeed.HIGHPERFORMANCE.name(), 1);
      assertTrue(api.serverNormalPredicate().apply(serverId), "server was not NORMAL after timeout");
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testApplyTagToServer() {
      api.getTagApi()
            .applyTags(serverId, "SERVER", Collections.singletonList(TagInfo.create(tagKeyId, "jcloudsValue")));
   }

   @Test(dependsOnMethods = "testApplyTagToServer")
   public void testListTags() {
      PagedIterable<Tag> response = api.getTagApi().listTags();
      assertTrue(FluentIterable.from(response.concat().toList()).anyMatch(new Predicate<Tag>() {
         @Override
         public boolean apply(Tag input) {
            return input.tagKeyId().equals(tagKeyId);
         }
      }), String.format("Couldn't find tagKeyId %s in listTags response", tagKeyId));
   }

   @Test(dependsOnMethods = "testListTags")
   public void testRemoveTagFromServer() {
      api.getTagApi().removeTags(serverId, "SERVER", Collections.singletonList(tagKeyId));
      assertFalse(FluentIterable.from(api.getTagApi().listTags().concat().toList()).anyMatch(new Predicate<Tag>() {
         @Override
         public boolean apply(Tag input) {
            return input.tagKeyId().equals(tagKeyId);
         }
      }));
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testRebootServer() {
      api.getServerApi().rebootServer(serverId);
      assertTrue(api.serverNormalPredicate().apply(serverId), "server was not NORMAL after timeout");
      assertTrue(api.vmToolsRunningPredicate().apply(serverId), "server vm tools not running after timeout");
   }

   @Test(dependsOnMethods = "testDeployAndStartServer")
   public void testAddPublicIPv4Block() {
      publicIpv4BlockId = api.getNetworkApi().addPublicIpBlock(networkDomainId);
      assertNotNull(publicIpv4BlockId);
   }

   @Test(dependsOnMethods = "testAddPublicIPv4Block")
   public void testListPublicIPv4AddressBlocks() {
      PagedIterable<PublicIpBlock> ipBlockList = api.getNetworkApi().listPublicIPv4AddressBlocks(networkDomainId);
      assertTrue(!ipBlockList.isEmpty());
      assertEquals(ipBlockList.last().get().first().get().size(), 2);
      assertEquals(ipBlockList.last().get().first().get().networkDomainId(), networkDomainId);
   }

   @Test(dependsOnMethods = "testAddPublicIPv4Block")
   public void testGetPublicIPv4AddressBlocks() {
      publicIpBlock = api.getNetworkApi().getPublicIPv4AddressBlock(publicIpv4BlockId);
      assertNotNull(publicIpBlock);
      assertEquals(publicIpBlock.size(), 2);
      assertEquals(publicIpBlock.networkDomainId(), networkDomainId);
   }

   @Test(dependsOnMethods = "testGetPublicIPv4AddressBlocks")
   public void testCreateNatRule() {
      natRuleId = api.getNetworkApi()
            .createNatRule(networkDomainId, PREPARED_PRIVATE_IPV4_ADDRESS, publicIpBlock.baseIp());
      assertNotNull(natRuleId);
   }

   @Test(dependsOnMethods = "testCreateNatRule")
   public void testListNatRules() {
      PagedIterable<NatRule> natRulesList = api.getNetworkApi().listNatRules(networkDomainId);
      assertTrue(!natRulesList.isEmpty());
      assertEquals(natRulesList.last().get().first().get().networkDomainId(), networkDomainId);
   }

   @Test(dependsOnMethods = { "testCreateNatRule", "testListNatRules" })
   public void testGetNatRule() {
      NatRule natRule = api.getNetworkApi().getNatRule(natRuleId);
      assertNotNull(natRule);
      assertEquals(natRule.networkDomainId(), networkDomainId);
   }

   @Test(dependsOnMethods = "testGetNatRule", alwaysRun = true)
   public void testDeleteNatRule() {
      api.getNetworkApi().deleteNatRule(natRuleId);
      NatRule natRule = api.getNetworkApi().getNatRule(natRuleId);
      assertNull(natRule);
   }

   @Test(dependsOnMethods = { "testDeleteNatRule" })
   public void testRemovePublicIpBlock() {
      api.getNetworkApi().removePublicIpBlock(publicIpv4BlockId);
      publicIpBlock = api.getNetworkApi().getPublicIPv4AddressBlock(publicIpv4BlockId);
      assertNull(publicIpBlock);
   }

   @Test(dependsOnMethods = "testRebootServer")
   public void testPowerOffServer() {
      api.getServerApi().powerOffServer(serverId);
      assertTrue(api.serverStoppedPredicate().apply(serverId), "server did not power off after timeout");
   }

   @Test(dependsOnMethods = "testPowerOffServer")
   public void testStartServer() {
      api.getServerApi().startServer(serverId);
      assertTrue(api.serverStartedPredicate().apply(serverId), "server did not start after timeout");
      assertTrue(api.vmToolsRunningPredicate().apply(serverId), "server vm tools not running after timeout");
   }

   @Test(dependsOnMethods = "testStartServer")
   public void testShutdownServer() {
      api.getServerApi().shutdownServer(serverId);
      assertTrue(api.serverStoppedPredicate().apply(serverId), "server did not shutdown after timeout");
   }

   @Test(dependsOnMethods = "testShutdownServer")
   public void testCloneServerToMakeCustomerImage() {
      CloneServerOptions options = CloneServerOptions.builder().clusterId("").description("")
            .guestOsCustomization(false).build();
      cloneImageId = api.getServerApi()
            .cloneServer(serverId, "ServerApiLiveTest-" + System.currentTimeMillis(), options);
      assertNotNull(cloneImageId);
      assertTrue(api.serverNormalPredicate().apply(serverId), "server was not NORMAL after timeout");
   }

   @Test(dependsOnMethods = "testCloneServerToMakeCustomerImage")
   public void testListCustomerImages() {
      FluentIterable<CustomerImage> customerImages = api.getServerImageApi().listCustomerImages().concat();
      assertNotNull(customerImages);
      assertTrue(customerImages.anyMatch(new Predicate<CustomerImage>() {
         @Override
         public boolean apply(CustomerImage input) {
            return input.id().equals(cloneImageId);
         }
      }));
   }

   @Test(dependsOnMethods = "testCloneServerToMakeCustomerImage")
   public void testGetCustomerImage() {
      CustomerImage customerImage = api.getServerImageApi().getCustomerImage(cloneImageId);
      assertNotNull(customerImage);
   }

   @Test(dependsOnMethods = "testGetCustomerImage")
   public void testDeleteCustomerImage() {
      boolean deleted = api.getCustomerImageApi().deleteCustomerImage(cloneImageId);
      assertTrue(deleted);
      assertTrue(api.customerImageDeletedPredicate().apply(cloneImageId),
            "customer image was not DELETED after timeout");
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteServerAndNetworking() {
      if (publicIpBlock != null) {
         api.getNetworkApi().removePublicIpBlock(publicIpBlock.id());
      }
      if (serverId != null) {
         api.getServerApi().deleteServer(serverId);
         assertTrue(api.serverDeletedPredicate().apply(serverId), "server was not DELETED after timeout");
      }
      if (vlanId != null) {
         api.getNetworkApi().deleteVlan(vlanId);
         assertTrue(api.vlanDeletedPredicate().apply(vlanId), "vlan is not in a DELETED state after timeout");
      }
      if (networkDomainId != null) {
         api.getNetworkApi().deleteNetworkDomain(networkDomainId);
         assertTrue(api.networkDomainDeletedPredicate().apply(networkDomainId),
               "network domain is not in a DELETED state after timeout");
      }
      if (tagKeyId != null && !tagKeyId.isEmpty()) {
         api.getTagApi().deleteTagKey(tagKeyId);
      }
   }

   private void findOsImage(final String datacenterId) {
      Optional<OsImage> osImageOptional = api.getServerImageApi()
            .listOsImages(DatacenterIdListFilters.Builder.datacenterId(datacenterId)).first();
      assertTrue(osImageOptional.isPresent(), "unable to find compatible image for datacenter");
      imageId = osImageOptional.get().id();
   }

   private void deployVlan() {
      vlanId = api.getNetworkApi()
            .deployVlan(networkDomainId, ServerApiLiveTest.class.getSimpleName() + new Date().getTime(),
                  ServerApiLiveTest.class.getSimpleName() + new Date().getTime(), DEFAULT_PRIVATE_IPV4_BASE_ADDRESS,
                  DEFAULT_PRIVATE_IPV4_PREFIX_SIZE);
      assertNotNull(vlanId);
      assertTrue(api.vlanNormalPredicate().apply(vlanId), "vlan is not in a NORMAL state after timeout");
   }

   private String deployNetworkDomain() {
      String networkDomainName = ServerApiLiveTest.class.getSimpleName() + new Date().getTime();
      final String datacenterId = datacenters.iterator().next();
      networkDomainId = api.getNetworkApi().deployNetworkDomain(datacenterId, networkDomainName,
            ServerApiLiveTest.class.getSimpleName() + new Date().getTime() + "description", "ESSENTIALS");
      assertNotNull(networkDomainId);
      assertTrue(api.networkDomainNormalPredicate().apply(networkDomainId),
            "network domain is not in a NORMAL state after timeout");
      return datacenterId;
   }

   private String createTagKey() {
      String tagKeyName = "jcloudsTagKeyName" + System.currentTimeMillis();
      String tagKeyId = api.getTagApi()
            .createTagKey(tagKeyName, "jcloudsTagKeyDescription", Boolean.TRUE, Boolean.FALSE);
      assertNotNull(tagKeyId);
      return tagKeyId;
   }

}
