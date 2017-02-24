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
import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;

public class HddToVolume implements Function<Hdd, Volume> {
   
   @Override
   public Volume apply(Hdd volume) {
      checkNotNull(volume, "Null storage");
      
      return new VolumeBuilder()
              .id(volume.id())
              .size((float) volume.size())
              .durable(true)
              .bootDevice(volume.isMain())
              .type(Volume.Type.LOCAL)
              .build();
      
   }
}
