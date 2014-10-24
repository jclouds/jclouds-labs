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
package org.jclouds.rackspace.autoscale.v1.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.rackspace.autoscale.v1.features.GroupApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * Autoscale GroupConfiguration.
 * 
 * @see GroupApi#create(GroupConfiguration, LaunchConfiguration, java.util.List)
 */
public class GroupConfiguration implements Comparable<GroupConfiguration>{
   private final String name;
   private final int cooldown;
   private final int minEntities;
   private final int maxEntities;
   private final ImmutableMap<String, String> metadata;

   @ConstructorProperties({
      "name", "cooldown", "minEntities", "maxEntities", "metadata"
   })
   protected GroupConfiguration(String name, int cooldown, int minEntities, int maxEntities, Map<String, String> metadata) {

      this.name = checkNotNull(name, "name required");
      checkArgument(cooldown >= 0, "cooldown should be non-negative");
      checkArgument(minEntities >= 0, "minEntities should be non-negative");
      checkArgument(maxEntities >= 0, "maxEntities should be non-negative");
      this.cooldown = cooldown;
      this.minEntities = minEntities;
      this.maxEntities = maxEntities;
      if (metadata == null) {
         this.metadata = ImmutableMap.of();
      } else {
         this.metadata = ImmutableMap.copyOf(metadata);
      }
   }

   /**
    * @return the name of this GroupConfiguration. The name is not a unique or even sufficient identifier in some cases.
    * @see GroupConfiguration.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }   

   /**
    * @return the cooldown for this GroupConfiguration.
    * @see GroupConfiguration.Builder#cooldown(int)
    */
   public int getCooldown() {
      return this.cooldown;
   }

   /**
    * @return the minimum number of entities for this GroupConfiguration.
    * @see GroupConfiguration.Builder#minEntities(int)
    */
   public int getMinEntities() {
      return this.minEntities;
   }

   /**
    * @return the maximum number of entities for this GroupConfiguration.
    * @see GroupConfiguration.Builder#maxEntities(int)
    */
   public int getMaxEntities() {
      return this.maxEntities;
   }

   /**
    * @return the metadata map for this GroupConfiguration.
    * @see GroupConfiguration.Builder#metadata(Map<String, String>)
    */
   public ImmutableMap<String, String> getMetadata() {
      return metadata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, cooldown, minEntities, maxEntities, metadata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      GroupConfiguration that = GroupConfiguration.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.cooldown, that.cooldown) &&
            Objects.equal(this.minEntities, that.minEntities) &&
            Objects.equal(this.maxEntities, that.maxEntities) &&
            Objects.equal(this.metadata, that.metadata);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("cooldown", cooldown)
            .add("minEntities", minEntities)
            .add("maxEntities", maxEntities)
            .add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromGroupConfiguration(this);
   }

   public static class Builder {
      protected String name;
      protected int cooldown;
      protected int minEntities;
      protected int maxEntities;
      protected Map<String, String> metadata;

      /** 
       * @param name The name of this GroupConfiguration.
       * @return The builder object.
       * @see GroupConfiguration#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param name The cooldown for this GroupConfiguration.
       * @return The builder object.
       * @see GroupConfiguration#getCooldown()
       */
      public Builder cooldown(int cooldown) {
         this.cooldown = cooldown;
         return this;
      }

      /** 
       * @param name The cooldown for this GroupConfiguration.
       * @return The builder object.
       * @see GroupConfiguration#getCooldown()
       */
      public Builder minEntities(int minEntities) {
         this.minEntities = minEntities;
         return this;
      }

      /** 
       * @param name The maxEntities for this GroupConfiguration.
       * @return The builder object.
       * @see GroupConfiguration#getCooldown()
       */
      public Builder maxEntities(int maxEntities) {
         this.maxEntities = maxEntities;
         return this;
      }

      /** 
       * @param name The metadata for this GroupConfiguration.
       * @return The builder object.
       * @see GroupConfiguration#getDatabases()
       */
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = metadata;
         return this;
      }

      /**
       * 
       * @return A new GroupConfiguration object.
       */
      public GroupConfiguration build() {
         return new GroupConfiguration(name, cooldown, minEntities, maxEntities, metadata);
      }

      public Builder fromGroupConfiguration(GroupConfiguration in) {
         return this
               .name(in.getName())
               .cooldown(in.getCooldown())
               .minEntities(in.getMinEntities())
               .maxEntities(in.getMaxEntities())
               .metadata(in.getMetadata());
      }        
   }

   @Override
   public int compareTo(GroupConfiguration that) {
      return this.getName().compareTo(that.getName());
   }
}
