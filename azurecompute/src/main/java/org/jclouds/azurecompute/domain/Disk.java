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

import org.jclouds.azurecompute.domain.Image.OSType;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * A disk in the image repository.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
public final class Disk {
   public static final class Attachment {
      /** The deployment in which the disk is being used. */
      public String deployment() {
         return deployment;
      }

      /** The hosted service in which the disk is being used. */
      public String hostedService() {
         return hostedService;
      }

      /** The virtual machine that the disk is attached to. */
      public String virtualMachine() {
         return virtualMachine;
      }

      public static Attachment create(String hostedService, String deployment, String virtualMachine) {
         return new Attachment(hostedService, deployment, virtualMachine);
      }

      // TODO: Remove from here down with @AutoValue.
      private Attachment(String hostedService, String deployment, String virtualMachine) {
         this.hostedService = checkNotNull(hostedService, "hostedService");
         this.deployment = checkNotNull(deployment, "deployment");
         this.virtualMachine = checkNotNull(virtualMachine, "virtualMachine");
      }

      private final String hostedService;
      private final String deployment;
      private final String virtualMachine;

      @Override
      public int hashCode() {
         return Objects.hashCode(hostedService, deployment, virtualMachine);
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
         return equal(this.hostedService, other.hostedService) &&
               equal(this.deployment, other.deployment) &&
               equal(this.virtualMachine, other.virtualMachine);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
               .add("hostedService", hostedService)
               .add("deployment", deployment)
               .add("virtualMachine", virtualMachine).toString();
      }

   }

   /**
    * The name of the disk. This is the name that is used when creating one or more virtual machines
    * using the disk.
    */
   public String name() {
      return name;
   }

   /**
    * The geo-location of the disk in Windows Azure, if the disk is not
    * associated with an affinity group. If a location has been specified, the AffinityGroup element
    * is not returned.
    */
   @Nullable public String location() {
      return location;
   }

   /**
    * The affinity group with which this disk is associated, if any. If the service is
    * associated with an affinity group, the Location element is not returned.
    */
   @Nullable public String affinityGroup() {
      return affinityGroup;
   }

   @Nullable public String description() {
      return description;
   }

   /** The operating system type of the OS image, or null if a data disk. */
   @Nullable public OSType os() {
      return os;
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
   @Nullable public URI mediaLink() {
      return mediaLink;
   }

   @Nullable public Integer logicalSizeInGB() {
      return logicalSizeInGB;
   }

   /**
    * Contains properties that specify a virtual machine that currently using the disk. A disk
    * cannot be deleted as long as it is attached to a virtual machine.
    */
   @Nullable public Attachment attachedTo() {
      return attachedTo;
   }

   /**
    * The name of the OS Image from which the disk was created. This property is populated
    * automatically when a disk is created from an OS image by calling the Add Role, Create
    * Deployment, or Provision Disk operations.
    */
   @Nullable public String sourceImage() {
      return sourceImage;
   }

   public static Disk create(String name, String location, String affinityGroup, String description,
         OSType os, URI mediaLink, Integer logicalSizeInGB, Attachment attachedTo, String sourceImage) {
      return new Disk(name, location, affinityGroup, description, os, mediaLink, logicalSizeInGB, attachedTo,
            sourceImage);
   }

   // TODO: Remove from here down with @AutoValue.
   private Disk(String name, String location, String affinityGroup, String description, OSType os, URI mediaLink,
         Integer logicalSizeInGB, Attachment attachedTo, String sourceImage) {
      this.name = checkNotNull(name, "name");
      this.location = location;
      this.affinityGroup = affinityGroup;
      this.description = description;
      this.os = os;
      this.mediaLink = mediaLink;
      this.logicalSizeInGB = checkNotNull(logicalSizeInGB, "logicalSizeInGB of %s", name);
      this.attachedTo = attachedTo;
      this.sourceImage = sourceImage;
   }

   private final String name;
   private final String location;
   private final String affinityGroup;
   private final String description;
   private final OSType os;
   private final URI mediaLink;
   private final Integer logicalSizeInGB;
   private final Attachment attachedTo;
   private final String sourceImage;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Disk) {
         Disk that = Disk.class.cast(object);
         return equal(name, that.name) &&
               equal(location, that.location) &&
               equal(affinityGroup, that.affinityGroup) &&
               equal(description, that.description) &&
               equal(os, that.os) &&
               equal(mediaLink, that.mediaLink) &&
               equal(logicalSizeInGB, that.logicalSizeInGB) &&
               equal(attachedTo, that.attachedTo) &&
               equal(sourceImage, that.sourceImage);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, location, affinityGroup, description, os, mediaLink, logicalSizeInGB,
            attachedTo, sourceImage);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("name", name)
            .add("location", location)
            .add("affinityGroup", affinityGroup)
            .add("description", description)
            .add("os", os)
            .add("mediaLink", mediaLink)
            .add("logicalSizeInGB", logicalSizeInGB)
            .add("attachedTo", attachedTo)
            .add("sourceImage", sourceImage).toString();
   }
}
