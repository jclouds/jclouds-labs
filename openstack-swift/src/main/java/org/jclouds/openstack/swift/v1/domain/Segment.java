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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi;

import com.google.common.base.Objects;

/**
 * Represents a single segment of a multi-part upload.
 * 
 * @author Adrian Cole
 * @author Jeremy Daggett
 * 
 * @see StaticLargeObjectApi
 */
public class Segment {

   private final String path;
   private final String etag;
   private final long size_bytes;

   private Segment(String path, String etag, long sizeBytes) {
      this.path = checkNotNull(path, "path");
      this.etag = checkNotNull(etag, "etag of %s", path);
      this.size_bytes = checkNotNull(sizeBytes, "sizeBytes of %s", path);
   }

   /**
    * @return The container and object name in the format: {@code <container-name>/<object-name>}
    */
   public String getPath() {
      return path;
   }

   /**
    * @return The ETag of the content of the segment object.
    */
   public String getEtag() {
      return etag;
   }

   /**
    * @return The size of the segment object.
    */
   public long getSizeBytes() {
      return size_bytes;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Segment) {
         Segment that = Segment.class.cast(object);
         return equal(getPath(), that.getPath())
               && equal(getEtag(), that.getEtag())
               && equal(getSizeBytes(), that.getSizeBytes());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getPath(), getEtag(), getSizeBytes());
   }

   @Override
   public String toString() {
      return toStringHelper("")
            .add("path", getPath())
            .add("etag", getEtag())
            .add("sizeBytes", getSizeBytes()).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String path;
      protected String etag;
      protected long sizeBytes;

      /**
       * @see Segment#getPath()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @see Segment#getEtag()
       */
      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      /**
       * @see Segment#getSizeBytes()
       */
      public Builder sizeBytes(long sizeBytes) {
         this.sizeBytes = sizeBytes;
         return this;
      }

      public Segment build() {
         return new Segment(path, etag, sizeBytes);
      }
   }
}
