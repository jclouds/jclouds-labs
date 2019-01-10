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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.json.SerializedNames;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class OsImage extends BaseImage {
   public static final String TYPE = "OS_IMAGE";

   OsImage() {
      type = TYPE;
   }

   public abstract String osImageKey();

   @SerializedNames({ "id", "name", "description", "cluster", "guest", "datacenterId", "cpu", "memoryGb", "nic", "disk",
         "softwareLabel", "createTime", "osImageKey" })
   public static OsImage create(String id, String name, String description, Cluster cluster, Guest guest,
         String datacenterId, CPU cpu, int memoryGb, List<ImageNic> nics, List<Disk> disks, List<String> softwareLabels,
         Date createTime, String osImageKey) {
      return builder().id(id).name(name).description(description).cluster(cluster).guest(guest)
            .datacenterId(datacenterId).osImageKey(osImageKey).createTime(createTime).cpu(cpu).memoryGb(memoryGb)
            .nics(nics).disks(disks).softwareLabels(softwareLabels).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_OsImage.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder description(String description);

      public abstract Builder cluster(Cluster cluster);

      public abstract Builder guest(Guest guest);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder osImageKey(String osImageKey);

      public abstract Builder createTime(Date createTime);

      public abstract Builder cpu(CPU cpu);

      public abstract Builder memoryGb(int memoryGb);

      public abstract Builder nics(List<ImageNic> nics);

      public abstract Builder disks(List<Disk> disks);

      public abstract Builder softwareLabels(List<String> softwareLabels);

      abstract OsImage autoBuild();

      abstract List<Disk> disks();

      abstract List<String> softwareLabels();

      abstract List<ImageNic> nics();

      public OsImage build() {
         disks(disks() != null ? ImmutableList.copyOf(disks()) : ImmutableList.<Disk>of());
         softwareLabels(softwareLabels() != null ? ImmutableList.copyOf(softwareLabels()) : ImmutableList.<String>of());
         nics(nics() != null ? ImmutableList.copyOf(nics()) : ImmutableList.<ImageNic>of());
         return autoBuild();
      }
   }
}
