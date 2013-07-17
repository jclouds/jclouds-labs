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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/s_listcontainers.html">api
 *      doc</a>
 */
public class Container implements Comparable<Container> {

   private final String name;
   private final long objectCount;
   private final long bytesUsed;

   @ConstructorProperties({ "name", "count", "bytes" })
   protected Container(String name, long objectCount, long bytesUsed) {
      this.name = checkNotNull(name, "name");
      this.objectCount = objectCount;
      this.bytesUsed = bytesUsed;
   }

   public String name() {
      return name;
   }

   public long objectCount() {
      return objectCount;
   }

   public long bytesUsed() {
      return bytesUsed;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Container) {
         final Container that = Container.class.cast(object);
         return equal(name(), that.name()) //
               && equal(objectCount(), that.objectCount()) //
               && equal(bytesUsed(), that.bytesUsed());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name(), objectCount(), bytesUsed());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("") //
            .add("name", name()) //
            .add("objectCount", objectCount()) //
            .add("bytesUsed", bytesUsed());
   }

   @Override
   public int compareTo(Container that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.name().compareTo(that.name());
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromContainer(this);
   }

   public static class Builder {
      protected String name;
      protected long objectCount;
      protected long bytesUsed;

      /**
       * @see Container#name()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Container#objectCount()
       */
      public Builder objectCount(long objectCount) {
         this.objectCount = objectCount;
         return this;
      }

      /**
       * @see Container#bytesUsed()
       */
      public Builder bytesUsed(long bytesUsed) {
         this.bytesUsed = bytesUsed;
         return this;
      }

      public Container build() {
         return new Container(name, objectCount, bytesUsed);
      }

      public Builder fromContainer(Container from) {
         return name(from.name()) //
               .objectCount(from.objectCount()) //
               .bytesUsed(from.bytesUsed());
      }
   }
}
