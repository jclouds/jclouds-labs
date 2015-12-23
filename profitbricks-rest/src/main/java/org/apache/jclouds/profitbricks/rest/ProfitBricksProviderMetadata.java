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
package org.apache.jclouds.profitbricks.rest;

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.Properties;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_MAX_PERIOD;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PERIOD;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

@AutoService(ProviderMetadata.class)
public class ProfitBricksProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public ProfitBricksProviderMetadata() {
      super(builder());
   }

   public ProfitBricksProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = ProfitBricksApiMetadata.defaultProperties();
            
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-NV,DE-HE,DE-BW");

      properties.setProperty(PROPERTY_REGIONS, "DE,US");
      properties.setProperty(PROPERTY_REGION + ".DE.zones", "FKB,FRA");
      properties.setProperty(PROPERTY_REGION + ".US.zones", "LAS,LASDEV");

      properties.setProperty(PROPERTY_ZONES, "FKB,FRA,LAS,LASDEV");
      properties.setProperty(PROPERTY_ZONE + ".FKB." + ISO3166_CODES, "DE-BW");
      properties.setProperty(PROPERTY_ZONE + ".FRA." + ISO3166_CODES, "DE-HE");
      properties.setProperty(PROPERTY_ZONE + ".LAS." + ISO3166_CODES, "US-NV");
      properties.setProperty(PROPERTY_ZONE + ".LASDEV." + ISO3166_CODES, "US-NV");
      
      long defaultTimeout = 60l * 60l; // 1 hour
      properties.put(POLL_TIMEOUT, defaultTimeout);
      properties.put(POLL_PERIOD, 2l);
      properties.put(POLL_MAX_PERIOD, 2l * 10l);

      properties.put(PROPERTY_SO_TIMEOUT, 60000 * 5);
      properties.put(PROPERTY_CONNECTION_TIMEOUT, 60000 * 5);
      
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("profitbricks-rest")
            .name("ProfitBricks REST Compute")
            .apiMetadata(new ProfitBricksApiMetadata())
            .homepage(URI.create("https://www.profitbricks.com/"))
            .console(URI.create("https://my.profitbricks.com/dashboard/dcdr2"))
            .iso3166Codes("DE-FKB", "DE-FRA", "US-LAS", "US-LASDEV")
            .endpoint("https://api.profitbricks.com/rest")
            .defaultProperties(ProfitBricksProviderMetadata.defaultProperties());
      }

      @Override
      public ProfitBricksProviderMetadata build() {
         return new ProfitBricksProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
