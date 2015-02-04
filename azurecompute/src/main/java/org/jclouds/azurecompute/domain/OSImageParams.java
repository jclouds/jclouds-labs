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

import java.net.URI;

import com.google.auto.value.AutoValue;

/** To create a new operating system image. */
@AutoValue
public abstract class OSImageParams {

   OSImageParams() {} // For AutoValue only!

   /** Specifies a name that is used to identify the image when you create a Virtual Machine. */
   public abstract String name();

   /** Specifies the friendly name of the image. */
   public abstract String label();

   /** Specifies the location of the vhd file for the image. */
   public abstract URI mediaLink();

   /** {@link OSImage#os() Os type} of the image. */
   public abstract OSImage.Type os();

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private String name;
      private String label;
      private URI mediaLink;
      private OSImage.Type os;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder label(String label) {
         this.label = label;
         return this;
      }

      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = mediaLink;
         return this;
      }

      public Builder os(OSImage.Type os) {
         this.os = os;
         return this;
      }

      public OSImageParams build() {
         return OSImageParams.create(name, label, mediaLink, os);
      }

      public Builder fromImageParams(OSImageParams in) {
         return name(in.name())
               .label(in.label())
               .mediaLink(in.mediaLink())
               .os(in.os());
      }
   }

   private static OSImageParams create(String name, String label, URI mediaLink, OSImage.Type os) {
      return new AutoValue_OSImageParams(name, label, mediaLink, os);
   }
}
