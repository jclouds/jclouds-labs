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
import org.apache.jclouds.oneandone.rest.domain.Types.ServerState;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class MonitoringCenter {

    public abstract String id();

    public abstract String name();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract Status status();

    @Nullable
    public abstract Alerts alerts();

    @Nullable
    public abstract Agent agent();

    @SerializedNames({"id", "name", "description", "status", "alerts", "agent"})
    public static MonitoringCenter create(String id, String name, String description, Status status, Alerts alerts, Agent agent) {
        return new AutoValue_MonitoringCenter(id, name, description, status, alerts, agent);
    }

    @AutoValue
    public abstract static class Status {

        @Nullable
        public abstract Cpu cpu();

        @Nullable
        public abstract Disk disk();

        @Nullable
        public abstract InternalPing internalPing();

        @Nullable
        public abstract Ram ram();

        @Nullable
        public abstract Transfer transfer();

        public abstract ServerState state();

        @SerializedNames({"cpu", "disk", "internal_ping", "ram", "transfer", "state"})
        public static Status create(Cpu cpu, Disk disk, InternalPing internalPing, Ram ram, Transfer transfer, ServerState state) {
            return new AutoValue_MonitoringCenter_Status(cpu, disk, internalPing, ram, transfer, state);
        }

        @AutoValue
        public abstract static class Cpu {

            @Nullable
            public abstract String state();

            @SerializedNames({"state"})
            public static Cpu create(String state) {
                return new AutoValue_MonitoringCenter_Status_Cpu(state);
            }
        }

        @AutoValue
        public abstract static class Disk {

            @Nullable
            public abstract String state();

            @SerializedNames({"state"})
            public static Disk create(String state) {
                return new AutoValue_MonitoringCenter_Status_Disk(state);
            }
        }

        @AutoValue
        public abstract static class InternalPing {

            @Nullable
            public abstract String state();

            @SerializedNames({"state"})
            public static InternalPing create(String state) {
                return new AutoValue_MonitoringCenter_Status_InternalPing(state);
            }
        }

        @AutoValue
        public abstract static class Ram {

            @Nullable
            public abstract String state();

            @SerializedNames({"state"})
            public static Ram create(String state) {
                return new AutoValue_MonitoringCenter_Status_Ram(state);
            }
        }

        @AutoValue
        public abstract static class Transfer {

            @Nullable
            public abstract String state();

            @SerializedNames({"state"})
            public static Transfer create(String state) {
                return new AutoValue_MonitoringCenter_Status_Transfer(state);
            }
        }
    }

    @AutoValue
    public abstract static class Alerts {

        public abstract Resources resources();

        public abstract Ports ports();

        public abstract Process process();

        @AutoValue
        public abstract static class Resources {

            public abstract int critical();

            public abstract int warning();

            public abstract int ok();

            @SerializedNames({"critical", "warning", "ok"})
            public static Resources create(int critical, int warning, int ok) {
                return new AutoValue_MonitoringCenter_Alerts_Resources(critical, warning, ok);
            }
        }

        @AutoValue
        public abstract static class Ports {

            public abstract int critical();

            public abstract int warning();

            public abstract int ok();

            @SerializedNames({"critical", "warning", "ok"})
            public static Ports create(int critical, int warning, int ok) {
                return new AutoValue_MonitoringCenter_Alerts_Ports(critical, warning, ok);
            }
        }

        @AutoValue
        public abstract static class Process {

            public abstract int critical();

            public abstract int warning();

            public abstract int ok();

            @SerializedNames({"critical", "warning", "ok"})
            public static Process create(int critical, int warning, int ok) {
                return new AutoValue_MonitoringCenter_Alerts_Process(critical, warning, ok);
            }
        }

        @SerializedNames({"resources", "ports", "process"})
        public static Alerts create(Resources resources, Ports ports, Process process) {
            return new AutoValue_MonitoringCenter_Alerts(resources, ports, process);
        }
    }

    @AutoValue
    public abstract static class Agent {

        public abstract boolean agentinstalled();

        public abstract boolean monitoringNeedsAgent();

        public abstract boolean missingAgentAlert();

        @SerializedNames({"agent_installed", "monitoring_needs_agent", "missing_agent_alert"})
        public static Agent create(boolean agentinstalled, boolean monitoringNeedsAgent, boolean missingAgentAlert) {
            return new AutoValue_MonitoringCenter_Agent(agentinstalled, monitoringNeedsAgent, missingAgentAlert);
        }
    }
}
