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
package org.jclouds.openstack.heat.v1.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.openstack.v2_0.domain.Link;

import java.util.Date;
import java.util.Set;

/**
 * Representation of an OpenStack Heat Stack Resources.
 */
@AutoValue
public abstract class StackResource {

   /**
    * @return Specifies the name of the stack
    */
   public abstract String getName();

   /**
    * @return logical resource ID.
    */
   public abstract String getLogicalResourceId();

   /**
    * @return physical resource ID.
    */
   public abstract String getPhysicalResourceId();

   /**
    * @return the status
    */
   public abstract StackResourceStatus getStatus();

   /**
    * @return Status reason
    */
   public abstract String getStatusReason();

   /**
    * @return the field required_by
    */
   @Nullable public abstract Set<String> getRequiredBy();

   /**
    * @return the resource type
    */
   public abstract String getResourceType();

   /**
    * @return the update time
    */
   public abstract Date getUpdated();

   /**
    * @return Specifies the self-navigating JSON document paths.
    */
   public abstract Set<Link> getLinks();

   @SerializedNames({"resource_name", "logical_resource_id", "resource_status_reason", "updated_time", "required_by", "resource_status", "physical_resource_id", "resource_type", "links"})
   private static StackResource create(String name, String logicalResourceId, String statusReason, Date updated, @Nullable Set<String> requiredBy,
                                       StackResourceStatus status, String physicalResourceId, String resourceType, Set<Link> links) {
      return new AutoValue_StackResource(
            name,
            logicalResourceId,
            physicalResourceId,
            status,
            statusReason,
            requiredBy != null ? ImmutableSet.copyOf(requiredBy) : null,
            resourceType,
            updated,
            ImmutableSet.copyOf(links));
   }

}

