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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;

@AutoValue
public abstract class Vlan {

   Vlan() {
   }

   public static Builder builder() {
      return new AutoValue_Vlan.Builder();
   }

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   public abstract String datacenterId();

   public abstract State state();

   public abstract Date createTime();

   public abstract String ipv4GatewayAddress();

   public abstract String ipv6GatewayAddress();

   public abstract NetworkDomain networkDomain();

   public abstract IpRange privateIpv4Range();

   public abstract IpRange ipv6Range();

   @SerializedNames({ "id", "name", "description", "datacenterId", "state", "createTime", "ipv4GatewayAddress",
         "ipv6GatewayAddress", "networkDomain", "privateIpv4Range", "ipv6Range" })
   public static Vlan create(String id, String name, String description, String datacenterId, State state,
         Date createTime, String ipv4GatewayAddress, String ipv6GatewayAddress, NetworkDomain networkDomain,
         IpRange privateIpv4Range, IpRange ipv6Range) {
      return builder().id(id).name(name).description(description).datacenterId(datacenterId).state(state)
            .createTime(createTime).ipv4GatewayAddress(ipv4GatewayAddress).ipv6GatewayAddress(ipv6GatewayAddress)
            .networkDomain(networkDomain).privateIpv4Range(privateIpv4Range).ipv6Range(ipv6Range).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder description(String description);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder state(State state);

      public abstract Builder createTime(Date createTime);

      public abstract Builder ipv4GatewayAddress(String ipv4GatewayAddress);

      public abstract Builder ipv6GatewayAddress(String ipv6GatewayAddress);

      public abstract Builder networkDomain(NetworkDomain networkDomain);

      public abstract Builder privateIpv4Range(IpRange privateIpv4Range);

      public abstract Builder ipv6Range(IpRange ipv6Range);

      public abstract Vlan build();
   }

}
