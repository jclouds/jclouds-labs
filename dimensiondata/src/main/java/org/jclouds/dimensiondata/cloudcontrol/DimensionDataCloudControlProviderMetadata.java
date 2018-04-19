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
package org.jclouds.dimensiondata.cloudcontrol;

import com.google.auto.service.AutoService;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;

/**
 * Implementation of {@link ProviderMetadata} for DimensionData CloudController.
 */
@AutoService(ProviderMetadata.class)
public class DimensionDataCloudControlProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public DimensionDataCloudControlProviderMetadata() {
      super(builder());
   }

   public DimensionDataCloudControlProviderMetadata(final Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = DimensionDataCloudControlApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_REGIONS, "na,eu,au,mea,ap,canada");
      properties.setProperty(PROPERTY_REGION + ".na.zones", "NA9,NA12");
      properties.setProperty(PROPERTY_ZONE + "NA9" + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_ZONE + "NA12" + ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_REGION + ".eu.zones", "EU6,EU7,EU8");
      properties.setProperty(PROPERTY_ZONE + "EU6" + ISO3166_CODES, "DE-HE");
      properties.setProperty(PROPERTY_ZONE + "EU7" + ISO3166_CODES, "NL-NH");
      properties.setProperty(PROPERTY_ZONE + "EU8" + ISO3166_CODES, "BE-BRU");
      properties.setProperty(PROPERTY_REGION + ".au.zones", "AU9,AU10,AU11");
      properties.setProperty(PROPERTY_ZONE + "AU9" + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_ZONE + "AU10" + ISO3166_CODES, "AU-VIC");
      properties.setProperty(PROPERTY_ZONE + "AU11" + ISO3166_CODES, "NZ-WKO");
      properties.setProperty(PROPERTY_REGION + ".mea.zones", "AF3");
      properties.setProperty(PROPERTY_ZONE + "AF3" + ISO3166_CODES, "ZA-GT");
      properties.setProperty(PROPERTY_REGION + ".ap.zones", "AP4,AP5");
      properties.setProperty(PROPERTY_ZONE + "AP4" + ISO3166_CODES, "JP-13");
      properties.setProperty(PROPERTY_ZONE + "AP5" + ISO3166_CODES, "HK");
      properties.setProperty(PROPERTY_REGION + ".canada.zones", "CA2");
      properties.setProperty(PROPERTY_ZONE + "CA2" + ISO3166_CODES, "CA-ON");
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("dimensiondata-cloudcontrol").name("DimensionData Cloud Control")
               .apiMetadata(new DimensionDataCloudControlApiMetadata())
               .homepage(URI.create("https://na-cloud.dimensiondata.com/"))
               .console(URI.create("https://na-cloud.dimensiondata.com/"))
               .endpoint("https://api-na.dimensiondata.com/caas")
               .defaultProperties(DimensionDataCloudControlProviderMetadata.defaultProperties());
      }

      @Override
      public DimensionDataCloudControlProviderMetadata build() {
         return new DimensionDataCloudControlProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(final ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
