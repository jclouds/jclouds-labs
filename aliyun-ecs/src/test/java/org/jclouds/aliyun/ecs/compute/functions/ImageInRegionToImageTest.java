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
package org.jclouds.aliyun.ecs.compute.functions;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.aliyun.ecs.domain.DiskDeviceMapping;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.domain.regionscoped.ImageInRegion;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.jclouds.aliyun.ecs.domain.Image.Status.AVAILABLE;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "ImageInRegionToImageTest")
public class ImageInRegionToImageTest {

   private final Location region = new LocationBuilder()
           .id(Regions.EU_CENTRAL_1.getName())
           .description(Regions.EU_CENTRAL_1.getDescription())
           .scope(LocationScope.REGION).build();
   private final Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(region));

   private ImageInRegionToImage imageInRegionToImage;

   @BeforeMethod
   public void setUp() {
      imageInRegionToImage = new ImageInRegionToImage(locations);
   }

   @Test
   public void testOsImageToImage() {
      final org.jclouds.aliyun.ecs.domain.Image ecsImage = org.jclouds.aliyun.ecs.domain.Image.builder()
              .id("centos_6_09_64_20G_alibase_20180326.vhd")
              .description("")
              .productCode("")
              .osType("linux")
              .architecture("x86_64")
              .osName("CentOS  6.9 64位")
              .imageOwnerAlias("system")
              .progress("100%")
              .isSupportCloudinit(true)
              .usage("instance")
              .creationTime(parseDate("2018-05-10T12:40:55Z"))
              .imageVersion("")
              .status(AVAILABLE)
              .name("centos_6_09_64_20G_alibase_20180326.vhd")
              .isSupportIoOptimizeds(true)
              .isCopied(false)
              .isSubscribed(false)
              .isSelfShared(false)
              .platform("CentOS")
              .size(20)
              .diskDeviceMappings(ImmutableMap.<String, List<DiskDeviceMapping>>of())
              .tags(ImmutableMap.<String, List<Tag>>of())
              .build();

      final Image image = imageInRegionToImage.apply(ImageInRegion.create(Regions.EU_CENTRAL_1.getName(), ecsImage));
      assertEquals(ecsImage.id(), image.getProviderId());
      assertEquals(ecsImage.name(), image.getName());
      assertEquals(Image.Status.AVAILABLE, image.getStatus());
      final org.jclouds.compute.domain.OperatingSystem operatingSystem = image.getOperatingSystem();

      assertEquals(ecsImage.osName(), operatingSystem.getName());
      assertEquals(ecsImage.description(), operatingSystem.getDescription());
      assertTrue(operatingSystem.is64Bit());
      assertEquals(region, image.getLocation());
   }

   @Test
   public void testOsImageFromOtherOSMapToImage() {
      final org.jclouds.aliyun.ecs.domain.Image ecsImage = org.jclouds.aliyun.ecs.domain.Image.builder()
              .id("alinux_17_01_64_20G_cloudinit_20171222.vhd")
              .description("")
              .productCode("")
              .osType("linux")
              .architecture("x86_64")
              .osName("Aliyun Linux  17.1 64位")
              .imageOwnerAlias("system")
              .progress("100%")
              .isSupportCloudinit(true)
              .usage("instance")
              .creationTime(parseDate("2017-12-22T05:56:16Z"))
              .imageVersion("")
              .status(AVAILABLE)
              .name("alinux_17_01_64_20G_cloudinit_20171222.vhd")
              .isSupportIoOptimizeds(true)
              .isCopied(false)
              .isSubscribed(false)
              .isSelfShared(false)
              .platform("Aliyun")
              .size(20)
              .diskDeviceMappings(ImmutableMap.<String, List<DiskDeviceMapping>>of())
              .tags(ImmutableMap.<String, List<Tag>>of())
              .build();

      final Image image = imageInRegionToImage.apply(ImageInRegion.create(Regions.EU_CENTRAL_1.getName(), ecsImage));
      assertEquals(ecsImage.id(), image.getProviderId());
      assertEquals(ecsImage.name(), image.getName());
      assertEquals(Image.Status.AVAILABLE, image.getStatus());
      final org.jclouds.compute.domain.OperatingSystem operatingSystem = image.getOperatingSystem();

      assertEquals(ecsImage.osName(), operatingSystem.getName());
      assertEquals(ecsImage.description(), operatingSystem.getDescription());
      assertTrue(operatingSystem.is64Bit());
      assertEquals(region, image.getLocation());
   }

   Date parseDate(final String dateString) {
      return DatatypeConverter.parseDateTime(dateString).getTime();
   }

}
