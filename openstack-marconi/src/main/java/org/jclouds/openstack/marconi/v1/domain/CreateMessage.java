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
import org.jclouds.domain.JsonBall;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message to be sent to a queue.
 */
public class CreateMessage {

   private int ttl;
   private String body;

   protected CreateMessage(int ttl, String body) {
      this.ttl = ttl;
      this.body = checkNotNull(body, "body required");
   }

   /**
    * @see Builder#ttl(int)
    */
   public int getTTL() {
      return ttl;
   }

   /**
    * @see Builder#body(String)
    */
   public String getBody() {
      return body;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ttl, body);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CreateMessage that = CreateMessage.class.cast(obj);
      return Objects.equal(this.ttl, that.ttl) && Objects.equal(this.body, that.body);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("ttl", ttl).add("body", body);
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

      protected int ttl;
      protected String body;

      /**
       * @param ttl The time-to-live of the message in seconds. The ttl attribute specifies how long the server waits
       *            before marking the message as expired and removing it from the queue. The valid range of values for
       *            the ttl are configurable by your cloud provider. Consult your cloud provider documentation to learn
       *            the valid range.
       *            </p>
       *            Note that the server might not actually delete the message until its age has reached up to
       *            (ttl + 60) seconds, to allow for flexibility in storage implementations.
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return self();
      }

      /**
       * @param json Specifies an arbitrary JSON document that constitutes the body of the message being sent.
       *             The size of the message allowed in one message is configurable by your cloud provider. Consult
       *             your cloud provider documentation to learn the valid range.
       */
      public Builder body(String json) {
         checkNotNull(json, "body required");
         this.body = new JsonBall(json).toString();
         return self();
      }

      public CreateMessage build() {
         return new CreateMessage(ttl, body);
      }

      public Builder fromMessage(CreateMessage in) {
         return this.ttl(in.getTTL()).body(in.getBody());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
