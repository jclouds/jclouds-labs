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

import java.net.URI;

public class HostedServiceWithDetailedProperties extends HostedService {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromHostedServiceWithDetailedProperties(this);
   }

   public static class Builder extends HostedService.Builder<Builder> {

      @Override
      public Builder properties(HostedServiceProperties properties) {
         this.properties = DetailedHostedServiceProperties.class.cast(properties);
         return this;
      }

      public HostedServiceWithDetailedProperties build() {
         return new HostedServiceWithDetailedProperties(url, name,
               DetailedHostedServiceProperties.class.cast(properties));
      }

      public Builder fromHostedServiceWithDetailedProperties(HostedServiceWithDetailedProperties in) {
         return fromHostedService(in);
      }

      @Override protected Builder self() {
         return this;
      }
   }

   protected HostedServiceWithDetailedProperties(URI url, String serviceName,
         DetailedHostedServiceProperties properties) {
      super(url, serviceName, properties);
   }

   @Override
   public DetailedHostedServiceProperties getProperties() {
      return DetailedHostedServiceProperties.class.cast(properties);
   }

}
