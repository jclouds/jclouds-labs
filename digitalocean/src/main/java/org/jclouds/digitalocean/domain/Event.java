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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.primitives.Ints.tryParse;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.inject.name.Named;

/**
 * An Event.
 */
public class Event {

   public enum Status {
      DONE, PENDING, ERROR;

      public static Status fromValue(String value) {
         // DigitalOcean return a 'null' status when the operation is still in
         // progress
         if (value == null) {
            return PENDING;
         }
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   private final int id;
   @Named("action_status")
   private final Status status;
   @Named("event_type_id")
   private final int typeId;
   private final Integer percentage;
   @Named("droplet_id")
   private final int dropletId;

   @ConstructorProperties({ "id", "action_status", "event_type_id", "percentage", "droplet_id" })
   public Event(int id, @Nullable Status status, int typeId, @Nullable String percentage, int dropletId) {
      this.id = id;
      this.status = status == null ? Status.PENDING : status;
      this.typeId = typeId;
      this.percentage = percentage == null ? null : tryParse(percentage);
      this.dropletId = dropletId;
   }

   public int getId() {
      return id;
   }

   public Status getStatus() {
      return status;
   }

   public int getTypeId() {
      return typeId;
   }

   public Integer getPercentage() {
      return percentage;
   }

   public int getDropletId() {
      return dropletId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + dropletId;
      result = prime * result + id;
      result = prime * result + (percentage == null ? 0 : percentage.hashCode());
      result = prime * result + (status == null ? 0 : status.hashCode());
      result = prime * result + typeId;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Event other = (Event) obj;
      if (dropletId != other.dropletId) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (percentage == null) {
         if (other.percentage != null) {
            return false;
         }
      } else if (!percentage.equals(other.percentage)) {
         return false;
      }
      if (status != other.status) {
         return false;
      }
      if (typeId != other.typeId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Event [id=" + id + ", status=" + status + ", typeId=" + typeId + ", percentage=" + percentage
            + ", dropletId=" + dropletId + "]";
   }

}
