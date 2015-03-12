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
 * To update an affinity group.
 */
@AutoValue
public abstract class UpdateAffinityGroupParams {

   UpdateAffinityGroupParams() {
   } // For AutoValue only!

   /**
    * Specifies the base-64-encoded identifier of the affinity group.
    *
    * @return the identifier of the affinity group
    */
   @Nullable
   public abstract String label();

   /**
    * Specified the description of this affinity group.
    *
    * @return the description of this affinity group
    */
   @Nullable
   public abstract String description();

   public Builder toBuilder() {
      return builder().fromUpdateAffinityGroupParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String label;

      private String description;

      public Builder label(final String label) {
         this.label = label;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public UpdateAffinityGroupParams build() {
         return UpdateAffinityGroupParams.create(label, description);
      }

      public Builder fromUpdateAffinityGroupParams(final UpdateAffinityGroupParams in) {
         return label(in.label()).
                 description(in.description());
      }
   }

   private static UpdateAffinityGroupParams create(final String label, final String description) {
      return new AutoValue_UpdateAffinityGroupParams(label, description);
   }
}
