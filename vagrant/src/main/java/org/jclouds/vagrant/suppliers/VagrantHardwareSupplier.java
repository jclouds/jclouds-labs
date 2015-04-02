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
package org.jclouds.vagrant.suppliers;

import java.util.Map;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public class VagrantHardwareSupplier implements Supplier<Map<String, Hardware>> {
   private static final Map<String, Hardware> HARDWARE = ImmutableMap.of(
         "micro", hardware("micro", 512, 1),
         "small", hardware("small", 1024, 1),
         "medium", hardware("medium", 2048, 2),
         "large", hardware("large", 4096, 2),
         "xlarge", hardware("xlarge", 8192, 4));

   private static Hardware hardware(String name, int ram, int cores) {
      return new HardwareBuilder()
            .ids(name)
            .hypervisor("VirtualBox")
            .name(name)
            .processor(new Processor(cores, 1))
            .ram(ram)
            .volume(new VolumeBuilder().bootDevice(true).durable(true).type(Type.LOCAL).build())
            .build();
   }

   @Override
   public Map<String, Hardware> get() {
      return HARDWARE;
   }

}
