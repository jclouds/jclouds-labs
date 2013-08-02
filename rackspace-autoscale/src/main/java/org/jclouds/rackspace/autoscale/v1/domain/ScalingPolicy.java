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
import java.util.EnumSet;

import org.jclouds.rackspace.autoscale.v1.features.GroupApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * Autoscale ScalingPolicy. This class is used for requests.
 * 
 * @see GroupApi#create(GroupConfiguration, LaunchConfiguration, java.util.List)
 * @see Group#getScalingPolicies()
 * @see ScalingPolicyResponse
 * @author Zack Shoylev
 */
public class ScalingPolicy implements Comparable<ScalingPolicy>{
   private final String name;
   private final ScalingPolicyType type;
   private final int cooldown;
   private final int target;
   private final ScalingPolicyTargetType targetType;

   @ConstructorProperties({
      "name", "type", "cooldown", "target", "targetType"
   })
   protected ScalingPolicy(String name, ScalingPolicyType type, int cooldown, int target, ScalingPolicyTargetType targetType) {

      this.name = checkNotNull(name, "name required");
      this.type = type;
      checkArgument(cooldown >= 0, "cooldown should be non-negative");
      this.cooldown = cooldown;
      this.target = target;
      this.targetType = targetType;
   }

   /**
    * @return the name of this ScalingPolicy.
    * @see ScalingPolicy.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }   

   /**
    * @return the type for this ScalingPolicy.
    * @see ScalingPolicyType
    * @see ScalingPolicy.Builder#type(String)
    */
   public ScalingPolicyType getType() {
      return this.type;
   }

   /**
    * @return the cooldown for this ScalingPolicy.
    * @see ScalingPolicy.Builder#cooldown(int)
    */
   public int getCooldown() {
      return this.cooldown;
   }

   /**
    * @return the target for this ScalingPolicy. This is a numeric value, but could represent a 0-100% for some target types. Scale-down policies might have negative values.
    * 
    * @see ScalingPolicy.Builder#target(int)
    */
   public int getTarget() {
      return this.target;
   }

   /**
    * @return the target type for this ScalingPolicy.
    * @see ScalingPolicyTargetType
    * @see ScalingPolicy.Builder#targetType(int)
    */
   public ScalingPolicyTargetType getTargetType() {
      return this.targetType;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, cooldown, target, targetType);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ScalingPolicy that = ScalingPolicy.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.type, that.type) &&
            Objects.equal(this.cooldown, that.cooldown) &&
            Objects.equal(this.target, that.target) &&
            Objects.equal(this.targetType, that.targetType);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("type", type)
            .add("cooldown", cooldown)
            .add("target", target)
            .add("targetType", "targetType");
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromScalingPolicy(this);
   }

   public static class Builder {
      protected String name;
      protected ScalingPolicyType type;
      protected int cooldown;
      protected int target;
      protected ScalingPolicyTargetType targetType;

      /** 
       * @param name The name of this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicy#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param name The type for this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicyType
       * @see ScalingPolicy#getType()
       */
      public Builder type(ScalingPolicyType type) {
         this.type = type;
         return this;
      }

      /** 
       * @param cooldown The cooldown of this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicy#getCooldown()
       */
      public Builder cooldown(int cooldown) {
         this.cooldown = cooldown;
         return this;
      }

      /** 
       * @param target The target of this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicy#getTarget()
       */
      public Builder target(int target) {
         this.target = target;
         return this;
      }

      /** 
       * @param target type The target type of this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicyTargetType
       * @see ScalingPolicy#getTargetType()
       */
      public Builder targetType(ScalingPolicyTargetType targetType) {
         this.targetType = targetType;
         return this;
      }

      /**
       * @return A new ScalingPolicy object.
       */
      public ScalingPolicy build() {
         return new ScalingPolicy(name, type, cooldown, target, targetType);
      }

      public Builder fromScalingPolicy(ScalingPolicy in) {
         return this
               .name(in.getName())
               .type(in.getType())
               .cooldown(in.getCooldown())
               .target(in.getTarget())
               .targetType(in.getTargetType());
      }        
   }

   @Override
   public int compareTo(ScalingPolicy that) {
      return this.getName().compareTo(that.getName());
   }

   /**
    * Enumerates different types of scaling policies
    */
   public static enum ScalingPolicyType {
      WEBHOOK("webhook");

      private final String name;

      private ScalingPolicyType(String name) {
         this.name = name;
      }

      public String toString() {
         return name;
      }

      public static Optional<ScalingPolicyType> getByValue(String value){
         for (final ScalingPolicyType element : EnumSet.allOf(ScalingPolicyType.class)) {
            if (element.toString().equals(value)) {
               return Optional.of(element);
            }
         }
         return Optional.absent();
      }
   }

   /**
    * Enumerates different types of targets a policy might have
    */
   public static enum ScalingPolicyTargetType {
      INCREMENTAL("change"),
      DESIRED_CAPACITY("desiredCapacity"),
      PERCENT_CHANGE("changePercent");

      private final String name;

      private ScalingPolicyTargetType(String name) {
         this.name = name;
      }

      public String toString() {
         return name;
      }

      public static Optional<ScalingPolicyTargetType> getByValue(String value){
         for (final ScalingPolicyTargetType element : EnumSet.allOf(ScalingPolicyTargetType.class)) {
            if (element.toString().equals(value)) {
               return Optional.of(element);
            }
         }
         return Optional.absent();
      }
   }
}
