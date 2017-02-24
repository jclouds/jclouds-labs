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

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour.Hardware.Hdd;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HardwareFlavourToHardwareTest", singleThreaded = true)
public class HardwareFlavourToHardwareTest {

   private HardwareFlavourToHardware fnHardware;

   @BeforeTest
   public void setup() {
      this.fnHardware = new HardwareFlavourToHardware();
   }

   @Test
   public void testHardwareFlavourToHardware() {
      List<Hdd> sourceHdds = new ArrayList<Hdd>();
      Hdd sourceHdd = Hdd.create("GB", 40, true);
      sourceHdds.add(sourceHdd);

      org.apache.jclouds.oneandone.rest.domain.HardwareFlavour.Hardware hrdware = org.apache.jclouds.oneandone.rest.domain.HardwareFlavour.Hardware.create(null, 1, 1, 1, sourceHdds);
      org.apache.jclouds.oneandone.rest.domain.HardwareFlavour hardware = HardwareFlavour.create("65929629F35BBFBA63022008F773F3EB", "name", hrdware);

      Hardware actual = fnHardware.apply(hardware);

      int MinRamSize = (int) hardware.hardware().ram();
      if (hardware.hardware().ram() < 1) {
         MinRamSize = 1;
      }
      List<Volume> volumes = new ArrayList<Volume>();
      for (HardwareFlavour.Hardware.Hdd hdd : hardware.hardware().hdds()) {
         Volume vol = new VolumeBuilder()
                 .bootDevice(hdd.isMain())
                 .device("hdd")
                 .type(Volume.Type.LOCAL)
                 .size((float) hdd.size())
                 .build();
         volumes.add(vol);
      }

      List<Processor> processors = new ArrayList<Processor>();
      for (int i = 0; i < hardware.hardware().vcore(); i++) {
         Processor proc = new Processor(hardware.hardware().coresPerProcessor(), 1);
         processors.add(proc);
      }
      Hardware expected = new HardwareBuilder()
              .ids(hardware.id())
              .name(hardware.name())
              .ram(MinRamSize)
              .volumes(volumes)
              .processors(ImmutableList.copyOf(processors)).build();

      assertEquals(actual, expected);
   }
}
