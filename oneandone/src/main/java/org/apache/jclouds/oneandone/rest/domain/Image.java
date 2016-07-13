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
import org.apache.jclouds.oneandone.rest.domain.Types.ImageFrequency;
import org.apache.jclouds.oneandone.rest.domain.Types.ImageType;
import org.apache.jclouds.oneandone.rest.domain.Types.OSFamliyType;
import org.apache.jclouds.oneandone.rest.domain.Types.OSType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Image {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract OSFamliyType osFamily();

   @Nullable
   public abstract OSType os();

   @Nullable
   public abstract String osVersion();

   public abstract List<String> availableSites();

   public abstract int architecture();

   @Nullable
   public abstract String osImageType();

   @Nullable
   public abstract ImageType type();

   public abstract int minHddSize();

   public abstract List<Licenses> licenses();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract List<Hdd> hdds();

   @Nullable
   public abstract String serverId();

   @Nullable
   public abstract ImageFrequency frequency();

   public abstract int numImages();

   @Nullable

   public abstract String creationDate();

   @SerializedNames({"id", "name", "os_family", "os", "os_version", "availableSites", "architecture", "os_image_type", "type", "min_hdd_size", "licenses", "state", "description", "hdds", "server_id", "frequency", "numImages", "creation_date"})
   public static Image create(String id, String name, OSFamliyType osFamily, OSType os, String osVersion, List<String> availableSites, int architecture, String osImageType, ImageType type, int minHddSize, List<Licenses> licenses, String state, String description, List<Hdd> hdds, String serverId, ImageFrequency frequency, int numImages, String creationDate) {
      return new AutoValue_Image(id, name, osFamily, os, osVersion, availableSites == null ? ImmutableList.<String>of() : availableSites,
              architecture, osImageType, type, minHddSize, licenses == null ? ImmutableList.<Licenses>of() : licenses, state,
              description, hdds == null ? ImmutableList.<Hdd>of() : hdds, serverId, frequency, numImages, creationDate);
   }

   @AutoValue
   public abstract static class CreateImage {

      public abstract String serverId();

      public abstract String name();

      public abstract ImageFrequency frequency();

      @Nullable
      public abstract String description();

      public abstract int numImages();

      @Nullable
      public abstract String datacenterId();

      @SerializedNames({"server_id", "name", "frequency", "description", "num_images", "datacenter_id"})
      public static CreateImage create(final String serverId, final String name, final ImageFrequency frequency, final String description, final int numImages,
              final String dataCenterId) {
         return builder()
                 .name(name)
                 .serverId(serverId)
                 .description(description)
                 .datacenterId(dataCenterId)
                 .frequency(frequency)
                 .numImages(numImages)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Image_CreateImage.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder serverId(String serverId);

         public abstract Builder frequency(ImageFrequency description);

         public abstract Builder description(String description);

         public abstract Builder numImages(int numImages);

         public abstract Builder datacenterId(String datacenterId);

         public abstract CreateImage build();
      }
   }

   @AutoValue
   public abstract static class UpdateImage {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract ImageFrequency frequency();

      @SerializedNames({"name", "description", "frequency"})
      public static UpdateImage create(final String name, final String description, final ImageFrequency frequency) {
         return new AutoValue_Image_UpdateImage(name, description, frequency);
      }
   }
}
