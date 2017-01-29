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
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.jclouds.date.TimeStamp;
import org.jclouds.vagrant.domain.VagrantNode;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class VagrantNodeRegistry {
   private static final long TERMINATED_NODES_EXPIRY_MS = TimeUnit.MINUTES.toMillis(5);
   private static final long VACUUM_PERIOD_MS = TimeUnit.SECONDS.toMillis(15);

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

   private static class TerminatedNode implements Delayed {
      Supplier<Long> timeSupplier;
      long expiryTime;
      VagrantNode node;

      TerminatedNode(VagrantNode node, Supplier<Long> timeSupplier) {
         this.expiryTime = System.currentTimeMillis() + TERMINATED_NODES_EXPIRY_MS;
         this.node = node;
         this.timeSupplier = timeSupplier;
      }
      @Override
      public int compareTo(Delayed o) {
         if (this == o) {
            return 0;
         } else if (o instanceof TerminatedNode) {
            TerminatedNode other = (TerminatedNode)o;
            if (expiryTime < other.expiryTime) {
               return -1;
            } else if (expiryTime > other.expiryTime) {
               return 1;
            } else {
               return 0;
            }
         } else {
            long diff = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
            if (diff < 0) {
               return -1;
            } else if (diff > 0) {
               return 1;
            } else {
               return 0;
            }
         }
      }
      @Override
      public long getDelay(TimeUnit unit) {
         return unit.convert(expiryTime - timeSupplier.get(), TimeUnit.MILLISECONDS);
      }
   }

   private final DelayQueue<TerminatedNode> terminatedNodes = new DelayQueue<TerminatedNode>();
   private final Supplier<Map<String, VagrantNode>> nodes;

   private volatile long lastVacuumMs;
   private final Supplier<Long> timeSupplier;

   @Inject
   VagrantNodeRegistry(@TimeStamp Supplier<Long> timeSupplier, Supplier<Collection<VagrantNode>> existingMachines) {
      this.timeSupplier = timeSupplier;
      this.nodes = Suppliers.memoize(new ConcurrentWrapperSupplier(existingMachines));
   }

   public VagrantNode get(String id) {
      vacuum();
      return nodes.get().get(id);
   }

   protected void vacuum() {
      // No need to lock on lastVacuumMs - not critical if we miss/do double vacuuming.
      if (timeSupplier.get() - lastVacuumMs > VACUUM_PERIOD_MS) {
         TerminatedNode terminated;
         while ((terminated = terminatedNodes.poll()) != null) {
            nodes.get().remove(terminated.node.id());
         }
         lastVacuumMs = timeSupplier.get();
      }
   }

   public void add(VagrantNode node) {
      nodes.get().put(node.id(), node);
   }

   public Collection<VagrantNode> list() {
      return nodes.get().values();
   }

   public void onTerminated(VagrantNode node) {
      terminatedNodes.add(new TerminatedNode(node, timeSupplier));
   }

}
