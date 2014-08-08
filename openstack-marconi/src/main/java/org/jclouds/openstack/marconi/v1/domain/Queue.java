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

package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.jclouds.javax.annotation.Nullable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A queue.
 */
public class Queue {

   private final String name;
   private final Map<String, String> metadata;

   protected Queue(String name, @Nullable Map<String, String> metadata) {
      this.name = checkNotNull(name, "name required");
      this.metadata = metadata;
   }

   /**
    * @return The name of this queue.
    */
   public String getName() {
      return name;
   }

   /**
    * @return The key/value metadata for this queue.
    */
   public Optional<Map<String, String>> getMetadata() {
      return Optional.fromNullable(metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Queue that = Queue.class.cast(obj);
      return Objects.equal(this.name, that.name);
   }

   protected MoreObjects.ToStringHelper string() {
      return MoreObjects.toStringHelper(this).omitNullValues()
         .add("name", name).add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromMessage(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String name;
      protected Map<String, String> metadata;

      /**
       * @param name The name of this queue.
       */
      public Builder name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @param metadata The key/value metadata for this queue.
       */
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = metadata;
         return self();
      }

      public Queue build() {
         return new Queue(name, metadata);
      }

      public Builder fromMessage(Queue in) {
         return this.name(in.getName()).metadata(in.getMetadata().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
