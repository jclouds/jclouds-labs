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
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * disk in the image repository
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
public class Disk {
   public static class Attachment {

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromAttachment(this);
      }

      public static class Builder {

         private String hostedService;
         private String deployment;
         private String role;

         /**
          * @see Attachment#getHostedService()
          */
         public Builder hostedService(String hostedService) {
            this.hostedService = hostedService;
            return this;
         }

         /**
          * @see Attachment#getDeployment()
          */
         public Builder deployment(String deployment) {
            this.deployment = deployment;
            return this;
         }

         /**
          * @see Attachment#getRole()
          */
         public Builder role(String role) {
            this.role = role;
            return this;
         }

         public Attachment build() {
            return new Attachment(hostedService, deployment, role);
         }

         public Builder fromAttachment(Attachment in) {
            return this.hostedService(in.hostedService).deployment(in.deployment).role(in.role);
         }
      }

      private final String hostedService;
      private final String deployment;
      private final String role;

      private Attachment(String hostedService, String deployment, String role) {
         this.hostedService = checkNotNull(hostedService, "hostedService");
         this.deployment = checkNotNull(deployment, "deployment");
         this.role = checkNotNull(role, "role");
      }

      /**
       * The deployment in which the disk is being used.
       */
      public String getDeployment() {
         return deployment;
      }

      /**
       * The hosted service in which the disk is being used.
       */
      public String getHostedService() {
         return hostedService;
      }

      /**
       * The virtual machine that the disk is attached to.
       */
      public String getRole() {
         return role;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(hostedService, deployment, role);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         Attachment other = (Attachment) obj;
         return Objects.equal(this.hostedService, other.hostedService) && Objects
               .equal(this.deployment, other.deployment) && Objects.equal(this.role, other.role);
      }

      @Override
      public String toString() {
         return MoreObjects.toStringHelper(this).omitNullValues().add("deployment", hostedService).add("role", role)
               .toString();
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromHostedService(this);
   }

   public static class Builder {

      private Optional<Attachment> attachedTo = Optional.absent();
      private OSType os;
      private String name;
      private Optional<Integer> logicalSizeInGB = Optional.absent();
      private Optional<String> description = Optional.absent();
      private Optional<String> location = Optional.absent();
      private Optional<String> affinityGroup = Optional.absent();
      private Optional<URI> mediaLink = Optional.absent();
      private Optional<String> sourceImage = Optional.absent();
      private Optional<String> label = Optional.absent();
      private boolean hasOperatingSystem;
      private boolean isCorrupted;

      /**
       * @see Disk#getAttachedTo()
       */
      public Builder attachedTo(Attachment attachedTo) {
         this.attachedTo = Optional.fromNullable(attachedTo);
         return this;
      }

      /**
       * @see Disk#getOS()
       */
      public Builder os(OSType os) {
         this.os = os;
         return this;
      }

      /**
       * @see Disk#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Disk#getDescription()
       */
      public Builder description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      /**
       * @see Disk#getLogicalSizeInGB()
       */
      public Builder logicalSizeInGB(Integer logicalSizeInGB) {
         this.logicalSizeInGB = Optional.fromNullable(logicalSizeInGB);
         return this;
      }

      /**
       * @see Disk#getLocation()
       */
      public Builder location(String location) {
         this.location = Optional.fromNullable(location);
         return this;
      }

      /**
       * @see Disk#getAffinityGroup()
       */
      public Builder affinityGroup(String affinityGroup) {
         this.affinityGroup = Optional.fromNullable(affinityGroup);
         return this;
      }

      /**
       * @see Disk#getMediaLink()
       */
      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = Optional.fromNullable(mediaLink);
         return this;
      }

      /**
       * @see Disk#getSourceImage()
       */
      public Builder sourceImage(String sourceImage) {
         this.sourceImage = Optional.fromNullable(sourceImage);
         return this;
      }

      /**
       * @see Disk#getLabel()
       */
      public Builder label(String label) {
         this.label = Optional.fromNullable(label);
         return this;
      }

      /**
       * @see Disk#hasOperatingSystem()
       */
      public Builder hasOperatingSystem(boolean hasOperatingSystem) {
         this.hasOperatingSystem = hasOperatingSystem;
         return this;
      }

      /**
       * @see Disk#isCorrupted()
       */
      public Builder isCorrupted(boolean isCorrupted) {
         this.isCorrupted = isCorrupted;
         return this;
      }

      public Disk build() {
         return new Disk(attachedTo, os, name, logicalSizeInGB, description, location, affinityGroup, mediaLink,
               sourceImage, label, hasOperatingSystem, isCorrupted);
      }

      public Builder fromHostedService(Disk in) {
         return this.attachedTo(in.attachedTo.orNull()).os(in.getOS()).name(in.getName())
               .logicalSizeInGB(in.getLogicalSizeInGB().orNull()).description(in.getDescription().orNull())
               .location(in.getLocation().orNull()).affinityGroup(in.getAffinityGroup().orNull())
               .mediaLink(in.getMediaLink().orNull()).sourceImage(in.getSourceImage().orNull())
               .label(in.getLabel().orNull()).hasOperatingSystem(in.hasOperatingSystem).isCorrupted(in.isCorrupted);
      }
   }

   private final Optional<Attachment> attachedTo;
   private final OSType os;
   private final String name;
   private final Optional<Integer> logicalSizeInGB;
   private final Optional<String> description;
   private final Optional<String> location;
   private final Optional<String> affinityGroup;
   private final Optional<URI> mediaLink;
   private final Optional<String> sourceImage;
   private final Optional<String> label;
   private final boolean hasOperatingSystem;
   private final boolean isCorrupted;

   private Disk(Optional<Attachment> attachedTo, OSType os, String name, Optional<Integer> logicalSizeInGB,
         Optional<String> description, Optional<String> location, Optional<String> affinityGroup,
         Optional<URI> mediaLink, Optional<String> sourceImage, Optional<String> label, boolean hasOperatingSystem,
         boolean isCorrupted) {
      this.name = checkNotNull(name, "name");
      this.attachedTo = checkNotNull(attachedTo, "attachedTo for %s", name);
      this.logicalSizeInGB = checkNotNull(logicalSizeInGB, "logicalSizeInGB for %s", name);
      this.description = checkNotNull(description, "description for %s", name);
      this.os = checkNotNull(os, "os for %s", name);
      this.location = checkNotNull(location, "location for %s", name);
      this.affinityGroup = checkNotNull(affinityGroup, "affinityGroup for %s", name);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", name);
      this.sourceImage = checkNotNull(sourceImage, "sourceImage for %s", name);
      this.label = checkNotNull(label, "label for %s", name);
      this.hasOperatingSystem = hasOperatingSystem;
      this.isCorrupted = isCorrupted;
   }

   /**
    * Contains properties that specify a virtual machine that currently using the disk. A disk
    * cannot be deleted as long as it is attached to a virtual machine.
    */
   public Optional<Attachment> getAttachedTo() {
      return attachedTo;
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the disk. This is the name that is used when creating one or more virtual machines
    * using the disk.
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
    * The location of the blob in the blob store in which the media for the disk is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/mydisk.vhd
    */
   public Optional<URI> getMediaLink() {
      return mediaLink;
   }

   /**
    * The name of the OS Image from which the disk was created. This property is populated
    * automatically when a disk is created from an OS image by calling the Add Role, Create
    * Deployment, or Provision Disk operations.
    */
   public Optional<String> getSourceImage() {
      return sourceImage;
   }

   /**
    * The description of the image.
    */
   public Optional<String> getLabel() {
      return label;
   }

   /**
    * Returns whether this disk contains operation system. Only disks that have an operating system
    * installed can be mounted as an OS Drive.
    */
   public boolean hasOperatingSystem() {
      return hasOperatingSystem;
   }

   /**
    * Returns whether there is a consistency failure detected with this disk. If a disk fails the
    * consistency check, you delete any virtual machines using it, delete the disk, and inspect the
    * blob media to see if the content is intact. You can then reregister the media in the blob as a
    * disk.
    */
   public boolean isCorrupted() {
      return isCorrupted;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Disk other = (Disk) obj;
      return Objects.equal(this.name, other.name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   private ToStringHelper string() {
      return MoreObjects.toStringHelper(this).omitNullValues().add("os", os).add("name", name)
            .add("attachedTo", attachedTo.orNull()).add("logicalSizeInGB", logicalSizeInGB.orNull())
            .add("description", description).add("location", location.orNull())
            .add("affinityGroup", affinityGroup.orNull()).add("mediaLink", mediaLink.orNull())
            .add("sourceImage", sourceImage.orNull()).add("label", label.orNull())
            .add("hasOperatingSystem", hasOperatingSystem).add("isCorrupted", isCorrupted);
   }

}
