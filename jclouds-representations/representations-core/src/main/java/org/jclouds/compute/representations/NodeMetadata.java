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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class NodeMetadata implements Serializable {

   private static final long serialVersionUID = 948372788993429243L;

   private static final int DEFAULT_SSH_PORT = 22;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String description;
      private String status;
      private String hostname;
      private String imageId;
      private String locationId;
      private int loginPort = DEFAULT_SSH_PORT;
      private String group;
      private Set<String> tags = ImmutableSet.of();
      private Map<String, String> metadata = ImmutableMap.<String, String>of();
      private LoginCredentials defaultCredentials;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder status(final String status) {
         this.status = status;
         return this;
      }


      public Builder loginPort(int loginPort) {
         this.loginPort = loginPort;
         return this;
      }

      public Builder locationId(String locationId) {
         this.locationId = locationId;
         return this;
      }

      public Builder imageId(final String imageId) {
         this.imageId = imageId;
         return this;
      }

      public Builder group(String group) {
         this.group = group;
         return this;
      }

      public Builder tags(Iterable<String> tags) {
         this.tags = ImmutableSet.copyOf(tags);
         return this;
      }

      public Builder metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(metadata);
         return this;
      }

      public Builder defaultCredentials(final LoginCredentials defaultCredentials) {
         this.defaultCredentials = defaultCredentials;
         return this;
      }

      public NodeMetadata build() {
         return new NodeMetadata(id, name, description, status, hostname, locationId, imageId, loginPort, group, tags,
                 metadata, defaultCredentials);
      }
   }

   public NodeMetadata(String id, String name, String description, String status, String hostname, String locationId,
                       String imageId, int loginPort, String group, Set<String> tags,
                       Map<String, String> metadata, LoginCredentials defaultCredentials) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.status = status;
      this.hostname = hostname;
      this.locationId = locationId;
      this.imageId = imageId;
      this.loginPort = loginPort;
      this.group = group;
      this.tags = tags;
      this.metadata = metadata;
      this.defaultCredentials = defaultCredentials;
   }

   private final String id;
   private final String name;
   private final String description;
   private final String status;
   private final String hostname;
   private final String locationId;
   private final String imageId;
   private final int loginPort;
   private final String group;
   private final Set<String> tags;
   private final Map<String, String> metadata;
   private final LoginCredentials defaultCredentials;

   public String getId() {
      return id;
   }

   public String getLocationId() {
      return locationId;
   }

   public String getImageId() {
      return imageId;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public String getGroup() {
      return group;
   }

   public String getHostname() {
      return hostname;
   }

   public String getStatus() {
      return status;
   }

   public int getLoginPort() {
      return loginPort;
   }

   public Set<String> getTags() {
      Set<String> tagSet = Sets.newHashSet();
      for (String tag : tags) {
         tagSet.add(tag);
      }
      return tagSet;
   }

   public Map<String, String> getMetadata() {
      return Maps.newLinkedHashMap(this.metadata);
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
      return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("description", description).add("status", status)
              .add("locationId", locationId).add("imageId", imageId).add("hostname", hostname)
              .add("group", group).add("loginPort", loginPort).add("tags", tags).add("metadata", metadata)
              .add("defaultCredentials", defaultCredentials).toString();
   }
}
