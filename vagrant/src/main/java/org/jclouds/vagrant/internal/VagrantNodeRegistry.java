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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.vagrant.domain.VagrantNode;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * The provider serves the following purposes:
 * <ol>
 *   <li>holds information that's not available elsewhere - hostname and IPs</li>
 *   <li>cache of available machines</li>
 *   <li>cache of the machine state</li>
 * </ol>
 * <p>
 * To expand on each one:
 * <p>
 * 1. Hostname and IPs of the machine are fetched on boot using vagrant's provisioning scripts.
 * There are a couple of reasons for that. First vagrant calls are very expensive (relatively
 * for a local call). Each exec takes at least several seconds. Integrating them with the boot
 * saves a couple of ssh calls. Second the windows winrm transport is available only through the
 * provisioning scripts. Can't call "vagrant powershell " later. This makes the information
 * available only when creating the machine.
 * <p>
 * Possible solutions:
 * <ul>
 *  <li>
 *     Store those in the yaml file that's already being created, containing the machine specs
 *     to be passed to the Vagrantfile script. We are already storing some additional info in there
 *     like imageId and hardwareId. A drawback is that the information could change
 *     so those values would become stale.
 *  </li>
 *  <li>
 *     Query the machine when we need them. Not possible with Windows; not possible if the machine
 *     is halted.
 *  </li>
 *  <li>
 *     Keep them as is (in memory only). When the process stops the information is lost. On a new
 *     start if doesn't know the hostname and IPs. Depending on the intended usage of the provider
 *     could be enough.
 *     Possibly a combination of the above would work best, depending on the OS. Perhaps coupled
 *     with a refreshing mechanism.
 *     The file could store other information like the tags and the metadata.
 *   </li>
 * </ul>
 * <p>
 * 2. Existing machines list can be reconstructed easily - just listing the files on the disk.
 * Worth mentioning that machines created by jclouds follow some conventions. Manually
 * created machines are not considered. The provider creates a yaml file describing the requirements
 * and some meta, then a generic Vagrantfile reads those and creates a machine based on them.
 * Presently the provider is only interested in machines it creates or machines created
 * from previous runs. That's a local "service" and no concurrent modifications of the machines
 * is expected. There could be parallel processes running but still each one would
 * manage its own machines. That's even strongly discouraged that since virtualbox (vboxmanage) has
 * problems when it's executed in parallel. Currently the vagrant bindings explicitly serialise
 * execs of vagrant.
 * <p>
 * 3. Machine status can reliably be inferred. The key here is that the vagrant commands are
 * synchronous. If "vagrant up" completes successfully then the status is RUNNING. If it fails
 * an exception propagates and signals an error. This makes it possible to save on expensive
 * state polling. It gets more obvious when several machines are spun up in parallel. Since
 * vagrant commands are executed sequentially a vagrant up would block other vagrant status
 * commands for quite a while.
 * Possible improvements: time out the status value, refreshing it after some period on request
 * <p>
 * The registry allows us to really streamline machine creation. All it takes is a single
 * "vagrant up". It needs around a minute to return a usable machine (obviously depends on the
 * image and is dominated by OS boot). Whereas before introducing the registry it would take
 * at least 50% more. And it gets worse with the more machines being created in parallel.
 * <p>
 * Expiring (and refreshing) machine list while running might not be wanted - would result in
 * parallel execution of vagrant command against another's process machines. Virtualbox (vboxmanage)
 * fails indeterministically when executed in parallel. Expiring the status would be a nice
 * improvement.
 */
@Singleton
public class VagrantNodeRegistry {

   private static class ConcurrentWrapperSupplier implements Supplier<Map<String, VagrantNode>> {
      private Supplier<Collection<VagrantNode>> existingMachines;

      public ConcurrentWrapperSupplier(Supplier<Collection<VagrantNode>> existingMachines) {
         this.existingMachines = existingMachines;
      }

      @Override
      public Map<String, VagrantNode> get() {
         Map<String, VagrantNode> nodes = new ConcurrentHashMap<String, VagrantNode>();
         for (VagrantNode node : existingMachines.get()) {
            nodes.put(node.id(), node);
         }
         return nodes;
      }

   }

   private final Supplier<Map<String, VagrantNode>> nodes;

   @Inject
   VagrantNodeRegistry(Supplier<Collection<VagrantNode>> existingMachines) {
      this.nodes = Suppliers.memoize(new ConcurrentWrapperSupplier(existingMachines));
   }

   public VagrantNode get(String id) {
      return nodes().get(id);
   }

   public void add(VagrantNode node) {
      nodes().put(node.id(), node);
   }

   public Collection<VagrantNode> list() {
      return nodes().values();
   }

   public void onTerminated(VagrantNode node) {
      nodes().remove(node.id());
   }

   private Map<String, VagrantNode> nodes() {
      return nodes.get();
   }

}
