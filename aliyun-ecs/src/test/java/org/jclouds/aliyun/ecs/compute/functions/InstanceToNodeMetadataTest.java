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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import org.jclouds.aliyun.ecs.domain.DedicatedHostAttribute;
import org.jclouds.aliyun.ecs.domain.EipAddress;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.NetworkInterface;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.VpcAttributes;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "InstanceToNodeMetadataTest")
public class InstanceToNodeMetadataTest {

   private InstanceToNodeMetadata instanceToNodeMetadata;
   private Image image;
   private Hardware hardware;
   private Location location;
   private Instance instance;
   private OperatingSystem os;
   private Map<String, List<Tag>> tags;

   private String imageId = "centos_6_09_64_20G_alibase_20180326.vhd";
   private String hardwareId = "ecs.t1.xsmall";
   private String regionId = Regions.EU_CENTRAL_1.getName();

   @BeforeMethod
   public void setUp() {

      location = new LocationBuilder().id(regionId)
              .description(Regions.EU_CENTRAL_1.getDescription())
              .scope(LocationScope.PROVIDER)
              .build();
      Supplier<Set<? extends Location>> locations = new Supplier<Set<? extends Location>>() {
         @Override
         public Set<? extends Location> get() {
            return ImmutableSet.of(location);
         }
      };

      GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

      hardware = new HardwareBuilder()
              .ids(hardwareId)
              .name(hardwareId)
              .ram(1024)
              .processor(new Processor(1, 1d))
              .location(location)
              .build();

      os = OperatingSystem.builder()
              .description("CentOS 6.9 64bit")
              .family(OsFamily.CENTOS)
              .version("6.9")
              .is64Bit(true)
              .build();

      image = new ImageBuilder()
              .id(RegionAndId.slashEncodeRegionAndId(regionId, imageId))
              .providerId(imageId)
              .name("CentOS  6.9 64位")
              .description("")
              .operatingSystem(os)
              .status(Image.Status.AVAILABLE)
              .build();

      tags = ImmutableMap.<String, List<Tag>>of("Tag", ImmutableList.of(Tag.create("hello", "")));

      instance = Instance.builder()
              .id("serverId")
              .name("instanceName")
              .regionId(regionId)
              .imageId(imageId)
              .instanceType(hardwareId)
              .instanceTypeFamily("linux")
              .vlanId("vlanId")
              .eipAddress(EipAddress.create("ipAddress", "allocationId", EipAddress.InternetChargeType.ECS_INSTANCE))
              .internetMaxBandwidthIn(1)
              .zoneId("zoneId")
              .internetChargeType(Instance.InternetChargeType.PAY_BY_TRAFFIC)
              .spotStrategy("spotStrategy")
              .stoppedMode("stoppedMode")
              .serialNumber("serialNumber")
              .ioOptimized(true)
              .memory(1024)
              .cpu(1)
              .vpcAttributes(VpcAttributes.create("natIpAddress", ImmutableMap.<String, List<String>>of(), "vSwitchId", "vpcId"))
              .internetMaxBandwidthOut(1)
              .deviceAvailable(true)
              .saleCycle("saleCycle")
              .spotPriceLimit(1d)
              .autoReleaseTime("")
              .startTime(new SimpleDateFormatDateService().iso8601DateParse("2014-03-22T07:16:45.784120972Z"))
              .description("desc")
              .resourceGroupId("resourceGroupId")
              .osType("osType")
              .osName("osName")
              .instanceNetworkType("instanceNetworkType")
              .hostname("hostname")
              .creationTime(new SimpleDateFormatDateService().iso8601DateParse("2014-03-22T05:16:45.784120972Z"))
              .status(Instance.Status.RUNNING)
              .clusterId("clusterId")
              .recyclable(false)
              .gpuSpec("")
              .dedicatedHostAttribute(DedicatedHostAttribute.create("id", "name"))
              .instanceChargeType("instanceChargeType")
              .gpuAmount(1)
              .expiredTime(new SimpleDateFormatDateService().iso8601DateParse("2014-03-22T09:16:45.784120972Z"))
              .innerIpAddress(ImmutableMap.<String, List<String>>of("IpAddress", ImmutableList.of("192.168.0.1", "192.168.0.2")))
              .publicIpAddress(ImmutableMap.<String, List<String>>of("IpAddress", ImmutableList.of("47.254.152.220", "47.254.153.230")))
              .securityGroupIds(ImmutableMap.<String, List<String>>of())
              .networkInterfaces(ImmutableMap.<String, List<NetworkInterface>>of())
              .operationLocks(ImmutableMap.<String, List<String>>of())
              .tags(tags)
              .build();

      Supplier<Map<String, ? extends Image>> images = new Supplier<Map<String, ? extends Image>>() {
         @Override
         public Map<String, ? extends Image> get() {
            return ImmutableMap.of(imageId, image);
         }
      };

      Supplier<Map<String, ? extends Hardware>> hardwares = new Supplier<Map<String, ? extends Hardware>>() {
         @Override
         public Map<String, ? extends Hardware> get() {
            return ImmutableMap.of(hardwareId, hardware);
         }
      };

      instanceToNodeMetadata = new InstanceToNodeMetadata(images, hardwares, locations,
              new InstanceStatusToStatus(), namingConvention);
   }

   @Test
   public void testInstanceToNodeMetadata() {
      NodeMetadata node = instanceToNodeMetadata.apply(instance);

      List<String> privateIpAddresses = instance.innerIpAddress().entrySet().iterator().next().getValue();
      List<String> publicIpAddresses = instance.publicIpAddress().entrySet().iterator().next().getValue();

      assertNotNull(node);
      assertEquals(node.getProviderId(), instance.id());
      assertEquals(node.getName(), instance.name());
      assertEquals(node.getHostname(), instance.hostname());
      assertEquals(node.getGroup(), instance.name());
      assertEquals(node.getHardware(), hardware);
      assertEquals(node.getImageId(), RegionAndId.slashEncodeRegionAndId(regionId, imageId));
      assertEquals(node.getOperatingSystem(), os);
      assertEquals(node.getLocation(), location);
      assertEquals(node.getImageId(), RegionAndId.slashEncodeRegionAndId(regionId, imageId));
      assertEquals(node.getStatus(), NodeMetadata.Status.RUNNING);
      assertEquals(node.getPrivateAddresses(), privateIpAddresses);
      assertEquals(node.getPublicAddresses(), publicIpAddresses);
      assertEquals(node.getTags(), ImmutableSet.of("hello"));
   }

   @Test
   public void testInstanceWithInvalidHardwareToNodeMetadata() {
      Instance instanceWithoutValidHardwareId = instance.toBuilder().instanceType("not.valid").build();
      NodeMetadata node = instanceToNodeMetadata.apply(instanceWithoutValidHardwareId);

      List<String> privateIpAddresses = instanceWithoutValidHardwareId.innerIpAddress().entrySet().iterator().next().getValue();
      List<String> publicIpAddresses = instanceWithoutValidHardwareId.publicIpAddress().entrySet().iterator().next().getValue();

      assertNotNull(node);
      assertEquals(node.getProviderId(), instanceWithoutValidHardwareId.id());
      assertEquals(node.getName(), instanceWithoutValidHardwareId.name());
      assertEquals(node.getHostname(), instanceWithoutValidHardwareId.hostname());
      assertEquals(node.getGroup(), instanceWithoutValidHardwareId.name());
      assertEquals(node.getHardware(), null);
      assertEquals(node.getImageId(), RegionAndId.slashEncodeRegionAndId(regionId, imageId));
      assertEquals(node.getOperatingSystem(), os);
      assertEquals(node.getLocation(), location);
      assertEquals(node.getImageId(), RegionAndId.slashEncodeRegionAndId(regionId, imageId));
      assertEquals(node.getStatus(), NodeMetadata.Status.RUNNING);
      assertEquals(node.getPrivateAddresses(), privateIpAddresses);
      assertEquals(node.getPublicAddresses(), publicIpAddresses);
      assertEquals(node.getTags(), ImmutableSet.of("hello"));
   }

   @Test
   public void testInstanceWithInvalidImageToNodeMetadata() {
      Instance instanceWithoutValidHardwareId = instance.toBuilder().imageId("not.valid").build();
      NodeMetadata node = instanceToNodeMetadata.apply(instanceWithoutValidHardwareId);

      List<String> privateIpAddresses = instanceWithoutValidHardwareId.innerIpAddress().entrySet().iterator().next().getValue();
      List<String> publicIpAddresses = instanceWithoutValidHardwareId.publicIpAddress().entrySet().iterator().next().getValue();

      assertNotNull(node);
      assertEquals(node.getProviderId(), instanceWithoutValidHardwareId.id());
      assertEquals(node.getName(), instanceWithoutValidHardwareId.name());
      assertEquals(node.getHostname(), instanceWithoutValidHardwareId.hostname());
      assertEquals(node.getGroup(), instanceWithoutValidHardwareId.name());
      assertEquals(node.getHardware(), hardware);
      assertEquals(node.getImageId(), null);
      assertEquals(node.getOperatingSystem(), null);
      assertEquals(node.getImageId(), null);
      assertEquals(node.getLocation(), location);
      assertEquals(node.getStatus(), NodeMetadata.Status.RUNNING);
      assertEquals(node.getPrivateAddresses(), privateIpAddresses);
      assertEquals(node.getPublicAddresses(), publicIpAddresses);
      assertEquals(node.getTags(), ImmutableSet.of("hello"));
   }

}
