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

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.util.AutomaticHardwareIdSpec;
import org.jclouds.logging.Logger;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.reference.VagrantConstants;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class VagrantExistingMachines implements Supplier<Collection<VagrantNode>> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final File home;
   private final MachineConfig.Factory machineConfigFactory;
   private final Supplier<Collection<Image>> imageLister;
   private final Supplier<? extends Map<String, Hardware>> hardwareSupplier;

   @Inject
   VagrantExistingMachines(@Named(VagrantConstants.JCLOUDS_VAGRANT_HOME) String home,
         MachineConfig.Factory machineConfigFactory,
         Supplier<Collection<Image>> imageLister,
         Supplier<? extends Map<String, Hardware>> hardwareSupplier) {
      this.home = new File(home);
      this.machineConfigFactory = machineConfigFactory;
      this.imageLister = imageLister;
      this.hardwareSupplier = hardwareSupplier;
   }

   @Override
   public Collection<VagrantNode> get() {
      File[] groups = home.listFiles();
      if (groups == null) return ImmutableList.of();
      Map<String, Image> images = getImages();
      Collection<VagrantNode> nodes = Lists.newArrayList();
      for (File group : groups) {
         File[] machines = new File(group, VagrantConstants.MACHINES_CONFIG_SUBFOLDER).listFiles();
         if (machines == null) continue;
         for (File machine : machines) {
            if (machine.getName().endsWith(VagrantConstants.MACHINES_CONFIG_EXTENSION)) {
               try {
                  VagrantNode node = createMachine(group, machine, images);
                  if (node != null) {
                     nodes.add(node);
                  }
               } catch (RuntimeException e) {
                  // Skip image, something is broken about it.
                  // Most probable cause is that another process just deleted it.
                  logger.debug("Failed loading machine " + machine.getAbsolutePath() + ". Skipping.", e);
               }
            }
         }
      }
      return nodes;
   }

   private Map<String, Image> getImages() {
      Collection<Image> images = imageLister.get();
      Map<String, Image> imageMap = Maps.newHashMap();
      for (Image image : images) {
         imageMap.put(image.getId(), image);
      }
      return imageMap;
   }

   // Build minimum viable VagrantNode. Just enough to allow users to halt the machine.
   // If this is found to be inadequate need to keep the missing information in the config
   // file as we can't always fetch it at this point (machine is halted or Windows).
   private VagrantNode createMachine(File group, File machine, Map<String, Image> images) {
      String machineName = machine.getName().replace(VagrantConstants.MACHINES_CONFIG_EXTENSION, "");
      String id = group.getName() + "/" + machineName;
      Map<String, Object> config = machineConfigFactory.newInstance(group, machineName).load();
      String imageName = (String) config.get(VagrantConstants.CONFIG_BOX);
      Image image = images.get(imageName);
      if (image == null) {
         // Machine is unusable if its image is not available, can't be running or started.
         logger.debug("Skipping machine " + machine.getAbsolutePath() +
               " because image " + imageName + " no longer available.");
         return null;
      }
      Hardware hardware = getHardware(id, config);
      // We've got the latest image. Depending on whether the machine is running
      // or halted it could be using an older image or switch to the latest on UP correspondingly.
      // Ubuntu for example will change passwords between image versions so we might need to fix
      // the image version used in future, so it doesn't change and we know which one is used.
      VagrantNode node = VagrantNode.builder()
            .setPath(group)
            .setId(id)
            .setGroup(group.getName())
            .setName(machineName)
            .setImage(image)
            .setHardware(hardware)
            .setNetworks(ImmutableList.<String>of())
            .setHostname("unknown")
            .build();
      // Don't bother asking Vagrant for the status as it could take quite a while for all the running machines
      node.setMachineState(Status.UNRECOGNIZED);
      return node;
   }

   private Hardware getHardware(String id, Map<String, ?> config) {
      String hardwareId = config.get(VagrantConstants.CONFIG_HARDWARE_ID).toString();
      if (hardwareId.equals(VagrantConstants.MACHINES_AUTO_HARDWARE)) {
         double cpus = Double.parseDouble(config.get(VagrantConstants.CONFIG_CPUS).toString());
         int memory = Integer.parseInt(config.get(VagrantConstants.CONFIG_MEMORY).toString());
         AutomaticHardwareIdSpec hardwareSpec = AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(cpus, memory, Optional.<Float>absent());
         return new HardwareBuilder()
               .id(hardwareSpec.toString())
               .providerId(hardwareSpec.toString())
               .processor(new Processor(cpus, 1.0))
               .ram(memory)
               .build();
      } else {
         Hardware hardware = hardwareSupplier.get().get(hardwareId);
         if (hardware == null) {
            throw new IllegalStateException("Unsupported hardwareId " + hardwareId + " for machine " + id);
         }
         return hardware;
      }
   }

}
