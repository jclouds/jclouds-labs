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
package org.jclouds.azurecompute.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import com.google.auto.value.AutoValue;
import java.util.Date;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class AffinityGroup {

   public enum Capability {

      /**
       * Enables an affinity group to support Virtual Machines.
       */
      PersistentVMRole,
      /**
       * Enables the affinity group to support Virtual Machines that use high memory relative to the number of CPUs.
       */
      HighMemory;

   }

   AffinityGroup() {
   } // For AutoValue only!

   /**
    * Specifies the name of the affinity group.
    *
    * @return the name of the affinity group
    */
   public abstract String name();

   /**
    * Specifies the base-64-encoded identifier of the affinity group.
    *
    * @return the identifier of the affinity group
    */
   public abstract String label();

   /**
    * Specified the description of this affinity group.
    *
    * @return the description of this affinity group
    */
   @Nullable
   public abstract String description();

   /**
    * Specifies the data center in which the affinity group is located.
    *
    * @return the data center in which the affinity group is located
    */
   public abstract String location();

   /**
    * Specifies the capabilities that of the affinity group.
    *
    * @return the capabilities that of the affinity group
    */
   public abstract List<Capability> capabilities();

   /**
    * Specifies in UTC format when the affinity group was created.
    *
    * @return when the affinity group was created (in UTC)
    */
   public abstract Date createdTime();

   /**
    * Specifies the roles sizes that are available for deployments in the affinity group.
    *
    * @return the roles sizes that are available for deployments in the affinity group
    */
   @Nullable
   public abstract ComputeCapabilities computeCapabilities();

   public static AffinityGroup create(final String name, final String label, final String description,
           final String location, final List<Capability> capabilities, final Date createdTime,
           final ComputeCapabilities computeCapabilities) {

      return new AutoValue_AffinityGroup(name, label, description, location, copyOf(capabilities),
              createdTime, computeCapabilities);
   }
}
