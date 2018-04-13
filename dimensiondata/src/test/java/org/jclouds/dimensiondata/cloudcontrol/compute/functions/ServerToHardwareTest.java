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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Volume;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.NIC;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "ServerToHardwareTest")
public class ServerToHardwareTest {

   private ServerToHardware serverToHardware;

   @BeforeMethod
   public void setUp() throws Exception {
      serverToHardware = new ServerToHardware();
   }

   @Test
   public void testApplyServer() throws Exception {
      final Server server = Server.builder().id("12ea8472-6e4e-4068-b2cb-f04ecacd3962").name("CentOS 5 64-bit")
            .description("DRaaS CentOS Release 5.9 64-bit").guest(Guest.builder().osCustomization(false)
                  .operatingSystem(
                        OperatingSystem.builder().id("CENTOS564").displayName("CENTOS5/64").family("UNIX").build())
                  .build()).cpu(CPU.builder().count(1).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4)
            .networkInfo(NetworkInfo.builder().primaryNic(
                  NIC.builder().id("def96a04-d1ee-48b9-b07d-3993594724d2").privateIpv4("192.168.1.2")
                        .vlanId("19737c24-259a-49e2-a5b7-a8a042a96108").build())
                  .additionalNic(Lists.<NIC>newArrayList()).networkDomainId("testNetworkDomain").build()).disks(ImmutableList
                  .of(Disk.builder().id("98299851-37a3-4ebe-9cf1-090da9ae42a0").scsiId(0).sizeGb(20).speed("STANDARD")
                        .build())).softwareLabels(Lists.newArrayList())
            .createTime(DatatypeConverter.parseDateTime("2016-06-09T17:36:31.000Z").getTime()).datacenterId("EU6")
            .state(State.NORMAL).sourceImageId("1806fe4a-0400-46ad-a6ab-1fe3c9ebc947").started(false).deployed(true)
            .build();
      applyAndAssert(server);
   }

   private void applyAndAssert(Server server) {
      final Hardware hardware = serverToHardware.apply(server);
      assertEquals(server.memoryGb() * 1024, hardware.getRam());
      assertEquals("vmx", hardware.getHypervisor());
      assertEquals(server.id(), hardware.getId());
      assertEquals(server.id(), hardware.getProviderId());
      assertEquals(server.name(), hardware.getName());
      assertEquals(server.disks().size(), hardware.getVolumes().size());
      assertEquals(Float.valueOf(server.disks().get(0).sizeGb()), hardware.getVolumes().get(0).getSize());
      assertEquals(Volume.Type.LOCAL, hardware.getVolumes().get(0).getType());
      assertEquals(server.disks().get(0).id(), hardware.getVolumes().get(0).getId());
      assertEquals(server.disks().get(0).scsiId().toString(), hardware.getVolumes().get(0).getDevice());
      assertEquals(server.cpu().count(), hardware.getProcessors().size());
      assertEquals(Double.valueOf(server.cpu().coresPerSocket()), hardware.getProcessors().get(0).getCores());
      assertEquals(CpuSpeed.STANDARD.getSpeed(), hardware.getProcessors().get(0).getSpeed());
   }

}
