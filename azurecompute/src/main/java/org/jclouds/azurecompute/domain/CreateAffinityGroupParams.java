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

/**
 * To create a new affinity group.
 */
@AutoValue
public abstract class CreateAffinityGroupParams {

   CreateAffinityGroupParams() {
   } // For AutoValue only!

   /**
    * Specifies the name of the affinity group.
    *
    * @return the name of the affinity group
    */
   public abstract String name();

   /**
    * Specifies the base-64-encoded identifier of the affinity group.
    *
    * @return the identifier of the affinity group
    */
   public abstract String label();

   /**
    * Specified the description of this affinity group.
    *
    * @return the description of this affinity group
    */
   @Nullable
   public abstract String description();

   /**
    * Specifies the data center in which the affinity group is located.
    *
    * @return the data center in which the affinity group is located
    */
   public abstract String location();

   public Builder toBuilder() {
      return builder().fromAffinityGroupParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String name;

      private String label;

      private String description;

      private String location;

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder label(final String label) {
         this.label = label;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder location(final String location) {
         this.location = location;
         return this;
      }

      public CreateAffinityGroupParams build() {
         return CreateAffinityGroupParams.create(name, label, description, location);
      }

      public Builder fromAffinityGroupParams(final CreateAffinityGroupParams in) {
         return name(in.name())
                 .label(in.label())
                 .description(in.description())
                 .location(in.location());
      }
   }

   private static CreateAffinityGroupParams create(
           final String name, final String label, final String description, final String location) {

      return new AutoValue_CreateAffinityGroupParams(name, label, description, location);
   }
}
