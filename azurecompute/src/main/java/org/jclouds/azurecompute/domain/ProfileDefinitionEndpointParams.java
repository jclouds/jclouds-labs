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
import org.jclouds.azurecompute.domain.ProfileDefinition.Status;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint.Type;
import org.jclouds.javax.annotation.Nullable;

/**
 * Encapsulates the list of Azure Traffic Manager endpoints. You can define up to 100 endpoints in the list.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758257.aspx">docs</a>
 */
@AutoValue
public abstract class ProfileDefinitionEndpointParams {

   ProfileDefinitionEndpointParams() {
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
   public abstract Status status();

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
   @Nullable
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
    * Optional. Can be specified when Type is set to TrafficManager. The minimum number of healthy endpoints within a
    * nested profile that determines whether any of the endpoints within that profile can receive traffic. Default value
    * is 1.
    *
    * @return minimum number of healthy endpoints.
    */
   @Nullable
   public abstract Integer min();

   /**
    * Optional. Specifies the priority of the endpoint in load balancing. The higher the weight, the more frequently the
    * endpoint will be made available to the load balancer. The value must be greater than 0. For endpoints that do not
    * specify a weight value, a default weight of 1 will be used.
    *
    * @return endpoint priority.
    */
   @Nullable
   public abstract Integer weight();

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String domain;

      private Status status;

      private Type type;

      private String location;

      private Integer min;

      private Integer weight;

      public Builder domain(final String domain) {
         this.domain = domain;
         return this;
      }

      public Builder status(final Status status) {
         this.status = status;
         return this;
      }

      public Builder type(final Type type) {
         this.type = type;
         return this;
      }

      public Builder location(final String location) {
         this.location = location;
         return this;
      }

      public Builder min(final Integer min) {
         this.min = min;
         return this;
      }

      public Builder weight(final Integer weight) {
         this.weight = weight;
         return this;
      }

      public ProfileDefinitionEndpointParams build() {
         return ProfileDefinitionEndpointParams.create(
                 domain,
                 status,
                 type,
                 location,
                 min,
                 weight);
      }

      public Builder fromImageParams(final ProfileDefinitionEndpointParams in) {
         return domain(in.domain())
                 .status(in.status())
                 .type(in.type())
                 .location(in.location())
                 .min(in.min())
                 .weight(in.weight());
      }
   }

   private static ProfileDefinitionEndpointParams create(
           final String domain,
           final Status status,
           final Type type,
           final String location,
           final Integer min,
           final Integer weight) {
      return new AutoValue_ProfileDefinitionEndpointParams(domain, status, type, location, min, weight);
   }
}
