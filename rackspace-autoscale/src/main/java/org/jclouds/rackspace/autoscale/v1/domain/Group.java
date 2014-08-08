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

import java.util.List;

import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Autoscale Group.
 * This domain object contains information about an autoscaling group.
 */

public class Group {
   private final String id;
   private final ImmutableList<Link> links;
   private final ImmutableList<ScalingPolicy> scalingPolicy;
   private final GroupConfiguration groupConfiguration;
   private final LaunchConfiguration launchConfiguration;

   protected Group(String id, ImmutableList<Link> links, ImmutableList<ScalingPolicy> scalingPolicy, GroupConfiguration groupConfiguration, LaunchConfiguration launchConfiguration) {
      this.id = id;
      this.links = links;
      this.scalingPolicy = scalingPolicy;
      this.groupConfiguration = groupConfiguration;
      this.launchConfiguration = launchConfiguration;
   }      

   /**
    * Unique group identifier, usually UUID
    * @return the id of this Group.
    * @see Group.Builder#id(String)
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the links of this Group.
    * @see Link
    * @see Group.Builder#links(List)
    */
   public ImmutableList<Link> getLinks() {
      return this.links;
   }

   /**
    * @return A list of scaling policies for this Group.
    * @see CreateScalingPolicy
    * @see Group.Builder#scalingPolicy(List)
    */
   public ImmutableList<ScalingPolicy> getScalingPolicies() {
      return this.scalingPolicy;
   }

   /**
    * @return the group configuration of this Group.
    * @see GroupConfiguration
    * @see Group.Builder#groupConfiguration(GroupConfiguration)
    */
   public GroupConfiguration getGroupConfiguration() {
      return this.groupConfiguration;
   }

   /**
    * @return the launch configuration of this Group.
    * @see LaunchConfiguration
    * @see Group.Builder#launchConfiguration(LaunchConfiguration)
    */
   public LaunchConfiguration getLaunchConfiguration() {
      return this.launchConfiguration;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, links, scalingPolicy, groupConfiguration, launchConfiguration);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Group that = Group.class.cast(obj);
      return Objects.equal(this.id, that.id) && 
            Objects.equal(this.links, that.links) &&
            Objects.equal(this.scalingPolicy, that.scalingPolicy) &&
            Objects.equal(this.groupConfiguration, that.groupConfiguration) &&
            Objects.equal(this.launchConfiguration, that.launchConfiguration);
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("links", links)
            .add("scalingPolicy", scalingPolicy)
            .add("groupConfiguration", groupConfiguration)
            .add("launchConfiguration", "launchConfiguration");
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromGroup(this);
   }

   public static class Builder {
      protected String id;
      protected ImmutableList<Link> links;
      protected ImmutableList<ScalingPolicy> scalingPolicy;
      protected GroupConfiguration groupConfiguration;
      protected LaunchConfiguration launchConfiguration;

      /** 
       * @param id The id of this Group.
       * @return The builder object.
       * @see Group#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /** 
       * @param links The links of this Group.
       * @return The builder object.
       * @see Group#getLinks()
       */
      public Builder links(List<Link> links) {
         this.links = ImmutableList.copyOf(links);
         return this;
      }

      /** 
       * @param scalingPolicy The scaling policies list of this Group.
       * @return The builder object.
       * @see Group#getScalingPolicy()
       */
      public Builder scalingPolicy(List<ScalingPolicy> scalingPolicy) {
         this.scalingPolicy = ImmutableList.copyOf(scalingPolicy);
         return this;
      }

      /** 
       * @param groupConfiguration The groupConfiguration of this Group.
       * @return The builder object.
       * @see Group#getGroupConfiguration()
       */
      public Builder groupConfiguration(GroupConfiguration groupConfiguration) {
         this.groupConfiguration = groupConfiguration;
         return this;
      }

      /** 
       * @param launchConfiguration The launchConfiguration of this Group.
       * @return The builder object.
       * @see Group#getLaunchConfiguration()
       */
      public Builder launchConfiguration(LaunchConfiguration launchConfiguration) {
         this.launchConfiguration = launchConfiguration;
         return this;
      }

      /**
       * @return A new ScalingPolicy object.
       */
      public Group build() {
         return new Group(id, links, scalingPolicy, groupConfiguration, launchConfiguration);
      }

      public Builder fromGroup(Group in) {
         return this
               .id(in.getId())
               .links(in.getLinks())
               .scalingPolicy(in.getScalingPolicies())
               .groupConfiguration(in.getGroupConfiguration())
               .launchConfiguration(in.getLaunchConfiguration());
      }        
   }
}
