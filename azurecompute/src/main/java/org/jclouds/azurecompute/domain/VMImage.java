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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
/**
 * OS image from the image repository
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn499770.aspx" >api</a>
 */
@AutoValue
public abstract class VMImage {

   @AutoValue
   public abstract static class OSDiskConfiguration {

      public enum Caching {
         READ_ONLY,
         READ_WRITE,
         NONE
      }

      public enum OSState {
         GENERALIZED,
         SPECIALIZED
      }

      /**
       * Specifies the name of the operating system disk.
       */
      public abstract String name();

      /**
       * Specifies the caching behavior of the operating system disk.
       * This setting impacts the consistency and performance of the disk.
       * The default value is ReadWrite
       */
      @Nullable public abstract Caching hostCaching();

      /**
       * Specifies the state of the operating system in the image.
       * A Virtual Machine that is fully configured and running contains a Specialized operating system.
       * A Virtual Machine on which the Sysprep command has been run with the generalize option contains
       * a Generalized operating system.
       */
      @Nullable public abstract OSState osState();

      /**
       * Specifies the operating system type of the image.
       */
      public abstract OSImage.Type os();

      /**
       * Specifies the location of the blob in Azure storage. The blob location belongs to a storage account in the
       * subscription specified by the <subscription-id> value in the operation call.
       */
      @Nullable public abstract URI mediaLink();

      /**
       * Specifies the size, in GB, of the operating system disk
       */
      @Nullable public abstract Integer logicalSizeInGB();

      /**
       * This property identifies the type of the storage account for the backing VHD.
       */
      @Nullable public abstract String ioType();

      public static OSDiskConfiguration create(String name, Caching hostCaching, OSState osState, OSImage.Type os,
            URI mediaLink, Integer logicalDiskSizeInGB, String ioType) {
         return new AutoValue_VMImage_OSDiskConfiguration(name, hostCaching, osState, os, mediaLink,
               logicalDiskSizeInGB, ioType);
      }

   }

   public abstract String name();

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   public abstract String label();

   /**
    * The repository classification of image. All user images have the category "User", but
    * categories for other images could be, for example "Canonical"
    */
   @Nullable public abstract String category();

   @Nullable public abstract String description();

   @Nullable public abstract OSDiskConfiguration osDiskConfiguration();

   public abstract List<DataVirtualHardDisk> dataDiskConfiguration();

   @Nullable public abstract String serviceName();

   @Nullable public abstract String deploymentName();

   @Nullable public abstract String roleName();

   /**
    * The geo-locations of the image, if the image is not associated with an affinity group.
    */
   @Nullable public abstract String location();

   /**
    * The affinity group with which this image is associated, if any.
    */
   @Nullable public abstract String affinityGroup();

   @Nullable public abstract Date createdTime();

   @Nullable public abstract Date modifiedTime();

   @Nullable public abstract String language();

   @Nullable public abstract String imageFamily();

   @Nullable public abstract RoleSize.Type recommendedVMSize();

   @Nullable public abstract Boolean isPremium();

   @Nullable public abstract String eula();

   @Nullable public abstract URI iconUri();

   @Nullable public abstract URI smallIconUri();

   @Nullable public abstract URI privacyUri();

   @Nullable public abstract Date publishedDate();

   public static VMImage create(String name, String label, String category, String description,
         OSDiskConfiguration osDiskConfiguration, List<DataVirtualHardDisk> dataDiskConfiguration, String serviceName,
         String deploymentName, String roleName, String location, String affinityGroup, Date createdTime,
         Date modifiedTime, String language, String imageFamily, RoleSize.Type recommendedVMSize, Boolean isPremium,
         String eula, URI iconUri, URI smallIconUri,
         URI privacyUri, Date publishedDate) {
      return new AutoValue_VMImage(name, label, category, description, osDiskConfiguration, copyOf(dataDiskConfiguration),
            serviceName, deploymentName, roleName, location, affinityGroup, createdTime, modifiedTime, language,
            imageFamily, recommendedVMSize, isPremium, eula, iconUri, smallIconUri, privacyUri, publishedDate);
   }
}
