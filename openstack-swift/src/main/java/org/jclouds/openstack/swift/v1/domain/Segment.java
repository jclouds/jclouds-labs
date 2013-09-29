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

import com.google.common.base.Objects;

/**
 * One piece of a multi-part upload.
 * 
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/static-large-objects.html">api
 *      doc</a>
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
    * {@code /container/objectName} which corresponds to the path of
    * {@link SwiftObject#uri()}.
    */
   public String path() {
      return path;
   }

   /**
    * Corresponds to the {@code ETag} header of the response, and is usually the
    * MD5 checksum of the object
    */
   public String etag() {
      return etag;
   }

   public long sizeBytes() {
      return size_bytes;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Segment) {
         Segment that = Segment.class.cast(object);
         return equal(path(), that.path()) //
               && equal(etag(), that.etag()) //
               && equal(sizeBytes(), that.sizeBytes());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(path(), etag(), sizeBytes());
   }

   @Override
   public String toString() {
      return toStringHelper("") //
            .add("path", path()) //
            .add("etag", etag()) //
            .add("sizeBytes", sizeBytes()).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String path;
      protected String etag;
      protected long sizeBytes;

      /**
       * @see Segment#path()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @see Segment#etag()
       */
      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      /**
       * @see Segment#sizeBytes()
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
