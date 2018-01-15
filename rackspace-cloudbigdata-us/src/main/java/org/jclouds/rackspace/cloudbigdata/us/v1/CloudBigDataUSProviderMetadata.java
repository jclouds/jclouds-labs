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
package org.jclouds.rackspace.cloudbigdata.us.v1;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.KEYSTONE_VERSION;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule;
import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule.RegionModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudbigdata.v1.CloudBigDataApiMetadata;
import org.jclouds.rackspace.cloudbigdata.v1.config.CloudBigDataHttpApiModule;
import org.jclouds.rackspace.cloudbigdata.v1.config.CloudBigDataParserModule;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ProviderMetadata} for Rackspace CloudBigData API
 */
@AutoService(ProviderMetadata.class)
public class CloudBigDataUSProviderMetadata extends BaseProviderMetadata {

   /**
    * @return The builder object.
    */
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   /**
    * Provider constructor.
    */
   public CloudBigDataUSProviderMetadata() {
      super(builder());
   }

   /**
    * @param builder Builder for the provider.
    */
   public CloudBigDataUSProviderMetadata(Builder builder) {
      super(builder);
   }

   // NOTE: SYD is disabled for now. Enable when service is enabled in SYD
   /**
    * @return Default provider properties.
    */
   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.BIG_DATA);
      properties.setProperty(KEYSTONE_VERSION, "2");
      //properties.setProperty(PROPERTY_REGIONS, "ORD,DFW,SYD");
      properties.setProperty(PROPERTY_REGIONS, "ORD");
      properties.setProperty(PROPERTY_REGION + ".ORD." + ISO3166_CODES, "US-IL");
      //properties.setProperty(PROPERTY_REGION + ".DFW." + ISO3166_CODES, "US-TX");
      //properties.setProperty(PROPERTY_REGION + ".SYD." + ISO3166_CODES, "AU-NSW");
      return properties;
   }

   /**
    * Builder pattern class.
    */
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudbigdata-us")
         .name("Rackspace CloudBigData US")
         .apiMetadata(new CloudBigDataApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .endpointName("identity service url ending in /v2.0/")
               .documentation(URI.create("http://docs.rackspace.com/cbs/api/v1.0/cbs-devguide/content/overview.html"))
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(ServiceCatalogModule.class)
                     .add(RegionModule.class)
                     .add(CloudBigDataParserModule.class)
                     .add(CloudBigDataHttpApiModule.class).build())
                     .build())
                     .homepage(URI.create("http://www.rackspace.com/big-data/"))
                     .console(URI.create("https://mycloud.rackspace.com"))
                     .linkedServices("rackspace-cloudservers-us", "cloudfiles-us")
                     //.iso3166Codes("US-IL", "US-TX", "AU-NSW")
                     .iso3166Codes("US-IL")
                     .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
                     .defaultProperties(CloudBigDataUSProviderMetadata.defaultProperties());
      }

      @Override
      public CloudBigDataUSProviderMetadata build() {
         return new CloudBigDataUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
