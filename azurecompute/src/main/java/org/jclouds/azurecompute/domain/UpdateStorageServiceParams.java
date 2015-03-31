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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class UpdateStorageServiceParams {

   public enum AccountType {

      Standard_LRS,
      Standard_GRS,
      Standard_RAGRS;

   }

   /**
    * Specifies information about a custom domain that is associated with the storage account.
    */
   @AutoValue
   public abstract static class CustomDomain {

      CustomDomain() {
      } // For AutoValue only!

      /**
       * Specifies the name of the custom domain.
       */
      public abstract String name();

      /**
       * Indicates whether indirect CName validation is enabled.
       */
      public abstract Boolean useSubDomainName();

      public static CustomDomain create(final String name, final boolean useSubDomainName) {
         return new AutoValue_UpdateStorageServiceParams_CustomDomain(name, useSubDomainName);
      }
   }

   UpdateStorageServiceParams() {
   } // For AutoValue only!

   /**
    * Specifies a base-64 encoded name for the storage account. The label may be up to 100 characters in length. The
    * label can be used identify the storage account for your tracking purposes. You must specify a value for either
    * Label or Description, or for both.
    */
   @Nullable
   public abstract String label();

   /**
    * A description for the storage account. The description may be up to 1024 characters in length. You must specify a
    * value for either Label or Description, or for both.
    */
   @Nullable
   public abstract String description();

   /**
    * Enables or disables geo-replication on the specified the storage. If set to true, the data in the storage account
    * is replicated across more than one geographic location so as to enable resilience in the face of catastrophic
    * service loss. If set to false, geo-replication is disabled. If the element is not included in the request body,
    * the current value is left unchanged.
    */
   @Nullable
   public abstract Boolean geoReplicationEnabled();

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
    * Specifies the custom domains that are associated with the storage account.
    */
   @Nullable
   public abstract List<CustomDomain> customDomains();

   /**
    * Specifies whether the account supports locally-redundant storage, geo-redundant storage, or read access
    * geo-redundant storage. Zone-redundant storage is not an option when you update a storage account.
    */
   @Nullable
   public abstract AccountType accountType();

   public Builder toBuilder() {
      return builder().fromUpdateStorageServiceParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String label;

      private String description;

      private Boolean geoReplicationEnabled;

      private Map<String, String> extendedProperties;

      private List<CustomDomain> customDomains;

      private AccountType accountType;

      public Builder label(final String label) {
         this.label = label;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder geoReplicationEnabled(final Boolean geoReplicationEnabled) {
         this.geoReplicationEnabled = geoReplicationEnabled;
         return this;
      }

      public Builder extendedProperties(final Map<String, String> extendedProperties) {
         this.extendedProperties = extendedProperties;
         return this;
      }

      public Builder customDomains(final List<CustomDomain> customDomains) {
         this.customDomains = customDomains;
         return this;
      }

      public Builder accountType(final AccountType accountType) {
         this.accountType = accountType;
         return this;
      }

      public UpdateStorageServiceParams build() {
         return UpdateStorageServiceParams.create(label, description,
                 geoReplicationEnabled, extendedProperties, customDomains, accountType);
      }

      public Builder fromUpdateStorageServiceParams(final UpdateStorageServiceParams storageServiceParams) {
         return label(storageServiceParams.label()).
                 description(storageServiceParams.description()).
                 geoReplicationEnabled(storageServiceParams.geoReplicationEnabled()).
                 extendedProperties(storageServiceParams.extendedProperties()).
                 customDomains(storageServiceParams.customDomains()).
                 accountType(storageServiceParams.accountType());
      }
   }

   private static UpdateStorageServiceParams create(final String label, final String description,
           final Boolean geoReplicationEnabled,
           final Map<String, String> extendedProperties, final List<CustomDomain> customDomains,
           final AccountType accountType) {

      return new AutoValue_UpdateStorageServiceParams(label, description, geoReplicationEnabled,
              extendedProperties == null ? null : ImmutableMap.copyOf(extendedProperties),
              customDomains == null ? null : ImmutableList.copyOf(customDomains),
              accountType);
   }
}
