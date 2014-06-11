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

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirewallPolicy extends Item {

   public static class Builder extends Item.Builder {
      private Map<String, String> meta;
      private Owner owner;
      private List<FirewallRule> rules;
      private List<Tag> tags;
      private List<Server> servers;

      /**
       * @param meta User assigned meta information for this policy
       * @return
       */
      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      /**
       * @param owner Owner of the policy
       * @return
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @param rules List of rules to be applied for this policy
       * @return
       */
      public Builder rules(List<FirewallRule> rules) {
         this.rules = ImmutableList.copyOf(rules);
         return this;
      }

      /**
       * @param servers Servers which have nics with this policy applied
       * @return
       */
      public Builder servers(List<Server> servers) {
         this.servers = ImmutableList.copyOf(servers);
         return this;
      }

      /**
       * @param tags Many related resources. Can be either a list of URIs or list of individually nested resource data.
       * @return
       */
      public Builder tags(List<Tag> tags) {
         this.tags = ImmutableList.copyOf(tags);
         return this;
      }

      /**
       * @param resourceUri Resource URI
       * @return
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      /**
       * @param name Human readable name of the firewall policy
       * @return
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param uuid UUID of the policy
       * @return
       */
      @Override
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public static Builder fromFirewallPolicy(FirewallPolicy firewallPolicy) {
         return new Builder()
               .resourceUri(firewallPolicy.getResourceUri())
               .uuid(firewallPolicy.getUuid())
               .rules(firewallPolicy.getRules())
               .name(firewallPolicy.getName())
               .meta(firewallPolicy.getMeta())
               .tags(firewallPolicy.getTags());
      }

      public FirewallPolicy build() {
         return new FirewallPolicy(meta, name, owner, resourceUri, rules, servers, tags, uuid);
      }
   }

   private final Map<String, String> meta;
   private final Owner owner;
   private final List<FirewallRule> rules;
   private final List<Tag> tags;
   private final List<Server> servers;

   @ConstructorProperties({
         "meta", "name", "owner", "resource_uri", "rules", "servers", "tags", "uuid"
   })
   public FirewallPolicy(Map<String, String> meta, String name, Owner owner, URI resourceUri, List<FirewallRule> rules,
                         List<Server> servers, List<Tag> tags, String uuid) {
      super(uuid, name, resourceUri);
      this.meta = meta;
      this.owner = owner;
      this.rules = rules == null ? new ArrayList<FirewallRule>() : rules;
      this.tags = tags == null ? new ArrayList<Tag>() : tags;
      this.servers = servers == null ? new ArrayList<Server>() : servers;
   }

   /**
    * @return User assigned meta information for this policy
    */
   public Map<String, String> getMeta() {
      return meta;
   }

   /**
    * @return Human readable name of the firewall policy
    */
   public String getName() {
      return name;
   }

   /**
    * @return Owner of the policy
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return resource uri
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   /**
    * @return List of rules to be applied for this policy
    */
   public List<FirewallRule> getRules() {
      return rules;
   }

   /**
    * @return Servers which have nics with this policy applied
    */
   public List<Server> getServers() {
      return servers;
   }

   /**
    * @return Related resources URI list.
    */
   public List<Tag> getTags() {
      return tags;
   }

   /**
    * @return UUID of the policy
    */
   public String getUuid() {
      return uuid;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      FirewallPolicy that = (FirewallPolicy) o;

      if (meta != null ? !meta.equals(that.meta) : that.meta != null) return false;
      if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
      if (rules != null ? !rules.equals(that.rules) : that.rules != null) return false;
      if (servers != null ? !servers.equals(that.servers) : that.servers != null) return false;
      if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (meta != null ? meta.hashCode() : 0);
      result = 31 * result + (owner != null ? owner.hashCode() : 0);
      result = 31 * result + (rules != null ? rules.hashCode() : 0);
      result = 31 * result + (tags != null ? tags.hashCode() : 0);
      result = 31 * result + (servers != null ? servers.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "meta=" + meta +
            ", owner=" + owner +
            ", rules=" + rules +
            ", tags=" + tags +
            ", servers=" + servers +
            "]";
   }

   public NIC toNIC() {
      return new NIC.Builder().firewallPolicy(this).build();
   }
}
