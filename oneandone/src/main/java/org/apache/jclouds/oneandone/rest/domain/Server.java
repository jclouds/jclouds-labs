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
import java.util.Date;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Types.ServerAction;
import org.apache.jclouds.oneandone.rest.domain.Types.ServerActionMethod;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Server {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract Date creationDate();

   @Nullable
   public abstract String firstPassword();

   @Nullable
   public abstract String description();

   @Nullable

   public abstract Status status();

   @Nullable

   public abstract Hardware hardware();

   @Nullable
   public abstract Image image();

   @Nullable
   public abstract Dvd dvd();

   @Nullable
   public abstract Snapshot snapshot();

   @Nullable
   public abstract DataCenter datacenter();

   public abstract List<ServerIp> ips();

   public abstract List<Alert> alerts();

   @Nullable
   public abstract ServerMonitoringPolicy monitoringPolicy();

   public abstract List<ServerPrivateNetwork> privateNetworks();

   @SerializedNames({"id", "name", "creation_date", "first_password", "description", "status", "hardware", "image", "dvd", "snapshot", "datacenter", "ips", "alerts", "monitoring_policy", "private_networks"})
   public static Server create(String id, String name, Date creationDate, String firstPassword, String description, Status status, Hardware hardware, Image image, Dvd dvd, Snapshot snapshot, DataCenter datacenter, List<ServerIp> ips, List<Alert> alerts, ServerMonitoringPolicy policy, List<ServerPrivateNetwork> privateNetworks) {
      return new AutoValue_Server(id, name, creationDate, firstPassword, description, status, hardware, image, dvd, snapshot, datacenter,
              ips == null ? ImmutableList.<ServerIp>of() : ips, alerts == null ? ImmutableList.<Alert>of() : alerts, policy,
              privateNetworks == null ? ImmutableList.<ServerPrivateNetwork>of() : privateNetworks);
   }

   @AutoValue
   public abstract static class Alert {

      public abstract List<WarningAlert> warning();

      public abstract List<CriticalAlert> critical();

      @SerializedNames({"warning", "critical"})
      public static Alert create(List<WarningAlert> warning, List<CriticalAlert> critical) {
         return new AutoValue_Server_Alert(warning == null ? ImmutableList.<WarningAlert>of() : warning,
                 critical == null ? ImmutableList.<CriticalAlert>of() : critical);
      }
   }

   @AutoValue
   public abstract static class CriticalAlert {

      public abstract String type();

      public abstract String description();

      public abstract String date();

      @SerializedNames({"type", "description", "date"})
      public static CriticalAlert create(String type, String description, String date) {
         return new AutoValue_Server_CriticalAlert(type, description, date);
      }
   }

   @AutoValue
   public abstract static class WarningAlert {

      public abstract String type();

      public abstract String description();

      public abstract Date date();

      @SerializedNames({"type", "description", "date"})
      public static WarningAlert create(String type, String description, Date date) {
         return new AutoValue_Server_WarningAlert(type, description, date);
      }
   }

   @AutoValue
   public abstract static class UpdateServerResponse {

      public abstract String id();

      public abstract String name();

      @Nullable
      public abstract Date creationDate();

      @Nullable
      public abstract String firstPassword();

      public abstract String description();

      @Nullable
      public abstract Status status();

      @Nullable
      public abstract Hardware hardware();

      @Nullable
      public abstract Image image();

      @Nullable
      public abstract Dvd dvd();

      @Nullable
      public abstract Snapshot snapshot();

      @Nullable
      public abstract DataCenter datacenter();

      public abstract List<String> ips();

      public abstract List<Alert> alerts();

      public abstract List<ServerMonitoringPolicy> monitoringPolicy();

      public abstract List<ServerPrivateNetwork> privateNetworks();

      @SerializedNames({"id", "name", "creation_date", "first_password", "description", "status", "hardware", "image", "dvd", "snapshot", "datacenter", "ips", "alerts", "monitoring_policy", "private_networks"})
      public static UpdateServerResponse create(String id, String name, Date creationDate, String firstPassword, String description, Status status, Hardware hardware, Image image, Dvd dvd, Snapshot snapshot, DataCenter datacenter, List<String> ips, List<Alert> alerts, List<ServerMonitoringPolicy> policy, List<ServerPrivateNetwork> privateNetworks) {
         return new AutoValue_Server_UpdateServerResponse(id, name, creationDate, firstPassword, description, status, hardware, image, dvd,
                 snapshot, datacenter, ips == null ? ImmutableList.<String>of() : ips, alerts == null ? ImmutableList.<Alert>of() : alerts,
                 policy == null ? ImmutableList.<ServerMonitoringPolicy>of() : policy, privateNetworks == null ? ImmutableList.<ServerPrivateNetwork>of() : privateNetworks);
      }
   }

   @AutoValue
   public abstract static class CreateServer {

      public abstract String name();

      public abstract String description();

      public abstract Hardware.CreateHardware hardware();

      public abstract String applianceId();

      @Nullable
      public abstract String dataCenterId();

      @Nullable
      public abstract String password();

      @Nullable
      public abstract String regionId();

      @Nullable
      public abstract Boolean powerOn();

      @Nullable
      public abstract String firewallPolicyId();

      @Nullable
      public abstract String ipId();

      @Nullable
      public abstract String loadrBalancerId();

      @Nullable
      public abstract String monitoringPolicyId();

      @Nullable
      public abstract String rsaKey();

      @SerializedNames({"name", "description", "hardware", "appliance_id", "datacenter_id", "password", "region_id", "power_on", "firewall_policy_id", "ip_id", "loadr_balancer_id", "monitoring_policy_id", "rsa_key"})
      public static CreateServer create(final String name, final String description, final Hardware.CreateHardware hardware, final String applianceId,
              final String dataCenterId, final String password, final String regionId, final Boolean powerOn, final String firewallPolicyId,
              final String ipId, final String loadrBalancerId, final String monitoringPolicyId, final String rsaKey) {
         return builder()
                 .name(name)
                 .description(description)
                 .hardware(hardware)
                 .applianceId(applianceId)
                 .dataCenterId(dataCenterId)
                 .password(password)
                 .regionId(regionId)
                 .powerOn(powerOn)
                 .firewallPolicyId(firewallPolicyId)
                 .ipId(ipId)
                 .rsaKey(rsaKey)
                 .loadrBalancerId(loadrBalancerId)
                 .monitoringPolicyId(monitoringPolicyId)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Server_CreateServer.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder hardware(Hardware.CreateHardware hardware);

         public abstract Builder applianceId(String applianceId);

         public abstract Builder dataCenterId(String dataCenterId);

         public abstract Builder password(String password);

         public abstract Builder regionId(String regionId);

         public abstract Builder powerOn(Boolean powerOn);

         public abstract Builder firewallPolicyId(String firewallPolicyId);

         public abstract Builder ipId(String ipId);

         public abstract Builder loadrBalancerId(String loadrBalancerId);

         public abstract Builder monitoringPolicyId(String monitoringPolicyId);

         public abstract Builder rsaKey(String rsaKey);

         public abstract CreateServer build();
      }
   }

   @AutoValue
   public abstract static class CreateFixedInstanceServer {

      public abstract String name();

      public abstract String description();

      public abstract FixedInstanceHardware hardware();

      public abstract String applianceId();

      @Nullable
      public abstract String dataCenterId();

      @Nullable
      public abstract String password();

      @Nullable
      public abstract String regionId();

      @Nullable
      public abstract Boolean powerOn();

      @Nullable
      public abstract String firewallPolicyId();

      @Nullable
      public abstract String ipId();

      @Nullable
      public abstract String loadrBalancerId();

      @Nullable
      public abstract String monitoringPolicyId();

      @SerializedNames({"name", "description", "hardware", "appliance_id", "datacenter_id", "password", "region_id", "power_on", "firewall_policy_id", "ip_id", "loadr_balancer_id", "monitoring_policy_id"})
      public static CreateFixedInstanceServer create(String name, String description, FixedInstanceHardware hardware, String applianceId, String dataCenterId, String password,
              String regionId, Boolean powerOn, String firewallPolicyId, String ipId, String loadrBalancerId, String monitoringPolicyId) {
         return builder()
                 .name(name)
                 .description(description)
                 .hardware(hardware)
                 .applianceId(applianceId)
                 .dataCenterId(dataCenterId)
                 .password(password)
                 .regionId(regionId)
                 .powerOn(powerOn)
                 .firewallPolicyId(firewallPolicyId)
                 .ipId(ipId)
                 .loadrBalancerId(loadrBalancerId)
                 .monitoringPolicyId(monitoringPolicyId)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Server_CreateFixedInstanceServer.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder hardware(FixedInstanceHardware hardware);

         public abstract Builder applianceId(String applianceId);

         public abstract Builder dataCenterId(String dataCenterId);

         public abstract Builder password(String password);

         public abstract Builder regionId(String regionId);

         public abstract Builder powerOn(Boolean powerOn);

         public abstract Builder firewallPolicyId(String firewallPolicyId);

         public abstract Builder ipId(String ipId);

         public abstract Builder loadrBalancerId(String loadrBalancerId);

         public abstract Builder monitoringPolicyId(String monitoringPolicyId);

         public abstract CreateFixedInstanceServer build();
      }
   }

   @AutoValue
   public abstract static class UpdateImage {

      public abstract String id();

      public abstract String password();

      @SerializedNames({"id", "password"})
      public static UpdateImage create(String id, String password) {
         return new AutoValue_Server_UpdateImage(id, password);
      }
   }

   @AutoValue
   public abstract static class Clone {

      public abstract String datacenterId();

      public abstract String name();

      @SerializedNames({"datacenter_id", "name"})
      public static Clone create(String datacenterId, String name) {
         return new AutoValue_Server_Clone(datacenterId, name);
      }
   }

   @AutoValue
   public abstract static class UpdateServer {

      public abstract String name();

      public abstract String description();

      @SerializedNames({"name", "description"})
      public static UpdateServer create(String name, String description) {
         return new AutoValue_Server_UpdateServer(name, description);
      }
   }

   @AutoValue
   public abstract static class UpdateStatus {

      public abstract ServerAction action();

      public abstract ServerActionMethod method();

      @SerializedNames({"action", "method"})
      public static UpdateStatus create(ServerAction action, ServerActionMethod method) {
         return new AutoValue_Server_UpdateStatus(action, method);
      }
   }
}
