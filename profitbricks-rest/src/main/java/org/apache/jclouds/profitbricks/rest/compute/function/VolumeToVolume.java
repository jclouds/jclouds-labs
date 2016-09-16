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
package org.apache.jclouds.profitbricks.rest.compute.function;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;

public class VolumeToVolume implements Function<org.apache.jclouds.profitbricks.rest.domain.Volume, Volume> {

   @Override
   public Volume apply(org.apache.jclouds.profitbricks.rest.domain.Volume volume) {
      checkNotNull(volume, "Null storage");

      String device = "";
      if (volume.properties().deviceNumber() != null) {
         device = volume.properties().deviceNumber().toString();
      }

      return new VolumeBuilder()
              .id(volume.id())
              .size((float) volume.properties().size())
              .device(device)
              .durable(true)
              .type(Volume.Type.LOCAL)
              .build();

   }
}
