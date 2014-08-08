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
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Auto Scale ScalingPolicy. This class is used for requests.
 * 
 * @see GroupApi#create(GroupConfiguration, LaunchConfiguration, java.util.List)
 * @see Group#getScalingPolicies()
 * @see ScalingPolicy
 */
public class CreateScalingPolicy implements Comparable<CreateScalingPolicy> {
   private final String name;
   private final ScalingPolicyType type;
   private final int cooldown;
   private final String target;
   private final ScalingPolicyTargetType targetType;
   private final Map<String, String> args;

   @ConstructorProperties({
      "name", "type", "cooldown", "target", "targetType", "args"
   })
   protected CreateScalingPolicy(String name, ScalingPolicyType type, int cooldown, String target, ScalingPolicyTargetType targetType, Map<String, String> args) {
      this.name = checkNotNull(name, "name required");
      this.type = type;
      checkArgument(cooldown >= 0, "cooldown should be non-negative");
      this.cooldown = cooldown;
      this.target = target;
      this.targetType = targetType;
      this.args = args;
   }

   /**
    * @return the name of this ScalingPolicy.
    * @see CreateScalingPolicy.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }   

   /**
    * @return the type for this ScalingPolicy.
    * @see ScalingPolicyType
    * @see CreateScalingPolicy.Builder#type(String)
    */
   public ScalingPolicyType getType() {
      return this.type;
   }

   /**
    * @return the cooldown for this ScalingPolicy.
    * @see CreateScalingPolicy.Builder#cooldown(int)
    */
   public int getCooldown() {
      return this.cooldown;
   }

   /**
    * @return the target for this ScalingPolicy. This is a numeric value, but could represent a 0-100% for some target types. Scale-down policies might have negative values.
    * 
    * @see CreateScalingPolicy.Builder#target(int)
    */
   public String getTarget() {
      return this.target;
   }

   /**
    * @return the target type for this ScalingPolicy.
    * @see ScalingPolicyTargetType
    * @see CreateScalingPolicy.Builder#targetType(int)
    */
   public ScalingPolicyTargetType getTargetType() {
      return this.targetType;
   }

   /**
    * @return The scheduling string, if any.
    * @see CreateScalingPolicy.Builder#atSchedule(String)
    * @see CreateScalingPolicy.Builder#cronSchedule(String)
    */
   protected Map<String, String> getSchedulingArgs() {
      return this.args;
   }

   /**
    * @return The scheduling string, if any.
    * @see CreateScalingPolicy.Builder#atSchedule(String)
    * @see CreateScalingPolicy.Builder#cronSchedule(String)
    */
   public String getSchedulingString() {
      if (this.args != null) {
         for (Map.Entry<String, String> entry : this.args.entrySet()) {
            return entry.getValue();
         }
      }
      return null;
   }

   /**
    * @return The type of the schedule this policy uses.
    */
   public ScalingPolicyScheduleType getSchedulingType() {
      if (this.args != null) {
         for (ScalingPolicyScheduleType type : ScalingPolicyScheduleType.values()) {
            if (this.args.get(type.toString()) != null) {
               return type;
            }
         }
      }
      return null;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, cooldown, target, targetType, args);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CreateScalingPolicy that = CreateScalingPolicy.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.type, that.type) &&
            Objects.equal(this.cooldown, that.cooldown) &&
            Objects.equal(this.target, that.target) &&
            Objects.equal(this.targetType, that.targetType) &&
            Objects.equal(this.args, that.args);
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("type", type)
            .add("cooldown", cooldown)
            .add("target", target)
            .add("targetType", targetType)
            .add("args", args);
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
      protected String target;
      protected ScalingPolicyTargetType targetType;
      protected Map<String, String> args;

      /** 
       * @param name The name of this ScalingPolicy.
       * @return The builder object.
       * @see CreateScalingPolicy#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param type The type for this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicyType
       * @see CreateScalingPolicy#getType()
       */
      public Builder type(ScalingPolicyType type) {
         this.type = type;
         return this;
      }

      /** 
       * @param cooldown The cooldown of this ScalingPolicy.
       * @return The builder object.
       * @see CreateScalingPolicy#getCooldown()
       */
      public Builder cooldown(int cooldown) {
         this.cooldown = cooldown;
         return this;
      }

      /** 
       * @param target The target of this ScalingPolicy.
       * @return The builder object.
       * @see CreateScalingPolicy#getTarget()
       */
      public Builder target(String target) {
         this.target = target;
         return this;
      }

      /** 
       * @param targetType The target type of this ScalingPolicy.
       * @return The builder object.
       * @see ScalingPolicyTargetType
       * @see CreateScalingPolicy#getTargetType()
       */
      public Builder targetType(ScalingPolicyTargetType targetType) {
         this.targetType = targetType;
         return this;
      }

      /** 
       * @param cron This parameter specifies the recurring time when the policy will be executed as a cron entry. 
       * For example, if this is parameter is set to "1 0 * * *",
       * the policy will be executed at one minute past midnight (00:01)
       * every day of the month, and every day of the week.
       * You can either provide "cron" or "at" for a given policy, but not both.
       * @return The builder object.
       * @see ScalingPolicyTargetType
       * @see CreateScalingPolicy#getTargetType()
       * @see <a href="http://en.wikipedia.org/wiki/Cron">Cron</a>
       */
      public Builder cronSchedule(String cron) {
         this.type = ScalingPolicyType.SCHEDULE;
         this.args = ImmutableMap.of("cron", cron);
         return this;
      }

      /** 
       * @param at This parameter specifies the time at which this policy will be executed.
       * This property is mutually exclusive with the "cron" parameter.
       * You can either provide "cron" or "at" for a given policy, but not both.
       * Example date string: "2013-12-05T03:12:00Z"
       * @return The builder object.
       * @see ScalingPolicyTargetType
       * @see CreateScalingPolicy#getTargetType()
       */
      public Builder atSchedule(String at) {
         this.type = ScalingPolicyType.SCHEDULE;
         this.args = ImmutableMap.of("at", at);
         return this;
      }

      private Builder scheduleArgs(Map<String, String> args) {
         this.args = args;
         return this;
      }

      /**
       * @return A new ScalingPolicy object.
       */
      public CreateScalingPolicy build() {
         return new CreateScalingPolicy(name, type, cooldown, target, targetType, args);
      }

      /**
       * @param in The target scaling policy
       * @return The scaling policy builder
       */
      public Builder fromScalingPolicy(CreateScalingPolicy in) {
         return this
               .name(in.getName())
               .type(in.getType())
               .cooldown(in.getCooldown())
               .target(in.getTarget())
               .targetType(in.getTargetType())
               .scheduleArgs(in.getSchedulingArgs());
      }        
   }

   @Override
   public int compareTo(CreateScalingPolicy that) {
      return this.getName().compareTo(that.getName());
   }

   /**
    * Enumerates different types of scaling policies
    */
   public static enum ScalingPolicyType {
      WEBHOOK("webhook"),
      SCHEDULE("schedule");

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

   /**
    * Enumerates different types of targets a policy might have
    */
   public static enum ScalingPolicyScheduleType {
      /**
       * Example: "1 0 * * *"
       * @see ScalingPolicy.Builder#cronSchedule(String)
       */
      AT("at"),
      /**
       * Example date string: "2013-12-05T03:12:00Z"
       * @see ScalingPolicy.Builder#atSchedule(String)
       */
      CRON("cron");

      private final String name;

      private ScalingPolicyScheduleType(String name) {
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
