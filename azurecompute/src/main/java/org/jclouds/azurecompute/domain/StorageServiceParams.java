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

// TODO: check which can be null.
@AutoValue
public abstract class StorageServiceParams {

   public enum Type {

      Standard_LRS,
      Standard_ZRS,
      Standard_GRS,
      Standard_RAGRS,
      Premium_LRS;

   }

   StorageServiceParams() {
   } // For AutoValue only!

   /**
    * The user-supplied name for this deployment.
    */
   public abstract String name();

   public abstract String label();

   public abstract String location();

   public abstract Type accountType();

   public Builder toBuilder() {
      return builder().fromStorageServiceParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String name;

      private String label;

      private String location;

      private Type accountType;

      public Builder name(final String name) {
         this.name = name;
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

      public Builder accountType(final Type accountType) {
         this.accountType = accountType;
         return this;
      }

      public StorageServiceParams build() {
         return StorageServiceParams.create(name, label, location, accountType);
      }

      public Builder fromStorageServiceParams(final StorageServiceParams storageServiceParams) {
         return name(storageServiceParams.name())
                 .label(storageServiceParams.label())
                 .location(storageServiceParams.location())
                 .accountType(storageServiceParams.accountType());
      }
   }

   private static StorageServiceParams create(
           final String name, final String label, final String location, final Type accountType) {

      return new AutoValue_StorageServiceParams(name, label, location, accountType);
   }
}
