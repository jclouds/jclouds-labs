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
package org.jclouds.azurecompute.compute.functions;

import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class RoleSizeToHardware implements Function<RoleSize, Hardware> {

   @Override
   public Hardware apply(RoleSize from) {
      final HardwareBuilder builder = new HardwareBuilder().
              ids(from.name().name()).
              name(from.name().name()).
              hypervisor("Hyper-V").
              processors(ImmutableList.of(new Processor(from.cores(), 2))).
              ram(from.memoryInMb());
      if (from.virtualMachineResourceDiskSizeInMb() != null) {
         builder.volume(new VolumeBuilder()
                 .size(Float.valueOf(from.virtualMachineResourceDiskSizeInMb()))
                 .type(Volume.Type.LOCAL)
                 .build());
      }
      return builder.build();
   }

}
