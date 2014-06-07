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
package org.jclouds.abiquo.domain.cloud;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.domain.task.VirtualMachineTask;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live integration tests for the {@link VirtualMachine} storage operations.
 */
@Test(groups = "api", testName = "VirtualMachineStorageLiveApiTest")
public class VirtualMachineStorageLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private Volume volume;

   private HardDisk hardDisk;

   public void testAttachVolumes() {
      volume = createVolume();

      // Since the virtual machine is not deployed, this should not generate a
      // task
      VirtualMachineTask task = env.virtualMachine.attachVolumes(volume);
      assertNull(task);

      Iterable<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertEquals(size(attached), 1);
      assertEquals(get(attached, 0).getId(), volume.getId());
   }

   @Test(dependsOnMethods = "testAttachVolumes")
   public void detachVolume() {
      env.virtualMachine.detachVolumes(volume);
      Iterable<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertTrue(isEmpty(attached));
   }

   @Test(dependsOnMethods = "detachVolume")
   public void detachAllVolumes() {
      // Since the virtual machine is not deployed, this should not generate a
      // task
      VirtualMachineTask task = env.virtualMachine.attachVolumes(volume);
      assertNull(task);

      env.virtualMachine.detachAllVolumes();
      Iterable<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertTrue(isEmpty(attached));

      deleteVolume(volume);
   }

   public void testAttachHardDisks() {
      hardDisk = createHardDisk();

      // Since the virtual machine is not deployed, this should not generate a
      // task
      VirtualMachineTask task = env.virtualMachine.attachHardDisks(hardDisk);
      assertNull(task);

      Iterable<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertEquals(size(attached), 1);
      assertEquals(get(attached, 0).getId(), hardDisk.getId());
   }

   @Test(dependsOnMethods = "testAttachHardDisks")
   public void detachHardDisk() {
      env.virtualMachine.detachHardDisks(hardDisk);
      Iterable<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertTrue(isEmpty(attached));
   }

   @Test(dependsOnMethods = "detachHardDisk")
   public void detachAllHardDisks() {
      // Since the virtual machine is not deployed, this should not generate a
      // task
      VirtualMachineTask task = env.virtualMachine.attachHardDisks(hardDisk);
      assertNull(task);

      env.virtualMachine.detachAllHardDisks();
      Iterable<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertTrue(isEmpty(attached));

      deleteHardDisk(hardDisk);
   }

   private Volume createVolume() {
      Tier tier = find(env.virtualDatacenter.listStorageTiers(), new Predicate<Tier>() {
         @Override
         public boolean apply(Tier input) {
            return input.getName().equals(env.tier.getName());
         }
      });

      Volume volume = Volume.builder(env.context.getApiContext(), env.virtualDatacenter, tier)
            .name(PREFIX + "Hawaian volume").sizeInMb(32).build();
      volume.save();

      assertNotNull(volume.getId());
      assertNotNull(env.virtualDatacenter.getVolume(volume.getId()));

      return volume;
   }

   private void deleteVolume(final Volume volume) {
      Integer id = volume.getId();
      volume.delete();
      assertNull(env.virtualDatacenter.getVolume(id));
   }

   private HardDisk createHardDisk() {
      HardDisk hardDisk = HardDisk.builder(env.context.getApiContext(), env.virtualDatacenter).sizeInMb(64L).build();
      hardDisk.save();

      assertNotNull(hardDisk.getId());
      assertNotNull(hardDisk.getSequence());

      return hardDisk;
   }

   private void deleteHardDisk(final HardDisk hardDisk) {
      Integer id = hardDisk.getId();
      hardDisk.delete();
      assertNull(env.virtualDatacenter.getHardDisk(id));
   }
}
