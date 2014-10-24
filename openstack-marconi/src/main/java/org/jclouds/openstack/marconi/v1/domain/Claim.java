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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A claim for messages in a queue.
 */
public class Claim {

   private final String id;
   private final int ttl;
   private final int age;
   private final List<Message> messages;

   protected Claim(String id, int ttl, int age, @Nullable List<Message> messages) {
      this.id = checkNotNull(id, "id required");
      this.ttl = ttl;
      this.age = age;
      this.messages = messages == null ? ImmutableList.<Message>of() : messages;
   }

   /**
    * @return The id of this message.
    */
   public String getId() {
      return id;
   }

   /**
    * @see CreateMessage.Builder#ttl(int)
    */
   public int getTTL() {
      return ttl;
   }

   /**
    * @return Age of this message in seconds.
    */
   public int getAge() {
      return age;
   }

   /**
    * @return The messages that are associated with this claim.
    */
   public List<Message> getMessages() {
      return messages;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Claim that = Claim.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("id", id).add("ttl", ttl).add("age", age).add("messages", messages);
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

      protected String id;
      protected int ttl;
      protected int age;
      protected List<Message> messages;

      /**
       * @see Claim#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see CreateMessage.Builder#ttl(int)
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return self();
      }

      /**
       * @see Claim#getAge()
       */
      public Builder age(int age) {
         this.age = age;
         return self();
      }

      /**
       * @see Claim#getMessages()
       */
      public Builder messages(List<Message> messages) {
         this.messages = messages;
         return self();
      }

      public Claim build() {
         return new Claim(id, ttl, age, messages);
      }

      public Builder fromMessage(Claim in) {
         return this.id(in.getId()).ttl(in.getTTL()).age(in.getAge()).messages(in.getMessages());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
