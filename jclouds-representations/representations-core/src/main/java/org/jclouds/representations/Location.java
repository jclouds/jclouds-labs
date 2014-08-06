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
package org.jclouds.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Set;

public class Location implements Serializable {

   private static final long serialVersionUID = -3061687522880229207L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String scope;
      private String description;
      private String parentId;
      private Set<String> iso3166Codes = ImmutableSet.of();

      public Builder id(final String id) {
         this.id = id;
         return this;
      }

      public Builder scope(final String scope) {
         this.scope = scope;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder parentId(final String parentId) {
         this.parentId = parentId;
         return this;
      }

      public Builder iso3166Codes(final Set<String> iso3166Codes) {
         this.iso3166Codes = ImmutableSet.copyOf(iso3166Codes);
         return this;
      }

      public Location build() {
         return new Location(id, scope, description, parentId, iso3166Codes);
      }

   }

   private final String id;
   private final String scope;
   private final String description;
   private final String parentId;
   private final Set<String> iso3166Codes;


   private Location(String id, String scope, String description, String parentId, Set<String> iso3166Codes) {
      this.id = id;
      this.scope = scope;
      this.description = description;
      this.parentId = parentId;
      this.iso3166Codes = iso3166Codes;
   }

   public String getId() {
      return id;
   }

   public String getScope() {
      return scope;
   }

   public String getDescription() {
      return description;
   }

   public String getParentId() {
      return parentId;
   }

   public Set<String> getIso3166Codes() {
      return iso3166Codes;
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
      return MoreObjects.toStringHelper(this).add("id", id).add("scope", scope).add("description", description)
              .add("perentId", parentId).add("iso3166Codes", iso3166Codes).toString();
   }
}
