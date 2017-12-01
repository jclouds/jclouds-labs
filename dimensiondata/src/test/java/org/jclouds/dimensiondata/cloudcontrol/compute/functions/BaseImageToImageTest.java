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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.jclouds.compute.domain.Image;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Cluster;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.Guest;
import org.jclouds.dimensiondata.cloudcontrol.domain.ImageNic;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "BaseImageToImageTest")
public class BaseImageToImageTest {

   private final Location zone = new LocationBuilder().id("EU6").description("EU6").scope(LocationScope.ZONE).build();
   private final Supplier<Set<Location>> locations = Suppliers.<Set<Location>>ofInstance(ImmutableSet.of(zone));
   private BaseImageToImage baseImageToImage;

   @BeforeMethod
   public void setUp() throws Exception {
      baseImageToImage = new BaseImageToImage(locations, new OperatingSystemToOsFamily());
   }

   @Test
   public void testOsImageToImage() throws Exception {
      final OsImage osImage = OsImage.builder().id("12ea8472-6e4e-4068-b2cb-f04ecacd3962").name("CentOS 5 64-bit")
            .description("DRaaS CentOS Release 5.9 64-bit").guest(Guest.builder().osCustomization(false)
                  .operatingSystem(
                        OperatingSystem.builder().id("CENTOS564").displayName("CENTOS5/64").family("UNIX").build())
                  .build()).cpu(CPU.builder().count(2).speed("STANDARD").coresPerSocket(1).build()).memoryGb(4)
            .nics(ImmutableList.of(ImageNic.builder().networkAdapter("E1000").key(4040).build())).disks(ImmutableList
                  .of(Disk.builder().id("98299851-37a3-4ebe-9cf1-090da9ae42a0").scsiId(0).sizeGb(20).speed("STANDARD")
                        .build())).softwareLabels(Lists.<String>newArrayList()).osImageKey("T-CENT-5-64-2-4-10")
            .createTime(DatatypeConverter.parseDateTime("2016-06-09T17:36:31.000Z").getTime()).datacenterId("EU6")
            .cluster(Cluster.builder().id("EU6-01").name("my cluster name").build()).build();

      final Image image = baseImageToImage.apply(osImage);
      assertEquals(osImage.id(), image.getId());
      assertEquals(osImage.name(), image.getName());
      assertEquals(Image.Status.AVAILABLE, image.getStatus());
      final org.jclouds.compute.domain.OperatingSystem operatingSystem = image.getOperatingSystem();

      assertEquals(osImage.name(), operatingSystem.getName());
      assertEquals(osImage.description(), operatingSystem.getDescription());
      assertTrue(operatingSystem.is64Bit());
      assertEquals(zone, image.getLocation());
   }

   @Test
   public void parseVersion_Centos() {
      assertEquals("5", baseImageToImage
            .parseVersion(OperatingSystem.builder().id("CENTOS532").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_Suse() {
      assertEquals("10",
            baseImageToImage.parseVersion(OperatingSystem.builder().id("SUSE1032").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_RedHat() {
      assertEquals("6", baseImageToImage
            .parseVersion(OperatingSystem.builder().id("REDHAT632").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_Ubuntu() {
      assertEquals("X", baseImageToImage
            .parseVersion(OperatingSystem.builder().id("UBUNTUX64").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_Windows() {
      assertEquals("2003S", baseImageToImage
            .parseVersion(OperatingSystem.builder().id("WIN2003S64").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_Solaris() {
      assertEquals("11", baseImageToImage
            .parseVersion(OperatingSystem.builder().id("SOLARIS1164").displayName("").family("").build()));
   }

   @Test
   public void parseVersion_Unknown() {
      assertEquals("unknown",
            baseImageToImage.parseVersion(OperatingSystem.builder().id("XXXX").displayName("").family("").build()));
   }

}
