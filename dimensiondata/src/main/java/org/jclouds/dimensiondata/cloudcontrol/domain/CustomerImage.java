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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class CustomerImage extends BaseImage {

   public static final String TYPE = "CUSTOMER_IMAGE";

   CustomerImage() {
      type = TYPE;
   }

   public static Builder builder() {
      return new AutoValue_CustomerImage.Builder();
   }

   @SerializedNames({ "id", "name", "description", "cluster", "guest", "datacenterId", "cpu", "memoryGb", "nic", "disk",
         "softwareLabel", "createTime", "state", "tag", "progress", "virtualHardware", "source" })
   public static CustomerImage create(String id, String name, String description, Cluster cluster, Guest guest,
         String datacenterId, CPU cpu, int memoryGb, List<ImageNic> nics, List<Disk> disk, List<String> softwareLabel,
         Date createTime, State state, List<TagWithIdAndName> tags, Progress progress, VirtualHardware virtualHardware,
         Source source) {
      return builder().id(id).datacenterId(datacenterId).name(name).description(description).cluster(cluster)
            .guest(guest).cpu(cpu).memoryGb(memoryGb).nics(nics).disks(disk).softwareLabels(softwareLabel)
            .createTime(createTime).state(state).tags(tags).progress(progress).virtualHardware(virtualHardware)
            .source(source).build();
   }

   public abstract State state();

   @Nullable
   public abstract List<TagWithIdAndName> tags();

   @Nullable
   public abstract Progress progress();

   @Nullable
   public abstract VirtualHardware virtualHardware();

   public abstract Source source();

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder name(String name);

      public abstract Builder description(String description);

      public abstract Builder cluster(Cluster cluster);

      public abstract Builder guest(Guest guest);

      public abstract Builder cpu(CPU cpu);

      public abstract Builder memoryGb(int memoryGb);

      public abstract Builder nics(List<ImageNic> nics);

      public abstract Builder disks(List<Disk> disks);

      public abstract Builder tags(List<TagWithIdAndName> tags);

      public abstract Builder softwareLabels(List<String> softwareLabels);

      public abstract Builder virtualHardware(VirtualHardware virtualHardware);

      public abstract Builder source(Source source);

      public abstract Builder createTime(Date createTime);

      public abstract Builder state(State state);

      public abstract Builder progress(Progress progress);

      abstract CustomerImage autoBuild();

      abstract List<Disk> disks();

      abstract List<String> softwareLabels();

      abstract List<ImageNic> nics();

      abstract List<TagWithIdAndName> tags();

      public CustomerImage build() {
         disks(disks() != null ? ImmutableList.copyOf(disks()) : ImmutableList.<Disk>of());
         softwareLabels(softwareLabels() != null ? ImmutableList.copyOf(softwareLabels()) : ImmutableList.<String>of());
         nics(nics() != null ? ImmutableList.copyOf(nics()) : ImmutableList.<ImageNic>of());
         tags(tags() != null ? ImmutableList.copyOf(tags()) : null);
         return autoBuild();
      }
   }

   @AutoValue
   public abstract static class TagWithIdAndName {

      TagWithIdAndName() {
      }

      public abstract String tagKeyName();

      @Nullable
      public abstract String value();

      public abstract String tagKeyId();

      public abstract Builder toBuilder();

      @SerializedNames({ "tagKeyName", "value", "tagKeyId" })
      public static TagWithIdAndName create(String tagKeyName, String value, String tagKeyId) {
         return builder().tagKeyName(tagKeyName).value(value).tagKeyId(tagKeyId).build();
      }

      public static Builder builder() {
         return new AutoValue_CustomerImage_TagWithIdAndName.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder tagKeyName(String tagKeyName);

         public abstract Builder value(String value);

         public abstract Builder tagKeyId(String tagKeyId);

         public abstract TagWithIdAndName build();
      }
   }

   @AutoValue
   public abstract static class Source {

      public enum Type {
         BASE, CLONE, IMPORT, COPY
      }

      public abstract List<Artifact> artifacts();

      public abstract Type type();

      @SerializedNames({ "artifact", "type" })
      public static Source create(List<Artifact> artifact, Type type) {
         return builder().artifacts(artifact).type(type).build();
      }

      public abstract Builder toBuilder();

      public static Builder builder() {
         return new AutoValue_CustomerImage_Source.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder type(Type type);

         public abstract Builder artifacts(List<Artifact> artifact);

         abstract List<Artifact> artifacts();

         abstract Source autoBuild();

         public Source build() {
            artifacts(artifacts() != null ? ImmutableList.copyOf(artifacts()) : ImmutableList.<Artifact>of());
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class Artifact {

      public enum Type {
         MF, OVF, VMDK, REFERENCE, SERVER_ID, LEGACY_IMPORT_ID, IMAGE_ID, OVF_PACKAGE_PREFIX
      }

      public abstract Type type();

      public abstract String value();

      @Nullable
      public abstract Date date();

      @SerializedNames({ "type", "value", "date" })
      public static Artifact create(Type type, String value, Date date) {
         return builder().type(type).value(value).date(date).build();
      }

      public abstract Builder toBuilder();

      public static Builder builder() {
         return new AutoValue_CustomerImage_Artifact.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder type(Type type);

         public abstract Builder value(String value);

         public abstract Builder date(Date date);

         public abstract Artifact build();
      }
   }
}

