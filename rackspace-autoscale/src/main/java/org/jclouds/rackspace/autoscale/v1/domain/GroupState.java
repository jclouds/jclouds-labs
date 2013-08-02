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

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.autoscale.v1.features.GroupApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

/**
 * Autoscale Group State. Contains information about a scaling group. 
 * 
 * @see Group
 * @see GroupApi#listGroupStates()
 * @see GroupApi#getState(String)
 * @author Zack Shoylev
 */
public class GroupState implements Comparable<GroupState> {
   private final String id;
   private final ImmutableList<Link> links;
   private final int activeCapacity;
   private final int pendingCapacity;
   private final int desiredCapacity;
   private final boolean paused;
   @SerializedName("active")
   private final ImmutableList<GroupInstance> groupInstances;

   @ConstructorProperties({ "id", "links", "activeCapacity", "pendingCapacity", "desiredCapacity", "paused",
   "active" })
   protected GroupState(String id, List<Link> links, int activeCapacity, int pendingCapacity,
         int desiredCapacity, boolean paused, List<GroupInstance> groupInstances) {
      this.id = id;
      this.links = ImmutableList.copyOf(links);
      this.activeCapacity = activeCapacity;
      this.pendingCapacity = pendingCapacity;
      this.desiredCapacity = desiredCapacity;
      this.paused = paused;
      this.groupInstances = ImmutableList.copyOf(groupInstances);
   }

   /**
    * @return the id of this GroupState.
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the links for this GroupState.
    */
   public List<Link> getLinks() {
      return this.links;
   }

   /**
    * @return the active capacity for this GroupState.
    */
   public int getActiveCapacity() {
      return this.activeCapacity;
   }

   /**
    * @return the pending capacity for this GroupState.
    */
   public int getPendingCapacity() {
      return this.pendingCapacity;
   }

   /**
    * @return the desired capacity for this GroupState.
    */
   public int getDesiredCapacity() {
      return this.desiredCapacity;
   }

   /**
    * @return the paused status for this GroupState.
    */
   public boolean getPaused() {
      return this.paused;
   }

   /**
    * @return the group instances for this GroupState.
    * @see GroupInstance
    */
   public List<GroupInstance> getGroupInstances() {
      return this.groupInstances;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, links, activeCapacity, pendingCapacity, desiredCapacity, paused, groupInstances);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      GroupState that = GroupState.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.links, that.links)
            && Objects.equal(this.activeCapacity, that.activeCapacity)
            && Objects.equal(this.pendingCapacity, that.pendingCapacity)
            && Objects.equal(this.desiredCapacity, that.desiredCapacity) && Objects.equal(this.paused, that.paused)
            && Objects.equal(this.groupInstances, that.groupInstances);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("links", links).add("activeCapacity", activeCapacity)
            .add("pendingCapacity", pendingCapacity).add("desiredCapacity", "desiredCapacity").add("paused", "paused")
            .add("groupInstances", "groupInstances");
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(GroupState that) {
      return this.getId().compareTo(that.getId());
   }   
}
