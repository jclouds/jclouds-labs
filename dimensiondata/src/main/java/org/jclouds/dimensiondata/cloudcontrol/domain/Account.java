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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Account {
   public abstract String userName();

   @Nullable
   public abstract String password();

   public abstract String fullName();

   public abstract String firstName();

   public abstract String lastName();

   public abstract String emailAddress();

   @Nullable
   public abstract String department();

   @Nullable
   public abstract String customDefined1();

   @Nullable
   public abstract String customDefined2();

   public abstract String orgId();

   @Nullable
   public abstract Roles roles();

   Account() {
   }

   public static Builder builder() {
      return new AutoValue_Account.Builder();
   }

   @SerializedNames({ "userName", "password", "fullName", "firstName", "lastName", "emailAddress", "department",
         "customDefined1", "customDefined2", "orgId", "roles" })
   public static Account create(String userName, String password, String fullName, String firstName, String lastName,
         String emailAddress, String department, String customDefined1, String customDefined2, String orgId,
         Roles roles) {
      return builder().userName(userName).password(password).fullName(fullName).firstName(firstName).lastName(lastName)
            .emailAddress(emailAddress).department(department).customDefined1(customDefined1)
            .customDefined2(customDefined2).orgId(orgId).roles(roles).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder userName(String userName);

      public abstract Builder password(String password);

      public abstract Builder fullName(String fullName);

      public abstract Builder firstName(String firstName);

      public abstract Builder lastName(String lastName);

      public abstract Builder emailAddress(String emailAddress);

      public abstract Builder department(String department);

      public abstract Builder customDefined1(String customDefined1);

      public abstract Builder customDefined2(String customDefined2);

      public abstract Builder orgId(String orgId);

      public abstract Builder roles(Roles roles);

      public abstract Account build();
   }

}
