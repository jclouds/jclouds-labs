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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/** To create a new operating system image. */
public final class OSImageParams {

   /** Specifies a name that is used to identify the image when you create a Virtual Machine. */
   public String name() {
      return name;
   }

   /** Specifies the friendly name of the image. */
   public String label() {
      return label;
   }

   /** Specifies the location of the vhd file for the image. */
   public URI mediaLink() {
      return mediaLink;
   }

   /** {@link OSImage#os() Os type} of the image. */
   public OSImage.Type os() {
      return os;
   }

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
      return new OSImageParams(name, label, mediaLink, os);
   }

   // TODO: Remove from here down with @AutoValue.
   private OSImageParams(String name, String label, URI mediaLink, OSImage.Type os) {
      this.name = checkNotNull(name, "name");
      this.label = checkNotNull(label, "label");
      this.mediaLink = checkNotNull(mediaLink, "mediaLink");
      this.os = checkNotNull(os, "os");
   }

   private final String name;
   private final String label;
   private final URI mediaLink;
   private final OSImage.Type os;

   @Override public int hashCode() {
      return Objects.hashCode(name, label, mediaLink, os);
   }

   @Override public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof OSImageParams) {
         OSImageParams that = OSImageParams.class.cast(object);
         return equal(name, that.name)
               && equal(label, that.label)
               && equal(mediaLink, that.mediaLink)
               && equal(os, that.os);
      } else {
         return false;
      }
   }

   @Override public String toString() {
      return toStringHelper(this)
            .add("name", name)
            .add("label", label)
            .add("mediaLink", mediaLink)
            .add("os", os).toString();
   }
}
