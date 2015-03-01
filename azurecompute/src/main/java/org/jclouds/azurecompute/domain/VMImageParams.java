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
import com.google.common.collect.Lists;
import org.jclouds.javax.annotation.Nullable;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * OS image from the image repository
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn499770.aspx" >api</a>
 */
@AutoValue
public abstract class VMImageParams {

   @AutoValue
   public abstract static class OSDiskConfigurationParams {

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
      @Nullable public abstract String name();

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

      public static OSDiskConfigurationParams OSDiskConfiguration(String name, Caching hostCaching, OSState osState,
            OSImage.Type os, URI mediaLink, Integer logicalDiskSizeInGB, String ioType) {
         return new AutoValue_VMImageParams_OSDiskConfigurationParams(name, hostCaching, osState, os, mediaLink,
               logicalDiskSizeInGB, ioType);
      }

   }

   @Nullable public abstract String name();

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   @Nullable public abstract String label();

   @Nullable public abstract String description();

   @Nullable public abstract OSDiskConfigurationParams osDiskConfiguration();

   public abstract List<DataVirtualHardDisk> dataDiskConfiguration();

   @Nullable public abstract String language();

   @Nullable public abstract String imageFamily();

   @Nullable public abstract RoleSize.Type recommendedVMSize();

   @Nullable public abstract String eula();

   @Nullable public abstract URI iconUri();

   @Nullable public abstract URI smallIconUri();

   @Nullable public abstract URI privacyUri();

   @Nullable public abstract Boolean showGui();

   public Builder toBuilder() {
      return builder().fromVMImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private String name;
      private String label;
      private String description;
      private OSDiskConfigurationParams osDiskConfiguration;
      private List<DataVirtualHardDisk> dataDiskConfiguration = Lists.newArrayList();
      private String language;
      private String imageFamily;
      private RoleSize.Type recommendedVMSize;
      private String eula;
      private URI iconUri;
      private URI smallIconUri;
      private URI privacyUri;
      private Boolean showGui;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder label(String label) {
         this.label = label;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder osDiskConfiguration(OSDiskConfigurationParams osDiskConfig) {
         this.osDiskConfiguration = osDiskConfig;
         return this;
      }

      public Builder language(String language) {
         this.language = language;
         return this;
      }

      public Builder imageFamily(String imageFamily) {
         this.imageFamily = imageFamily;
         return this;
      }

      public Builder recommendedVMSize(RoleSize.Type recommendedRoleSize) {
         this.recommendedVMSize = recommendedRoleSize;
         return this;
      }

      public Builder showGui(Boolean showGui) {
         this.showGui = showGui;
         return this;
      }

      public Builder eula(String eula) {
         this.eula = eula;
         return this;
      }

      public Builder iconUri(URI iconUri) {
         this.iconUri = iconUri;
         return this;
      }

      public Builder smallIconUri(URI smallIconUri) {
         this.smallIconUri = smallIconUri;
         return this;
      }

      public Builder privacyUri(URI privacyUri) {
         this.privacyUri = smallIconUri;
         return this;
      }

      public Builder dataDiskConfiguration(DataVirtualHardDisk dataDiskConfiguration) {
         this.dataDiskConfiguration.add(dataDiskConfiguration);
         return this;
      }

      public Builder dataDiskConfigurations(Collection<DataVirtualHardDisk> dataDiskConfiguration) {
         this.dataDiskConfiguration.addAll(dataDiskConfiguration);
         return this;
      }

      public VMImageParams build() {
         return VMImageParams
               .create(name, label, description, osDiskConfiguration, dataDiskConfiguration, language,
                     imageFamily, recommendedVMSize, eula, iconUri, smallIconUri, privacyUri,
                     showGui);
      }

      public Builder fromVMImageParams(VMImageParams in) {
         return name(in.name()).label(in.label()).description(in.description())
               .osDiskConfiguration(in.osDiskConfiguration()).dataDiskConfigurations(in.dataDiskConfiguration())
               .language(in.language()).imageFamily(in.imageFamily()).recommendedVMSize(in.recommendedVMSize())
               .eula(in.eula()).iconUri(in.iconUri()).smallIconUri(in.smallIconUri()).privacyUri(in.privacyUri());
      }

   }

   public static VMImageParams create(String name, String label, String description,
         OSDiskConfigurationParams osDiskConfiguration, List<DataVirtualHardDisk> dataDiskConfiguration,
         String language, String imageFamily, RoleSize.Type recommendedVMSize, String eula, URI iconUri,
         URI smallIconUri, URI privacyUri, Boolean showGui) {
      return new AutoValue_VMImageParams(name, label, description, osDiskConfiguration, dataDiskConfiguration,
            language, imageFamily, recommendedVMSize, eula, iconUri, smallIconUri, privacyUri,
            showGui);
   }
}
