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
import org.apache.jclouds.oneandone.rest.domain.Types.AlertIfType;
import org.apache.jclouds.oneandone.rest.domain.Types.ProcessAlertType;
import org.apache.jclouds.oneandone.rest.domain.Types.ProtocolType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class MonitoringPolicy {

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract String state();

   @Nullable
   public abstract Date creationDate();

   public abstract int isDefault();

   @Nullable
   public abstract String email();

   public abstract boolean agent();

   @Nullable
   public abstract List<Server> servers();

   public abstract Threshold thresholds();

   @Nullable
   public abstract List<Port> ports();

   @Nullable
   public abstract List<Process> processes();

   public abstract String cloudpanelId();

   @SerializedNames({"id", "name", "description", "state", "creation_date", "default", "email", "agent", "servers", "thresholds", "ports", "processes", "cloudpanel_id"})
   public static MonitoringPolicy create(String id, String name, String description, String state,
           Date creationDate, int isDefault, String email, boolean agent, List<Server> servers, Threshold thresholds, List<Port> ports, List<Process> processes, String cloudpanelId) {
      return new AutoValue_MonitoringPolicy(id, name, description, state, creationDate, isDefault, email, agent, servers == null ? ImmutableList.<Server>of() : ImmutableList.copyOf(servers), thresholds, ports == null ? ImmutableList.<Port>of() : ImmutableList.copyOf(ports), processes == null ? ImmutableList.<Process>of() : ImmutableList.copyOf(processes), cloudpanelId);
   }

   @AutoValue
   public abstract static class Server {

      public abstract String id();

      public abstract String name();

      @SerializedNames({"id", "name"})
      public static Server create(String id, String name) {
         return new AutoValue_MonitoringPolicy_Server(id, name);
      }

      @AutoValue
      public abstract static class CreateServer {

         public abstract List<String> servers();

         @SerializedNames({"servers"})
         public static CreateServer create(List<String> servers) {
            return new AutoValue_MonitoringPolicy_Server_CreateServer(servers == null ? ImmutableList.<String>of() : ImmutableList.copyOf(servers));
         }
      }
   }

   @AutoValue
   public abstract static class Threshold {

      public abstract Cpu cpu();

      public abstract Ram ram();

      @Nullable
      public abstract Disk disk();

      public abstract Transfer transfer();

      public abstract InternalPing internalPing();

      @SerializedNames({"cpu", "ram", "disk", "transfer", "internal_ping"})
      public static Threshold create(Cpu cpu, Ram ram, Disk disk, Transfer transfer, InternalPing internalPing) {
         return new AutoValue_MonitoringPolicy_Threshold(cpu, ram, disk, transfer, internalPing);
      }

      @AutoValue
      public abstract static class Cpu {

         public abstract Warning warning();

         public abstract Critical critical();

         @SerializedNames({"warning", "critical"})
         public static Cpu create(Warning warning, Critical critical) {
            return new AutoValue_MonitoringPolicy_Threshold_Cpu(warning, critical);
         }

         @AutoValue
         public abstract static class Warning {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Warning create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Cpu_Warning(value, alert);
            }
         }

         @AutoValue
         public abstract static class Critical {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Critical create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Cpu_Critical(value, alert);
            }

         }

      }

      @AutoValue
      public abstract static class Disk {

         public abstract Warning warning();

         public abstract Critical critical();

         @SerializedNames({"warning", "critical"})
         public static Disk create(Warning warning, Critical critical) {
            return new AutoValue_MonitoringPolicy_Threshold_Disk(warning, critical);
         }

         @AutoValue
         public abstract static class Warning {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Warning create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Disk_Warning(value, alert);
            }
         }

         @AutoValue
         public abstract static class Critical {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Critical create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Disk_Critical(value, alert);
            }

         }

      }

      @AutoValue
      public abstract static class Ram {

         public abstract Warning warning();

         public abstract Critical critical();

         @SerializedNames({"warning", "critical"})
         public static Ram create(Warning warning, Critical critical) {
            return new AutoValue_MonitoringPolicy_Threshold_Ram(warning, critical);
         }

         @AutoValue
         public abstract static class Warning {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Warning create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Ram_Warning(value, alert);
            }
         }

         @AutoValue
         public abstract static class Critical {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Critical create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Ram_Critical(value, alert);
            }

         }

      }

      @AutoValue
      public abstract static class Transfer {

         public abstract Warning warning();

         public abstract Critical critical();

         @SerializedNames({"warning", "critical"})
         public static Transfer create(Warning warning, Critical critical) {
            return new AutoValue_MonitoringPolicy_Threshold_Transfer(warning, critical);
         }

         @AutoValue
         public abstract static class Warning {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Warning create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Transfer_Warning(value, alert);
            }
         }

         @AutoValue
         public abstract static class Critical {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Critical create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_Transfer_Critical(value, alert);
            }
         }
      }

      @AutoValue
      public abstract static class InternalPing {

         public abstract Warning warning();

         public abstract Critical critical();

         @SerializedNames({"warning", "critical"})
         public static InternalPing create(Warning warning, Critical critical) {
            return new AutoValue_MonitoringPolicy_Threshold_InternalPing(warning, critical);
         }

         @AutoValue
         public abstract static class Warning {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Warning create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_InternalPing_Warning(value, alert);
            }
         }

         @AutoValue
         public abstract static class Critical {

            public abstract int value();

            public abstract boolean alert();

            @SerializedNames({"value", "alert"})
            public static Critical create(int value, boolean alert) {
               return new AutoValue_MonitoringPolicy_Threshold_InternalPing_Critical(value, alert);
            }

         }
      }
   }

   @AutoValue
   public abstract static class Port {

      public abstract String id();

      public abstract ProtocolType protocol();

      public abstract int port();

      public abstract AlertIfType alertIf();

      public abstract boolean emailNotification();

      @SerializedNames({"id", "protocol", "port", "alert_if", "email_notification"})
      public static Port create(String id, ProtocolType protocol, int port, AlertIfType alertIf, boolean emailNotification) {
         return new AutoValue_MonitoringPolicy_Port(id, protocol, port, alertIf, emailNotification);
      }

      @AutoValue
      public abstract static class AddPort {

         public abstract int port();

         public abstract AlertIfType alertIf();

         public abstract boolean emailNotification();

         public abstract ProtocolType protocol();

         @SerializedNames({"port", "alert_if", "email_notification", "protocol"})
         public static AddPort create(int port, AlertIfType alertIf, boolean emailNotification, ProtocolType protocol) {
            return new AutoValue_MonitoringPolicy_Port_AddPort(port, alertIf, emailNotification, protocol);
         }
      }

      @AutoValue
      public abstract static class CreatePort {

         public abstract List<AddPort> ports();

         @SerializedNames({"ports"})
         public static CreatePort create(List<AddPort> ports) {
            return new AutoValue_MonitoringPolicy_Port_CreatePort(ports == null ? ImmutableList.<AddPort>of() : ImmutableList.copyOf(ports));
         }
      }

      @AutoValue
      public abstract static class UpdatePort {

         public abstract AddPort ports();

         @SerializedNames({"ports"})
         public static UpdatePort create(AddPort ports) {
            return new AutoValue_MonitoringPolicy_Port_UpdatePort(ports);
         }
      }
   }

   @AutoValue
   public abstract static class Process {

      public abstract String id();

      public abstract String process();

      public abstract int port();

      public abstract ProcessAlertType alertIf();

      public abstract boolean emailNotification();

      @SerializedNames({"id", "process", "port", "alert_if", "email_notification"})
      public static Process create(String id, String process, int port, ProcessAlertType alertIf, boolean emailNotification) {
         return new AutoValue_MonitoringPolicy_Process(id, process, port, alertIf, emailNotification);
      }

      @AutoValue
      public abstract static class AddProcess {

         public abstract String process();

         public abstract AlertIfType alertIf();

         public abstract boolean emailNotification();

         @SerializedNames({"process", "alert_if", "email_notification"})
         public static AddProcess create(String process, AlertIfType alertIf, boolean emailNotification) {
            return new AutoValue_MonitoringPolicy_Process_AddProcess(process, alertIf, emailNotification);
         }
      }

      @AutoValue
      public abstract static class CreateProcess {

         public abstract List<AddProcess> processes();

         @SerializedNames({"processes"})
         public static CreateProcess create(List<AddProcess> processes) {
            return new AutoValue_MonitoringPolicy_Process_CreateProcess(processes == null ? ImmutableList.<AddProcess>of() : ImmutableList.copyOf(processes));
         }
      }

      @AutoValue
      public abstract static class UpdateProcess {

         public abstract Process.AddProcess processes();

         @SerializedNames({"processes"})
         public static UpdateProcess create(Process.AddProcess processes) {
            return new AutoValue_MonitoringPolicy_Process_UpdateProcess(processes);
         }
      }
   }

   @AutoValue
   public abstract static class CreatePolicy {

      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract String email();

      public abstract boolean agent();

      public abstract Threshold thresholds();

      public abstract List<Port.AddPort> ports();

      public abstract List<Process.AddProcess> processes();

      @SerializedNames({"name", "description", "email", "agent", "thresholds", "ports", "processes"})
      public static CreatePolicy create(String name, String description, String email,
              boolean agent, Threshold thresholds, List<Port.AddPort> ports, List<Process.AddProcess> processes) {
         return builder()
                 .agent(agent)
                 .description(description)
                 .email(email)
                 .name(name)
                 .ports(ports == null ? ImmutableList.<Port.AddPort>of() : ImmutableList.copyOf(ports))
                 .processes(processes == null ? ImmutableList.<Process.AddProcess>of() : ImmutableList.copyOf(processes))
                 .thresholds(thresholds)
                 .build();

      }

      public static Builder builder() {
         return new AutoValue_MonitoringPolicy_CreatePolicy.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder email(String email);

         public abstract Builder agent(boolean agent);

         public abstract Builder thresholds(Threshold thresholds);

         public abstract Builder ports(List<Port.AddPort> ports);

         public abstract Builder processes(List<Process.AddProcess> processes);

         public abstract CreatePolicy build();
      }
   }

   @AutoValue
   public abstract static class UpdatePolicy {

      @Nullable
      public abstract String name();

      @Nullable
      public abstract String description();

      public abstract String email();

      public abstract boolean agent();

      public abstract Threshold thresholds();

      @SerializedNames({"name", "description", "email", "agent", "thresholds"})
      public static UpdatePolicy create(String name, String description, String email,
              boolean agent, Threshold thresholds, List<Port.AddPort> ports, List<Process.AddProcess> processes) {
         return builder()
                 .agent(agent)
                 .description(description)
                 .email(email)
                 .name(name)
                 .thresholds(thresholds)
                 .build();

      }

      public static Builder builder() {
         return new AutoValue_MonitoringPolicy_UpdatePolicy.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         public abstract Builder description(String description);

         public abstract Builder email(String email);

         public abstract Builder agent(boolean agent);

         public abstract Builder thresholds(Threshold thresholds);

         public abstract UpdatePolicy build();
      }
   }
}
