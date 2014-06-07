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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.inject.name.Named;

/**
 * An DropletCreation response.
 */
public class DropletCreation {

   private final int id;
   private final String name;
   @Named("image_id")
   private final int imageId;
   @Named("size_id")
   private final int sizeId;
   @Named("event_id")
   private final int eventId;

   @ConstructorProperties({ "id", "name", "image_id", "size_id", "event_id" })
   public DropletCreation(int id, String name, int imageId, int sizeId, int eventId) {
      this.id = id;
      this.name = checkNotNull(name, "name cannot be null");
      this.imageId = imageId;
      this.sizeId = sizeId;
      this.eventId = eventId;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getImageId() {
      return imageId;
   }

   public int getSizeId() {
      return sizeId;
   }

   public int getEventId() {
      return eventId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + eventId;
      result = prime * result + id;
      result = prime * result + imageId;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + sizeId;
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
      DropletCreation other = (DropletCreation) obj;
      if (eventId != other.eventId) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (imageId != other.imageId) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (sizeId != other.sizeId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "DropletCreation [id=" + id + ", name=" + name + ", imageId=" + imageId + ", sizeId=" + sizeId
            + ", eventId=" + eventId + "]";
   }

}
