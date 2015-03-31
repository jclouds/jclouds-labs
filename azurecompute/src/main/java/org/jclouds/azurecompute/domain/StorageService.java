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
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class StorageService {

   public enum AccountType {

      Standard_LRS,
      Standard_ZRS,
      Standard_GRS,
      Standard_RAGRS,
      Premium_LRS,
      UNRECOGNIZED;

      public static AccountType fromString(final String text) {
         if (text != null) {
            for (AccountType type : AccountType.values()) {
               if (text.equalsIgnoreCase(type.name())) {
                  return type;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   public enum RegionStatus {

      Available,
      Unavailable,
      UNRECOGNIZED;

      public static RegionStatus fromString(final String text) {
         if (text != null) {
            for (RegionStatus status : RegionStatus.values()) {
               if (text.equalsIgnoreCase(status.name())) {
                  return status;
               }
            }
         }
         return UNRECOGNIZED;
      }

   }

   public enum Status {

      Creating,
      Created,
      Deleting,
      Deleted,
      Changing,
      ResolvingDns,
      UNRECOGNIZED;

      public static Status fromString(final String text) {
         if (text != null) {
            for (Status status : Status.values()) {
               if (text.equalsIgnoreCase(status.name())) {
                  return status;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   @AutoValue
   public abstract static class StorageServiceProperties {

      StorageServiceProperties() {
      } // For AutoValue only!

      /**
       * A description for the storage account. The description can be up to 1024 characters in length.
       */
      @Nullable
      public abstract String description();

      /**
       * Required if Location is not specified. The name of an existing affinity group associated with this
       * subscription.
       */
      @Nullable
      public abstract String affinityGroup();

      /**
       * Required if AffinityGroup is not specified. The location where the storage account will be created.
       */
      @Nullable
      public abstract String location();

      /**
       * A name for the hosted service that is base-64 encoded. The name can be up to 100 characters in length. The name
       * can be used identify the storage account for your tracking purposes
       */
      public abstract String label();

      /**
       * The status of the storage account.
       */
      public abstract Status status();

      /**
       * Specifies the endpoints of the storage account.
       */
      @Nullable
      public abstract List<URL> endpoints();

      /**
       * Indicates the primary geographical region in which the storage account exists at this time.
       */
      public abstract String geoPrimaryRegion();

      /**
       * Indicates whether the primary storage region is available.
       */
      public abstract RegionStatus statusOfPrimary();

      /**
       * A timestamp that indicates the most recent instance of a failover to the secondary region. In case of multiple
       * failovers only the latest failover date and time maintained
       */
      @Nullable
      public abstract Date lastGeoFailoverTime();

      /**
       * Indicates the geographical region in which the storage account is being replicated.
       */
      @Nullable
      public abstract String geoSecondaryRegion();

      /**
       * Indicates whether the secondary storage region is available.
       */
      @Nullable
      public abstract RegionStatus statusOfSecondary();

      /**
       * Specifies the time that the storage account was created.
       */
      public abstract Date creationTime();

      /**
       * Specifies the custom domains that are associated with the storage account.
       */
      @Nullable
      public abstract List<String> customDomains();

      /**
       * Specifies the secondary endpoints of the storage account.
       */
      @Nullable
      public abstract List<URL> secondaryEndpoints();

      /**
       * Specifies whether the account supports locally-redundant storage, geo-redundant storage, zone-redundant
       * storage, or read access geo-redundant storage.
       */
      public abstract AccountType accountType();

      public static StorageServiceProperties create(final String description, final String affinityGroup,
              final String location, final String label, final Status status, final List<URL> endpoints,
              final String geoPrimaryRegion, final RegionStatus statusOfPrimary,
              final Date lastGeoFailoverTime, final String geoSecondaryRegion, final RegionStatus statusOfSecondary,
              final Date creationTime, final List<String> customDomains, final List<URL> secondaryEndpoints,
              final AccountType accountType) {

         return new AutoValue_StorageService_StorageServiceProperties(description, affinityGroup, location,
                 label, status, endpoints == null ? null : ImmutableList.copyOf(endpoints),
                 geoPrimaryRegion, statusOfPrimary, lastGeoFailoverTime, geoSecondaryRegion, statusOfSecondary,
                 creationTime, customDomains,
                 secondaryEndpoints == null ? null : ImmutableList.copyOf(secondaryEndpoints), accountType);
      }
   }

   StorageService() {
   } // For AutoValue only!

   /**
    * Specifies the URI of the storage account.
    */
   public abstract URL url();

   /**
    * Specifies the name of the storage account. This name is the DNS prefix name and can be used to access blobs,
    * queues, and tables in the storage account.
    */
   public abstract String serviceName();

   /**
    * Specifies the properties of the storage account.
    */
   public abstract StorageServiceProperties storageServiceProperties();

   /**
    * Specifies the name and value of an extended property that was added to the storage account.
    */
   @Nullable
   public abstract Map<String, String> extendedProperties();

   /**
    * Indicates whether the storage account is able to perform virtual machine related operations. If so, this element
    * returns a string containing PersistentVMRole. Otherwise, this element will not be present.
    */
   @Nullable
   public abstract String capability();

   public static StorageService create(final URL url, final String serviceName,
           final StorageServiceProperties storageServiceProperties, final Map<String, String> extendedProperties,
           final String capability) {

      return new AutoValue_StorageService(url, serviceName, storageServiceProperties,
              extendedProperties == null ? null : ImmutableMap.copyOf(extendedProperties), capability);
   }
}
