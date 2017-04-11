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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Account {

   public abstract String userName();

   public abstract String fullName();

   public abstract String firstName();

   public abstract String lastName();

   public abstract String emailAddress();

   @Nullable
   public abstract List<RoleType> roles();

   @Nullable
   public abstract AccountPhoneNumber phone();

   @Nullable
   public abstract String department();

   @Nullable
   public abstract String customDefined1();

   @Nullable
   public abstract String customDefined2();

   public abstract AccountOrganization organization();

   @Nullable
   public abstract String state();

   Account() {
   }

   @SerializedNames({ "userName", "fullName", "firstName", "lastName", "emailAddress", "roles", "phone", "department",
         "customDefined1", "customDefined2", "organization", "state" })
   public static Account create(String userName, String fullName, String firstName, String lastName,
         String emailAddress, List<RoleType> roles, AccountPhoneNumber phone, String department, String customDefined1,
         String customDefined2, AccountOrganization organization, String state) {
      return builder().userName(userName).fullName(fullName).firstName(firstName).lastName(lastName)
            .emailAddress(emailAddress).roles(roles).phone(phone).department(department).customDefined1(customDefined1)
            .customDefined2(customDefined2).organization(organization).state(state).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Account.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder userName(String username);

      public abstract Builder fullName(String fullName);

      public abstract Builder firstName(String firstName);

      public abstract Builder lastName(String lastName);

      public abstract Builder emailAddress(String emailAddress);

      public abstract Builder roles(List<RoleType> roles);

      public abstract Builder phone(AccountPhoneNumber phone);

      public abstract Builder department(String department);

      public abstract Builder customDefined1(String customDefined1);

      public abstract Builder customDefined2(String customDefined2);

      public abstract Builder organization(AccountOrganization organization);

      public abstract Builder state(String state);

      abstract Account autoBuild();

      abstract List<RoleType> roles();

      public Account build() {
         roles(roles() != null ? ImmutableList.copyOf(roles()) : null);
         return autoBuild();
      }

   }

   @AutoValue
   public abstract static class AccountOrganization {

      AccountOrganization() {
      }

      public abstract String id();

      public abstract String name();

      public abstract String homeGeoName();

      public abstract String homeGeoApiHost();

      public abstract String homeGeoId();

      @SerializedNames({ "id", "name", "homeGeoName", "homeGeoApiHost", "homeGeoId" })
      public static AccountOrganization create(String id, String name, String homeGeoName, String homeGeoApiHost,
            String homeGeoId) {
         return builder().id(id).name(name).homeGeoName(homeGeoName).homeGeoApiHost(homeGeoApiHost).homeGeoId(homeGeoId)
               .build();
      }

      public abstract Builder toBuilder();

      public static Builder builder() {
         return new AutoValue_Account_AccountOrganization.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder id(String id);

         public abstract Builder name(String name);

         public abstract Builder homeGeoName(String homeGeoName);

         public abstract Builder homeGeoApiHost(String homeGeoApiHost);

         public abstract Builder homeGeoId(String homeGeoId);

         public abstract AccountOrganization build();
      }
   }

   @AutoValue
   public abstract static class AccountPhoneNumber {

      AccountPhoneNumber() {
      }

      public abstract String countryCode();

      public abstract String number();

      @SerializedNames({ "countryCode", "number" })
      public static AccountPhoneNumber create(String countryCode, String number) {
         return builder().countryCode(countryCode).number(number).build();
      }

      public abstract Builder toBuilder();

      public static Builder builder() {
         return new AutoValue_Account_AccountPhoneNumber.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder countryCode(String countryCode);

         public abstract Builder number(String number);

         public abstract AccountPhoneNumber build();
      }

   }

   @AutoValue
   public abstract static class RoleType {

      RoleType() {
      }

      public abstract String name();

      @SerializedNames({ "role" })
      public static RoleType create(String name) {
         return builder().name(name).build();
      }

      public abstract Builder toBuilder();

      public static Builder builder() {
         return new AutoValue_Account_RoleType.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder name(String name);

         public abstract RoleType build();
      }
   }
}
