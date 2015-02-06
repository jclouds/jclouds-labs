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

package org.jclouds.openstack.poppy.v1.domain;

public enum ServiceStatus {
   /**
    * Specifies that the service is currently being created and deployed.
    */
   CREATE_IN_PROGRESS("create_in_progress "),
   /**
    * Specifies that the service has been deployed and is ready to use.
    */
   DEPLOYED("deployed "),
   /**
    * Specifies that the service is currently being updated.
    */
   UPDATE_IN_PROGRESS("update_in_progress "),
   /**
    * Specifies that the service is currently being deleted.
    */
   DELETE_IN_PROGRESS("delete_in_progress "),
   /**
    * Specifies that the previous operation on the service failed to create, deploy, update, or delete.
    * Looks for the errors for details.
    * @see Service#getErrors()
    */
   FAILED("failed "),
   /**
    * An unexpected status jclouds could not recognize.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private ServiceStatus(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * @param name The string representation of this enum value.
    * @return The corresponding enum value.
    */
   public static ServiceStatus fromValue(String name) {
      if (name != null) {
         for (ServiceStatus value : ServiceStatus.values()) {
            if (name.equalsIgnoreCase(value.name)) {
               return value;
            }
         }
         return UNRECOGNIZED;
      }
      return null;
   }
}
