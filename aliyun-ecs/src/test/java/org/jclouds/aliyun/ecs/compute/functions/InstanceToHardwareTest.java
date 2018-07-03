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
package org.jclouds.aliyun.ecs.compute.functions;

import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.compute.domain.Hardware;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "InstanceToHardwareTest")
public class InstanceToHardwareTest {

   private InstanceTypeToHardware instanceTypeToHardware;

   @BeforeMethod
   public void setUp() {
      instanceTypeToHardware = new InstanceTypeToHardware();
   }

   @Test
   public void testApplyServer() {
      final InstanceType instanceType = InstanceType.builder()
              .id("ecs.t1.small")
              .cpuCoreCount(1)
              .instanceTypeFamily("ecs.t1")
              .eniQuantity(1)
              .memorySize(1.0)
              .gpuAmount(0d)
              .localStorageCategory("")
              .gpuSpec("")
              .build();
      applyAndAssert(instanceType);
   }

   private void applyAndAssert(InstanceType instanceType) {
      final Hardware hardware = instanceTypeToHardware.apply(instanceType);
      assertEquals(instanceType.memorySize().intValue() * 1024, hardware.getRam());
      assertEquals(instanceType.id(), hardware.getId());
      assertEquals(instanceType.id(), hardware.getProviderId());
      assertEquals(instanceType.id(), hardware.getName());
      assertEquals(instanceType.cpuCoreCount().intValue(), hardware.getProcessors().size());
      assertEquals(Double.valueOf(instanceType.cpuCoreCount().intValue()), hardware.getProcessors().get(0).getCores());
   }

}
