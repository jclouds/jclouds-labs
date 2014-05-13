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

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The response to creating messages on a queue.
 *
 * @author Everett Toews
 */
public class MessagesCreated {

   @Named("resources")
   private final List<String> messageIds;

   @ConstructorProperties({
         "resources"
   })
   protected MessagesCreated(List<String> messageIds) {
      this.messageIds = checkNotNull(messageIds, "messageIds required");
   }

   /**
    * @return A list of message ids that correspond to each message submitted in the request, in order.
    */
   public List<String> getMessageIds() {
      return messageIds;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(messageIds);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MessagesCreated that = MessagesCreated.class.cast(obj);
      return Objects.equal(this.messageIds, that.messageIds);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("messageIds", messageIds);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromMessageCreated(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected List<String> messageIds;

      /**
       * @see MessagesCreated#getMessageIds()
       */
      public Builder messageIds(List<String> messageIds) {
         this.messageIds = messageIds;
         return self();
      }

      public MessagesCreated build() {
         return new MessagesCreated(messageIds);
      }

      public Builder fromMessageCreated(MessagesCreated in) {
         return this.messageIds(in.getMessageIds());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
