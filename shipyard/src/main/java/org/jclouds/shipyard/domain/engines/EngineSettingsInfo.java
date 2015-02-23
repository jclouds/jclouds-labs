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
package org.jclouds.shipyard.domain.engines;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EngineSettingsInfo {

   public abstract String id();
      
   public abstract String addr();
   
   public abstract double cpus();
   
   public abstract double memory();
   
   public abstract List<String> labels();

   EngineSettingsInfo() {
   }

   @SerializedNames({ "id", "addr", "cpus", "memory", "labels" })
   public static EngineSettingsInfo create(String id, String addr, 
                                          double cpus, double memory, 
                                          List<String> labels) {
      
      checkNotNull(labels, "labels must be non-null");
      checkState(labels.size() > 0, "labels must have at least 1 entry");
      
      return new AutoValue_EngineSettingsInfo(id, addr, cpus, memory, labels);
   }
}
