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
