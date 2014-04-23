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
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Represents a Container in OpenStack Object Storage.
 * 
 * @see org.jclouds.openstack.swift.v1.features.ContainerApi
 */
public class Container implements Comparable<Container> {

   private final String name;
   private final long objectCount;
   private final long bytesUsed;
   private final Optional<Boolean> anybodyRead;
   private final Map<String, String> metadata;

   @ConstructorProperties({ "name", "count", "bytes", "anybodyRead", "metadata" })
   protected Container(String name, long objectCount, long bytesUsed, Optional<Boolean> anybodyRead,
         Map<String, String> metadata) {
      this.name = checkNotNull(name, "name");
      this.objectCount = objectCount;
      this.bytesUsed = bytesUsed;
      this.anybodyRead = anybodyRead == null ? Optional.<Boolean> absent() : anybodyRead;
      this.metadata = metadata == null ? ImmutableMap.<String, String> of() : metadata;
   }

   /**
    * @return The name of this container.
    */
   public String getName() {
      return name;
   }

   /**
    * @return The count of objects for this container.
    */
   public long getObjectCount() {
      return objectCount;
   }

   /**
    * @return The number of bytes used by this container.
    */
   public long getBytesUsed() {
      return bytesUsed;
   }

   /**
    * Absent except in {@link ContainerApi#get(String) Get Container} commands.
    * 
    * @return true  if this container is publicly readable, false otherwise.
    * 
    * @see org.jclouds.openstack.swift.v1.options.CreateContainerOptions#anybodyRead()
    */
   public Optional<Boolean> getAnybodyRead() {
      return anybodyRead;
   }

   /**
    * <h3>NOTE</h3>
    * In current swift implementations, headers keys are lower-cased. This means
    * characters such as turkish will probably not work out well.
    * 
    * @return a {@code Map<String, String>} containing this container's metadata.
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Container) {
         final Container that = Container.class.cast(object);
         return equal(getName(), that.getName())
               && equal(getObjectCount(), that.getObjectCount())
               && equal(getBytesUsed(), that.getBytesUsed())
               && equal(getMetadata(), that.getMetadata());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getName(), getObjectCount(), getBytesUsed(), getAnybodyRead(), getMetadata());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues()
            .add("name", getName())
            .add("objectCount", getObjectCount())
            .add("bytesUsed", getBytesUsed())
            .add("anybodyRead", getAnybodyRead().orNull())
            .add("metadata", getMetadata());
   }

   @Override
   public int compareTo(Container that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getName().compareTo(that.getName());
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
      protected Optional<Boolean> anybodyRead = Optional.absent();
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @see Container#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Container#getObjectCount()
       */
      public Builder objectCount(long objectCount) {
         this.objectCount = objectCount;
         return this;
      }

      /**
       * @see Container#getBytesUsed()
       */
      public Builder bytesUsed(long bytesUsed) {
         this.bytesUsed = bytesUsed;
         return this;
      }

      /**
       * @see Container#getAnybodyRead()
       */
      public Builder anybodyRead(Boolean anybodyRead) {
         this.anybodyRead = Optional.fromNullable(anybodyRead);
         return this;
      }

      /**
       * <h3>NOTE</h3>
       * This method will lower-case all metadata keys.
       * 
       * @see Container#getMetadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (Entry<String, String> entry : checkNotNull(metadata, "metadata").entrySet()) {
            builder.put(entry.getKey().toLowerCase(), entry.getValue());
         }
         this.metadata = builder.build();
         return this;
      }

      public Container build() {
         return new Container(name, objectCount, bytesUsed, anybodyRead, metadata);
      }

      public Builder fromContainer(Container from) {
         return name(from.getName())
               .objectCount(from.getObjectCount())
               .bytesUsed(from.getBytesUsed())
               .anybodyRead(from.getAnybodyRead().orNull())
               .metadata(from.getMetadata());
      }
   }
}
