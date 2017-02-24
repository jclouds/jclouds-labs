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
package org.apache.jclouds.oneandone.rest.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Types.ApplianceType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class ServerAppliance {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract List<String> availableDataCenters();

   @Nullable
   public abstract String osInstallationBase();

   @Nullable
   public abstract Types.OSFamliyType osFamily();

   @Nullable
   public abstract String os();

   @Nullable
   public abstract String osVersion();

   public abstract int osArchitecture();

   @Nullable
   public abstract Types.OSImageType osImageType();

   public abstract int minHddSize();

   @Nullable
   public abstract ApplianceType type();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String version();

   @Nullable
   public abstract List<String> categories();

   @Nullable
   public abstract String eulaUrl();

   @SerializedNames({"id", "name", "available_datacenters", "os_installation_base", "os_family", "os", "os_version", "os_architecture", "os_image_type",
      "min_hdd_size", "type", "state", "version", "categories", "eula_url"})
   public static ServerAppliance create(String id, String name, List<String> availableDataCenters, String osInstallationBase, Types.OSFamliyType osFamily, String os,
           String osVersion, int osArchitecture, Types.OSImageType osImageType, int minHddSize, ApplianceType type, String state, String version, List<String> categories, String eulaUrl) {
      return new AutoValue_ServerAppliance(id, name, availableDataCenters == null ? ImmutableList.<String>of() : ImmutableList.copyOf(availableDataCenters), osInstallationBase, osFamily, os, osVersion, osArchitecture, osImageType, minHddSize, type, state, version, categories == null ? ImmutableList.<String>of() : ImmutableList.copyOf(categories), eulaUrl);
   }

}
