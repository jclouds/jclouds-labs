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
 * Representation of an OpenStack Poppy Access Restriction.
 */
@AutoValue
public abstract class Restriction {
   /**
    * @see Builder#name(String)
    */
   public abstract String getName();

   /**
    * @see Builder#rules(List)
    */
   @Nullable public abstract List<RestrictionRule> getRules();

   @SerializedNames({ "name", "rules" })
   private static Restriction create(String name, List<RestrictionRule> rules) {
      return builder().name(name).rules(rules).build();
   }

   public static Builder builder() {
      return new AutoValue_Restriction.Builder();
   }
   public Builder toBuilder(){
      return builder()
            .name(getName())
            .rules(getRules());
   }

   public static final class Builder {
      private String name;
      private List<RestrictionRule> rules;
      Builder() {
      }
      Builder(Restriction source) {
         name(source.getName());
         rules(source.getRules());
      }

      /**
       * Required.
       * @param name Specifies the name of this restriction. The minimum length for name is 1.
       *             The maximum length is 256.
       * @return The Restriction builder.
       */
      public Restriction.Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * Optional.
       * @param rules Specifies a collection of rules that determine if this restriction should be applied to an asset.
       * @return The Restriction builder.
       */
      public Restriction.Builder rules(List<RestrictionRule> rules) {
         this.rules = rules;
         return this;
      }

      public Restriction build() {
         String missing = "";
         if (name == null) {
            missing += " name";
         }
         if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required properties:" + missing);
         }
         Restriction result = new AutoValue_Restriction(
               this.name,
               rules != null ? ImmutableList.copyOf(this.rules) : null);
         return result;
      }
   }
}
