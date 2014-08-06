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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ApiMetadata implements Serializable {

   private static final long serialVersionUID = 3475663463134958705L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String endpointName;
      private String identityName;
      private String credentialName;
      private String version;
      private String defaultEndpoint;
      private String defaultIdentity;
      private String defaultCredential;
      private Map<String, String> defaultProperties = ImmutableMap.of();
      private Set<String> defaultModules = ImmutableSet.of();
      private String documentation;
      private String context;
      private Set<String> views = ImmutableSet.of();

      public Builder id(final String id) {
         this.id = id;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder endpointName(final String endpointName) {
         this.endpointName = endpointName;
         return this;
      }

      public Builder identityName(final String identityName) {
         this.identityName = identityName;
         return this;
      }

      public Builder credentialName(final String credentialName) {
         this.credentialName = credentialName;
         return this;
      }

      public Builder version(final String version) {
         this.version = version;
         return this;
      }

      public Builder defaultEndpoint(final String defaultEndpoint) {
         this.defaultEndpoint = defaultEndpoint;
         return this;
      }

      public Builder defaultIdentity(final String defaultIdentity) {
         this.defaultIdentity = defaultIdentity;
         return this;
      }

      public Builder defaultCredential(final String defaultCredential) {
         this.defaultCredential = defaultCredential;
         return this;
      }

      public Builder defaultProperties(final Properties defaultProperties) {
         if (defaultProperties != null) {
            this.defaultProperties = Maps.fromProperties(defaultProperties);
         }
         return this;
      }

      public Builder defaultProperties(final Map<String, String> defaultProperties) {
         this.defaultProperties = defaultProperties;
         return this;
      }

      public Builder defaultModules(final Set<String> defaultModules) {
         this.defaultModules = defaultModules;
         return this;
      }

      public Builder documentation(final URI documentation) {
         if (documentation != null) {
            this.documentation = documentation.toString();
         }
         return this;
      }

      public Builder documentation(final String documentation) {
         this.documentation = documentation;
         return this;
      }

      public Builder context(final String context) {
         this.context = context;
         return this;
      }

      public Builder views(final Set<String> views) {
         this.views = ImmutableSet.copyOf(views);
         return this;
      }

      public ApiMetadata build() {
         return new ApiMetadata(id, name, endpointName, identityName, credentialName, version, defaultEndpoint,
                 defaultIdentity, defaultCredential, defaultProperties, defaultModules, documentation, context, views);
      }
   }

   private final String id;
   private final String name;
   private final String endpointName;
   private final String identityName;
   private final String credentialName;
   private final String version;
   private final String defaultEndpoint;
   private final String defaultIdentity;
   private final String defaultCredential;
   private final Map<String, String> defaultProperties;
   private final Set<String> defaultModules;
   private final String documentation;
   private final String context;
   private final Set<String> views;

   public ApiMetadata(String id, String name, String endpointName, String identityName, String credentialName, String version,
                      String defaultEndpoint, String defaultIdentity, String defaultCredential,
                      Map<String, String> defaultProperties, Set<String> defaultModules, String documentation, String context,
                      Set<String> views) {

      this.id = id;
      this.name = name;
      this.endpointName = endpointName;
      this.identityName = identityName;
      this.credentialName = credentialName;
      this.version = version;
      this.defaultEndpoint = defaultEndpoint;
      this.defaultIdentity = defaultIdentity;
      this.defaultCredential = defaultCredential;
      this.defaultProperties = defaultProperties;
      this.defaultModules = defaultModules;
      this.documentation = documentation;
      this.context = context;
      this.views = views;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getEndpointName() {
      return endpointName;
   }

   public String getIdentityName() {
      return identityName;
   }

   public String getCredentialName() {
      return credentialName;
   }

   public String getVersion() {
      return version;
   }

   public String getDefaultEndpoint() {
      return defaultEndpoint;
   }

   public String getDefaultIdentity() {
      return defaultIdentity;
   }

   public String getDefaultCredential() {
      return defaultCredential;
   }

   public Map<String, String> getDefaultProperties() {
      return defaultProperties;
   }

   public Set<String> getDefaultModules() {
      return defaultModules;
   }

   public String getDocumentation() {
      return documentation;
   }

   public String getContext() {
      return context;
   }

   public Set<String> getViews() {
      return views;
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
      return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("endpointName", endpointName)
              .add("identityName", identityName).add("credentialName", credentialName).add("version", version)
              .add("defaultEndpoint", defaultEndpoint).add("defaultIdentity", defaultIdentity)
              .add("defaultCredential", defaultCredential).add("defaultProperties", defaultProperties).add("defaultModules", defaultModules)
              .add("documentation", documentation).add("context", context).add("views", views).toString();
   }
}
