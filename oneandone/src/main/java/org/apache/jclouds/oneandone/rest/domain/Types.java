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

import com.google.common.base.Enums;

public class Types {

   public enum OSFamliyType {

      Windows, Linux, Others, Null, UNRECOGNIZED;

      public static OSFamliyType fromValue(String v) {
         return Enums.getIfPresent(OSFamliyType.class, v).or(UNRECOGNIZED);
      }

   }

   public enum ImageType {
      IMAGES("IMAGES"), MYIMAGE("MY_IMAGE"), PERSONAL("PERSONAL"), UNRECOGNIZED("");

      public static ImageType fromValue(String v) {
         return Enums.getIfPresent(ImageType.class, v).or(UNRECOGNIZED);
      }
      // the value which is used for matching
      // the json node value with this enum
      private final String value;

      ImageType(final String type) {
         value = type;
      }

      @Override
      public String toString() {
         return value;
      }
   }

   public enum OSType {
      CentOS("CentOS"),
      Debian("Debian"),
      Ubuntu("Ubuntu"),
      RedHat("Red Hat"),
      Windows2008("Windows 2008"),
      Windows2012("Windows 2012"),
      WindowsDatacenter("WindowsDatacenter"),
      UNRECOGNIZED("");

      public static OSType fromValue(String v) {
         return Enums.getIfPresent(OSType.class, v).or(UNRECOGNIZED);
      }

      // the value which is used for matching
      // the json node value with this enum
      private final String value;

      OSType(final String type) {
         value = type;
      }

      @Override
      public String toString() {
         return value;
      }
   }

   public enum ImageFrequency {
      ONCE,
      DAILY,
      WEEKLY,
      UNRECOGNIZED;

      public static ImageFrequency fromValue(String v) {
         return Enums.getIfPresent(ImageFrequency.class, v).or(UNRECOGNIZED);
      }
   }

   public enum ServerState {
      POWERING_ON,
      POWERING_OFF,
      POWERED_ON,
      POWERED_OFF,
      DEPLOYING,
      REBOOTING,
      REMOVING,
      CONFIGURING, UNRECOGNIZED;

      public static ServerState fromValue(String v) {
         return Enums.getIfPresent(ServerState.class, v).or(UNRECOGNIZED);
      }
   }

   public enum GenericState {
      ACTIVE, REMOVING,
      CONFIGURING, UNRECOGNIZED;

      public static GenericState fromValue(String v) {
         return Enums.getIfPresent(GenericState.class, v).or(UNRECOGNIZED);
      }
   }

   public enum ServerAction {
      POWER_ON, POWER_OFF, REBOOT, UNRECOGNIZED;

      public static ServerAction fromValue(String v) {
         return Enums.getIfPresent(ServerAction.class, v).or(UNRECOGNIZED);
      }

   }

   public enum ServerActionMethod {
      SOFTWARE, HARDWARE, UNRECOGNIZED;

      public static ServerActionMethod fromValue(String v) {
         return Enums.getIfPresent(ServerActionMethod.class, v).or(UNRECOGNIZED);
      }
   }

   public enum IPType {
      IPV4, IPV6, UNRECOGNIZED;

      public static IPType fromValue(String v) {
         return Enums.getIfPresent(IPType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum StorageServerRights {
      R,
      RW,
      UNRECOGNIZED;

      public static StorageServerRights fromValue(String v) {
         return Enums.getIfPresent(StorageServerRights.class, v).or(UNRECOGNIZED);
      }
   }

   public enum RuleProtocol {
      TCP,
      UDP,
      ICMP,
      AH,
      ESP,
      GRE,
      UNRECOGNIZED;

      public static RuleProtocol fromValue(String v) {
         return Enums.getIfPresent(RuleProtocol.class, v).or(UNRECOGNIZED);
      }
   }

   public enum LoadBalancerMethod {
      ROUND_ROBIN, LEAST_CONNECTIONS, UNRECOGNIZED;

      public static LoadBalancerMethod fromValue(String v) {
         return Enums.getIfPresent(LoadBalancerMethod.class, v).or(UNRECOGNIZED);
      }
   }

   public enum HealthCheckTestTypes {
      TCP, HTTP, NONE, UNRECOGNIZED;

      public static HealthCheckTestTypes fromValue(String v) {
         return Enums.getIfPresent(HealthCheckTestTypes.class, v).or(UNRECOGNIZED);
      }
   }

   public enum IPOwner {

      SERVER, LOAD_BALANCER, UNRECOGNIZED;

      public static IPOwner fromValue(String v) {
         return Enums.getIfPresent(IPOwner.class, v).or(UNRECOGNIZED);
      }
   }

   public enum VPNState {
      ACTIVE, UNRECOGNIZED;

      public static VPNState fromValue(String v) {
         return Enums.getIfPresent(VPNState.class, v).or(UNRECOGNIZED);
      }
   }

   public enum VPNType {
      SSL, UNRECOGNIZED;

      public static VPNType fromValue(String v) {
         return Enums.getIfPresent(VPNType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum PeriodType {
      LAST_HOUR, LAST_24H, LAST_7D, LAST_30D, LAST_365D, UNRECOGNIZED;

      public static PeriodType fromValue(String v) {
         return Enums.getIfPresent(PeriodType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum CustomPeriodType {
      CUSTOM, UNRECOGNIZED;

      public static CustomPeriodType fromValue(String v) {
         return Enums.getIfPresent(CustomPeriodType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum ProtocolType {
      TCP, UDP, UNRECOGNIZED;

      public static ProtocolType fromValue(String v) {
         return Enums.getIfPresent(ProtocolType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum AlertIfType {
      RESPONDING, NOT_RESPONDING, UNRECOGNIZED;

      public static AlertIfType fromValue(String v) {
         return Enums.getIfPresent(AlertIfType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum ProcessAlertType {
      RUNNING, NOT_RUNNING, UNRECOGNIZED;

      public static ProcessAlertType fromValue(String v) {
         return Enums.getIfPresent(ProcessAlertType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum ApplianceType {

      IMAGE, MY_IMAGE, ISO, Null, UNRECOGNIZED;

      public static ApplianceType fromValue(String v) {
         return Enums.getIfPresent(ApplianceType.class, v).or(UNRECOGNIZED);
      }
   }

   public enum OSImageType {
      Standard, Minimal, Personal, ISO_OS, ISO_TOOL, NULL, UNRECOGNIZED;

      public static OSImageType fromValue(String v) {
         return Enums.getIfPresent(OSImageType.class, v).or(UNRECOGNIZED);
      }

   }
}
