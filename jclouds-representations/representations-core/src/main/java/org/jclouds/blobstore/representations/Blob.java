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
package org.jclouds.blobstore.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class Blob implements Serializable {

   private static final long serialVersionUID = 6035812193926955340L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Map<String, Collection<String>> allHeaders;
      private BlobMetadata blobMetadata;

      public Builder allHeaders(final Map<String, Collection<String>> allHeaders) {
         this.allHeaders = allHeaders;
         return this;
      }

      public Builder blobMetadata(final BlobMetadata blobMetadata) {
         this.blobMetadata = blobMetadata;
         return this;
      }

      public Blob build() {
         return new Blob(allHeaders, blobMetadata);
      }
   }

   private final Map<String, Collection<String>> allHeaders;
   private final BlobMetadata blobMetadata;

   public Blob(Map<String, Collection<String>> allHeaders, BlobMetadata blobMetadata) {
      this.allHeaders = allHeaders;
      this.blobMetadata = blobMetadata;
   }

   public Map<String, Collection<String>> getAllHeaders() {
      return allHeaders;
   }

   public BlobMetadata getBlobMetadata() {
      return blobMetadata;
   }

   public int hashCode() {
      return Objects.hashCode(blobMetadata, allHeaders);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("blobMetadata", blobMetadata).add("allHeaders", allHeaders)
              .toString();
   }
}
