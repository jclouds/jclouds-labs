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
package org.jclouds.rackspace.orchestration.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.KEYSTONE_VERSION;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.heat.v1.HeatApiMetadata;
import org.jclouds.openstack.heat.v1.config.HeatHttpApiModule;
import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule;
import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule.RegionModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * This is a Rackspace specific provider for Openstack Heat.
 * Cloud Orchestration is a service that allows you to create, update and manage groups of cloud resources and their
 * software components as a single unit and then deploy them in an automated, repeatable fashion via a template.
 *
 */
@AutoService(ProviderMetadata.class)
public class OrchestrationUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public OrchestrationUSProviderMetadata() {
      this(new Builder());
   }

   protected OrchestrationUSProviderMetadata(Builder builder) {
      super(builder);
   }

   /**
    * @return a {@link Properties} object containing the default provider properties.
    * This returns the credential type, service type, and configured regions.
    */
   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.ORCHESTRATION);
      properties.setProperty(KEYSTONE_VERSION, "2");

      properties.setProperty(PROPERTY_REGIONS, "ORD,DFW,IAD,SYD,HKG");
      properties.setProperty(PROPERTY_REGION + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_REGION + ".DFW." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_REGION + ".IAD." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_REGION + ".SYD." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_REGION + ".HKG." + ISO3166_CODES, "HK");

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("rackspace-orchestration-us")
         .name("Rackspace Orchestration US")
         .apiMetadata(new HeatApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .documentation(
                     URI.create("http://docs.rackspace.com/orchestration/api/v1/orchestration-devguide/content/"))
               .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
               .version("1")
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(ServiceCatalogModule.class)
                     .add(RegionModule.class)
                     .add(HeatHttpApiModule.class)
                     .build())
               .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/orchestration"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-autoscale-us", "rackspace-cloudblockstorage-us", "rackspace-clouddatabases-us",
               "rackspace-clouddns-us", "rackspace-cloudidentity", "rackspace-cloudloadbalancers-us",
               "rackspace-cloudqueues-us", "rackspace-cdn-us")
         .iso3166Codes("US-IL", "US-TX", "US-VA", "AU-NSW", "HK")
         .defaultProperties(OrchestrationUSProviderMetadata.defaultProperties());

      }

      @Override
      public OrchestrationUSProviderMetadata build() {
         return new OrchestrationUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
