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

import javax.inject.Named;
import java.beans.ConstructorProperties;

public class NIC {
   public static class Builder {
      private int bootOrder;
      private FirewallPolicy firewallPolicy;
      private IPConfiguration ipV4Configuration;
      private IPConfiguration ipV6Configuration;
      private String mac;
      private Model model;
      private NICStats runtime;
      private VLANInfo vlan;

      public Builder bootOrder(int bootOrder) {
         this.bootOrder = bootOrder;
         return this;
      }

      public Builder model(Model model) {
         this.model = model;
         return this;
      }

      public Builder vlan(VLANInfo vlan) {
         this.vlan = vlan;
         return this;
      }

      public Builder mac(String mac) {
         this.mac = mac;
         return this;
      }

      public Builder runtime(NICStats runtime) {
         this.runtime = runtime;
         return this;
      }

      public Builder firewallPolicy(FirewallPolicy firewallPolicy) {
         this.firewallPolicy = firewallPolicy;
         return this;
      }

      public Builder ipV4Configuration(IPConfiguration ipV4Configuration) {
         this.ipV4Configuration = ipV4Configuration;
         return this;
      }

      public Builder ipV6Configuration(IPConfiguration ipV6Configuration) {
         this.ipV6Configuration = ipV6Configuration;
         return this;
      }

      public NIC build() {
         return new NIC(bootOrder, firewallPolicy, ipV4Configuration, ipV6Configuration, mac, model, runtime, vlan);
      }

      public Builder fromNIC(NIC nic) {
         return new Builder()
               .bootOrder(nic.getBootOrder())
               .firewallPolicy(nic.getFirewallPolicy())
               .ipV4Configuration(nic.getIpV4Configuration())
               .ipV6Configuration(nic.getIpV6Configuration())
               .mac(nic.getMac())
               .model(nic.getModel())
               .runtime(nic.getRuntime())
               .vlan(nic.getVlan());
      }
   }

   @Named("boot_order")
   private final int bootOrder;
   @Named("firewall_policy")
   private final FirewallPolicy firewallPolicy;
   @Named("ip_v4_conf")
   private final IPConfiguration ipV4Configuration;
   @Named("ip_v6_conf")
   private final IPConfiguration ipV6Configuration;
   private final String mac;
   private final Model model;
   private final NICStats runtime;
   private final VLANInfo vlan;

   @ConstructorProperties({
         "boot_order", "firewall_policy", "ip_v4_conf", "ip_v6_conf",
         "mac", "model", "runtime", "vlan"
   })
   public NIC(int bootOrder, FirewallPolicy firewallPolicy, IPConfiguration ipV4Configuration,
              IPConfiguration ipV6Configuration, String mac, Model model, NICStats runtime, VLANInfo vlan) {
      this.bootOrder = bootOrder;
      this.firewallPolicy = firewallPolicy;
      this.ipV4Configuration = ipV4Configuration;
      this.ipV6Configuration = ipV6Configuration;
      this.mac = mac;
      this.model = model;
      this.runtime = runtime;
      this.vlan = vlan;
   }

   /**
    * @return Model of NIC.
    */
   public Model getModel() {
      return model;
   }

   /**
    * @return UUID of the private VLAN.
    */
   public VLANInfo getVlan() {
      return vlan;
   }

   /**
    * @return MAC address of the server NIC.
    */
   public String getMac() {
      return mac;
   }

   /**
    * @return Device boot order.
    */
   public int getBootOrder() {
      return bootOrder;
   }

   /**
    * @return Public IPv4 configuration.
    */
   public IPConfiguration getIpV4Configuration() {
      return ipV4Configuration;
   }

   /**
    * @return Public IPv6 configuration.
    */
   public IPConfiguration getIpV6Configuration() {
      return ipV6Configuration;
   }

   /**
    * @return Firewall policy
    */
   public FirewallPolicy getFirewallPolicy() {
      return firewallPolicy;
   }

   /**
    * @return NIC runtime information
    */
   public NICStats getRuntime() {
      return runtime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof NIC)) return false;

      NIC nic = (NIC) o;

      if (bootOrder != nic.bootOrder) return false;
      if (firewallPolicy != null ? !firewallPolicy.equals(nic.firewallPolicy) : nic.firewallPolicy != null)
         return false;
      if (ipV4Configuration != null ? !ipV4Configuration.equals(nic.ipV4Configuration) : nic.ipV4Configuration != null)
         return false;
      if (ipV6Configuration != null ? !ipV6Configuration.equals(nic.ipV6Configuration) : nic.ipV6Configuration != null)
         return false;
      if (mac != null ? !mac.equals(nic.mac) : nic.mac != null) return false;
      if (model != nic.model) return false;
      if (runtime != null ? !runtime.equals(nic.runtime) : nic.runtime != null) return false;
      if (vlan != null ? !vlan.equals(nic.vlan) : nic.vlan != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = bootOrder;
      result = 31 * result + (firewallPolicy != null ? firewallPolicy.hashCode() : 0);
      result = 31 * result + (ipV4Configuration != null ? ipV4Configuration.hashCode() : 0);
      result = 31 * result + (ipV6Configuration != null ? ipV6Configuration.hashCode() : 0);
      result = 31 * result + (mac != null ? mac.hashCode() : 0);
      result = 31 * result + (model != null ? model.hashCode() : 0);
      result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
      result = 31 * result + (vlan != null ? vlan.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "bootOrder=" + bootOrder +
            ", firewallPolicy='" + firewallPolicy + '\'' +
            ", ipV4Configuration=" + ipV4Configuration +
            ", ipV6Configuration=" + ipV6Configuration +
            ", mac='" + mac + '\'' +
            ", model=" + model +
            ", runtime=" + runtime +
            ", vlan='" + vlan + '\'' +
            "]";
   }
}
