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
package org.jclouds.vagrant.internal;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.jclouds.vagrant.util.VagrantUtils;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class VagrantNodeLoaderTest {
    @Test
    public void testAutoMachine() throws Exception {
        ImmutableMap<String, Object> hardwareConfig = ImmutableMap.<String, Object>of(
                VagrantConstants.CONFIG_HARDWARE_ID, VagrantConstants.MACHINES_AUTO_HARDWARE,
                VagrantConstants.CONFIG_CPUS, "2",
                VagrantConstants.CONFIG_MEMORY, "1024");
        Hardware expectedHardware = new HardwareBuilder().ids("automatic:cores=2.0;ram=1024").ram(1024).processor(new Processor(2.0, 1)).build();

        doTest(hardwareConfig, expectedHardware);
    }

    @Test
    public void testSmallMachine() throws Exception {
        ImmutableMap<String, Object> hardwareConfig = ImmutableMap.<String, Object>of(
                VagrantConstants.CONFIG_HARDWARE_ID, "small");
        Hardware expectedHardware = new HardwareBuilder().ids("small").ram(1024).processor(new Processor(1.0, 1)).build();

        doTest(hardwareConfig, expectedHardware);
    }

    protected void doTest(ImmutableMap<String, Object> hardwareConfig, Hardware expectedHardware) throws IOException {
        String groupName = "groupId";
        String machineName = "machineId";

        File home = Files.createTempDir();
        File group = new File(home, groupName);
        File machines = new File(group, VagrantConstants.MACHINES_CONFIG_SUBFOLDER);
        machines.mkdirs();
        File machine = new File(machines, machineName + VagrantConstants.MACHINES_CONFIG_EXTENSION);
        Files.write("dummy", machine, Charsets.UTF_8);

        MachineConfig config = EasyMock.createMock(MachineConfig.class);
        String imageId = "centos/7";
        EasyMock.expect(config.load()).andReturn(ImmutableMap.<String, Object>builder()
                .put(VagrantConstants.CONFIG_BOX, imageId)
                .putAll(hardwareConfig)
                .build());
        MachineConfig.Factory factory = EasyMock.createMock(MachineConfig.Factory.class);
        EasyMock.expect(factory.newInstance(group, machineName)).andReturn(config);

        Image image = EasyMock.createMock(Image.class);
        EasyMock.expect(image.getId()).andReturn(imageId);

        @SuppressWarnings("unchecked")
        Supplier<Collection<Image>> imageSupplier = EasyMock.createMock(Supplier.class);
        EasyMock.expect(imageSupplier.get()).andReturn(ImmutableList.<Image>of(image));

        @SuppressWarnings("unchecked")
        Supplier<Map<String, Hardware>> hardwareSupplier = EasyMock.createMock(Supplier.class);
        EasyMock.expect(hardwareSupplier.get()).andReturn(ImmutableMap.<String, Hardware>of(
                "small", new HardwareBuilder().ids("small").ram(1024).processor(new Processor(1.0, 1)).build()));

        EasyMock.replay(config, factory, imageSupplier, image, hardwareSupplier);

        VagrantExistingMachines nodeLoader = new VagrantExistingMachines(home.getAbsolutePath(), factory, imageSupplier, hardwareSupplier);
        Collection<VagrantNode> nodes = nodeLoader.get();

        VagrantNode actualNode = Iterables.getOnlyElement(nodes);
        VagrantNode expectedNode = VagrantNode.builder()
                .setPath(group)
                .setId(group.getName() + "/" + machineName)
                .setGroup(group.getName())
                .setName(machineName)
                .setImage(image)
                .setHardware(expectedHardware)
                .setNetworks(ImmutableList.<String>of())
                .setHostname("unknown")
                .build();
        assertEquals(actualNode, expectedNode);
        VagrantUtils.deleteFolder(home);
    }

}
