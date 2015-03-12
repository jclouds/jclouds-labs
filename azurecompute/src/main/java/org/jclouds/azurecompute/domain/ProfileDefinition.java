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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Cloud service certifcate.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee795178.aspx" >ServiceCertificate</a>
 */
@AutoValue
public abstract class ProfileDefinition {

   public static enum Status {

      UNRECOGNIZED(""),
      ENABLED("Enabled"),
      DISABLED("Disabled");

      private final String value;

      private Status(final String value) {
         this.value = value;
      }

      public static Status fromString(final String value) {
         for (Status status : Status.values()) {
            if (status.value.equalsIgnoreCase(value)) {
               return status;
            }
         }
         return UNRECOGNIZED;
      }

      public String getValue() {
         return value;
      }
   }

   public static enum LBMethod {

      UNRECOGNIZED(""),
      PERFORMANCE("Performance"),
      FAILOVER("Failover"),
      ROUNDROBIN("RoundRobin");

      private final String value;

      private LBMethod(final String value) {
         this.value = value;
      }

      public static LBMethod fromString(final String value) {
         for (LBMethod lb : LBMethod.values()) {
            if (lb.value.equalsIgnoreCase(value)) {
               return lb;
            }
         }
         return UNRECOGNIZED;
      }

      public String getValue() {
         return value;
      }
   }

   public static enum Protocol {

      UNRECOGNIZED, HTTP, HTTPS;

      public static Protocol fromString(final String value) {
         for (Protocol protocol : Protocol.values()) {
            if (protocol.name().equalsIgnoreCase(value)) {
               return protocol;
            }
         }
         return UNRECOGNIZED;
      }
   }

   public static enum HealthStatus {

      UNRECOGNIZED(""),
      ONLINE("Online"),
      DEGRADED("Degraded"),
      INACTIVE("Inactive"),
      DISABLED("Disabled"),
      STOPPED("Stopped"),
      CHECKINGENDPOINT("CheckingEndpoint");

      private final String value;

      private HealthStatus(final String value) {
         this.value = value;
      }

      public static HealthStatus fromString(final String value) {
         for (HealthStatus status : HealthStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
               return status;
            }
         }
         return UNRECOGNIZED;
      }

      public String getValue() {
         return value;
      }
   }

   ProfileDefinition() {
   } // For AutoValue only!

   /**
    * Specifies the DNS Time-To-Live (TTL) that informs the Local DNS resolvers how long to cache DNS entries. The value
    * is an integer from 30 through 999,999.
    *
    * @return DNS cache Time-To-Live (TTL)
    */
   public abstract int ttl();

   /**
    * Indicates whether this definition is enabled or disabled for the profile. Possible values are: Enabled, Disabled.
    *
    * @return profile definition status.
    */
   public abstract Status status();

   /**
    * Specifies the version of the definition. This value is always 1.
    *
    * @return version.
    */
   public abstract String version();

   /**
    * Encapsulates the list of Azure Traffic Manager endpoint monitors. You have to define just 1 monitor in the list.
    *
    * @return profile definition monitors;
    */
   public abstract List<ProfileDefinitionMonitor> monitors();

   /**
    * Specifies the load balancing method to use to distribute connection. Possible values are: Performance, Failover,
    * RoundRobin
    *
    * @return load balancing method..
    */
   public abstract LBMethod lb();

   /**
    * Encapsulates the list of Azure Traffic Manager endpoints. You can define up to 100 endpoints in the list.
    *
    * @return endpoints.
    * @see EndpointParams
    */
   public abstract List<ProfileDefinitionEndpoint> endpoints();

   /**
    * When defined as part of a policy, indicates the health status for the overall load balancing policy. Possible
    * values are: Online, Degraded, Inactive, Disabled, CheckingEndpoints.
    *
    * When defined as part of an endpoint, indicates the health status for the endpoint. Possible values are: Online,
    * Degraded, Inactive, Disabled, Stopped, CheckingEndpoint.
    *
    * @return endpoint health status.
    */
   public abstract ProfileDefinition.HealthStatus healthStatus();

   public static ProfileDefinition create(
           final int ttl,
           final Status status,
           final String version,
           final List<ProfileDefinitionMonitor> monitors,
           final LBMethod lb,
           final List<ProfileDefinitionEndpoint> endpoints,
           final ProfileDefinition.HealthStatus healthStatus) {

      return new AutoValue_ProfileDefinition(
              ttl, status, version, ImmutableList.copyOf(monitors), lb, ImmutableList.copyOf(endpoints), healthStatus);
   }
}
