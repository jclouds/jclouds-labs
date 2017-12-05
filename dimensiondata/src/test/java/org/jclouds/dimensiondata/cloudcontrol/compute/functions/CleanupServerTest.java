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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterables;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.VmTools;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerApi;
import org.jclouds.dimensiondata.cloudcontrol.utils.DimensionDataCloudControlResponseUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "CleanupServerTest")
public class CleanupServerTest {

   private CleanupServer cleanupServer;
   private DimensionDataCloudControlApi api;
   private ServerApi serverApi;
   private NetworkApi networkApi;
   private final String serverId = "serverId";
   private final String jcloudsNetworkDomainId = "jcloudsNetworkDomainId";
   private Server.Builder serverBuilder;
   private String internalIp;
   private NatRule.Builder natRuleBuilder;
   private PublicIpBlock.Builder publicIpBlockBuilder;
   private FirewallRule.Builder firewallRuleBuilder;

   @BeforeMethod
   public void setUp() throws Exception {
      serverApi = EasyMock.createMock(ServerApi.class);
      networkApi = EasyMock.createMock(NetworkApi.class);
      api = EasyMock.createMock(DimensionDataCloudControlApi.class);

      internalIp = "172.0.0.1";
      final String datacenterId = "EU10";
      serverBuilder = Server.builder().id(serverId).name("jclouds server").memoryGb(10)
            .cpu(CPU.builder().count(1).coresPerSocket(2).speed(CpuSpeed.STANDARD.getDimensionDataSpeed()).build())
            .datacenterId(datacenterId).deployed(true).started(true).description("jclouds server")
            .sourceImageId("sourceImageId").createTime(new Date()).guest(Guest.builder().operatingSystem(
                  OperatingSystem.builder().id("WIN2012DC64").displayName("WIN2012DC/64").family("WINDOWS").build())
                  .vmTools(VmTools.builder().versionStatus(VmTools.VersionStatus.CURRENT)
                        .runningStatus(VmTools.RunningStatus.NOT_RUNNING).apiVersion(9354)
                        .type(VmTools.Type.VMWARE_TOOLS).build()).osCustomization(true).build()).networkInfo(
                  NetworkInfo.builder().networkDomainId(jcloudsNetworkDomainId)
                        .primaryNic(NIC.builder().vlanId("vlanId").privateIpv4(internalIp).build())
                        .additionalNic(Lists.<NIC>newArrayList()).build());

      final String externalIp = "externalIp";
      natRuleBuilder = NatRule.builder().id("natRuleId").internalIp(internalIp).networkDomainId(jcloudsNetworkDomainId)
            .datacenterId(datacenterId).createTime(new Date()).externalIp(externalIp);
      publicIpBlockBuilder = PublicIpBlock.builder().id("publicIpBlockId").baseIp(externalIp).datacenterId(datacenterId)
            .networkDomainId(jcloudsNetworkDomainId).createTime(new Date()).size(2);
      firewallRuleBuilder = FirewallRule.builder().id("firewallRuleId").networkDomainId(jcloudsNetworkDomainId)
            .datacenterId(datacenterId).ruleType("ruleType").action("action").ipVersion("ipVersion")
            .source(FirewallRuleTarget.builder().build()).enabled(false)
            .name(DimensionDataCloudControlResponseUtils.generateFirewallRuleName(serverId)).protocol("protocol");
   }

   @Test
   public void testApply_ServerAndNetworkingDeleted() throws Exception {
      final Server server = serverBuilder.state(State.NORMAL).build();
      loadServerExpectations(server);
      networkApiExpectations();
      natRuleExpectations();
      publicIpBlockExpectations();
      firewallRuleAndPortListExpectations();
      powerOffAndDeleteServerExpectations();
      applyAndAssertDeleted();
   }

   private void powerOffAndDeleteServerExpectations() {
      serverApi.powerOffServer(serverId);
      expectLastCall();
      final Server poweredOffServer = serverBuilder.state(State.NORMAL).started(false).build();
      expect(serverApi.getServer(serverId)).andReturn(poweredOffServer);
      serverApi.deleteServer(serverId);
      expectLastCall();
      expect(serverApi.getServer(serverId)).andReturn(null);
   }

   private void applyAndAssertDeleted() {
      replay(serverApi, networkApi, api);
      cleanupServer = new CleanupServer(api, new ComputeServiceConstants.Timeouts());
      assertTrue(cleanupServer.apply(serverId));
   }

   @Test(dependsOnMethods = "testApply_ServerAndNetworkingDeleted")
   public void testApply_ServerNotInNormalState() throws Exception {
      final Server server = serverBuilder.state(State.FAILED_ADD).build();
      loadServerExpectations(server);
      replay(api, serverApi, networkApi);
      cleanupServer = new CleanupServer(api, new ComputeServiceConstants.Timeouts());
      applyWithExpectedErrorMessage("Server(serverId) not deleted as it is in state(FailedAdd).");
   }

   @Test(dependsOnMethods = "testApply_ServerNotInNormalState")
   public void testApply_NatRuleNotInNormalState() throws Exception {
      final Server server = serverBuilder.state(State.NORMAL).build();
      loadServerExpectations(server);
      networkApiExpectations();

      final NatRule natRule = natRuleBuilder.state(State.FAILED_ADD).build();
      expect(networkApi.listNatRules(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(natRule))));

      publicIpBlockExpectations();
      firewallRuleAndPortListExpectations();
      powerOffAndDeleteServerExpectations();
      applyAndAssertDeleted();
   }

   @Test(dependsOnMethods = "testApply_NatRuleNotInNormalState")
   public void testApply_PublicIpBlockNotInNormalState() throws Exception {
      final Server server = serverBuilder.state(State.NORMAL).build();
      loadServerExpectations(server);
      networkApiExpectations();
      natRuleExpectations();

      final PublicIpBlock publicIpBlock = publicIpBlockBuilder.state(State.FAILED_CHANGE).build();
      expect(networkApi.listPublicIPv4AddressBlocks(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(publicIpBlock))));

      firewallRuleAndPortListExpectations();
      powerOffAndDeleteServerExpectations();
      applyAndAssertDeleted();
   }

   @Test(dependsOnMethods = "testApply_PublicIpBlockNotInNormalState")
   public void testApply_FirewallRuleNotInNormalState() throws Exception {
      final Server server = serverBuilder.state(State.NORMAL).build();
      loadServerExpectations(server);
      networkApiExpectations();
      natRuleExpectations();
      publicIpBlockExpectations();

      final FirewallRule firewallRule = firewallRuleBuilder.state(State.FAILED_CHANGE).destination(
            FirewallRuleTarget.builder()
                  .portList(FirewallRuleTarget.PortList.create("portListId", null, null, null, null)).build()).build();
      expect(networkApi.listFirewallRules(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(firewallRule))));

      powerOffAndDeleteServerExpectations();
      applyAndAssertDeleted();
   }

   private void firewallRuleAndPortListExpectations() {
      final FirewallRule firewallRule = firewallRuleBuilder.state(State.NORMAL).destination(FirewallRuleTarget.builder()
            .portList(FirewallRuleTarget.PortList.create("portListId", null, null, null, null)).build()).build();
      expect(networkApi.listFirewallRules(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(firewallRule))));
      networkApi.deleteFirewallRule(firewallRule.id());
      expectLastCall();
      networkApi.deletePortList(firewallRule.destination().portList().id());
      expectLastCall();
   }

   private void publicIpBlockExpectations() {
      final PublicIpBlock publicIpBlock = publicIpBlockBuilder.state(State.NORMAL).build();
      expect(networkApi.listPublicIPv4AddressBlocks(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(publicIpBlock))));
      networkApi.removePublicIpBlock(publicIpBlock.id());
      expectLastCall();
   }

   private void natRuleExpectations() {
      final NatRule natRule = natRuleBuilder.state(State.NORMAL).build();
      expect(networkApi.listNatRules(jcloudsNetworkDomainId))
            .andReturn(PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(natRule))));
      networkApi.deleteNatRule(natRule.id());
      expectLastCall();
   }

   private void loadServerExpectations(Server server) {
      expect(api.getServerApi()).andReturn(serverApi);
      expect(serverApi.getServer(serverId)).andReturn(server);
   }

   private void applyWithExpectedErrorMessage(String expectedErrorMessage) {
      try {
         cleanupServer.apply(serverId);
      } catch (IllegalStateException e) {
         assertEquals(expectedErrorMessage, e.getMessage());
      }
   }

   private void networkApiExpectations() {
      expect(api.getNetworkApi()).andReturn(networkApi);
   }

}
