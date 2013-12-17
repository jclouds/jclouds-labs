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
package org.jclouds.rackspace.cloudqueues.uk;

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
public class CloudQueuesUKProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public CloudQueuesUKProviderMetadata() {
      super(builder());
   }

   public CloudQueuesUKProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.QUEUES);
      properties.setProperty(PROPERTY_ZONES, "LON");
      properties.setProperty(PROPERTY_ZONE + ".LON." + ISO3166_CODES, "GB-SLG");

      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudqueues-uk")
         .name("Rackspace Cloud Queues UK")
         .apiMetadata(new MarconiApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
               .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
               .documentation(URI.create("http://docs.rackspace.com/queues/api/v1.0/cq-devguide/content/overview.html"))
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationApiModule.class)
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(ZoneModule.class)
                     .add(MarconiTypeAdapters.class)
                     .add(MarconiHttpApiModule.class).build())
               .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/queues/"))
         .console(URI.create("https://mycloud.rackspace.co.uk"))
         .linkedServices("rackspace-cloudservers-uk", "cloudfiles-uk")
         .iso3166Codes("GB-SLG")
         .endpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudQueuesUKProviderMetadata.defaultProperties());
      }

      @Override
      public CloudQueuesUKProviderMetadata build() {
         return new CloudQueuesUKProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
