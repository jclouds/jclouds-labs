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
import org.jclouds.azurecompute.domain.ProfileDefinition.HealthStatus;
import org.jclouds.javax.annotation.Nullable;

/**
 * Encapsulates the list of Azure Traffic Manager endpoints. You can define up to 100 endpoints in the list.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758257.aspx">docs</a>
 */
@AutoValue
public abstract class ProfileDefinitionEndpoint {

   public static enum Type {

      UNRECOGNIZED(""),
      CLOUDSERVICE("CloudService"),
      AZUREWEBSITE("AzureWebsite"),
      ANY("Any"),
      TRAFFICMANAGER("TrafficManager");

      private final String value;

      private Type(final String value) {
         this.value = value;
      }

      public static Type fromString(final String value) {
         for (Type type : Type.values()) {
            if (type.value.equalsIgnoreCase(value)) {
               return type;
            }
         }
         return UNRECOGNIZED;
      }

      public String getValue() {
         return value;
      }
   }

   ProfileDefinitionEndpoint() {
   } // For AutoValue only!

   /**
    * Specifies the endpoint domain name. The value depends on endpoint type. If Type is CloudService, the value must be
    * a fully qualified domain name (FQDN) of a cloud service that belongs to the subscription ID that owns the
    * definition. If Type is AzureWebsite, the value must be an FQDN of an Azure web site that belongs to the
    * subscription ID that owns the definition. If Type is Any, the value can be any FQDN for an Azure service or a
    * service outside of Azure.
    *
    * @return endpoint domain name.
    */
   public abstract String domain();

   /**
    * Specifies the status of the monitoring endpoint. If set to Enabled, the endpoint is considered by the load
    * balancing method and is monitored. Possible values are:Enabled, Disabled
    *
    * @return status of the monitoring endpoint.
    */
   public abstract ProfileDefinition.Status status();

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

   /**
    * Optional. Specifies the type of endpoint being added to the definition. Possible values are: CloudService,
    * AzureWebsite, Any, TrafficManager.
    *
    * If there is more than one AzureWebsite endpoint, they must be in different datacenters. This limitation doesn’t
    * apply to cloud services. The default value is CloudService. Use the TrafficManager type when configuring nested
    * profiles..
    *
    * @return endpoint type.
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh744833.aspx">Traffic Manager Overview</a>
    */
   public abstract Type type();

   /**
    * Required when LoadBalancingMethod is set to Performance and Type is set to Any or TrafficManager. Specifies the
    * name of the Azure region. The Location cannot be specified for endpoints of type CloudService or AzureWebsite, in
    * which the locations are determined from the service.
    *
    * @return endpoint protocol.
    * @see <a href="https://msdn.microsoft.com/en-us/library/gg441293.aspx">List Locations</a>
    */
   @Nullable
   public abstract String location();

   /**
    * Optional. Specifies the priority of the endpoint in load balancing. The higher the weight, the more frequently the
    * endpoint will be made available to the load balancer. The value must be greater than 0. For endpoints that do not
    * specify a weight value, a default weight of 1 will be used.
    *
    * @return
    */
   @Nullable
   public abstract Integer weight();

   /**
    * Optional. Can be specified when Type is set to TrafficManager. The minimum number of healthy endpoints within a
    * nested profile that determines whether any of the endpoints within that profile can receive traffic. Default value
    * is 1.
    *
    * @return minimum number of healthy endpoints.
    */
   @Nullable
   public abstract Integer min();

   public static ProfileDefinitionEndpoint create(
           final String domain,
           final ProfileDefinition.Status status,
           final HealthStatus healthStatus,
           final Type type,
           final String location,
           final Integer weight,
           final Integer min) {

      return new AutoValue_ProfileDefinitionEndpoint(
              domain, status, healthStatus, type, location, weight, min);
   }
}
