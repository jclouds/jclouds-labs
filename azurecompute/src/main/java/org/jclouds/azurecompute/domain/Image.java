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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * OS images from the image repository
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
public class Image {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOSImage(this);
   }

   public static class Builder {
      private OSType os;
      private String name;
      private Optional<Integer> logicalSizeInGB = Optional.absent();
      private Optional<String> description = Optional.absent();
      private Optional<String> category = Optional.absent();
      private Optional<String> location = Optional.absent();
      private Optional<String> affinityGroup = Optional.absent();
      private Optional<URI> mediaLink = Optional.absent();
      private ImmutableList.Builder<String> eula = ImmutableList.builder();
      private String label;

      /**
       * @see Image#getOS()
       */
      public Builder os(OSType os) {
         this.os = os;
         return this;
      }

      /**
       * @see Image#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Image#getDescription()
       */
      public Builder description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      /**
       * @see Image#getLogicalSizeInGB()
       */
      public Builder logicalSizeInGB(Integer logicalSizeInGB) {
         this.logicalSizeInGB = Optional.fromNullable(logicalSizeInGB);
         return this;
      }

      /**
       * @see Image#getCategory()
       */
      public Builder category(String category) {
         this.category = Optional.fromNullable(category);
         return this;
      }

      /**
       * @see Image#getLocation()
       */
      public Builder location(String location) {
         this.location = Optional.fromNullable(location);
         return this;
      }

      /**
       * @see Image#getAffinityGroup()
       */
      public Builder affinityGroup(String affinityGroup) {
         this.affinityGroup = Optional.fromNullable(affinityGroup);
         return this;
      }

      /**
       * @see Image#getMediaLink()
       */
      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = Optional.fromNullable(mediaLink);
         return this;
      }

      /**
       * @see Image#getEula()
       */
      public Builder eula(Iterable<String> eula) {
         this.eula.addAll(eula);
         return this;
      }

      /**
       * @see Image#getEula()
       */
      public Builder eula(String eula) {
         this.eula.add(eula);
         return this;
      }

      /**
       * @see Image#getLabel()
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      public Image build() {
         return new Image(os, name, logicalSizeInGB, description, category, location, affinityGroup, mediaLink,
               eula.build(), label);
      }

      public Builder fromOSImage(Image in) {
         return this.os(in.getOS()).name(in.getName()).logicalSizeInGB(in.getLogicalSizeInGB().orNull())
                  .description(in.getDescription().orNull()).category(in.getCategory().orNull())
                  .location(in.getLocation().orNull()).affinityGroup(in.getAffinityGroup().orNull())
                  .mediaLink(in.getMediaLink().orNull()).eula(in.getEula()).label(in.getLabel());
      }
   }

   private final OSType os;
   private final String name;
   private final Optional<Integer> logicalSizeInGB;
   private final Optional<String> description;
   private final Optional<String> category;
   private final Optional<String> location;
   private final Optional<String> affinityGroup;
   private final Optional<URI> mediaLink;
   private final List<String> eula;
   private final String label;

   private Image(OSType os, String name, Optional<Integer> logicalSizeInGB, Optional<String> description,
         Optional<String> category, Optional<String> location, Optional<String> affinityGroup, Optional<URI> mediaLink,
         List<String> eula, String label) {
      this.name = checkNotNull(name, "name");
      this.logicalSizeInGB = checkNotNull(logicalSizeInGB, "logicalSizeInGB for %s", name);
      this.description = checkNotNull(description, "description for %s", name);
      this.os = checkNotNull(os, "os for %s", name);
      this.category = checkNotNull(category, "category for %s", name);
      this.location = checkNotNull(location, "location for %s", name);
      this.affinityGroup = checkNotNull(affinityGroup, "affinityGroup for %s", name);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", name);
      this.eula = checkNotNull(eula, "eula for %s", name);
      this.label = checkNotNull(label, "label for %s", name);
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the hosted service. This name is the DNS prefix name and can be used to access the
    * hosted service.
    *
    * For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public String getName() {
      return name;
   }

   /**
    * The size, in GB, of the image.
    */
   public Optional<Integer> getLogicalSizeInGB() {
      return logicalSizeInGB;
   }

   /**
    * The description for the image.
    */
   public Optional<String> getDescription() {
      return description;
   }

   /**
    * The repository classification of image. All user images have the category "User", but
    * categories for other images could be, for example "Canonical"
    */
   public Optional<String> getCategory() {
      return category;
   }

   /**
    * The geo-location in which this media is located. The Location value is derived from storage
    * account that contains the blob in which the media is located. If the storage account belongs
    * to an affinity group the value is absent.
    */
   public Optional<String> getLocation() {
      return location;
   }

   /**
    * The affinity in which the media is located. The AffinityGroup value is derived from storage
    * account that contains the blob in which the media is located. If the storage account does not
    * belong to an affinity group the value is absent.
    */
   public Optional<String> getAffinityGroup() {
      return affinityGroup;
   }

   /**
    * The location of the blob in the blob store in which the media for the image is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   public Optional<URI> getMediaLink() {
      return mediaLink;
   }

   /**
    * The eulas for the image, if available.
    */
   // Not URI as some providers put non-uri data in, such as riverbed.
   public List<String> getEula() {
      return eula;
   }

   /**
    * The description of the image.
    */
   public String getLabel() {
      return label;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Image other = (Image) obj;
      return Objects.equal(this.name, other.name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   private ToStringHelper string() {
      return MoreObjects.toStringHelper(this).omitNullValues().add("os", os).add("name", name)
               .add("logicalSizeInGB", logicalSizeInGB.orNull()).add("description", description)
               .add("category", category.orNull()).add("location", location.orNull())
               .add("affinityGroup", affinityGroup.orNull()).add("mediaLink", mediaLink.orNull())
               .add("eula", eula).add("label", label);
   }

}
