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
package org.jclouds.compute.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class Volume implements Serializable {

   private static final long serialVersionUID = -4171587668537155633L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String type;
      private Float size;
      private String device;
      private boolean durable;
      private boolean bootDevice;

      public Builder id(final String id) {
         this.id = id;
         return this;
      }

      public Builder type(final String type) {
         this.type = type;
         return this;
      }

      public Builder size(final Float size) {
         this.size = size;
         return this;
      }

      public Builder device(final String device) {
         this.device = device;
         return this;
      }

      public Builder durable(final boolean durable) {
         this.durable = durable;
         return this;
      }

      public Builder bootDevice(final boolean bootDevice) {
         this.bootDevice = bootDevice;
         return this;
      }

      public Volume build() {
         return new Volume(id, type, size, device, durable, bootDevice);
      }

   }

   private final String id;
   private final String type;
   private final Float size;
   private final String device;
   private final boolean durable;
   private final boolean bootDevice;

   public Volume(String id, String type, Float size, String device, boolean durable, boolean bootDevice) {
      this.id = id;
      this.type = type;
      this.size = size;
      this.device = device;
      this.durable = durable;
      this.bootDevice = bootDevice;
   }

   public String getId() {
      return id;
   }

   public String getType() {
      return type;
   }

   public Float getSize() {
      return size;
   }

   public String getDevice() {
      return device;
   }

   public boolean isDurable() {
      return durable;
   }

   public boolean isBootDevice() {
      return bootDevice;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).add("type", type).add("size", size)
              .add("device", device).add("isDurable", durable).add("bootDevice", bootDevice).toString();
   }
}
