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

import com.google.inject.name.Named;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.net.URI;

public class IP {

   public static class Builder {
      protected Owner owner;
      protected String uuid;
      protected Server server;
      protected URI resourceUri;

      /**
       * @param uuid Uuid of the ip.
       * @return IP Builder
       */
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @param owner Owner of the ip.
       * @return IP Builder
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @param server Server this IP assigned to
       * @return IP Builder
       */
      public Builder server(Server server) {
         this.server = server;
         return this;
      }

      /**
       * @param resourceUri Resource uri
       * @return IP Builder
       */
      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public IP build() {
         return new IP(uuid, owner, server, resourceUri);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Builder)) return false;

         Builder builder = (Builder) o;

         if (owner != null ? !owner.equals(builder.owner) : builder.owner != null) return false;
         if (resourceUri != null ? !resourceUri.equals(builder.resourceUri) : builder.resourceUri != null)
            return false;
         if (server != null ? !server.equals(builder.server) : builder.server != null) return false;
         if (uuid != null ? !uuid.equals(builder.uuid) : builder.uuid != null) return false;

         return true;
      }

      @Override
      public int hashCode() {
         int result = owner != null ? owner.hashCode() : 0;
         result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
         result = 31 * result + (server != null ? server.hashCode() : 0);
         result = 31 * result + (resourceUri != null ? resourceUri.hashCode() : 0);
         return result;
      }
   }

   protected final Owner owner;
   protected final String uuid;
   protected final Server server;
   @Named("resource_uri")
   protected final URI resourceUri;

   @ConstructorProperties({
         "uuid", "owner", "server", "resource_uri"
   })
   public IP(String uuid, Owner owner, Server server, URI resourceUri) {
      this.owner = owner;
      this.uuid = uuid;
      this.server = server;
      this.resourceUri = resourceUri;
   }

   /**
    * @return Uuid of the ip.
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * @return Owner of the ip.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return Server this IP assigned to
    */
   public Server getServer() {
      return server;
   }

   /**
    * @return Resource uri
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof IP)) return false;

      IP ip = (IP) o;

      if (owner != null ? !owner.equals(ip.owner) : ip.owner != null) return false;
      if (resourceUri != null ? !resourceUri.equals(ip.resourceUri) : ip.resourceUri != null) return false;
      if (server != null ? !server.equals(ip.server) : ip.server != null) return false;
      if (uuid != null ? !uuid.equals(ip.uuid) : ip.uuid != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = owner != null ? owner.hashCode() : 0;
      result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
      result = 31 * result + (server != null ? server.hashCode() : 0);
      result = 31 * result + (resourceUri != null ? resourceUri.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "owner=" + owner +
            ", uuid='" + uuid + '\'' +
            ", server=" + server +
            ", resourceUri=" + resourceUri +
            "]";
   }

   /**
    * Creates DHCP V4 NIC for attaching to server
    *
    * @param model          Interface model
    * @param firewallPolicy Firewall policy for interface
    * @return server's NIC
    */
   public static NIC createDHCPIPv4ConfNIC(Model model, FirewallPolicy firewallPolicy) {
      return new NIC.Builder()
            .ipV4Configuration(new IPConfiguration(IPConfigurationType.DHCP, null))
            .model(model)
            .firewallPolicy(firewallPolicy)
            .build();
   }

   /**
    * Creates DHCP V4 NIC for attaching to server
    *
    * @param model Interface model
    * @return server's NIC
    */
   public static NIC createDHCPIPv4ConfNIC(Model model) {
      return createDHCPIPv4ConfNIC(model, null);
   }

   /**
    * Creates DHCP V6 NIC for attaching to server
    *
    * @param model          Interface model
    * @param firewallPolicy Firewall policy for interface
    * @return server's NIC
    */
   public static NIC createDHCPIPv6ConfNIC(Model model, FirewallPolicy firewallPolicy) {
      return new NIC.Builder()
            .ipV4Configuration(new IPConfiguration(IPConfigurationType.DHCP, null))
            .model(model)
            .firewallPolicy(firewallPolicy)
            .build();
   }

   /**
    * Creates DHCP V6 NIC for attaching to server
    *
    * @param model Interface model
    * @return server's NIC
    */
   public static NIC createDHCPIPv6ConfNIC(Model model) {
      return createDHCPIPv6ConfNIC(model, null);
   }

   /**
    * Creates NIC with static IP for attaching to server
    *
    * @param model          Interface model
    * @param firewallPolicy Firewall policy
    * @return server's NIC
    */
   public NIC toNIC(Model model, FirewallPolicy firewallPolicy) {
      return new NIC.Builder()
            .ipV4Configuration(new IPConfiguration(IPConfigurationType.STATIC, this))
            .firewallPolicy(firewallPolicy)
            .build();
   }

   /**
    * Creates NIC with static IP for attaching to server
    *
    * @return server's NIC
    */
   public NIC toNIC() {
      return toNIC(null, null);
   }
}
