/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message to be sent to a queue.
 *
 * @author Everett Toews
 */
public class Message {

   private final String id;
   private final int ttl;
   private final String body;
   private final int age;

   protected Message(String id, int ttl, String body, int age) {
      this.id = checkNotNull(id, "id required");
      this.ttl = ttl;
      this.body = checkNotNull(body, "body required");
      this.age = age;
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
    * @see CreateMessage.Builder#body(String)
    */
   public String getBody() {
      return body;
   }

   /**
    * @return Age of this message in seconds.
    */
   public int getAge() {
      return age;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Message that = Message.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("id", id).add("ttl", ttl).add("body", body).add("age", age);
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
      protected String body;
      protected int age;

      /**
       * @see Message#getId()
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
       * @see CreateMessage.Builder#body(String)
       */
      public Builder body(String json) {
         this.body = json;
         return self();
      }

      /**
       * @see Message#getAge()
       */
      public Builder age(int age) {
         this.age = age;
         return self();
      }

      public Message build() {
         return new Message(id, ttl, body, age);
      }

      public Builder fromMessage(Message in) {
         return this.id(in.getId()).ttl(in.getTTL()).body(in.getBody()).age(in.getAge());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
