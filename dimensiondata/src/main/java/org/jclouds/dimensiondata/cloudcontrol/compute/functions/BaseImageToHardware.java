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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.dimensiondata.cloudcontrol.domain.BaseImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.CpuSpeed;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BaseImageToHardware implements Function<BaseImage, Hardware> {

   private static final int GB_TO_MB_MULTIPLIER = 1024;

   @Override
   public Hardware apply(final BaseImage from) {
      HardwareBuilder builder = new HardwareBuilder().ids(from.id()).name(from.name()).hypervisor("vmx")
            .processors(buildProcessorList(from.cpu())).ram(from.memoryGb() * GB_TO_MB_MULTIPLIER);

      if (from.disks() != null) {
         builder.volumes(FluentIterable.from(from.disks()).transform(new Function<Disk, Volume>() {
            @Override
            public Volume apply(final Disk disk) {
               return new VolumeBuilder().id(disk.id()).device(String.valueOf(disk.scsiId()))
                     .size(Float.valueOf(disk.sizeGb())).type(Volume.Type.LOCAL).build();
            }
         }).toSet());
      }
      return builder.build();
   }

   private List<Processor> buildProcessorList(final CPU cpu) {
      final List<Processor> processorList = new ArrayList<Processor>();
      final double speed = CpuSpeed.fromDimensionDataSpeed(cpu.speed()).getSpeed();
      for (int count = 0; count < cpu.count(); count++) {
         processorList.add(new Processor(cpu.coresPerSocket(), speed));
      }
      return processorList;
   }
}
