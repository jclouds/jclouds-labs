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
import org.apache.jclouds.oneandone.rest.domain.Types.HealthCheckTestTypes;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class LoadBalancer {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract String creationDate();

   @Nullable
   public abstract String ip();

   @Nullable
   public abstract HealthCheckTestTypes healthCheckTest();

   public abstract int healthCheckInterval();

   @Nullable
   public abstract String healthCheckPath();

   @Nullable
   public abstract String healthCheckPathParser();

   public abstract Boolean persistence();

   @Nullable
   public abstract Integer persistenceTime();

   public abstract Types.LoadBalancerMethod method();

   public abstract DataCenter datacenter();

   public abstract List<Rule> rules();

   public abstract List<ServerIp> serverIps();

   public abstract String cloudpanelId();

   @SerializedNames({"id", "name", "description", "state", "creation_date", "ip", "health_check_test", "health_check_interval", "health_check_path",
      "health_check_path_parser", "persistence", "persistence_time", "method", "datacenter", "rules", "server_ips", "cloudpanel_id"})
   public static LoadBalancer create(String id, String name, String description, String state, String creationDate, String ip, HealthCheckTestTypes healthCheckTest, int healthCheckInterval, String healthCheckPath, String healthCheckPathParser, Boolean persistence, Integer persistenceTime, Types.LoadBalancerMethod method, DataCenter datacenter, List<Rule> rules, List<ServerIp> serverIps, String cloudpanelId) {
      return new AutoValue_LoadBalancer(id, name, description, state, creationDate, ip, healthCheckTest, healthCheckInterval, healthCheckPath, healthCheckPathParser, persistence, persistenceTime, method, datacenter, rules == null ? ImmutableList.<Rule>of() : ImmutableList.copyOf(rules), serverIps == null ? ImmutableList.<ServerIp>of() : ImmutableList.copyOf(serverIps), cloudpanelId
      );
   }

   @AutoValue
   public abstract static class CreateLoadBalancer {

      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract HealthCheckTestTypes healthCheckTest();

      public abstract int healthCheckInterval();

      @Nullable
      public abstract String healthCheckPath();

      @Nullable
      public abstract String healthCheckParse();

      public abstract Boolean persistence();

      @Nullable
      public abstract Integer persistenceTime();

      public abstract Types.LoadBalancerMethod method();

      @Nullable
      public abstract String datacenterId();

      public abstract List<Rule.CreatePayload> rules();

      @SerializedNames({"name", "description", "health_check_test", "health_check_interval", "health_check_path", "health_check_parse", "persistence", "persistence_time", "method", "datacenter_id", "rules"})
      public static CreateLoadBalancer create(final String name, final String description, final HealthCheckTestTypes healthCheckTest, final int healthCheckInterval, final String healthCheckPath,
              final String healthCheckParse, final Boolean persistence, final Integer persistenceTime, final Types.LoadBalancerMethod method, final String dataCenterId, final List<Rule.CreatePayload> rules) {
         return builder()
                 .name(name)
                 .description(description)
                 .healthCheckTest(healthCheckTest)
                 .healthCheckInterval(healthCheckInterval)
                 .healthCheckPath(healthCheckPath)
                 .healthCheckParse(healthCheckParse)
                 .persistence(persistence)
                 .persistenceTime(persistenceTime)
                 .method(method)
                 .datacenterId(dataCenterId)
                 .rules(rules == null ? ImmutableList.<Rule.CreatePayload>of() : ImmutableList.copyOf(rules))
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancer_CreateLoadBalancer.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder healthCheckTest(HealthCheckTestTypes healthCheckTest);

         public abstract Builder healthCheckInterval(int healthCheckInterval);

         public abstract Builder healthCheckPath(String healthCheckPath);

         public abstract Builder healthCheckParse(String healthCheckParse);

         public abstract Builder persistence(Boolean persistence);

         public abstract Builder persistenceTime(Integer persistenceTime);

         public abstract Builder method(Types.LoadBalancerMethod method);

         public abstract Builder datacenterId(String datacenterId);

         public abstract Builder rules(List<Rule.CreatePayload> rules);

         abstract List<Rule.CreatePayload> rules();

         public CreateLoadBalancer build() {
            rules(rules() != null ? ImmutableList.copyOf(rules()) : ImmutableList.<Rule.CreatePayload>of());
            return autoBuild();
         }

         abstract CreateLoadBalancer autoBuild();
      }
   }

   @AutoValue
   public abstract static class UpdateLoadBalancer {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      @Nullable
      public abstract HealthCheckTestTypes healthCheckTest();

      @Nullable
      public abstract Integer healthCheckInterval();

      public abstract Boolean persistence();

      @Nullable
      public abstract Integer persistenceTime();

      public abstract Types.LoadBalancerMethod method();

      @SerializedNames({"name", "description", "health_check_test", "health_check_interval", "persistence", "persistence_time", "method"})
      public static UpdateLoadBalancer create(final String name, final String description, final HealthCheckTestTypes healthCheckTest, final int healthCheckInterval,
              final Boolean persistence, final Integer persistenceTime, final Types.LoadBalancerMethod method) {
         return builder()
                 .name(name)
                 .description(description)
                 .healthCheckTest(healthCheckTest)
                 .healthCheckInterval(healthCheckInterval)
                 .persistence(persistence)
                 .persistenceTime(persistenceTime)
                 .method(method)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancer_UpdateLoadBalancer.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder healthCheckTest(HealthCheckTestTypes healthCheckTest);

         public abstract Builder healthCheckInterval(Integer healthCheckInterval);

         public abstract Builder persistence(Boolean persistence);

         public abstract Builder persistenceTime(Integer persistenceTime);

         public abstract Builder method(Types.LoadBalancerMethod method);

         public abstract UpdateLoadBalancer build();
      }
   }

   @AutoValue
   public abstract static class ServerIp {

      public abstract String id();

      public abstract String ip();

      public abstract String serverName();

      @SerializedNames({"id", "ip", "server_name"})
      public static ServerIp create(String id, String ip, String serverName) {
         return new AutoValue_LoadBalancer_ServerIp(id, ip, serverName);
      }

      @AutoValue
      public abstract static class CreateServerIp {

         public abstract List<String> serverIps();

         @SerializedNames({"server_ips"})
         public static CreateServerIp create(final List<String> serverIps) {
            return new AutoValue_LoadBalancer_ServerIp_CreateServerIp(serverIps == null ? ImmutableList.<String>of() : ImmutableList.copyOf(serverIps));
         }
      }
   }

   @AutoValue
   public abstract static class Rule {

      @Nullable
      public abstract String id();

      @Nullable
      public abstract Types.RuleProtocol protocol();

      @Nullable
      public abstract Integer portBalancer();

      @Nullable
      public abstract Integer portServer();

      @Nullable
      public abstract String source();

      @SerializedNames({"id", "protocol", "port_balancer", "port_server", "source"})
      public static Rule create(String id, Types.RuleProtocol protocol, Integer portBalancer, Integer portServer, String source) {
         return new AutoValue_LoadBalancer_Rule(id, protocol, portBalancer, portServer, source);
      }

      @AutoValue
      public abstract static class CreatePayload {

         public abstract Types.RuleProtocol protocol();

         @Nullable
         public abstract Integer portBalancer();

         @Nullable
         public abstract Integer portServer();

         @Nullable
         public abstract String source();

         @SerializedNames({"protocol", "port_balancer", "port_server", "source"})
         public static CreatePayload create(Types.RuleProtocol protocol, Integer portBalancer, Integer portServer, String source) {
            return builder()
                    .portBalancer(portBalancer)
                    .portServer(portServer)
                    .protocol(protocol)
                    .source(source)
                    .build();
         }

         public static Builder builder() {
            return new AutoValue_LoadBalancer_Rule_CreatePayload.Builder();
         }

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder protocol(Types.RuleProtocol protocol);

            public abstract Builder portBalancer(Integer portBalancer);

            public abstract Builder portServer(Integer portServer);

            public abstract Builder source(String source);

            public abstract CreatePayload build();
         }
      }

      @AutoValue
      public abstract static class AddRule {

         public abstract List<CreatePayload> rules();

         @SerializedNames({"rules"})
         public static AddRule create(List<CreatePayload> rules) {
            return new AutoValue_LoadBalancer_Rule_AddRule(rules == null ? ImmutableList.<CreatePayload>of() : ImmutableList.copyOf(rules));
         }
      }
   }
}
