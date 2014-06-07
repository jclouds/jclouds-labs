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
package org.jclouds.digitalocean.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.digitalocean.domain.Size;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms an {@link Size} to the jclouds portable model.
 */
@Singleton
public class SizeToHardware implements Function<Size, Hardware> {

   @Override
   public Hardware apply(Size input) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.id(input.getSlug());
      builder.providerId(String.valueOf(input.getId()));
      builder.name(input.getName());
      builder.ram(input.getMemory());
      // DigitalOcean does not provide the processor speed. We configure it to
      // make the bigger template the faster.
      builder.processor(new Processor(input.getCpu(), input.getCpu()));

      builder.volume(new VolumeBuilder() 
            .size(Float.valueOf(input.getDisk())) 
            .type(Type.LOCAL) 
            .build());

      ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
      metadata.put("costPerHour", input.getCostPerHour());
      metadata.put("costPerMonth", input.getCostPerMonth());
      builder.userMetadata(metadata.build());

      return builder.build();
   }
}
