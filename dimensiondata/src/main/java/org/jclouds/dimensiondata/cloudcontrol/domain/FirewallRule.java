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
public abstract class FirewallRule {

   public static Builder builder() {
      return new AutoValue_FirewallRule.Builder();
   }

   FirewallRule() {
   } // For AutoValue only!

   public abstract String id();

   public abstract String name();

   public abstract String ruleType();

   public abstract String networkDomainId();

   public abstract String state();

   public abstract String action();

   public abstract String ipVersion();

   public abstract String protocol();

   public abstract String datacenterId();

   public abstract FirewallRuleTarget source();

   public abstract FirewallRuleTarget destination();

   public abstract Boolean enabled();

   @Nullable
   public abstract Placement placement();

   @SerializedNames({ "id", "name", "ruleType", "networkDomainId", "state", "action", "ipVersion", "protocol",
         "datacenterId", "source", "destination", "enabled", "placement" })
   public static FirewallRule create(String id, String name, String ruleType, String networkDomainId, String state,
         String action, String ipVersion, String protocol, String datacenterId, FirewallRuleTarget source,
         FirewallRuleTarget destination, Boolean enabled, Placement placement) {
      return builder().id(id).name(name).ruleType(ruleType).networkDomainId(networkDomainId).state(state).action(action)
            .ipVersion(ipVersion).protocol(protocol).datacenterId(datacenterId).source(source).destination(destination)
            .enabled(enabled).placement(placement).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder ruleType(String ruleType);

      public abstract Builder networkDomainId(String networkDomainId);

      public abstract Builder state(String state);

      public abstract Builder action(String action);

      public abstract Builder ipVersion(String ipVersion);

      public abstract Builder protocol(String protocol);

      public abstract Builder datacenterId(String datacenterId);

      public abstract Builder source(FirewallRuleTarget source);

      public abstract Builder destination(FirewallRuleTarget destination);

      public abstract Builder enabled(Boolean enabled);

      public abstract Builder placement(Placement placement);

      public abstract FirewallRule build();
   }

}
