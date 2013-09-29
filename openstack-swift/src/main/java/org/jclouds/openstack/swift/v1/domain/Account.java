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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-account-metadata.html">api
 *      doc</a>
 */
public class Account {

   private final long containerCount;
   private final long objectCount;
   private final long bytesUsed;
   private final Map<String, String> metadata;

   // parsed from headers, so ConstructorProperties here would be misleading
   protected Account(long containerCount, long objectCount, long bytesUsed, Map<String, String> metadata) {
      this.containerCount = containerCount;
      this.objectCount = objectCount;
      this.bytesUsed = bytesUsed;
      this.metadata = checkNotNull(metadata, "metadata");
   }

   public long containerCount() {
      return containerCount;
   }

   public long objectCount() {
      return objectCount;
   }

   public long bytesUsed() {
      return bytesUsed;
   }

   /**
    * In current swift implementations, headers keys are lower-cased. This means
    * characters such as turkish will probably not work out well.
    */
   public Map<String, String> metadata() {
      return metadata;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Account) {
         Account that = Account.class.cast(object);
         return equal(containerCount(), that.containerCount()) //
               && equal(objectCount(), that.objectCount()) //
               && equal(bytesUsed(), that.bytesUsed()) //
               && equal(metadata(), that.metadata());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(containerCount(), objectCount(), bytesUsed());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("") //
            .add("containerCount", containerCount()) //
            .add("objectCount", objectCount()) //
            .add("bytesUsed", bytesUsed()) //
            .add("metadata", metadata());
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromContainer(this);
   }

   public static class Builder {
      protected long containerCount;
      protected long objectCount;
      protected long bytesUsed;
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @see Account#containerCount()
       */
      public Builder containerCount(long containerCount) {
         this.containerCount = containerCount;
         return this;
      }

      /**
       * @see Account#objectCount()
       */
      public Builder objectCount(long objectCount) {
         this.objectCount = objectCount;
         return this;
      }

      /**
       * @see Account#bytesUsed()
       */
      public Builder bytesUsed(long bytesUsed) {
         this.bytesUsed = bytesUsed;
         return this;
      }

      /**
       * Will lower-case all metadata keys due to a swift implementation
       * decision.
       * 
       * @see Account#metadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (Entry<String, String> entry : checkNotNull(metadata, "metadata").entrySet()) {
            builder.put(entry.getKey().toLowerCase(), entry.getValue());
         }
         this.metadata = builder.build();
         return this;
      }

      public Account build() {
         return new Account(containerCount, objectCount, bytesUsed, metadata);
      }

      public Builder fromContainer(Account from) {
         return containerCount(from.containerCount())//
               .objectCount(from.objectCount())//
               .bytesUsed(from.bytesUsed()) //
               .metadata(from.metadata());
      }
   }
}
