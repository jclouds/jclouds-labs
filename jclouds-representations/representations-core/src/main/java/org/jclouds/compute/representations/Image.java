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
package org.jclouds.compute.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Set;

public class Image implements Serializable {

   private static final long serialVersionUID = 1332541821219215234L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String status;
      private String version;
      private String description;
      private OperatingSystem operatingSystem;
      private LoginCredentials defaultCredentials;
      private Set<String> tags = ImmutableSet.of();

      public Builder id(final String id) {
         this.id = id;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder status(final String status) {
         this.status = status;
         return this;
      }

      public Builder tags(final Set<String> tags) {
         this.tags = ImmutableSet.copyOf(tags);
         return this;
      }

      public Builder operatingSystem(final OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      public Builder version(final String version) {
         this.version = version;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder defaultCredentials(final LoginCredentials defaultCredentials) {
         this.defaultCredentials = defaultCredentials;
         return this;
      }

      public Image build() {
         return new Image(id, name, version, description, status, operatingSystem, defaultCredentials, tags);
      }

   }

   private final String id;
   private final String name;
   private final String version;
   private final String description;
   private final String status;
   private final OperatingSystem operatingSystem;
   private final LoginCredentials defaultCredentials;
   private final Set<String> tags;

   public Image(String id, String name, String version, String description, String status, OperatingSystem operatingSystem, LoginCredentials defaultCredentials, Set<String> tags) {
      this.id = id;
      this.name = name;
      this.version = version;
      this.description = description;
      this.status = status;
      this.operatingSystem = operatingSystem;
      this.defaultCredentials = defaultCredentials;
      this.tags = tags;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getStatus() {
      return status;
   }

   public Set<String> getTags() {
      return tags;
   }

   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   public String getVersion() {
      return version;
   }

   public String getDescription() {
      return description;
   }

   public LoginCredentials getDefaultCredentials() {
      return defaultCredentials;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("status", status)
              .add("description", description)
              .add("tags", tags).add("os", operatingSystem).add("version", version)
              .add("defaultCredentials", defaultCredentials).toString();
   }
}
