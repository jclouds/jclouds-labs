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
package org.jclouds.cloudsigma2.domain;

import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class VLANInfo {
   public static class Builder {
      private Map<String, String> meta;
      private String uuid;
      private Owner owner;
      private URI resourceUri;
      private List<Server> servers;
      private Subscription subscription;
      private List<Tag> tags;

      /**
       * @param uuid VLAN UUID
       * @return VLANInfo Builder
       */
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @param owner VLAN owner
       * @return VLANInfo Builder
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @param meta User defined meta information
       * @return VLANInfo Builder
       */
      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      /**
       * @param resourceUri Resource URI
       * @return VLANInfo Builder
       */
      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      /**
       * @param servers Servers in this VLAN
       * @return VLANInfo Builder
       */
      public Builder servers(List<Server> servers) {
         this.servers = ImmutableList.copyOf(servers);
         return this;
      }

      /**
       * @param subscription Subscription related to this VLAN
       * @return VLANInfo Builder
       */
      public Builder subscription(Subscription subscription) {
         this.subscription = subscription;
         return this;
      }

      /**
       * @param tags Tags added to this VLAN
       * @return VLANInfo Builder
       */
      public Builder tags(List<Tag> tags) {
         this.tags = ImmutableList.copyOf(tags);
         return this;
      }

      public VLANInfo build() {
         return new VLANInfo(meta, uuid, owner, resourceUri, servers, subscription, tags);
      }
   }

   private final Map<String, String> meta;
   private final String uuid;
   private final Owner owner;
   @Named("resource_uri")
   private final URI resourceUri;
   private final List<Server> servers;
   private final Subscription subscription;
   private final List<Tag> tags;

   @ConstructorProperties({
         "meta", "uuid", "owner", "resource_uri", "servers", "subscription", "tags"
   })
   public VLANInfo(Map<String, String> meta, String uuid, Owner owner, URI resourceUri, List<Server> servers,
                   Subscription subscription, List<Tag> tags) {
      this.meta = meta;
      this.uuid = uuid;
      this.owner = owner;
      this.resourceUri = resourceUri;
      this.servers = servers;
      this.subscription = subscription;
      this.tags = tags;
   }

   /**
    * @return VLAN UUID
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * @return VLAN owner
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return User defined meta information
    */
   public Map<String, String> getMeta() {
      return meta;
   }

   /**
    * @return Resource URI
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   /**
    * @return Servers in this VLAN
    */
   public List<Server> getServers() {
      return servers;
   }

   /**
    * @return Subscription related to this VLAN
    */
   public Subscription getSubscription() {
      return subscription;
   }

   /**
    * @return Tags added to this VLAN
    */
   public List<Tag> getTags() {
      return tags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof VLANInfo)) return false;

      VLANInfo vlanInfo = (VLANInfo) o;

      if (meta != null ? !meta.equals(vlanInfo.meta) : vlanInfo.meta != null) return false;
      if (owner != null ? !owner.equals(vlanInfo.owner) : vlanInfo.owner != null) return false;
      if (resourceUri != null ? !resourceUri.equals(vlanInfo.resourceUri) : vlanInfo.resourceUri != null)
         return false;
      if (servers != null ? !servers.equals(vlanInfo.servers) : vlanInfo.servers != null) return false;
      if (subscription != null ? !subscription.equals(vlanInfo.subscription) : vlanInfo.subscription != null)
         return false;
      if (tags != null ? !tags.equals(vlanInfo.tags) : vlanInfo.tags != null) return false;
      if (uuid != null ? !uuid.equals(vlanInfo.uuid) : vlanInfo.uuid != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = meta != null ? meta.hashCode() : 0;
      result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
      result = 31 * result + (owner != null ? owner.hashCode() : 0);
      result = 31 * result + (resourceUri != null ? resourceUri.hashCode() : 0);
      result = 31 * result + (servers != null ? servers.hashCode() : 0);
      result = 31 * result + (subscription != null ? subscription.hashCode() : 0);
      result = 31 * result + (tags != null ? tags.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "meta=" + meta +
            ", uuid='" + uuid + '\'' +
            ", owner=" + owner +
            ", resourceUri=" + resourceUri +
            ", servers=" + servers +
            ", subscription=" + subscription +
            ", tags=" + tags +
            "]";
   }

   /**
    * Creates VLAN NIC
    *
    * @param firewallPolicy
    * @return server's NIC
    */
   public NIC toNIC(FirewallPolicy firewallPolicy) {
      return new NIC.Builder()
            .vlan(new Builder().uuid(this.uuid).build())
            .firewallPolicy(firewallPolicy)
            .build();
   }

   /**
    * Creates VLAN NIC
    *
    * @return server's NIC
    */
   public NIC toNIC() {
      return toNIC(null);
   }
}
