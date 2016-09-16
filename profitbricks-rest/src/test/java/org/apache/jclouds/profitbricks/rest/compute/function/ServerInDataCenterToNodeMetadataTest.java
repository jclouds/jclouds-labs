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
package org.apache.jclouds.profitbricks.rest.compute.function;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.Set;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApiMetadata;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.ServerInDataCenter;
import org.apache.jclouds.profitbricks.rest.features.DataCenterApi;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.jclouds.compute.domain.HardwareBuilder;
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

@Test(groups = "unit", testName = "ServerInDataCenterToNodeMetadataTest", singleThreaded = true)
public class ServerInDataCenterToNodeMetadataTest extends BaseProfitBricksApiMockTest {

   private ServerInDataCenterToNodeMetadata fnNodeMetadata;
   private DataCenterApi dataCenterApi;

   @BeforeTest
   public void setup() {
      Supplier<Set<? extends Location>> locationsSupply = new Supplier<Set<? extends Location>>() {

         @Override
         public Set<? extends Location> get() {
            return ImmutableSet.of(
                    new LocationBuilder()
                    .id("mock")
                    .description("JClouds-DC")
                    .scope(LocationScope.REGION)
                    .metadata(ImmutableMap.<String, Object>of(
                            "version", "10",
                            "state", "AVAILABLE"))
                    .parent(new LocationBuilder()
                            .id("de/fra")
                            .description("Germany, Frankfurt (M)")
                            .scope(LocationScope.PROVIDER)
                            .build())
                    .build());
         }
      };

      GroupNamingConvention.Factory namingConvention = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new ProfitBricksApiMetadata().getDefaultProperties());
         }
      }).getInstance(GroupNamingConvention.Factory.class);

      api = EasyMock.createMock(ProfitBricksApi.class);
      dataCenterApi = EasyMock.createMock(DataCenterApi.class);
      expect(dataCenterApi.getDataCenter("mock")).andReturn(
              DataCenter.create("mock", "datacenter", "href", null, DataCenter.Properties.create("location", "location", org.apache.jclouds.profitbricks.rest.domain.Location.MOCK, 0), null));
      expect(api.dataCenterApi()).andReturn(dataCenterApi);

      replay(dataCenterApi, api);

      this.fnNodeMetadata = new ServerInDataCenterToNodeMetadata(new VolumeToVolume(), locationsSupply, api, namingConvention);
   }

   @Test
   public void testServerInDataCenterToNodeMetadata() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/server.json"))
      );
      Server serverObject = api.serverApi().getServer("mock", "some-id");

      ServerInDataCenter server = new ServerInDataCenter(serverObject, "mock");

      NodeMetadata expected = fnNodeMetadata.apply(server);
      assertNotNull(expected);

      NodeMetadata actual = new NodeMetadataBuilder()
              .group("docker001")
              .ids(server.getDataCenter() + "/" + serverObject.id())
              .name(server.getServer().properties().name())
              .backendStatus("AVAILABLE")
              .status(NodeMetadata.Status.RUNNING)
              .hardware(new HardwareBuilder()
                      .ids("cpu=4,ram=4096,disk=40")
                      .name("cpu=4,ram=4096,disk=40")
                      .ram(server.getServer().properties().ram())
                      .processor(new Processor(server.getServer().properties().cores(), 1d))
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
                      .id("mock")
                      .description("JClouds-DC")
                      .scope(LocationScope.REGION)
                      .metadata(ImmutableMap.<String, Object>of(
                              "version", "10",
                              "state", "AVAILABLE"))
                      .parent(new LocationBuilder()
                              .id("de/fra")
                              .description("Germany, Frankfurt (M)")
                              .scope(LocationScope.PROVIDER)
                              .build())
                      .build())
              .publicAddresses(ImmutableList.<String>of("173.252.120.6"))
              .build();

      assertEquals(actual, expected);
   }
}
