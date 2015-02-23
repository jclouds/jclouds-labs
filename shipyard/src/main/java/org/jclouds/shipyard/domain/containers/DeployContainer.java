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
public abstract class DeployContainer {

   public abstract String name();
   
   public abstract String containerName();
   
   public abstract double cpus();
   
   public abstract int memory();
   
   public abstract String type();
   
   @Nullable public abstract String hostName();
   
   @Nullable public abstract String domain();
   
   public abstract List<String> labels();
   
   public abstract List<String> args();
   
   public abstract Map<String, String> environment();
   
   public abstract Map<String, String> restartPolicy();
   
   public abstract List<ImagePortsInfo> bindPorts();
   
   public abstract Map<String, String> links();
   
   DeployContainer() {
   }

   @SerializedNames({ "name", "container_name", "cpus", "memory", "type", "hostname", "domain", "labels", "args", "environment", "restart_policy", "bind_ports", "links" })
   public static DeployContainer create(String name, String containerName, double cpus, 
                                       int memory, String type, String hostName, 
                                       String domain, List<String> labels, List<String> args,
                                       Map<String, String> environment, Map<String, String> restartPolicy,
                                       List<ImagePortsInfo> bindPorts, Map<String, String> links) {
      
      if (labels == null) labels = Lists.newArrayList();
      if (args == null) args = Lists.newArrayList();
      if (environment == null) environment = Maps.newHashMap();
      if (restartPolicy == null) restartPolicy = Maps.newHashMap();
      if (bindPorts == null) bindPorts = Lists.newArrayList();
      if (links == null) links = Maps.newHashMap();
         
      return new AutoValue_DeployContainer(name, containerName, cpus, 
                                          memory, "service", hostName, 
                                          domain, labels, args, 
                                          environment, restartPolicy, bindPorts, 
                                          links);         
   }
}
