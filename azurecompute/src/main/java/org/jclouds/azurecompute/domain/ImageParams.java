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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.azurecompute.domain.Image.OSType;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

/**
 * The Add OS Image operation adds an OS image that is currently stored in a storage account in your
 * subscription to the image repository.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
public class ImageParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static class Builder {
      private String label;
      private URI mediaLink;
      private OSType os;
      private String name;

      /**
       * @see ImageParams#getLabel()
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      /**
       * @see ImageParams#getMediaLink()
       */
      public Builder mediaLink(URI mediaLink) {
         this.mediaLink = mediaLink;
         return this;
      }

      /**
       * @see ImageParams#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ImageParams#getOS()
       */
      public Builder os(OSType os) {
         this.os = os;
         return this;
      }

      public ImageParams build() {
         return new ImageParams(label, mediaLink, name, os);
      }

      public Builder fromImageParams(ImageParams in) {
         return this.label(in.getLabel()).mediaLink(in.getMediaLink()).name(in.getName()).os(in.getOS());
      }
   }

   private final String label;
   private final URI mediaLink;
   private final String name;
   private final OSType os;

   private ImageParams(String label, URI mediaLink, String name, OSType os) {
      this.label = checkNotNull(label, "label");
      this.name = checkNotNull(name, "name for %s", label);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", label);
      this.os = checkNotNull(os, "os for %s", label);
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the hosted service. This name is the DNS prefix name and can be used to access the
    * hosted service.
    *
    * For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public String getName() {
      return name;
   }

   /**
    * The location of the blob in the blob store in which the media for the image is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   public URI getMediaLink() {
      return mediaLink;
   }

   /**
    * The description of the image.
    */
   public String getLabel() {
      return label;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ImageParams other = (ImageParams) obj;
      return Objects.equal(this.name, other.name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   private ToStringHelper string() {
      return MoreObjects.toStringHelper(this).add("label", label).add("mediaLink", mediaLink).add("name", name)
               .add("os", os);
   }
}
