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
package org.jclouds.rackspace.cloudqueues.us;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.marconi.v1.MarconiApiMetadata;
import org.jclouds.openstack.marconi.v1.config.MarconiHttpApiModule;
import org.jclouds.openstack.marconi.v1.config.MarconiTypeAdapters;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

/**
 * Implementation of Rackspace Cloud Queues.
 * 
 * @author Everett Toews
 */
public class CloudQueuesUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public CloudQueuesUSProviderMetadata() {
      super(builder());
   }

   public CloudQueuesUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.QUEUES);
      properties.setProperty(PROPERTY_ZONES, "ORD,DFW,IAD,SYD,HKG");
      properties.setProperty(PROPERTY_ZONE + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_ZONE + ".DFW." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_ZONE + ".IAD." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_ZONE + ".SYD." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_ZONE + ".HKG." + ISO3166_CODES, "HK");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudqueues-us")
         .name("Rackspace Cloud Queues US")
         .apiMetadata(new MarconiApiMetadata().toBuilder()
                  .identityName("${userName}")
                  .credentialName("${apiKey}")
                  .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
                  .endpointName("identity service url ending in /v2.0/")
                  .documentation(URI.create("http://docs.rackspace.com/queues/api/v1.0/cq-devguide/content/overview.html"))
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(CloudIdentityAuthenticationApiModule.class)
                                              .add(CloudIdentityAuthenticationModule.class)
                                              .add(ZoneModule.class)
                                              .add(MarconiTypeAdapters.class)
                                              .add(MarconiHttpApiModule.class).build())
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/queues/"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudservers-us", "cloudfiles-us")
         .iso3166Codes("US-IL", "US-TX")
         .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudQueuesUSProviderMetadata.defaultProperties());
      }

      @Override
      public CloudQueuesUSProviderMetadata build() {
         return new CloudQueuesUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
