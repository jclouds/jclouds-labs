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
package org.jclouds.rackspace.autoscale.v1.domain;

import java.beans.ConstructorProperties;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Autoscale LaunchConfiguration.
 * 
 * @see GroupApi#create(GroupConfiguration, LaunchConfiguration, List)
 * @author Zack Shoylev
 */
public class LaunchConfiguration implements Comparable<LaunchConfiguration>{
   private final ImmutableList<LoadBalancer> loadBalancers;
   private final LaunchConfigurationType type;
   private final ImmutableList<String> networks;
   private final ImmutableList<Personality> personalities;

   private final String serverName;
   private final String serverImageRef;
   private final String serverFlavorRef;
   private final String serverDiskConfig;
   private final ImmutableMap<String, String> serverMetadata;

   @ConstructorProperties({
      "loadBalancers", "type", "networks", "personalities", "serverName", "serverImageRef", "serverFlavorRef", "serverDiskConfig", "serverMetadata"
   })
   protected LaunchConfiguration(List<LoadBalancer> loadBalancers, LaunchConfigurationType type, List<String> networks, List<Personality> personalities, String serverName, String serverImageRef, String serverFlavorRef, String serverDiskConfig, Map<String, String> serverMetadata) {
      this.loadBalancers = ImmutableList.copyOf(loadBalancers);
      this.type = type;
      this.networks = ImmutableList.copyOf(networks);
      this.personalities = ImmutableList.copyOf(personalities);
      this.serverName = serverName;
      this.serverImageRef = serverImageRef;
      this.serverFlavorRef = serverFlavorRef;
      this.serverDiskConfig = serverDiskConfig;
      this.serverMetadata = ImmutableMap.copyOf(serverMetadata);
   }

   /**
    * @return the list of load balancers of this LaunchConfiguration.
    * @see LaunchConfiguration.Builder#loadBalancers(List)
    */
   public List<LoadBalancer> getLoadBalancers() {
      return this.loadBalancers;
   }

   /**
    * @return the type for this LaunchConfiguration.
    * @see LaunchConfigurationType
    * @see LaunchConfiguration.Builder#type(LaunchConfigurationType)
    */
   public LaunchConfigurationType getType() {
      return this.type;
   }

   /**
    * @return the networks for this LaunchConfiguration.
    * @see LaunchConfiguration#getNetworks()
    * @see LaunchConfiguration.Builder#networks(List)
    */
   public List<String> getNetworks() {
      return this.networks;
   }

   /**
    * @return the personalities for this LaunchConfiguration.
    * @see Personality
    * @see LaunchConfiguration.Builder#personalities(List)
    */
   public List<Personality> getPersonalities() {
      return this.personalities;
   }

   /**
    * @return the server name for this LaunchConfiguration.
    * @see LaunchConfiguration.Builder#serverName(String)
    */
   public String getServerName() {
      return this.serverName;
   }

   /**
    * @return the server image ref for this LaunchConfiguration.
    * @see LaunchConfiguration.Builder#serverImageRef(String)
    */
   public String getServerImageRef() {
      return this.serverImageRef;
   }

   /**
    * @return the server flavor ref for this LaunchConfiguration.
    * @see LaunchConfiguration.Builder#serverFlavorRef(String)
    */
   public String getServerFlavorRef() {
      return this.serverFlavorRef;
   }

   /**
    * @return the server disk config for this LaunchConfiguration, ex. "AUTO"
    * @see LaunchConfiguration.Builder#serverDiskConfig(String)
    */
   public String getServerDiskConfig() {
      return this.serverDiskConfig;
   }

   /**
    * @return the server metadata for this LaunchConfiguration.
    * @see LaunchConfiguration.Builder#serverMetadata(ImmutableMap)
    */
   public ImmutableMap<String, String> getServerMetadata() {
      return this.serverMetadata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(loadBalancers, type, networks, personalities, serverName, serverImageRef, serverFlavorRef, serverDiskConfig, serverMetadata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      LaunchConfiguration that = LaunchConfiguration.class.cast(obj);
      return Objects.equal(this.loadBalancers, that.loadBalancers) && 
            Objects.equal(this.type, that.type) &&
            Objects.equal(this.networks, that.networks) &&
            Objects.equal(this.personalities, that.personalities) &&
            Objects.equal(this.serverName, that.serverName) &&      
            Objects.equal(this.serverImageRef, that.serverImageRef) &&
            Objects.equal(this.serverFlavorRef, that.serverFlavorRef) &&
            Objects.equal(this.serverDiskConfig, that.serverDiskConfig) &&
            Objects.equal(this.serverMetadata, that.serverMetadata);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("loadBalancers", loadBalancers)
            .add("type", type)
            .add("networks", networks)
            .add("personalities", personalities)
            .add("serverName", serverName)
            .add("serverImageRef", serverImageRef)
            .add("serverFlavorRef", serverFlavorRef)
            .add("serverDiskConfig", serverDiskConfig)
            .add("serverMetadata", serverMetadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromLaunchConfiguration(this);
   }

   public static class Builder {
      protected List<LoadBalancer> loadBalancers;
      protected LaunchConfigurationType type;
      protected List<String> networks;
      protected List<Personality> personalities;

      protected String serverName;
      protected String serverImageRef;
      protected String serverFlavorRef;
      protected String serverDiskConfig;
      protected ImmutableMap<String, String> serverMetadata;

      /** 
       * @param loadBalancers The load balancers of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getLoadBalancers()
       */
      public Builder loadBalancers(List<LoadBalancer> loadBalancers) {
         this.loadBalancers = loadBalancers;
         return this;
      }

      /** 
       * @param type The type for this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getType()
       */
      public Builder type(LaunchConfigurationType type) {
         this.type = type;
         return this;
      }

      /** 
       * @param networks The networks of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getNetworks()
       */
      public Builder networks(List<String> networks) {
         this.networks = networks;
         return this;
      }

      /** 
       * @param personalities The personalities of this LaunchConfiguration.
       * @return The builder object.
       * @see Personality
       * @see LaunchConfiguration#getPersonalities()
       */
      public Builder personalities(List<Personality> personalities) {
         this.personalities = personalities;
         return this;
      }

      /** 
       * @param serverName The serverName of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getServerName()
       */
      public Builder serverName(String serverName) {
         this.serverName = serverName;
         return this;
      }

      /** 
       * @param serverImageRef The serverImageRef of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getServerImageRef()
       */
      public Builder serverImageRef(String serverImageRef) {
         this.serverImageRef = serverImageRef;
         return this;
      }

      /** 
       * @param serverFlavorRef The serverFlavorRef of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getServerFlavorRef()
       */
      public Builder serverFlavorRef(String serverFlavorRef) {
         this.serverFlavorRef = serverFlavorRef;
         return this;
      }

      /** 
       * @param serverDiskConfig The serverDiskConfig of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getServerDiskConfig()
       */
      public Builder serverDiskConfig(String serverDiskConfig) {
         this.serverDiskConfig = serverDiskConfig;
         return this;
      }

      /** 
       * @param serverMetadata The serverMetadata of this LaunchConfiguration.
       * @return The builder object.
       * @see LaunchConfiguration#getServerMetadata()
       */
      public Builder serverMetadata(Map<String, String> serverMetadata) {
         this.serverMetadata = ImmutableMap.copyOf(serverMetadata);
         return this;
      }

      /**
       * @return A new LaunchConfiguration object.
       */
      public LaunchConfiguration build() {
         return new LaunchConfiguration(loadBalancers, type, networks, personalities, serverName, serverImageRef, serverFlavorRef, serverDiskConfig, serverMetadata);
      }

      public Builder fromLaunchConfiguration(LaunchConfiguration in) {
         return this
               .loadBalancers(in.getLoadBalancers())
               .type(in.getType())
               .networks(in.getNetworks())
               .personalities(in.getPersonalities())
               .serverName(in.getServerName())
               .serverImageRef(in.getServerImageRef())
               .serverFlavorRef(in.getServerFlavorRef())
               .serverDiskConfig(in.getServerDiskConfig())
               .serverMetadata(in.getServerMetadata());
      }        
   }

   @Override
   public int compareTo(LaunchConfiguration that) {
      return this.getServerName().compareTo(that.getServerName());
   }

   /**
    * Enumerates launch configuration types
    */
   public static enum LaunchConfigurationType {
      LAUNCH_SERVER("launch_server");

      private final String name;

      private LaunchConfigurationType(String name) {
         this.name = name;
      }

      public String toString() {
         return name;
      }

      public static Optional<LaunchConfigurationType> getByValue(String value){
         for (final LaunchConfigurationType element : EnumSet.allOf(LaunchConfigurationType.class)) {
            if (element.toString().equals(value)) {
               return Optional.of(element);
            }
         }
         return Optional.absent();
      }
   }
}
