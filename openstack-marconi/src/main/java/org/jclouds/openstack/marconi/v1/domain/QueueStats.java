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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Queue statistics, including how many messages are in the queue.
 */
public class QueueStats {

   private final MessagesStats messages;

   protected QueueStats(MessagesStats messageStats) {
      this.messages = checkNotNull(messageStats, "messageStats required");
   }

   /**
    * @return The statistics of the messages in this queue.
    */
   public MessagesStats getMessagesStats() {
      return messages;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(messages);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      QueueStats that = QueueStats.class.cast(obj);
      return Objects.equal(this.messages, that.messages);
   }

   protected MoreObjects.ToStringHelper string() {
      return MoreObjects.toStringHelper(this)
         .add("messagesStats", messages);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromQueueStats(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected MessagesStats messagesStats;

      /**
       * @see QueueStats#getMessagesStats()
       */
      public Builder messageStats(MessagesStats messagesStats) {
         this.messagesStats = messagesStats;
         return self();
      }

      public QueueStats build() {
         return new QueueStats(messagesStats);
      }

      public Builder fromQueueStats(QueueStats in) {
         return this.messageStats(in.getMessagesStats());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
