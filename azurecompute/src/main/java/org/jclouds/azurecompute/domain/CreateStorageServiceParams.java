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
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.jclouds.azurecompute.domain.StorageService.AccountType;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class CreateStorageServiceParams {

   CreateStorageServiceParams() {
   } // For AutoValue only!

   /**
    * A name for the storage account that is unique within Azure. Storage account names must be between 3 and 24
    * characters in length and use numbers and lower-case letters only.
    */
   public abstract String serviceName();

   /**
    * A description for the storage account. The description may be up to 1024 characters in length.
    */
   @Nullable
   public abstract String description();

   /**
    * A label for the storage account specified as a base64-encoded string. The label may be up to 100 characters in
    * length. The label can be used identify the storage account for your tracking purposes.
    */
   public abstract String label();

   /**
    * Required if AffinityGroup is not specified. The location where the storage account is created.
    */
   @Nullable
   public abstract String location();

   /**
    * Required if Location is not specified. The name of an existing affinity group in the specified subscription.
    */
   @Nullable
   public abstract String affinityGroup();

   /**
    * Represents the name of an extended cloud service property. Each extended property must have both a defined name
    * and value. You can have a maximum of 50 extended property name/value pairs.
    *
    * <p/>
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and underscores are valid in
    * the Name, and the name must start with a letter. Each extended property value has a maximum length of 255
    * characters.
    */
   @Nullable
   public abstract Map<String, String> extendedProperties();

   /**
    * Specifies whether the account supports locally-redundant storage, geo-redundant storage, zone-redundant storage,
    * or read access geo-redundant storage.
    */
   public abstract AccountType accountType();

   public Builder toBuilder() {
      return builder().fromCreateStorageServiceParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String serviceName;

      private String description;

      private String label;

      private String location;

      private String affinityGroup;

      private Map<String, String> extendedProperties;

      private AccountType accountType;

      public Builder serviceName(final String serviceName) {
         this.serviceName = serviceName;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder label(final String label) {
         this.label = label;
         return this;
      }

      public Builder location(final String location) {
         this.location = location;
         return this;
      }

      public Builder affinityGroup(final String affinityGroup) {
         this.affinityGroup = affinityGroup;
         return this;
      }

      public Builder extendedProperties(final Map<String, String> extendedProperties) {
         this.extendedProperties = extendedProperties;
         return this;
      }

      public Builder accountType(final AccountType accountType) {
         this.accountType = accountType;
         return this;
      }

      public CreateStorageServiceParams build() {
         return CreateStorageServiceParams.create(serviceName, description, label, location, affinityGroup,
                 extendedProperties, accountType);
      }

      public Builder fromCreateStorageServiceParams(final CreateStorageServiceParams storageServiceParams) {
         return serviceName(storageServiceParams.serviceName()).
                 description(storageServiceParams.description()).
                 label(storageServiceParams.label()).
                 location(storageServiceParams.location()).
                 affinityGroup(storageServiceParams.affinityGroup()).
                 extendedProperties(storageServiceParams.extendedProperties()).
                 accountType(storageServiceParams.accountType());
      }
   }

   private static CreateStorageServiceParams create(
           final String serviceName, final String description, final String label, final String location,
           final String affinityGroup, final Map<String, String> extendedProperties, final AccountType accountType) {

      return new AutoValue_CreateStorageServiceParams(serviceName, description, label, location, affinityGroup,
              extendedProperties == null ? null : ImmutableMap.copyOf(extendedProperties), accountType);
   }
}
