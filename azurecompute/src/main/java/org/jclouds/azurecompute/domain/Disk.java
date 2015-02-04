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

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * A disk in the image repository.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 */
@AutoValue
public abstract class Disk {

   Disk() {} // For AutoValue only!

   @AutoValue
   public abstract static class Attachment {

      Attachment() {} // For AutoValue only!

      /** The deployment in which the disk is being used. */
      public abstract String deployment();

      /** The cloud service in which the disk is being used. */
      public abstract String hostedService();

      /** The virtual machine that the disk is attached to. */
      public abstract String virtualMachine();

      public static Attachment create(String hostedService, String deployment, String virtualMachine) {
         return new AutoValue_Disk_Attachment(hostedService, deployment, virtualMachine);
      }
   }

   /**
    * The name of the disk. This is the name that is used when creating one or more virtual machines
    * using the disk.
    */
   public abstract String name();

   /**
    * The geo-location of the disk in Windows Azure, if the disk is not
    * associated with an affinity group. If a location has been specified, the AffinityGroup element
    * is not returned.
    */
   @Nullable public abstract String location();

   /**
    * The affinity group with which this disk is associated, if any. If the service is
    * associated with an affinity group, the Location element is not returned.
    */
   @Nullable public abstract String affinityGroup();

   @Nullable public abstract String description();

   /** The operating system type of the OS image, or null if a data disk. */
   @Nullable public abstract OSImage.Type os();

   /**
    * The location of the blob in the blob store in which the media for the image is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   @Nullable public abstract URI mediaLink();

   @Nullable public abstract Integer logicalSizeInGB();

   /**
    * Contains properties that specify a virtual machine that currently using the disk. A disk
    * cannot be deleted as long as it is attached to a virtual machine.
    */
   @Nullable public abstract Attachment attachedTo();

   /**
    * The name of the OS Image from which the disk was created. This property is populated
    * automatically when a disk is created from an OS image by calling the Add Role, Create
    * Deployment, or Provision Disk operations.
    */
   @Nullable public abstract String sourceImage();

   public static Disk create(String name, String location, String affinityGroup, String description, OSImage.Type os,
         URI mediaLink, Integer logicalSizeInGB, Attachment attachedTo, String sourceImage) {
      return new AutoValue_Disk(name, location, affinityGroup, description, os, mediaLink, logicalSizeInGB, attachedTo,
            sourceImage);
   }
}
