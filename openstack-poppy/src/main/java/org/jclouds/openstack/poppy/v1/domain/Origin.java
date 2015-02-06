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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Representation of an OpenStack Poppy Origin.
 */
@AutoValue
public abstract class Origin {
   /**
    * @see Builder#origin(String)
    */
   public abstract String getOrigin();

   /**
    * @see Builder#port(Integer)
    */
   @Nullable public abstract Integer getPort();

   /**
    * @see Builder#sslEnabled(Boolean)
    */
   @Nullable public abstract Boolean getSslEnabled();

   /**
    * @see Builder#rules(List)
    */
   @Nullable public abstract List<CachingRule> getRules();

   @SerializedNames({ "origin", "port", "ssl", "rules" })
   private static Origin create(String origin, int port, boolean sslEnabled, List<CachingRule> rules) {
      return builder().origin(origin).port(port).sslEnabled(sslEnabled)
            .rules(rules).build();
   }

   public static Builder builder() {
      return new AutoValue_Origin.Builder();
   }
   public Builder toBuilder() {
      return builder()
            .origin(getOrigin())
            .port(getPort())
            .sslEnabled(getSslEnabled())
            .rules(getRules());
   }

   public static final class Builder {
      private String origin;
      private Integer port;
      private Boolean sslEnabled;
      private List<CachingRule> rules;
      Builder() {
      }
      Builder(Origin source) {
         origin(source.getOrigin());
         port(source.getPort());
         sslEnabled(source.getSslEnabled());
         rules(source.getRules());
      }

      /**
       * Required.
       * @param origin Specifies the URL or IP address from which to pull origin content. The minimum length for
       *               origin is 3. The maximum length is 253.
       * @return The Origin builder.
       */
      public Origin.Builder origin(String origin) {
         this.origin = origin;
         return this;
      }

      /**
       * Optional.
       * @param port Specifies the port used to access the origin. The default is port 80. Note: Rackspace CDN cannot
       *             be used with custom ports. Services are required to run on HTTP (80) and HTTPS (443) for the
       *             origin servers.
       * @return The Origin builder.
       */
      public Origin.Builder port(Integer port) {
         this.port = port;
         return this;
      }

      /**
       * Optional.
       * @param sslEnabled Uses HTTPS to access the origin. The default is false.
       * @return The Origin builder.
       */
      public Origin.Builder sslEnabled(Boolean sslEnabled) {
         this.sslEnabled = sslEnabled;
         return this;
      }

      /**
       * Required.
       * @param rules Specifies a collection of rules that define the conditions when this origin should be accessed.
       *              If there is more than one origin, the rules parameter is required.
       * @return The Origin builder.
       */
      public Origin.Builder rules(List<CachingRule> rules) {
         this.rules = rules;
         return this;
      }

      public Origin build() {
         String missing = "";
         if (origin == null) {
            missing += " origin";
         }
         if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties:" + missing);
         }
         Origin result = new AutoValue_Origin(
               this.origin,
               this.port,
               this.sslEnabled,
               rules != null ? ImmutableList.copyOf(this.rules) : null);
         return result;
      }
   }
}
