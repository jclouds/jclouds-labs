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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;

public class HardwareFlavourToHardware implements Function<HardwareFlavour, Hardware> {

   @Override
   public Hardware apply(HardwareFlavour from) {
      double minRamSize = (int) from.hardware().ram();
      if (from.hardware().ram() < 1) {
         minRamSize = 0.5;
      }
      List<Volume> volumes = new ArrayList<Volume>();
      for (HardwareFlavour.Hardware.Hdd hdd : from.hardware().hdds()) {
         Volume vol = new VolumeBuilder()
                 .bootDevice(hdd.isMain())
                 .device("hdd")
                 .type(Volume.Type.LOCAL)
                 .size((float) hdd.size())
                 .build();
         volumes.add(vol);
      }

      List<Processor> processors = new ArrayList<Processor>();
      for (int i = 0; i < from.hardware().coresPerProcessor(); i++) {
         Processor proc = new Processor(from.hardware().vcore(), 1d);
         processors.add(proc);
      }
      final HardwareBuilder builder;
      builder = new HardwareBuilder()
              .ids(from.id())
              .name(from.name())
              .ram((int) (minRamSize * 1024))
              .volumes(volumes)
              .processors(ImmutableList.copyOf(processors));
      return builder.build();
   }

}
