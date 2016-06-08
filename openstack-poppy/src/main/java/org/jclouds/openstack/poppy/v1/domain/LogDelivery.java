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
package org.jclouds.openstack.poppy.v1.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Representation of an OpenStack Poppy Service Logging.
 */
@AutoValue
public abstract class LogDelivery {

   /**
    * @return Specifies the delivery logging status
    */
   public abstract boolean getEnabled();

   @SerializedNames({ "enabled" })
   private static LogDelivery create(boolean enabled) {
      return builder().enabled(enabled).build();
   }

   public static Builder builder() {
      return new AutoValue_LogDelivery.Builder();
   }

   public Builder toBuilder() {
      return builder().enabled(getEnabled());
   }

   public static final class Builder {
      private boolean enabled;

      Builder() {
      }

      Builder(LogDelivery source) {
         enabled(source.getEnabled());
      }

      public LogDelivery.Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public LogDelivery build() {
         LogDelivery result = new AutoValue_LogDelivery(this.enabled);
         return result;
      }
   }
}
