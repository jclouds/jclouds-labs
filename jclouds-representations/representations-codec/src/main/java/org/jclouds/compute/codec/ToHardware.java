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
package org.jclouds.compute.codec;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.jclouds.compute.representations.Hardware;
import org.jclouds.compute.representations.Processor;
import org.jclouds.compute.representations.Volume;
import org.jclouds.javax.annotation.Nullable;

import static com.google.common.collect.Iterables.transform;

public enum ToHardware implements Function<org.jclouds.compute.domain.Hardware, Hardware> {

   INSTANCE;

   @Override
   public Hardware apply(@Nullable org.jclouds.compute.domain.Hardware input) {
      if (input == null) {
         return null;
      }
      return Hardware.builder().id(input.getId()).name(input.getName()).hypervisor(input.getHypervisor())
                     .ram(input.getRam())
                      //Transformation is lazy and doesn't serialize well, so let's wrap the processor list
                     .processors(ImmutableList.<Processor>builder().addAll(transform(input.getProcessors(), ToProcessor.INSTANCE)).build())

                      //Transformation is lazy and doesn't serialize well, so let's wrap the volume list
                     .volumes(ImmutableList.<Volume>builder().addAll(transform(input.getVolumes(), ToVolume.INSTANCE)).build())
                     .build();
   }
}
