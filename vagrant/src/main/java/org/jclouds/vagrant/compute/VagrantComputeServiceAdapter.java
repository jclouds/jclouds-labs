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
package org.jclouds.vagrant.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.util.AutomaticHardwareIdSpec;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.logging.Logger;
import org.jclouds.vagrant.api.VagrantApiFacade;
import org.jclouds.vagrant.domain.VagrantNode;
import org.jclouds.vagrant.internal.MachineConfig;
import org.jclouds.vagrant.internal.VagrantNodeRegistry;
import org.jclouds.vagrant.reference.VagrantConstants;
import org.jclouds.vagrant.util.VagrantUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class VagrantComputeServiceAdapter<B> implements ComputeServiceAdapter<VagrantNode, Hardware, B, Location> {
   private static final Pattern PATTERN_IP_ADDR = Pattern.compile("inet ([0-9\\.]+)/(\\d+)");
   private static final Pattern PATTERN_IPCONFIG = Pattern.compile("IPv4 Address[ .]+: ([0-9\\.]+)");

   @Resource
   protected Logger logger = Logger.NULL;

   private final File home;
   private final JustProvider locationSupplier;
   private final VagrantNodeRegistry nodeRegistry;
   private final MachineConfig.Factory machineConfigFactory;
   private final Function<Collection<B>, Collection<B>> outdatedBoxesFilter;
   private final VagrantApiFacade.Factory<B> cliFactory;
   private final Supplier<? extends Map<String, Hardware>> hardwareSupplier;

   @Inject
   VagrantComputeServiceAdapter(@Named(VagrantConstants.JCLOUDS_VAGRANT_HOME) String home,
         JustProvider locationSupplier,
         VagrantNodeRegistry nodeRegistry,
         MachineConfig.Factory machineConfigFactory,
         Function<Collection<B>, Collection<B>> outdatedBoxesFilter,
         VagrantApiFacade.Factory<B> cliFactory,
         Supplier<? extends Map<String, Hardware>> hardwareSupplier) {
      this.home = new File(checkNotNull(home, "home"));
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");
      this.nodeRegistry = checkNotNull(nodeRegistry, "nodeRegistry");
      this.machineConfigFactory = checkNotNull(machineConfigFactory, "machineConfigFactory");
      this.outdatedBoxesFilter = checkNotNull(outdatedBoxesFilter, "outdatedBoxesFilter");
      this.cliFactory = checkNotNull(cliFactory, "cliFactory");
      this.hardwareSupplier = checkNotNull(hardwareSupplier, "hardwareSupplier");
      this.home.mkdirs();
   }

   @Override
   public NodeAndInitialCredentials<VagrantNode> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      String machineName = removeFromStart(name, group);
      File nodePath = new File(home, group);

      init(nodePath, machineName, template);

      NodeAndInitialCredentials<VagrantNode> node = startMachine(nodePath, group, machineName, template.getImage());
      nodeRegistry.add(node.getNode());
      return node;
   }

   private NodeAndInitialCredentials<VagrantNode> startMachine(File path, String group, String name, Image image) {

      VagrantApiFacade<B> vagrant = cliFactory.create(path);
      String rawOutput = vagrant.up(name);
      String output = normalizeOutput(name, rawOutput);

      OsFamily osFamily = image.getOperatingSystem().getFamily();
      String id = group + "/" + name;
      VagrantNode node = VagrantNode.builder()
            .setPath(path)
            .setId(id)
            .setGroup(group)
            .setName(name)
            .setImage(image)
            .setNetworks(getNetworks(output, getOsInterfacePattern(osFamily)))
            .setHostname(getHostname(output))
            .build();
      node.setMachineState(Status.RUNNING);

      LoginCredentials loginCredentials = null;
      if (osFamily != OsFamily.WINDOWS) {
         loginCredentials = vagrant.sshConfig(name);
      }

      // PrioritizeCredentialsFromTemplate will overwrite loginCredentials with image credentials
      // AdaptingComputeServiceStrategies saves the merged credentials in credentialStore
      return new NodeAndInitialCredentials<VagrantNode>(node, node.id(), loginCredentials);
   }

   private String normalizeOutput(String name, String output) {
      return output
            .replaceAll("(?m)^([^,]*,){4}", "")
            .replace("==> " + name + ": ", "")
            // Vagrant shows some of the \n verbatim in provisioning command results.
            .replace("\\n", "\n");
   }

   private Pattern getOsInterfacePattern(OsFamily osFamily) {
      if (osFamily == OsFamily.WINDOWS) {
         return PATTERN_IPCONFIG;
      } else {
         return PATTERN_IP_ADDR;
      }
   }

   private Collection<String> getNetworks(String output, Pattern ifPattern) {
      String networks = getDelimitedString(
            output,
            VagrantConstants.DELIMITER_NETWORKS_START,
            VagrantConstants.DELIMITER_NETWORKS_END);
      Matcher m = ifPattern.matcher(networks);
      Collection<String> ips = new ArrayList<String>();
      while (m.find()) {
         String network = m.group(1);
         // TODO figure out a more generic approach to ignore unreachable networkds (this one is the NAT'd address).
         if (network.startsWith("10.")) continue;
         ips.add(network);
      }
      return ips;
   }

   private String getHostname(String output) {
      return getDelimitedString(
            output,
            VagrantConstants.DELIMITER_HOSTNAME_START,
            VagrantConstants.DELIMITER_HOSTNAME_END);
   }

   private String getDelimitedString(String value, String delimStart, String delimEnd) {
      int startPos = value.indexOf(delimStart);
      int endPos = value.indexOf(delimEnd);
      if (startPos == -1) {
         throw new IllegalStateException("Delimiter " + delimStart + " not found in output \n" + value);
      }
      if (endPos == -1) {
         throw new IllegalStateException("Delimiter " + delimEnd + " not found in output \n" + value);
      }
      return value.substring(startPos + delimStart.length(), endPos).trim();
   }

   private void init(File path, String name, Template template) {
      try {
         writeVagrantfile(path);
         initMachineConfig(path, name, template);
      } catch (IOException e) {
         throw new IllegalStateException("Unable to initialize Vagrant configuration at " +
               path + " for machine " + name, e);
      }
   }

   private void writeVagrantfile(File path) throws IOException {
      path.mkdirs();
      VagrantUtils.write(
            new File(path, VagrantConstants.VAGRANTFILE),
            getClass().getClassLoader().getResourceAsStream(VagrantConstants.VAGRANTFILE));
   }

   private void initMachineConfig(File path, String name, Template template) {
      MachineConfig config = machineConfigFactory.newInstance(path, name);
      List<? extends Volume> volumes = template.getHardware().getVolumes();
      if (volumes != null) {
         if (volumes.size() == 1) {
            Volume volume = Iterables.getOnlyElement(volumes);
            if (volume.getType() != Type.LOCAL || volume.getSize() != null) {
               throw new IllegalStateException("Custom volume settings not supported. Volumes required: " + volumes);
            }
         } else if (volumes.size() > 1) {
            throw new IllegalStateException("Custom volume settings not supported. Volumes required: " + volumes);
         }
      }
      config.save(ImmutableMap.<String, Object>of(
            VagrantConstants.CONFIG_BOX, template.getImage().getName(),
            VagrantConstants.CONFIG_OS_FAMILY, template.getImage().getOperatingSystem().getFamily(),
            VagrantConstants.CONFIG_HARDWARE_ID, getHardwareId(template),
            VagrantConstants.CONFIG_MEMORY, Integer.toString(template.getHardware().getRam()),
            VagrantConstants.CONFIG_CPUS, Integer.toString(countProcessors(template))));
   }

   private String getHardwareId(Template template) {
      String id = template.getHardware().getId();
      if (AutomaticHardwareIdSpec.isAutomaticId(id)) {
         return VagrantConstants.MACHINES_AUTO_HARDWARE;
      } else {
         return id;
      }
   }

   private int countProcessors(Template template) {
      int cnt = 0;
      for (Processor p : template.getHardware().getProcessors()) {
         cnt += p.getCores();
      }
      return cnt;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return hardwareSupplier.get().values();
   }

   @Override
   public Iterable<B> listImages() {
      Collection<B> allBoxes = cliFactory.create(new File(".")).listBoxes();
      return outdatedBoxesFilter.apply(allBoxes);
   }

   @Override
   public B getImage(String id) {
      return cliFactory.create(new File(".")).getBox(id);
   }

   @Override
   public Iterable<Location> listLocations() {
      Location provider = Iterables.getOnlyElement(locationSupplier.get());
      return ImmutableList.of(
            new LocationBuilder().id("localhost").description("localhost").parent(provider).scope(LocationScope.HOST).build());
   }

   @Override
   public VagrantNode getNode(String id) {
      // needed for BaseComputeServiceLiveTest.testAScriptExecutionAfterBootWithBasicTemplate()
      // waits for the thread updating the credentialStore to execute
      try {
         Thread.sleep(200);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw Throwables.propagate(e);
      }

      return nodeRegistry.get(id);
   }

   @Override
   public void destroyNode(String id) {
      VagrantNode node = nodeRegistry.get(id);
      node.setMachineState(Status.TERMINATED);
      getMachine(node).destroy(node.name());
      nodeRegistry.onTerminated(node);
      deleteMachine(node);
   }

   private void deleteMachine(VagrantNode node) {
      File nodeFolder = node.path();
      File machinesFolder = new File(nodeFolder, VagrantConstants.MACHINES_CONFIG_SUBFOLDER);
      String filePattern = node.name() + ".";
      logger.debug("Deleting machine %s", node.id());
      VagrantUtils.deleteFiles(machinesFolder, filePattern);
      // No more machines in this group, remove everything
      if (machinesFolder.list().length == 0) {
         logger.debug("Machine %s is last in group, deleting Vagrant folder %s", node.id(), nodeFolder.getAbsolutePath());
         VagrantUtils.deleteFolder(nodeFolder);
      }
   }

   @Override
   public void rebootNode(String id) {
      halt(id);

      VagrantNode node = nodeRegistry.get(id);
      String name = node.name();
      VagrantApiFacade<B> vagrant = getMachine(node);
      vagrant.up(name);
   }

   private void halt(String id) {
      VagrantNode node = nodeRegistry.get(id);
      String name = node.name();
      VagrantApiFacade<B> vagrant = getMachine(node);

      try {
         vagrant.halt(name);
      } catch (IllegalStateException e) {
         logger.warn(e, "Failed graceful shutdown of machine " + id + ". Will try to halt it forcefully instead.");
         vagrant.haltForced(name);
      }
   }

   @Override
   public void resumeNode(String id) {
      VagrantNode node = nodeRegistry.get(id);
      String name = node.name();
      VagrantApiFacade<B> vagrant = getMachine(node);
      vagrant.up(name);
      node.setMachineState(Status.RUNNING);
   }

   @Override
   public void suspendNode(String id) {
      halt(id);
      VagrantNode node = nodeRegistry.get(id);
      node.setMachineState(Status.SUSPENDED);
   }

   @Override
   public Iterable<VagrantNode> listNodes() {
      return FluentIterable.from(Arrays.asList(home.listFiles()))
            .transformAndConcat(new Function<File, Collection<VagrantNode>>() {
               @Override
               public Collection<VagrantNode> apply(File input) {
                  File machines = new File(input, VagrantConstants.MACHINES_CONFIG_SUBFOLDER);
                  VagrantApiFacade<B> vagrant = cliFactory.create(input);
                  if (input.isDirectory() && machines.exists() && vagrant.exists()) {
                     Collection<VagrantNode> nodes = new ArrayList<VagrantNode>();
                     for (File machine : machines.listFiles()) {
                        if (machine.getName().endsWith(VagrantConstants.MACHINES_CONFIG_EXTENSION)) {
                           String id = input.getName() + "/" + machine.getName().replace(VagrantConstants.MACHINES_CONFIG_EXTENSION, "");
                           VagrantNode n = nodeRegistry.get(id);
                           if (n != null) {
                              nodes.add(n);
                           }
                        }
                     }
                     return nodes;
                  } else {
                     return ImmutableList.of();
                  }
               }
            });
   }

   @Override
   public Iterable<VagrantNode> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<VagrantNode>() {
         @Override
         public boolean apply(VagrantNode input) {
            return Iterables.contains(ids, input.id());
         }
      });
   }

   private VagrantApiFacade<B> getMachine(VagrantNode node) {
      File nodePath = node.path();
      return cliFactory.create(nodePath);
   }

   private String removeFromStart(String name, String group) {
      if (name.startsWith(group)) {
         String machineName = name.substring(group.length());
         // Can't pass names starting with dash on the command line
         if (machineName.startsWith("-")) {
            return machineName.substring(1);
         } else {
            return machineName;
         }
      } else {
         return name;
      }
   }

}
