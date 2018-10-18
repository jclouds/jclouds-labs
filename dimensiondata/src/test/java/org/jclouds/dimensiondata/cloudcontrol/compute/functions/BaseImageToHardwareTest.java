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
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.dimensiondata.cloudcontrol.domain.BaseImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Cluster;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.CustomerImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.ImageNic;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.VirtualHardware;
import org.jclouds.dimensiondata.cloudcontrol.domain.VmTools;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "BaseImageToHardwareTest")
public class BaseImageToHardwareTest {

   private BaseImageToHardware baseImageToHardware;

   @BeforeMethod
   public void setUp() throws Exception {
      baseImageToHardware = new BaseImageToHardware();
   }

   @Test
   public void testApply_OsImage() throws Exception {
      final OsImage osImage = OsImage.builder().id("12ea8472-6e4e-4068-b2cb-f04ecacd3962").name("CentOS 5 64-bit")
            .description("DRaaS CentOS Release 5.9 64-bit").guest(Guest.builder().osCustomization(false)
                  .operatingSystem(
                        OperatingSystem.builder().id("CENTOS564").displayName("CENTOS5/64").family("UNIX").build())
                  .build()).cpu(CPU.builder().count(1).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4)
            .nics(ImmutableList.of(ImageNic.builder().networkAdapter("E1000").key(4040).build())).disks(ImmutableList
                  .of(Disk.builder().id("98299851-37a3-4ebe-9cf1-090da9ae42a0").scsiId(0).sizeGb(20).speed("STANDARD")
                        .build())).softwareLabels(Lists.<String>newArrayList()).osImageKey("T-CENT-5-64-2-4-10")
            .createTime(DatatypeConverter.parseDateTime("2016-06-09T17:36:31.000Z").getTime()).datacenterId("EU6")
            .cluster(Cluster.builder().id("EU6-01").name("my cluster name").build()).build();
      applyAndAssert(osImage);

   }

   private void applyAndAssert(BaseImage baseImage) {
      final Hardware hardware = baseImageToHardware.apply(baseImage);
      assertEquals(baseImage.memoryGb() * 1024, hardware.getRam());
      assertEquals("vmx", hardware.getHypervisor());
      assertEquals(baseImage.id(), hardware.getId());
      assertEquals(baseImage.id(), hardware.getProviderId());
      assertEquals(baseImage.name(), hardware.getName());
      assertEquals(baseImage.disks().size(), hardware.getVolumes().size());
      assertEquals(Float.valueOf(baseImage.disks().get(0).sizeGb()), hardware.getVolumes().get(0).getSize());
      assertEquals(Volume.Type.LOCAL, hardware.getVolumes().get(0).getType());
      assertEquals(baseImage.disks().get(0).id(), hardware.getVolumes().get(0).getId());
      assertEquals(baseImage.disks().get(0).scsiId().toString(), hardware.getVolumes().get(0).getDevice());
      assertEquals(baseImage.cpu().count(), hardware.getProcessors().size());
      assertEquals(Double.valueOf(baseImage.cpu().coresPerSocket()), hardware.getProcessors().get(0).getCores());
      assertEquals(CpuSpeed.STANDARD.getSpeed(), hardware.getProcessors().get(0).getSpeed());
   }

   @Test
   public void testApply_CustomerImage() throws Exception {
      CustomerImage customerImage = CustomerImage.builder().id("f27b7ead-9cdc-4cee-be50-8f8e6cec8534")
            .name("CloneForDrs").cluster(Cluster.builder().id("QA1_N2_VMWARE_1-01").name("QA1_N2_VMWARE_1-01").build())
            .cpu(CPU.builder().count(1).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4).disks(ImmutableList
                  .of(Disk.builder().id("1bddd4ed-67dc-4e5e-a0d5-b5a6c012ec14").scsiId(0).sizeGb(50)
                        .speed("HIGHPERFORMANCE").build()))
            .createTime(new SimpleDateFormatDateService().iso8601DateParse("2016-07-17T23:53:48.000Z"))
            .datacenterId("QA1_N2_VMWARE_1").state(State.FAILED_ADD).guest(Guest.builder().operatingSystem(
                  OperatingSystem.builder().id("WIN2012DC64").displayName("WIN2012DC/64").family("WINDOWS").build())
                  .vmTools(VmTools.builder().versionStatus(VmTools.VersionStatus.CURRENT)
                        .runningStatus(VmTools.RunningStatus.NOT_RUNNING).apiVersion(9354)
                        .type(VmTools.Type.VMWARE_TOOLS).build()).osCustomization(true).build())
            .virtualHardware(VirtualHardware.builder().version("vmx-08").upToDate(false).build()).tags(ImmutableList
                  .of(CustomerImage.TagWithIdAndName.builder().tagKeyName("DdTest3")
                              .tagKeyId("ee58176e-305b-4ec2-85e0-330a33729a94").build(),
                        CustomerImage.TagWithIdAndName.builder().tagKeyName("Lukas11")
                              .tagKeyId("c5480364-d3cd-4391-9536-5c1af683a8f1").value("j").build(),
                        CustomerImage.TagWithIdAndName.builder().tagKeyName("Lukas5")
                              .tagKeyId("a3e869df-6427-404f-99c2-b50f526369aa").build()))
            .softwareLabels(ImmutableList.<String>of()).nics(ImmutableList.<ImageNic>of()).source(
                  CustomerImage.Source.builder().artifacts(ImmutableList
                        .of(CustomerImage.Artifact.builder().value("cb4b8674-09a4-4194-9593-9cdc81489de1")
                              .type(CustomerImage.Artifact.Type.SERVER_ID).build()))
                        .type(CustomerImage.Source.Type.CLONE).build()).build();
      applyAndAssert(customerImage);
   }
}
