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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import org.easymock.EasyMock;
import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.CloudSigma2ApiMetadata;
import org.jclouds.cloudsigma2.domain.DeviceEmulationType;
import org.jclouds.cloudsigma2.domain.Drive;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.IP;
import org.jclouds.cloudsigma2.domain.IPConfiguration;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.ServerStatus;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.all.JustProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.net.URI;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.cloudsigma2.compute.config.CloudSigma2ComputeServiceContextModule.serverStatusToNodeStatus;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "ServerInfoToNodeMetadataTest")
public class ServerInfoToNodeMetadataTest {

   private ServerInfo input;
   private NodeMetadata expected;
   private Map<String, Credentials> credentialStore;
   private GroupNamingConvention.Factory namingConvention;
   private LoginCredentials credentials = LoginCredentials.builder().user("ubuntu").password("ubuntu").build();
   private JustProvider justProvider;

   @BeforeMethod
   public void setUp() throws Exception {
      CloudSigma2ApiMetadata metadata = new CloudSigma2ApiMetadata();
      justProvider = new JustProvider(metadata.getId(), Suppliers.ofInstance(URI.create(metadata
            .getDefaultEndpoint().get())), ImmutableSet.<String>of());

      input = new ServerInfo.Builder()
            .uuid("a19a425f-9e92-42f6-89fb-6361203071bb")
            .name("jclouds-cloudsigma-test_acc_full_server")
            .cpu(1000)
            .memory(new BigInteger("268435456"))
            .status(ServerStatus.STOPPED)
            .drives(ImmutableList.of(
                  new Drive.Builder()
                        .uuid("ae78e68c-9daa-4471-8878-0bb87fa80260")
                        .resourceUri(new URI("/api/2.0/drives/ae78e68c-9daa-4471-8878-0bb87fa80260/"))
                        .build().toServerDrive(0, "0:0", DeviceEmulationType.IDE),
                  new Drive.Builder()
                        .uuid("22826af4-d6c8-4d39-bd41-9cea86df2976")
                        .resourceUri(new URI("/api/2.0/drives/22826af4-d6c8-4d39-bd41-9cea86df2976/"))
                        .build().toServerDrive(1, "0:0", DeviceEmulationType.VIRTIO)))
            .nics(ImmutableList.of(new NIC.Builder()
                  .ipV4Configuration(new IPConfiguration.Builder()
                        .ip(new IP.Builder().uuid("1.2.3.4").build())
                        .build())
                  .build()))
            .meta(ImmutableMap.of("foo", "bar", "image_id", "image"))
            .tags(ImmutableList.of(new Tag.Builder().uuid("foo").name("foo").build(),
                  new Tag.Builder().uuid("jclouds-cloudsigma2-s").name("jclouds-cloudsigma2-s").build(),
                  new Tag.Builder().uuid("jclouds-cloudsigma2").name("jclouds-cloudsigma2").build()
            ))
            .build();

      expected = new NodeMetadataBuilder()
            .ids("a19a425f-9e92-42f6-89fb-6361203071bb")
            .name("jclouds-cloudsigma-test_acc_full_server")
            .group("jclouds-cloudsigma")
            .status(NodeMetadata.Status.SUSPENDED)
            .location(getOnlyElement(justProvider.get()))
            .imageId("image")
            .hardware(new HardwareBuilder()
                  .ids("a19a425f-9e92-42f6-89fb-6361203071bb")
                  .processor(new Processor(1, 1000))
                  .ram(268435456)
                  .volumes(ImmutableSet.of(
                        new VolumeBuilder()
                              .id("ae78e68c-9daa-4471-8878-0bb87fa80260")
                              .size(1024000f)
                              .durable(true)
                              .type(Volume.Type.NAS)
                              .bootDevice(false)
                              .build(),
                        new VolumeBuilder()
                              .id("22826af4-d6c8-4d39-bd41-9cea86df2976")
                              .size(1024000f)
                              .durable(true)
                              .type(Volume.Type.NAS)
                              .bootDevice(true)
                              .build()))
                  .build())
            .publicAddresses(ImmutableSet.of("1.2.3.4"))
            .userMetadata(ImmutableMap.of("foo", "bar", "image_id", "image"))
            .tags(ImmutableList.of("foo", "cloudsigma2-s", "cloudsigma2"))
            .credentials(credentials)
            .build();

      namingConvention = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new CloudSigma2ApiMetadata().getDefaultProperties());
         }
      }).getInstance(GroupNamingConvention.Factory.class);

      credentialStore = ImmutableMap.of("node#a19a425f-9e92-42f6-89fb-6361203071bb", (Credentials) credentials);
   }

   public void testConvertServerInfo() {
      CloudSigma2Api api = EasyMock.createMock(CloudSigma2Api.class);

      for (ServerDrive drive : input.getDrives()) {
         DriveInfo mockDrive = new DriveInfo.Builder()
               .uuid(drive.getDriveUuid())
               .size(new BigInteger("1024000"))
               .build();

         expect(api.getDriveInfo(drive.getDriveUuid())).andReturn(mockDrive);
      }

      // tags
      expect(api.getTagInfo("foo")).andReturn(new Tag.Builder().name("foo").build());
      expect(api.getTagInfo("jclouds-cloudsigma2-s")).andReturn(new Tag.Builder().name("jclouds-cloudsigma2-s")
            .build());
      expect(api.getTagInfo("jclouds-cloudsigma2")).andReturn(new Tag.Builder().name("jclouds-cloudsigma2").build());

      replay(api);

      ServerInfoToNodeMetadata function = new ServerInfoToNodeMetadata(new ServerDriveToVolume(api), new NICToAddress(),
            serverStatusToNodeStatus, namingConvention, credentialStore, justProvider, api);

      NodeMetadata converted = function.apply(input);
      assertEquals(converted, expected);
      assertEquals(converted.getName(), expected.getName());
      assertEquals(converted.getStatus(), expected.getStatus());
      assertEquals(converted.getPublicAddresses(), expected.getPublicAddresses());
      assertEquals(converted.getImageId(), expected.getImageId());
      assertEquals(converted.getHardware(), expected.getHardware());
      assertEquals(converted.getCredentials(), expected.getCredentials());
      assertEquals(converted.getGroup(), expected.getGroup());
      assertEquals(converted.getTags(), expected.getTags());
      assertEquals(converted.getUserMetadata(), expected.getUserMetadata());

      verify(api);
   }
}
