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
package org.jclouds.dimensiondata.cloudcontrol.compute.function;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.internal.ServerWithExternalIp;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.easymock.EasyMock.expect;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "ServerToServerWithExternalIpTest")
public class ServerToServerWithExternalIpTest {

   private DimensionDataCloudControlApi dimensionDataCloudControlApi;
   private NetworkApi networkApi;
   private Server server;
   private NIC nic;
   private CPU cpu;
   private OperatingSystem os;
   private NatRule natRule;

   @BeforeMethod
   public void setUp() {
      dimensionDataCloudControlApi = EasyMock.createNiceMock(DimensionDataCloudControlApi.class);
      networkApi = EasyMock.createNiceMock(NetworkApi.class);
      natRule = EasyMock.createNiceMock(NatRule.class);

      nic = EasyMock.createNiceMock(NIC.class);
      cpu = EasyMock.createNiceMock(CPU.class);
      os = OperatingSystem.builder().family(OsFamily.WINDOWS.name()).id("Win10").displayName("Windows 10").build();

      EasyMockSupport.injectMocks(dimensionDataCloudControlApi);
      EasyMockSupport.injectMocks(networkApi);
   }

   @Test
   public void testServerToServerWithExternalIpApplyNotNull() {
      String internalIp = "192.168.1.1";
      String externalIp = "10.12.122.1";
      String networkDomainId = "NetworkDomain1";

      server = Server.builder().id("serverId").name("serverName").datacenterId("NA1")
            .networkInfo(NetworkInfo.create(networkDomainId, nic, new ArrayList<NIC>())).cpu(cpu).deployed(true)
            .state(State.NORMAL).sourceImageId("imageId").started(false).createTime(new Date()).memoryGb(1024)
            .guest(Guest.builder().osCustomization(false).operatingSystem(os).build()).build();

      PagedIterable<NatRule> natRules = PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(natRule)));

      expect(dimensionDataCloudControlApi.getNetworkApi()).andReturn(networkApi);
      expect(networkApi.listNatRules(networkDomainId)).andReturn(natRules);
      expect(nic.privateIpv4()).andReturn(internalIp);
      expect(natRule.externalIp()).andReturn(externalIp);
      expect(natRule.internalIp()).andReturn(internalIp);

      EasyMock.replay(dimensionDataCloudControlApi, networkApi, natRule, nic);

      ServerWithExternalIp result = new ServerToServerWithExternalIp(dimensionDataCloudControlApi).apply(server);
      assertNotNull(result);
      assertEquals(result.server(), server);
      assertEquals(result.externalIp(), externalIp);
   }

   @Test(dependsOnMethods = "testServerToServerWithExternalIpApplyNotNull")
   public void testServerToServerWithExternalIpApplyNull() {
      server = null;
      ServerWithExternalIp result = new ServerToServerWithExternalIp(dimensionDataCloudControlApi).apply(server);
      assertNull(result);
   }

   @Test(dependsOnMethods = "testServerToServerWithExternalIpApplyNotNull")
   public void testServerToServerWithExternalIpApplyNetworkInfoNull() {
      server = Server.builder().id("serverId").name("serverName").datacenterId("NA1").networkInfo(null).cpu(cpu)
            .deployed(true).state(State.NORMAL).sourceImageId("imageId").started(false).createTime(new Date())
            .memoryGb(1024).guest(Guest.builder().osCustomization(false).operatingSystem(os).build()).build();

      ServerWithExternalIp result = new ServerToServerWithExternalIp(dimensionDataCloudControlApi).apply(server);
      assertNotNull(result);
      assertEquals(result.server(), server);
      assertNull(result.externalIp());
   }

   @Test(dependsOnMethods = "testServerToServerWithExternalIpApplyNetworkInfoNull")
   public void testServerToServerWithExternalIpApplyNoMathingNatRuleFound() {
      String internalIp = "192.168.1.1";
      String natIp = "192.168.1.2";
      String networkDomainId = "NetworkDomain1";

      server = Server.builder().id("serverId").name("serverName").datacenterId("NA1")
            .networkInfo(NetworkInfo.create(networkDomainId, nic, new ArrayList<NIC>())).cpu(cpu).deployed(true)
            .state(State.NORMAL).sourceImageId("imageId").started(false).createTime(new Date()).memoryGb(1024)
            .guest(Guest.builder().osCustomization(false).operatingSystem(os).build()).build();

      PagedIterable<NatRule> natRules = PagedIterables.onlyPage(IterableWithMarkers.from(Lists.newArrayList(natRule)));

      expect(dimensionDataCloudControlApi.getNetworkApi()).andReturn(networkApi);
      expect(networkApi.listNatRules(networkDomainId)).andReturn(natRules);
      expect(nic.privateIpv4()).andReturn(internalIp);
      expect(natRule.internalIp()).andReturn(natIp);

      EasyMock.replay(dimensionDataCloudControlApi, networkApi, natRule, nic);

      ServerWithExternalIp result = new ServerToServerWithExternalIp(dimensionDataCloudControlApi).apply(server);
      assertNotNull(result);
      assertEquals(result.server(), server);
      assertNull(result.externalIp());
   }
}
