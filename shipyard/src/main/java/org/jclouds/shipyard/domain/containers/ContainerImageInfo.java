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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.shipyard.domain.images.ImagePortsInfo;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@AutoValue
public abstract class ContainerImageInfo {
   
   @Nullable public abstract String name();
   
   public abstract Map<String, String> environment();
   
   public abstract List<String> entryPoint();
   
   @Nullable public abstract String hostName();
   
   public abstract List<ImagePortsInfo> bindPorts();
   
   public abstract List<String> volumes();
   
   public abstract Map<String, String> restartPolicy();
   
   public abstract boolean publish();
   
   @Nullable public abstract String networkMode();
      
   ContainerImageInfo() {
   }

   @SerializedNames({ "name", "environment", "entrypoint", "hostname", "bind_ports", "volumes", "restart_policy", "publish", "network_mode" })
   public static ContainerImageInfo create(String name, Map<String, String> environment, List<String> entryPoint,
                                             String hostName, List<ImagePortsInfo> bindPorts,
                                             List<String> volumes, Map<String, String> restartPolicy,
                                             boolean publish, String networkMode) {
      
      if (environment == null) environment = Maps.newHashMap();
      if (entryPoint == null) entryPoint = Lists.newArrayList();
      if (bindPorts == null) bindPorts = Lists.newArrayList();
      if (volumes == null) volumes = Lists.newArrayList();
      if (restartPolicy == null) restartPolicy = Maps.newHashMap();
         
      return new AutoValue_ContainerImageInfo(name, environment, entryPoint, 
                                             hostName, bindPorts, 
                                             volumes, restartPolicy, 
                                             publish, networkMode);         
   }
}
