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

public class ProviderMetadata implements Serializable {

   private static final long serialVersionUID = -8444359103759144528L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String endpointName;
      private String identityName;
      private String credentialName;
      private Set<String> defaultModules = ImmutableSet.of();
      private String documentation;
      private Set<String> views = ImmutableSet.of();
      private String endpoint;
      private Map<String, String> defaultProperties = ImmutableMap.of();
      private String console;
      private String homePage;
      private Set<String> linkedServices = ImmutableSet.of();
      private Set<String> iso3166Codes = ImmutableSet.of();

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

      public Builder defaultModules(final Set<String> defaultModules) {
         this.defaultModules = defaultModules;
         return this;
      }

      public Builder documentation(final String documentation) {
         this.documentation = documentation;
         return this;
      }

      public Builder views(final Set<String> views) {
         this.views = views;
         return this;
      }

      public Builder endpoint(final String endpoint) {
         this.endpoint = endpoint;
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

      public Builder console(final URI console) {
         if (console != null) {
            this.console = console.toString();
         }
         return this;
      }

      public Builder console(final String console) {
         this.console = console;
         return this;
      }

      public Builder homePage(final URI homePage) {
         if (homePage != null) {
            this.homePage = homePage.toString();
         }
         return this;
      }

      public Builder homePage(final String homePage) {
         this.homePage = homePage;
         return this;
      }

      public Builder linkedServices(final Set<String> linkedServices) {
         this.linkedServices = ImmutableSet.copyOf(linkedServices);
         return this;
      }

      public Builder iso3166Codes(final Set<String> iso3166Codes) {
         this.iso3166Codes = ImmutableSet.copyOf(iso3166Codes);
         return this;
      }

      public ProviderMetadata build() {
         return new ProviderMetadata(id, name, documentation, endpointName, identityName, credentialName, defaultModules, views, endpoint, defaultProperties, console, homePage, linkedServices, iso3166Codes);
      }
   }

   private final String id;
   private final String name;
   private final String documentation;
   private final String endpointName;
   private final String identityName;
   private final String credentialName;
   private final Set<String> defaultModules;
   private final Set<String> views;
   private final String endpoint;
   private final Map<String, String> defaultProperties;
   private final String console;
   private final String homePage;
   private final Set<String> linkedServices;
   private final Set<String> iso3166Codes;

   public ProviderMetadata(String id, String name, String documentation, String endpointName, String identityName, String credentialName,
                           Set<String> defaultModules, Set<String> views,
                           String endpoint, Map<String, String> defaultProperties, String console, String homePage,
                           Set<String> linkedServices, Set<String> iso3166Codes) {
      this.id = id;
      this.name = name;
      this.documentation = documentation;
      this.endpointName = endpointName;
      this.identityName = identityName;
      this.credentialName = credentialName;
      this.defaultModules = defaultModules;
      this.views = views;
      this.endpoint = endpoint;
      this.defaultProperties = defaultProperties;
      this.console = console;
      this.homePage = homePage;
      this.linkedServices = linkedServices;
      this.iso3166Codes = iso3166Codes;
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

   public Set<String> getDefaultModules() {
      return defaultModules;
   }

   public String getDocumentation() {
      return documentation;
   }

   public Set<String> getViews() {
      return views;
   }

   public String getEndpoint() {
      return endpoint;
   }

   public Map<String, String> getDefaultProperties() {
      return defaultProperties;
   }

   public String getConsole() {
      return console;
   }

   public String getHomePage() {
      return homePage;
   }

   public Set<String> getLinkedServices() {
      return ImmutableSet.copyOf(linkedServices);
   }

   public Set<String> getIso3166Codes() {
      return ImmutableSet.copyOf(iso3166Codes);
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
              .add("identityName", identityName).add("credentialName", credentialName).add("defaultModules", defaultModules)
              .add("documentation", documentation).add("views", views)
              .add("endpoint", endpoint).add("defaultProperties", defaultProperties).add("console", console)
              .add("homePage", homePage).add("linkedServices", linkedServices).add("iso3166Codes", iso3166Codes).toString();
   }
}
