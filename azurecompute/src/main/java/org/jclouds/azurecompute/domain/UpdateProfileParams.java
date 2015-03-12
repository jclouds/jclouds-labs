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
 * The Create Profile operation creates a new profile for a status version, owned by the specified subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758254.aspx">docs</a>
 */
@AutoValue
public abstract class UpdateProfileParams {

   UpdateProfileParams() {
   } // For AutoValue only!

   /**
    * Specifies whether the profile should be enabled or disabled. If there is a currently enabled profile, it is
    * disabled. Possible values are: Enabled, Disabled;
    *
    * @return profile status.
    */
   public abstract ProfileDefinition.Status status();

   /**
    * This element is not used.
    *
    * @return profile definition version.
    */
   @Nullable
   public abstract String version();

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private ProfileDefinition.Status status;
      private String version;

      public Builder status(final ProfileDefinition.Status status) {
         this.status = status;
         return this;
      }

      public Builder version(final String version) {
         this.version = version;
         return this;
      }

      public UpdateProfileParams build() {
         return UpdateProfileParams.create(status, version);
      }

      public Builder fromImageParams(final UpdateProfileParams in) {
         return status(in.status()).version(in.version());
      }
   }

   private static UpdateProfileParams create(
           final ProfileDefinition.Status status,
           final String version) {
      return new AutoValue_UpdateProfileParams(status, version);
   }
}
