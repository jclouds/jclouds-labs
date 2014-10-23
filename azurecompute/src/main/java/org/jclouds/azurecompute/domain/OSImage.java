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
package org.jclouds.azurecompute.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * OS image from the image repository
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
public final class OSImage {
   public enum Type {
      LINUX, WINDOWS;
   }

   public String name() {
      return name;
   }

   /** The geo-locations of the image, if the image is not associated with an affinity group. */
   public List<String> locations() {
      return locations;
   }

   /** The affinity group with which this image is associated, if any. */
   @Nullable public String affinityGroup() {
      return affinityGroup;
   }

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   public String label() {
      return label;
   }

   @Nullable public String description() {
      return description;
   }

   /**
    * The repository classification of image. All user images have the category "User", but
    * categories for other images could be, for example "Canonical"
    */
   @Nullable public String category() {
      return category;
   }

   /** The operating system type of the OS image. */
   public Type os() {
      return os;
   }

   /**
    * The locations of the blob in the blob store in which the media for the image is located. The
    * blob locations belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   @Nullable public URI mediaLink() {
      return mediaLink;
   }

   public int logicalSizeInGB() {
      return logicalSizeInGB;
   }

   /** The eulas for the image, if available. */
   // Not URI as some providers put non-uri data in, such as riverbed.
   public List<String> eula() {
      return eula;
   }

   public static OSImage create(String name, List<String> locations, String affinityGroup, String label,
         String description, String category, Type os, URI mediaLink, int logicalSizeInGB, List<String> eula) {
      return new OSImage(name, locations, affinityGroup, label, description, category, os, mediaLink, logicalSizeInGB,
            eula);
   }

   // TODO: Remove from here down with @AutoValue.
   private OSImage(String name, List<String> locations, String affinityGroup, String label, String description,
         String category, Type os, URI mediaLink, int logicalSizeInGB, List<String> eula) {
      this.name = checkNotNull(name, "name");
      this.locations = locations;
      this.affinityGroup = affinityGroup;
      this.label = checkNotNull(label, "label");
      this.description = description;
      this.category = category;
      this.os = checkNotNull(os, "os");
      this.mediaLink = mediaLink;
      this.logicalSizeInGB = logicalSizeInGB;
      this.eula = checkNotNull(eula, "eula");
   }

   private final String name;
   private final List<String>  locations;
   private final String affinityGroup;
   private final String label;
   private final String category;
   private final String description;
   private final Type os;
   private final URI mediaLink;
   private final int logicalSizeInGB;
   private final List<String> eula;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof OSImage) {
         OSImage that = OSImage.class.cast(object);
         return equal(name, that.name)
               && equal(locations, that.locations)
               && equal(affinityGroup, that.affinityGroup)
               && equal(label, that.label)
               && equal(description, that.description)
               && equal(category, that.category)
               && equal(os, that.os)
               && equal(mediaLink, that.mediaLink)
               && equal(logicalSizeInGB, that.logicalSizeInGB)
               && equal(eula, that.eula);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, locations, affinityGroup, label, description, category, os, mediaLink,
            logicalSizeInGB, eula);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("name", name)
            .add("locations", locations)
            .add("affinityGroup", affinityGroup)
            .add("label", label)
            .add("description", description)
            .add("category", category)
            .add("os", os)
            .add("mediaLink", mediaLink)
            .add("logicalSizeInGB", logicalSizeInGB)
            .add("eula", eula).toString();
   }
}
