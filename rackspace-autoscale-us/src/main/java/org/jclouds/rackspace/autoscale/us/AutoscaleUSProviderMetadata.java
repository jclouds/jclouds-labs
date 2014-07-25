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
package org.jclouds.rackspace.autoscale.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.autoscale.v1.AutoscaleApiMetadata;
import org.jclouds.rackspace.autoscale.v1.config.AutoscaleHttpApiModule;
import org.jclouds.rackspace.autoscale.v1.config.AutoscaleParserModule;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ProviderMetadata} for Rackspace US Auto Scale API.
 */
public class AutoscaleUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public AutoscaleUSProviderMetadata() {
      super(builder());
   }

   public AutoscaleUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.AUTO_SCALE);

      properties.setProperty(PROPERTY_REGIONS, "ORD,DFW,IAD,SYD,HKG");
      properties.setProperty(PROPERTY_REGION + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_REGION + ".DFW." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_REGION + ".IAD." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_REGION + ".SYD." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_REGION + ".HKG." + ISO3166_CODES, "HK");

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-autoscale-us")
         .name("Rackspace Auto Scale US")
         .apiMetadata(new AutoscaleApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
               .documentation(URI.create("http://docs.rackspace.com/cas/api/v1.0/autoscale-devguide/content/Overview.html"))
               .version("1.0")
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationApiModule.class)
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(RegionModule.class)
                     .add(AutoscaleParserModule.class)
                     .add(AutoscaleHttpApiModule.class)
                     .build())
               .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/public/autoscale/"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudbigdata-us", "rackspace-cloudblockstorage-us",
               "rackspace-clouddatabases-us", "rackspace-clouddns-us", "rackspace-cloudfiles-us",
               "rackspace-cloudloadbalancers-us", "rackspace-cloudqueues-us",
               "rackspace-cloudservers-us")
         .iso3166Codes("US-IL", "US-TX", "US-VA", "AU-NSW", "HK")
         .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(AutoscaleUSProviderMetadata.defaultProperties());
      }

      @Override
      public AutoscaleUSProviderMetadata build() {
         return new AutoscaleUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
