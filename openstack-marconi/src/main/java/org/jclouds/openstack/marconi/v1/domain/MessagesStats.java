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

import java.beans.ConstructorProperties;

/**
 * Statistics on messages in this queue.
 */
public class MessagesStats {

   private final int claimed;
   private final int free;
   private final int total;
   private final Aged oldest;
   private final Aged newest;

   @ConstructorProperties({
         "claimed", "free", "total", "oldest", "newest"
   })
   protected MessagesStats(int claimed, int free, int total, @Nullable Aged oldest, @Nullable Aged newest) {
      this.claimed = claimed;
      this.free = free;
      this.total = total;
      this.oldest = oldest;
      this.newest = newest;
   }

   /**
    * @return The number of claimed messages in this queue.
    */
   public int getClaimed() {
      return claimed;
   }

   /**
    * @return The number of free messages in this queue.
    */
   public int getFree() {
      return free;
   }

   /**
    * @return The total number of messages in this queue.
    */
   public int getTotal() {
      return total;
   }

   /**
    * @return Statistics on the oldest messages in this queue. If the value of total is 0, then oldest is not present.
    */
   public Optional<Aged> getOldest() {
      return Optional.fromNullable(oldest);
   }

   /**
    * @return Statistics on the newest messages in this queue. If the value of total is 0, then newest is not present.
    */
   public Optional<Aged> getNewest() {
      return Optional.fromNullable(newest);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(claimed, free, total, oldest, newest);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MessagesStats that = MessagesStats.class.cast(obj);
      return Objects.equal(this.claimed, that.claimed) 
            && Objects.equal(this.free, that.free)
            && Objects.equal(this.total, that.total)
            && Objects.equal(this.oldest, that.oldest)
            && Objects.equal(this.newest, that.newest);
   }

   protected MoreObjects.ToStringHelper string() {
      return MoreObjects.toStringHelper(this).omitNullValues()
         .add("claimed", claimed).add("free", free).add("total", total).add("oldest", oldest).add("newest", newest);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromMessagesStats(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected int claimed;
      protected int free;
      protected int total;
      protected Aged oldest;
      protected Aged newest;

      /**
       * @see MessagesStats#getClaimed()
       */
      public Builder claimed(int claimed) {
         this.claimed = claimed;
         return self();
      }

      /**
       * @see MessagesStats#getFree()
       */
      public Builder free(int free) {
         this.free = free;
         return self();
      }

      /**
       * @see MessagesStats#getTotal()
       */
      public Builder total(int total) {
         this.total = total;
         return self();
      }

      /**
       * @see MessagesStats#getOldest()
       */
      public Builder oldest(Aged oldest) {
         this.oldest = oldest;
         return self();
      }

      /**
       * @see MessagesStats#getNewest()
       */
      public Builder newest(Aged newest) {
         this.newest = newest;
         return self();
      }

      public MessagesStats build() {
         return new MessagesStats(claimed, free, total, oldest, newest);
      }

      public Builder fromMessagesStats(MessagesStats in) {
         return this.claimed(in.getClaimed()).free(in.getFree()).total(in.getTotal()).oldest(in.getOldest().orNull())
               .newest(in.getNewest().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
