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
package org.apache.jclouds.oneandone.rest.compute.function;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.OneAndOneApiMetadata;
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.ServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.features.DataCenterApi;
import org.apache.jclouds.oneandone.rest.features.ServerApi;
import org.apache.jclouds.oneandone.rest.features.ServerApplianceApi;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerToNodeMetadataTest", singleThreaded = true)
public class ServerToNodeMetadataTest extends BaseOneAndOneApiMockTest {

   private ServerToNodeMetadata fnNodeMetadata;
   private DataCenterApi dataCenterApi;
   private ServerApplianceApi serverApplianceApi;
   private ServerApi serverApi;

   @BeforeTest
   public void setup() {
      Supplier<Set<? extends Location>> locationsSupply = new Supplier<Set<? extends Location>>() {

         @Override
         public Set<? extends Location> get() {
            return ImmutableSet.of(
                    new LocationBuilder()
                    .id("908DC2072407C94C8054610AD5A53B8C")
                    .description("us")
                    .scope(LocationScope.REGION)
                    .metadata(ImmutableMap.<String, Object>of(
                            "version", "10",
                            "state", "AVAILABLE"))
                    .parent(new LocationBuilder()
                            .id("de")
                            .description("Germany")
                            .scope(LocationScope.PROVIDER)
                            .build())
                    .build());
         }
      };
      Supplier<Map<String, ? extends Hardware>> hardwareFlavours = Suppliers.<Map<String, ? extends Hardware>>ofInstance(ImmutableMap
              .<String, Hardware>of("65929629F35BBFBA63022008F773F3EB", new HardwareBuilder().id("65929629F35BBFBA63022008F773F3EB").build()));

      Supplier<Map<String, ? extends Image>> images = Suppliers.<Map<String, ? extends Image>>ofInstance(ImmutableMap
              .<String, Image>of("B5F778B85C041347BCDCFC3172AB3F3C", new ImageBuilder().id("B5F778B85C041347BCDCFC3172AB3F3C")
                      .operatingSystem(OperatingSystem.builder().description("").is64Bit(true).build()).status(Image.Status.AVAILABLE).build()));

      GroupNamingConvention.Factory namingConvention = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new OneAndOneApiMetadata().getDefaultProperties());
         }
      }).getInstance(GroupNamingConvention.Factory.class);

      api = EasyMock.createMock(OneAndOneApi.class);
      dataCenterApi = EasyMock.createMock(DataCenterApi.class);
      serverApi = EasyMock.createMock(ServerApi.class);
      serverApplianceApi = EasyMock.createMock(ServerApplianceApi.class);

      expect(dataCenterApi.get("908DC2072407C94C8054610AD5A53B8C")).andReturn(
              DataCenter.create("908DC2072407C94C8054610AD5A53B8C", "usa", "US"));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "B5F778B85C041347BCDCFC3172AB3F3C", null);
      List<ServerAppliance> appliances = new ArrayList<ServerAppliance>();
      ServerAppliance appliance = ServerAppliance.create("B5F778B85C041347BCDCFC3172AB3F3C", "name", null, "empty", Types.OSFamliyType.Linux, "ubuntu",
              "Ubuntu14.04", 64, Types.OSImageType.Minimal, 20, Types.ApplianceType.IMAGE, null, null, null, null);
      appliances.add(appliance);
      List<HardwareFlavour.Hardware.Hdd> hdds = new ArrayList<HardwareFlavour.Hardware.Hdd>();
      HardwareFlavour.Hardware.Hdd hdd = HardwareFlavour.Hardware.Hdd.create("GB", 30, true);
      hdds.add(hdd);
      expect(serverApi.getHardwareFlavour("3D4C49EAEDD42FBC23DB58FE3DEF464F")).andReturn(
              HardwareFlavour.create("3D4C49EAEDD42FBC23DB58FE3DEF464F", "mock",
                      HardwareFlavour.Hardware.create("3D4C49EAEDD42FBC23DB58FE3DEF464F", 1, 1, 0.5, hdds)));

      expect(serverApplianceApi.list(options)).andReturn(appliances);

      expect(api.dataCenterApi()).andReturn(dataCenterApi);
      expect(api.serverApi()).andReturn(serverApi);
      expect(api.serverApplianceApi()).andReturn(serverApplianceApi);

      replay(serverApplianceApi, dataCenterApi, serverApi, api);

      this.fnNodeMetadata = new ServerToNodeMetadata(new HddToVolume(), locationsSupply, hardwareFlavours, images, api, namingConvention);
   }

   @Test
   public void testServerToNodeMetadata() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/server.json"))
      );
      Server serverObject = api.serverApi().get("mock");

      NodeMetadata expected = fnNodeMetadata.apply(serverObject);
      assertNotNull(expected);

      NodeMetadata actual = new NodeMetadataBuilder()
              .group("docker001")
              .ids(serverObject.id())
              .name(serverObject.name())
              .backendStatus("AVAILABLE")
              .status(NodeMetadata.Status.RUNNING)
              .hardware(new HardwareBuilder()
                      .ids("cpu=4,ram=4096,disk=40")
                      .name("cpu=4,ram=4096,disk=40")
                      .ram((int) serverObject.hardware().ram())
                      .processor(new Processor(serverObject.hardware().coresPerProcessor(), 1d))
                      .hypervisor("kvm")
                      .volume(new VolumeBuilder()
                              .bootDevice(true)
                              .size(40f)
                              .id("c04a2198-7e60-4bc0-b869-6e9c9dbcb8e1")
                              .durable(true)
                              .type(Volume.Type.LOCAL)
                              .build())
                      .build())
              .operatingSystem(new OperatingSystem.Builder()
                      .description(OsFamily.LINUX.value())
                      .family(OsFamily.LINUX)
                      .build())
              .location(new LocationBuilder()
                      .id("908DC2072407C94C8054610AD5A53B8C")
                      .description("us")
                      .scope(LocationScope.REGION)
                      .metadata(ImmutableMap.<String, Object>of(
                              "version", "10",
                              "state", "AVAILABLE"))
                      .parent(new LocationBuilder()
                              .id("de")
                              .description("Germany")
                              .scope(LocationScope.PROVIDER)
                              .build())
                      .build())
              .publicAddresses(ImmutableList.<String>of("173.252.120.6"))
              .build();

      assertEquals(actual, expected);
   }
}
