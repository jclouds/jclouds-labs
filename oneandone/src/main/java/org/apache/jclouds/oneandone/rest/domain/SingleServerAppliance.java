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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class SingleServerAppliance {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract List<AvailableDataCenters> availableDataCenters();

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
   public abstract Types.ApplianceType type();

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
   public static SingleServerAppliance create(String id, String name, List<AvailableDataCenters> availableDataCenters, String osInstallationBase, Types.OSFamliyType osFamily, String os,
           String osVersion, int osArchitecture, Types.OSImageType osImageType, int minHddSize, Types.ApplianceType type, String state, String version, List<String> categories, String eulaUrl) {
      return builder()
              .id(id)
              .name(name)
              .availableDataCenters(availableDataCenters == null ? ImmutableList.<AvailableDataCenters>of() : ImmutableList.copyOf(availableDataCenters))
              .osInstallationBase(osInstallationBase)
              .osFamily(osFamily)
              .os(os)
              .osVersion(osVersion)
              .osArchitecture(osArchitecture)
              .osImageType(osImageType)
              .minHddSize(minHddSize)
              .type(type)
              .state(state)
              .version(version)
              .categories(categories == null ? ImmutableList.<String>of() : ImmutableList.copyOf(categories))
              .eulaUrl(eulaUrl)
              .build();
   }

   public static Builder builder() {
      return new AutoValue_SingleServerAppliance.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder availableDataCenters(List<AvailableDataCenters> availableDataCenters);

      public abstract Builder osInstallationBase(String osInstallationBase);

      public abstract Builder osFamily(Types.OSFamliyType osFamily);

      public abstract Builder os(String os);

      public abstract Builder osVersion(String osVersion);

      public abstract Builder osArchitecture(int osArchitecture);

      public abstract Builder osImageType(Types.OSImageType osImageType);

      public abstract Builder minHddSize(int minHddSize);

      public abstract Builder type(Types.ApplianceType type);

      public abstract Builder state(String state);

      public abstract Builder version(String version);

      public abstract Builder categories(List<String> categories);

      public abstract Builder eulaUrl(String eulaUrl);

      abstract List<String> categories();

      abstract List<AvailableDataCenters> availableDataCenters();

      abstract SingleServerAppliance autoBuild();

      public SingleServerAppliance build() {
         availableDataCenters(availableDataCenters() != null ? ImmutableList.copyOf(availableDataCenters()) : null);
         categories(categories() != null ? ImmutableList.copyOf(categories()) : null);
         return autoBuild();
      }
   }

   @AutoValue
   public abstract static class AvailableDataCenters {

      public abstract String id();

      public abstract String name();

      @SerializedNames({"id", "name"})
      public static AvailableDataCenters create(String id, String name) {
         return new AutoValue_SingleServerAppliance_AvailableDataCenters(id, name);
      }
   }
}
