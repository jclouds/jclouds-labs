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
import java.util.List;
import org.jclouds.azurecompute.domain.ProfileDefinition.LBMethod;
import org.jclouds.azurecompute.domain.ProfileDefinition.Protocol;

/**
 * The Create Definition operation creates a new definition for a specified profile. This definition will be assigned a
 * version number by the service. For more information about creating a profile, see Create Profile.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758257.aspx">docs</a>
 */
@AutoValue
public abstract class ProfileDefinitionParams {

   ProfileDefinitionParams() {
   } // For AutoValue only!

   /**
    * Specifies the DNS Time-To-Live (TTL) that informs the Local DNS resolvers how long to cache DNS entries. The value
    * is an integer from 30 through 999,999.
    *
    * @return DNS cache Time-To-Live (TTL)
    */
   public abstract Integer ttl();

   /**
    * Specifies the protocol to use to monitor endpoint health. Possible values are: HTTP, HTTPS.
    *
    * @return endpoint protocol.
    */
   public abstract Protocol protocol();

   /**
    * Specifies the port used to monitor endpoint health. Accepted values are integer values greater than 0 and less or
    * equal to 65,535.
    *
    * @return endpoint port.
    */
   public abstract Integer port();

   /**
    * Specifies the path relative to the endpoint domain name to probe for health state. Restrictions are: The path must
    * be from 1 through 1000 characters. It must start with a forward slash /. It must contain no brackets &lt;&gt;. It
    * must contain no double slashes //. It must be a well-formed URI string.
    *
    * @return endpoint relative path.
    * @see <a href="https://msdn.microsoft.com/en-us/library/system.uri.iswellformeduristring.aspx">
    * Uri.IsWellFormedUriString Method</a>
    */
   public abstract String path();

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
    * @see ProfileDefinitionEndpointParams
    */
   public abstract List<ProfileDefinitionEndpointParams> endpoints();

   public Builder toBuilder() {
      return builder().fromImageParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private Integer ttl;

      private Protocol protocol;

      private Integer port;

      private String path;

      private LBMethod lb;

      private List<ProfileDefinitionEndpointParams> endpoints;

      public Builder ttl(final Integer ttl) {
         this.ttl = ttl;
         return this;
      }

      public Builder protocol(final Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder port(final Integer port) {
         this.port = port;
         return this;
      }

      public Builder path(final String path) {
         this.path = path;
         return this;
      }

      public Builder lb(final LBMethod lb) {
         this.lb = lb;
         return this;
      }

      public Builder endpoints(final List<ProfileDefinitionEndpointParams> endpoints) {
         this.endpoints = endpoints;
         return this;
      }

      public ProfileDefinitionParams build() {
         return ProfileDefinitionParams.create(ttl, protocol, port, path, lb, endpoints);
      }

      public Builder fromImageParams(final ProfileDefinitionParams in) {
         return ttl(in.ttl())
                 .protocol(in.protocol())
                 .port(in.port())
                 .path(in.path())
                 .lb(in.lb())
                 .endpoints(in.endpoints());
      }
   }

   private static ProfileDefinitionParams create(
           final Integer ttl,
           final Protocol protocol,
           final Integer port,
           final String path,
           final LBMethod lb,
           final List<ProfileDefinitionEndpointParams> endpoints) {
      return new AutoValue_ProfileDefinitionParams(ttl, protocol, port, path, lb, endpoints);
   }
}
