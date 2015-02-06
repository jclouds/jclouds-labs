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
 * Representation of an OpenStack Poppy Caching Rule.
 */
@AutoValue
public abstract class Caching {
   /**
    * @see Builder#name(String)
    */
   public abstract String getName();

   /**
    * @see Builder#ttl(int)
    */
   public abstract int getTtl();

   /**
    * @see Builder#rules(List)
    */
   @Nullable public abstract List<CachingRule> getRules();

   @SerializedNames({ "name", "ttl", "rules" })
   static Caching create(String name, int ttl, List<CachingRule> rules) {
      return builder().name(name).ttl(ttl).rules(rules).build();
   }

   public static Builder builder() {
      return new Builder();
   }
   public Builder toBuilder() {
      return builder()
            .name(getName())
            .ttl(getTtl())
            .rules(getRules());
   }

   public static final class Builder {
      private String name;
      private Integer ttl;
      private List<CachingRule> rules;
      Builder() {
      }
      Builder(Caching source) {
         name(source.getName());
         ttl(source.getTtl());
         rules(source.getRules());
      }

      /**
       * Required.
       * @param name Specifies the name of this caching rule. The minimum length for name is 1. The maximum length is
       *             256. Note: default is a reserved name used for the default TTL setting.
       * @return The Caching builder.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * Required.
       * @param ttl Specifies the TTL to apply. The value of ttl must be greater than or equal to 0.
       * @return The Caching builder.
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * Optional.
       * @param rules Specifies a collection of rules that determine if this TTL should be applied to an asset.
       *              Note: This is a required property if more than one entry is present for caching.
       * @return The Caching builder.
       */
      public Builder rules(List<CachingRule> rules) {
         this.rules = rules;
         return this;
      }

      public Caching build() {
         String missing = "";
         if (name == null) {
            missing += " name";
         }
         if (ttl == null) {
            missing += " ttl";
         }
         if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties:" + missing);
         }
         Caching result = new AutoValue_Caching(
               this.name,
               this.ttl,
               rules != null ? ImmutableList.copyOf(this.rules) : null);
         return result;
      }
   }
}
