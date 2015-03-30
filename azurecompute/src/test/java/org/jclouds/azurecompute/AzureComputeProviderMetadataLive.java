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
package org.jclouds.azurecompute;

import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.Properties;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.providers.ProviderMetadata;

@AutoService(ProviderMetadata.class)
public class AzureComputeProviderMetadataLive extends AzureComputeProviderMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromProviderMetadata(this);
   }

   public AzureComputeProviderMetadataLive() {
      super(new Builder());
   }

   public static Properties defaultProperties() {
      Properties properties = AzureManagementApiMetadata.defaultProperties();
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=.*14\\.10.*,loginUser=jclouds,"
              + "locationId=" + BaseAzureComputeApiLiveTest.LOCATION);
      properties.setProperty(OPERATION_TIMEOUT, "" + 600 * 1000);
      properties.setProperty(OPERATION_POLL_INITIAL_PERIOD, "" + 5);
      properties.setProperty(OPERATION_POLL_MAX_PERIOD, "" + 15);
      properties.setProperty(AzureComputeProperties.TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(AzureComputeProperties.TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");
      return properties;
   }

   public AzureComputeProviderMetadataLive(final Builder builder) {
      super(builder);
   }

   public static class Builder extends AzureComputeProviderMetadata.Builder {

      protected Builder() {
         id("azurecompute")
                 .name("Microsoft Azure Service Management Service")
                 .apiMetadata(new AzureManagementApiMetadata())
                 .endpoint("https://management.core.windows.net/SUBSCRIPTION_ID")
                 .homepage(URI.create("https://www.windowsazure.com/"))
                 .console(URI.create("https://windows.azure.com/default.aspx"))
                 .linkedServices("azureblob", "azurequeue", "azuretable")
                 .iso3166Codes("US-TX", "US-IL", "IE-D", "SG", "NL-NH", "HK")
                 .defaultProperties(AzureComputeProviderMetadataLive.defaultProperties());
      }

      @Override
      public AzureComputeProviderMetadataLive build() {
         return new AzureComputeProviderMetadataLive(this);
      }

      @Override
      public Builder fromProviderMetadata(final ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
