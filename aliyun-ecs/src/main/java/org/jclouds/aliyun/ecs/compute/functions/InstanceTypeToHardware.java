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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;

import javax.inject.Singleton;

@Singleton
public class InstanceTypeToHardware implements Function<InstanceType, Hardware> {

   private static final int GB_TO_MB_MULTIPLIER = 1024;

   @Override
   public Hardware apply(InstanceType input) {
      HardwareBuilder builder = new HardwareBuilder()
              .ids(input.id())
              .name(input.id())
              .hypervisor("none")
              .processors(getProcessors(input.cpuCoreCount()))
              .ram(input.memorySize().intValue() * GB_TO_MB_MULTIPLIER);
      return builder.build();
   }

   private Iterable<Processor> getProcessors(Integer cpuCoreCount) {
      // No cpu speed from API, so assume more cores == faster
      return ImmutableList.of(new Processor(cpuCoreCount, cpuCoreCount));
   }

}
