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

import autovalue.shaded.com.google.common.common.collect.ImmutableMap;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.openstack.v2_0.domain.Link;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of an OpenStack Heat Stack.
 */
@AutoValue
public abstract class Stack {

   /**
    * @return Specifies the Stack ID . The value is a UUID, such as 96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0.
    */
   public abstract String getId();

   /**
    * @return Specifies the name of the stack
    */
   @Nullable public abstract String getName();

   /**
    * @return the template description of this Stack.
    */
   @Nullable public abstract String getTemplateDescription();

   /**
    * @return the description of this Stack.
    */
   @Nullable public abstract String getDescription();

   /**
    * @return the owner of this Stack.
    */
   @Nullable public abstract String getOwner();

   /**
    * @return the project ID of the stack
    */
   @Nullable public abstract String getProject();

   /**
    * @return the parameters of this Stack.
    */
   @Nullable public abstract Map<String, String> getParameters();

   /**
    * @return the capabilities of this Stack.
    */
   @Nullable public abstract Set<String> getCapabilities();

   /**
    * @return the outputs of this Stack.
    */
   @Nullable public abstract List<String> getOutputs();

   /**
    * @return the notification topics of this Stack.
    */
   @Nullable public abstract List<String> getNotificationTopics();

   /**
    * @return the status of this Stack.
    */
   @Nullable public abstract StackStatus getStatus();

   /**
    * @return the status reason of this Stack.
    */
   @Nullable public abstract String getSatusReason();

   /**
    * @return the date this Stack was created.
    */
   @Nullable public abstract Date getCreated();

   /**
    * @return the date this Stack was last updated.
    */
   @Nullable public abstract Date getUpdated();

   /**
    * @return the timeout of this Stack (in minutes).
    */
   public abstract int getTimeoutMins();

   /**
    * @return true is disableRollback is true
    */
   public abstract boolean isDisableRollback();

   /**
    * @return Specifies the self-navigating JSON document paths.
    */
   public abstract Set<Link> getLinks();

   @SerializedNames({"id", "stack_name", "description", "owner", "capabilities", "parameters", "outputs",
         "notification_topics", "template_description", "stack_status", "stack_status_reason", "creation_time",
         "updated_time", "timeout_mins", "disable_rollback", "project", "links"})
   private static Stack create(String id, String name, String description, String owner, Set<String> capabilities,
                               Map<String, String> parameters, List<String> outputs, List<String> notificationTopics,
                               String templateDescription, StackStatus status, String statusReason, Date created, Date updated, int timeoutMins,
                               boolean disableRollback, String project, Set<Link> links) {
      return new AutoValue_Stack(
            id,
            name,
            templateDescription,
            description,
            owner,
            project,
            parameters != null ? ImmutableMap.copyOf(parameters) : null,
            capabilities != null ? ImmutableSet.copyOf(capabilities) : null,
            outputs != null ? ImmutableList.copyOf(outputs) : null,
            notificationTopics != null ? ImmutableList.copyOf(notificationTopics) : null,
            status,
            statusReason,
            created,
            updated,
            timeoutMins,
            disableRollback,
            ImmutableSet.copyOf(links));
   }
}

