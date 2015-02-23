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
package org.jclouds.shipyard.domain.containers;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.shipyard.domain.engines.EngineSettingsInfo;
import org.jclouds.shipyard.domain.images.ImagePortsInfo;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;

@AutoValue
public abstract class ContainerInfo {

   public abstract String id();
   
   @Nullable public abstract String name();
   
   public abstract ContainerImageInfo image();
   
   public abstract EngineSettingsInfo engine();
   
   public abstract String state();
   
   public abstract List<ImagePortsInfo> ports();
   
   ContainerInfo() {
   }

   @SerializedNames({ "id", "name", "image", "engine", "state", "ports" })
   public static ContainerInfo create(String id, String name, 
                                       ContainerImageInfo image,
                                       EngineSettingsInfo engine,
                                       String state, List<ImagePortsInfo> ports) {
      
      if (state == null) state = "unknown";
      if (ports == null) ports = Lists.newArrayList();
      
      return new AutoValue_ContainerInfo(id, name, image, engine, state, ports);
   }
}
