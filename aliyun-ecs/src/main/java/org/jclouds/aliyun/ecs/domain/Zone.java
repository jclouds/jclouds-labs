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
public abstract class Zone {

   Zone() {}

   @SerializedNames({ "ZoneId", "LocalName", "DedicatedHostGenerations", "AvailableResourceCreation",
                          "AvailableDedicatedHostTypes", "AvailableResources", "AvailableInstanceTypes",
                          "AvailableVolumeCategories", "AvailableDiskCategories" })
   public static Zone create(String id, String localName,
                             Map<String, List<Object>> dedicatedHostGenerations, // FIXME neither doc nor example showed the type in the list
                             Map<String, List<String>> availableResourceCreation,
                             Map<String, List<String>> availableDedicatedHostTypes,
                             Map<String, List<ResourceInfo>> availableResources,
                             Map<String, List<String>> availableInstanceTypes,
                             Map<String, List<String>> availableVolumeCategories,
                             Map<String, List<String>> availableDiskCategories) {
      return new AutoValue_Zone(id, localName,
              dedicatedHostGenerations == null ? ImmutableMap.<String, List<Object>>of() : ImmutableMap.copyOf(dedicatedHostGenerations),
              availableResourceCreation == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(availableResourceCreation),
              availableDedicatedHostTypes == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(availableDedicatedHostTypes),
              availableResources == null ? ImmutableMap.<String, List<ResourceInfo>>of() : ImmutableMap.copyOf(availableResources),
              availableInstanceTypes == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(availableInstanceTypes),
              availableVolumeCategories == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(availableVolumeCategories),
              availableDiskCategories == null ? ImmutableMap.<String, List<String>>of() : ImmutableMap.copyOf(availableDiskCategories)
      );
   }

   public abstract String id();

   public abstract String localName();

   public abstract Map<String, List<Object>> dedicatedHostGenerations();

   public abstract Map<String, List<String>> availableResourceCreation();

   public abstract Map<String, List<String>> availableDedicatedHostTypes();

   public abstract Map<String, List<ResourceInfo>> availableResources();

   public abstract Map<String, List<String>> availableInstanceTypes();

   public abstract Map<String, List<String>> availableVolumeCategories();

   public abstract Map<String, List<String>> availableDiskCategories();

}
