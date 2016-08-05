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
package org.apache.jclouds.oneandone.rest.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Types.RuleProtocol;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class FirewallPolicy {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract String defaultState();

   public abstract List<Rule> rules();

   public abstract List<ServerIp> serverIps();

   @Nullable
   public abstract String cloudpanelId();

   @SerializedNames({"id", "name", "description", "state", "creation_date", "default", "rules", "server_ips", "cloudpanel_id"})
   public static FirewallPolicy create(String id, String name, String description, String state, String creationDate, String defaultState, List<Rule> rules, List<ServerIp> serverIps, String cloudpanelId) {
      return new AutoValue_FirewallPolicy(id, name, description, state, creationDate, defaultState, rules == null ? ImmutableList.<Rule>of() : ImmutableList.copyOf(rules), serverIps == null ? ImmutableList.<ServerIp>of() : ImmutableList.copyOf(serverIps), cloudpanelId);
   }

   @AutoValue
   public abstract static class CreateFirewallPolicy {

      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract List<Rule.CreatePayload> rules();

      @SerializedNames({"name", "description", "rules"})
      public static CreateFirewallPolicy create(final String name, final String description, List<Rule.CreatePayload> rules) {
         return new AutoValue_FirewallPolicy_CreateFirewallPolicy(name, description, rules == null ? ImmutableList.<Rule.CreatePayload>of() : ImmutableList.copyOf(rules));
      }
   }

   @AutoValue
   public abstract static class UpdateFirewallPolicy {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @SerializedNames({"name", "description"})
      public static UpdateFirewallPolicy create(final String name, final String description) {
         return new AutoValue_FirewallPolicy_UpdateFirewallPolicy(name, description);
      }
   }

   @AutoValue
   public abstract static class ServerIp {

      public abstract String id();

      public abstract String ip();

      public abstract String serverName();

      @SerializedNames({"id", "ip", "server_name"})
      public static ServerIp create(String id, String ip, String serverName) {
         return new AutoValue_FirewallPolicy_ServerIp(id, ip, serverName);
      }

      @AutoValue
      public abstract static class CreateServerIp {

         public abstract List<String> serverIps();

         @SerializedNames({"server_ips"})
         public static CreateServerIp create(final List<String> serverIps) {
            return new AutoValue_FirewallPolicy_ServerIp_CreateServerIp(serverIps == null ? ImmutableList.<String>of() : ImmutableList.copyOf(serverIps));
         }
      }
   }

   @AutoValue
   public abstract static class Rule {

      public abstract String id();

      @Nullable
      public abstract RuleProtocol protocol();

      @Nullable
      public abstract Integer portFrom();

      @Nullable
      public abstract Integer portTo();

      @Nullable
      public abstract String source();

      @SerializedNames({"id", "protocol", "port_from", "port_to", "source"})
      public static Rule create(String id, RuleProtocol protocol, Integer portFrom, Integer portTo, String source) {
         return new AutoValue_FirewallPolicy_Rule(id, protocol, portFrom, portTo, source);
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract RuleProtocol protocol();

         @Nullable
         public abstract Integer portFrom();

         @Nullable
         public abstract Integer portTo();

         @Nullable
         public abstract String source();

         @SerializedNames({"protocol", "port_from", "port_to", "source"})
         public static CreatePayload create(RuleProtocol protocol, Integer portFrom, Integer portTo, String source) {
            return builder()
                    .portFrom(portFrom)
                    .portTo(portTo)
                    .protocol(protocol)
                    .source(source)
                    .build();
         }

         public static Builder builder() {
            return new AutoValue_FirewallPolicy_Rule_CreatePayload.Builder();
         }

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder protocol(RuleProtocol protocol);

            public abstract Builder portFrom(Integer portFrom);

            public abstract Builder portTo(Integer portTo);

            public abstract Builder source(String source);

            public abstract CreatePayload build();
         }
      }

      @AutoValue
      public abstract static class AddRule {

         public abstract List<CreatePayload> rules();

         @SerializedNames({"rules"})
         public static AddRule create(List<CreatePayload> rules) {
            return new AutoValue_FirewallPolicy_Rule_AddRule(rules == null ? ImmutableList.<CreatePayload>of() : ImmutableList.copyOf(rules));
         }
      }
   }
}
