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
package org.jclouds.aliyun.ecs.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class ResourceInfo {

   ResourceInfo() {
   }

   @SerializedNames(
         { "IoOptimized", "SystemDiskCategories", "InstanceTypes", "InstanceTypeFamilies", "DataDiskCategories",
               "InstanceGenerations", "NetworkTypes" })
   public static ResourceInfo create(boolean ioOptimized, Map<String, List<String>> systemDiskCategories,
                                     Map<String, List<String>> instanceTypes, Map<String, List<String>> instanceTypeFamilies,
                                     Map<String, List<String>> dataDiskCategories, Map<String, List<String>> instanceGenerations,
                                     Map<String, List<String>> networkTypes) {
      return new AutoValue_ResourceInfo(ioOptimized, systemDiskCategories == null ?
            ImmutableMap.<String, List<String>>of() :
            ImmutableMap.copyOf(systemDiskCategories),
            instanceTypes == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(instanceTypes),
            instanceTypeFamilies == null ?
                  ImmutableMap.<String, List<String>>of() :
                  ImmutableMap.copyOf(instanceTypeFamilies), dataDiskCategories == null ?
            ImmutableMap.<String, List<String>>of() :
            ImmutableMap.copyOf(dataDiskCategories), instanceGenerations == null ?
            ImmutableMap.<String, List<String>>of() :
            ImmutableMap.copyOf(instanceGenerations),
            networkTypes == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(networkTypes));
   }

   public abstract boolean ioOptimized();

   public abstract Map<String, List<String>> systemDiskCategories();

   public abstract Map<String, List<String>> instanceTypes();

   public abstract Map<String, List<String>> instanceTypeFamilies();

   public abstract Map<String, List<String>> dataDiskCategories();

   public abstract Map<String, List<String>> instanceGenerations();

   public abstract Map<String, List<String>> networkTypes();
}
