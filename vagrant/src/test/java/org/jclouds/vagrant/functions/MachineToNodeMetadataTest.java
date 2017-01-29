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
package org.jclouds.vagrant.functions;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Set;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.domain.Location;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import vagrant.api.domain.Box;

public class MachineToNodeMetadataTest {
   private abstract static class MachineToNodeMetadataFixture {
      @Test
      public void doTest() {
         OperatingSystem os = new OperatingSystem(getOsFamily(), "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
         Image image = new ImageBuilder()
               .ids("jclouds/box")
               .operatingSystem(os)
               .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
               .build();
         Hardware hardware = new HardwareBuilder().ids("mini").ram(100).processor(new Processor(1.0, 1)).build();
         ImmutableList<String> networks = ImmutableList.of("172.28.128.3");

         VagrantNode node = VagrantNode.builder()
               .setPath(new File("/path/to/machine"))
               .setId("vagrant/node")
               .setGroup("vagrant")
               .setName("node")
               .setImage(image)
               .setHardware(hardware)
               .setNetworks(networks)
               .setHostname("vagrant-node")
               .build();

         node.setMachineState(Status.RUNNING);

         Location location = EasyMock.createMock(Location.class);

         BoxConfig boxConfig = EasyMock.createMock(BoxConfig.class);
         expectBoxConfig(boxConfig);

         BoxConfig.Factory boxConfigFactory = EasyMock.createMock(BoxConfig.Factory.class);
         EasyMock.expect(boxConfigFactory.newInstance((Image)EasyMock.<Box>anyObject())).andReturn(boxConfig);

         EasyMock.replay(location, boxConfig, boxConfigFactory);

         @SuppressWarnings({ "unchecked", "rawtypes" })
         Supplier<Set<? extends Location>> locations = (Supplier<Set<? extends Location>>)(Supplier)Suppliers.ofInstance(ImmutableSet.of(location));

         MachineToNodeMetadata machineToNodeMetadata = new MachineToNodeMetadata(
               locations,
               boxConfigFactory);

         NodeMetadata nodeMetadataActual = machineToNodeMetadata.apply(node);

         NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder()
               .ids("vagrant/node")
               .name("node")
               .location(location)
               .group("vagrant")
               .imageId("jclouds/box")
               .operatingSystem(os)
               .status(Status.RUNNING)
               .hostname("vagrant-node")
               .privateAddresses(networks)
               .hardware(hardware);
         customizeBuilder(nodeMetadataBuilder);
         NodeMetadata nodeMetadataExpected = nodeMetadataBuilder
               .build();

         assertEquals(nodeMetadataActual.toString(), nodeMetadataExpected.toString());
      }

      protected abstract void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder);
      protected abstract OsFamily getOsFamily();
      protected abstract void expectBoxConfig(BoxConfig boxConfig);
   }

   @Test
   public void testMiniLinux() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {
         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(2222);
         }

         protected OsFamily getOsFamily() {
            return OsFamily.LINUX;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_SSH_PORT)).andReturn(Optional.of("2222"));
         }
      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

   @Test
   public void testDefaultSshPort() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {

         protected OsFamily getOsFamily() {
            return OsFamily.LINUX;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_SSH_PORT)).andReturn(Optional.<String>absent());
         }

         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(22);
         }
      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

   @Test
   public void testAutoLinux() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {
         protected OsFamily getOsFamily() {
            return OsFamily.LINUX;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_SSH_PORT)).andReturn(Optional.of("2222"));
         }

         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(2222);
         }

      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

   @Test
   public void testMiniWin() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {
         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(8899);
         }

         protected OsFamily getOsFamily() {
            return OsFamily.WINDOWS;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_WINRM_PORT)).andReturn(Optional.of("8899"));
         }
      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

   @Test
   public void testDefaultWinrmPort() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {

         protected OsFamily getOsFamily() {
            return OsFamily.WINDOWS;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_WINRM_PORT)).andReturn(Optional.<String>absent());
         }

         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(5985);
         }

      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

   @Test
   public void testAutoWin() {
      class MachineToNodeMetadataLinuxMini extends MachineToNodeMetadataFixture {
         protected OsFamily getOsFamily() {
            return OsFamily.WINDOWS;
         }

         protected void expectBoxConfig(BoxConfig boxConfig) {
            EasyMock.expect(boxConfig.getKey(VagrantConstants.KEY_WINRM_PORT)).andReturn(Optional.of("8899"));
         }

         protected void customizeBuilder(NodeMetadataBuilder nodeMetadataBuilder) {
            nodeMetadataBuilder.loginPort(8899);
         }

      }
      new MachineToNodeMetadataLinuxMini().doTest();
   }

}
