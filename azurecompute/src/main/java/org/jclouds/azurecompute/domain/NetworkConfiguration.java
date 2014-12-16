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
package org.jclouds.azurecompute.domain;

import static com.google.common.collect.ImmutableList.copyOf;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NetworkConfiguration {

   @AutoValue
   public abstract static class Subnet {

      Subnet() {} // For AutoValue only!

      public abstract String name();

      @Nullable public abstract String addressPrefix();

      @Nullable public abstract String networkSecurityGroup();

      public static Subnet create(String name, String addressPrefix, String networkSecurityGroup) {
         return new AutoValue_NetworkConfiguration_Subnet(name, addressPrefix, networkSecurityGroup);
      }
   }

   @AutoValue
   public abstract static class AddressSpace {

      AddressSpace() {} // For AutoValue only!

      @Nullable public abstract String addressPrefix();

      public static AddressSpace create(String addressPrefix) {
         return new AutoValue_NetworkConfiguration_AddressSpace(addressPrefix);
      }
   }

   @AutoValue
   public abstract static class VirtualNetworkSite {

      VirtualNetworkSite() {} // For AutoValue only!

      @Nullable public abstract String id();

      @Nullable public abstract String name();

      @Nullable public abstract String location();

      public abstract AddressSpace addressSpace();

      public abstract List<Subnet> subnets();

      public static VirtualNetworkSite create(String id, String name, String location, AddressSpace addressSpace, List<Subnet> subnets) {
         return new AutoValue_NetworkConfiguration_VirtualNetworkSite(id, name, location, addressSpace, subnets);
      }

   }

   @AutoValue
   public abstract static class VirtualNetworkConfiguration {

      VirtualNetworkConfiguration() {
      } // For AutoValue only!

      @Nullable public abstract String dns();
      @Nullable public abstract List<VirtualNetworkSite> virtualNetworkSites();

      public static VirtualNetworkConfiguration create(String dns, List<VirtualNetworkSite> virtualNetworkSites) {
         return new AutoValue_NetworkConfiguration_VirtualNetworkConfiguration(dns, copyOf(virtualNetworkSites));
      }
   }

   public NetworkConfiguration() {} // For AutoValue only!

   public abstract VirtualNetworkConfiguration virtualNetworkConfiguration();

   public static NetworkConfiguration create(VirtualNetworkConfiguration virtualNetworkConfiguration) {
      return new AutoValue_NetworkConfiguration(virtualNetworkConfiguration);
   }
}
