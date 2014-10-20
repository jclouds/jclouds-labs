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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;

/** A data center location that is valid for your subscription. */
public final class Location {

   /** The name of the data center location. Ex. {@code West Europe}. */
   public String name() {
      return name;
   }

   /** The localized name of the data center location. */
   public String displayName() {
      return displayName;
   }

   /** Indicates the services available at this location. Ex. {@code Compute}. */
   public List<String> availableServices() {
      return availableServices;
   }

   public static Location create(String name, String displayName, List<String> availableServices) {
      return new Location(name, displayName, availableServices);
   }

   // TODO: Remove from here down with @AutoValue.
   private Location(String name, String displayName, List<String> availableServices) {
      this.name = checkNotNull(name, "name");
      this.displayName = checkNotNull(displayName, "displayName");
      this.availableServices = checkNotNull(availableServices, "availableServices");
   }

   private final String name;
   private final String displayName;
   private final List<String> availableServices;

   @Override
   public int hashCode() {
      return Objects.hashCode(name, displayName, availableServices);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Location other = (Location) obj;
      return equal(this.name, other.name) &&
            equal(this.displayName, other.displayName) &&
            equal(this.availableServices, other.availableServices);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("displayName", displayName)
            .add("availableServices", availableServices).toString();
   }
}
