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

@AutoValue
public abstract class NIC {

   NIC() {
   }

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String privateIpv4();

   @Nullable
   public abstract String ipv6();

   public abstract String vlanId();

   @Nullable
   public abstract String vlanName();

   @Nullable
   public abstract State state();

   @SerializedNames({ "id", "privateIpv4", "ipv6", "vlanId", "vlanName", "state" })
   public static NIC create(String id, String privateIpv4, String ipv6, String vlanId, String vlanName, State state) {
      return builder().id(id).privateIpv4(privateIpv4).ipv6(ipv6).vlanId(vlanId).vlanName(vlanName).state(state)
            .build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder privateIpv4(String privateIpv4);

      public abstract Builder ipv6(String ipv6);

      public abstract Builder vlanId(String vlanId);

      public abstract Builder vlanName(String vlanName);

      public abstract Builder state(State state);

      public abstract NIC build();
   }

   public static Builder builder() {
      return new AutoValue_NIC.Builder();
   }
}
