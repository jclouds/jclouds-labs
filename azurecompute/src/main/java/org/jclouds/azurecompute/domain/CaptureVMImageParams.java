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
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn499768.aspx" >api</a>
 */
@AutoValue
public abstract class CaptureVMImageParams {

   public abstract VMImage.OSDiskConfiguration.OSState osState();

   public abstract String name();

   public abstract String label();

   @Nullable public abstract String description();

   @Nullable public abstract String language();

   @Nullable public abstract String imageFamily();

   @Nullable public abstract RoleSize.Type recommendedVMSize();

   public Builder toBuilder() {
      return builder().fromVMImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private VMImage.OSDiskConfiguration.OSState osState;
      private String name;
      private String label;
      private String description;
      private String language;
      private String imageFamily;
      private RoleSize.Type recommendedVMSize;

      public Builder osState(VMImage.OSDiskConfiguration.OSState osState) {
         this.osState = osState;
         return this;
      }

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

      public Builder fromVMImageParams(CaptureVMImageParams in) {
         return name(in.name())
               .label(in.label())
               .osState(in.osState())
               .description(in.description())
               .language(in.language())
               .imageFamily(in.imageFamily())
               .recommendedVMSize(in.recommendedVMSize());

      }

      public CaptureVMImageParams build() {
         return CaptureVMImageParams.create(osState, name, label, description, language,
               imageFamily, recommendedVMSize);
      }

   }

   public static CaptureVMImageParams create(VMImage.OSDiskConfiguration.OSState osState, String name, String label,
         String description, String language, String imageFamily, RoleSize.Type recommendedVMSize) {
      return new AutoValue_CaptureVMImageParams(osState, name, label, description, language, imageFamily,
            recommendedVMSize);
   }
}
