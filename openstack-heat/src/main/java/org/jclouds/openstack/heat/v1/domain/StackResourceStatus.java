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


import com.google.common.base.CaseFormat;

import static com.google.common.base.Preconditions.checkNotNull;

public enum StackResourceStatus {
   INIT_COMPLETE, CREATE_IN_PROGRESS, CREATE_COMPLETE, CREATE_FAILED,
   UPDATE_IN_PROGRESS, UPDATE_COMPLETE, UPDATE_FAILED,
   DELETE_IN_PROGRESS, DELETE_COMPLETE, DELETE_FAILED,
   ROLLBACK_IN_PROGRESS, ROLLBACK_COMPLETE, ROLLBACK_FAILED,
   SUSPEND_IN_PROGRESS, SUSPEND_COMPLETE, SUSPEND_FAILED,
   RESUME_IN_PROGRESS, RESUME_COMPLETE, RESUME_FAILED,
   UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   /**
    * This provides GSON enum support in jclouds.
    *
    * @param name The string representation of this enum value.
    * @return The corresponding enum value.
    */

   public static StackResourceStatus fromValue(String status) {
      try {
         return valueOf(checkNotNull(status, "status"));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}

