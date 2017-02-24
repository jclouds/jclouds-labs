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
package org.apache.jclouds.oneandone.rest.compute.function;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Hardware;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.util.AutomaticHardwareIdSpec;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CustomHardwareToHardwareTest", singleThreaded = true)
public class CustomHardwareToHardwareTest {

   @Test
   public void testCustomHardwareToHardwareTest() {
      List<Hdd> hdds = new ArrayList<Hdd>();
      Hdd hdd = Hdd.create("hdd", 20, true);
      hdds.add(hdd);

      Hardware hardware = Hardware.create("", 2, 1, 2, hdds);

      int MinRamSize = (int) hardware.ram();
      if (hardware.ram() < 1) {
         MinRamSize = 1;
      }
      double size = 0d;
      List<Volume> volumes = new ArrayList<Volume>();
      for (Hdd _hdd : hardware.hdds()) {
         size += hdd.size();
         Volume vol = new VolumeBuilder()
                 .bootDevice(_hdd.isMain())
                 .device("hdd")
                 .type(Volume.Type.LOCAL)
                 .size((float) _hdd.size())
                 .build();
         volumes.add(vol);
      }

      List<Processor> processors = new ArrayList<Processor>();
      for (int i = 0; i < hardware.vcore(); i++) {
         Processor proc = new Processor(hardware.coresPerProcessor(), 1);
         processors.add(proc);
      }
      AutomaticHardwareIdSpec id = AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(hardware.vcore(), MinRamSize, Optional.of((float) size));
      org.jclouds.compute.domain.Hardware actual = new HardwareBuilder()
              .ids(id.toString())
              .ram(MinRamSize)
              .volumes(volumes)
              .processors(ImmutableList.copyOf(processors)).build();

      org.jclouds.compute.domain.Hardware expected = new HardwareBuilder()
              .ids(id.toString())
              .ram(2)
              .volumes(volumes)
              .processors(ImmutableList.copyOf(processors)).build();

      assertEquals(actual, expected);
   }
}
