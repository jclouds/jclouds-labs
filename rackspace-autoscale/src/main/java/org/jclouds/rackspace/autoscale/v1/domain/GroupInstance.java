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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * Autoscale Group Instance (as in hardware instance). Part of the group state.
 * 
 * @see GroupState#getGroupInstances()
 * @author Zack Shoylev
 */
public class GroupInstance {
   private final String id;
   private final ImmutableList<Link> links;

   @ConstructorProperties({ "id", "links" })
   protected GroupInstance(String id, ImmutableList<Link> links) {
      this.id = checkNotNull(id, "id should not be null");
      this.links = checkNotNull(links, "links should not be null");
   }

   /**
    * @return the id of this GroupInstance.
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the links for this GroupInstance.
    */
   public ImmutableList<Link> getLinks() {
      return this.links;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(links, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      GroupInstance that = GroupInstance.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.links, that.links);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("links", links);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
