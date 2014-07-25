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
package org.jclouds.rackspace.cloudbigdata.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.rackspace.cloudbigdata.v1.config.CloudBigDataHttpApiModule;
import org.jclouds.rackspace.cloudbigdata.v1.config.CloudBigDataParserModule;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.apis.ApiMetadata} for the Rackspace Cloud Big Data API
 *
 * @see CloudBigDataApi
 */
public class CloudBigDataApiMetadata extends BaseHttpApiMetadata<CloudBigDataApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   /**
    *
    */
   public CloudBigDataApiMetadata() {
      this(new Builder());
   }

   protected CloudBigDataApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.BIG_DATA);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      return properties;
   }

   /**
    * Metadata setup
    */
   public static class Builder extends BaseHttpApiMetadata.Builder<CloudBigDataApi, Builder> {

      protected Builder() {
         id("rackspace-cloudbigdata")
         .name("Rackspace Cloud Big Data API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .endpointName("Keystone base URL ending in /v2.0/")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.0")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(CloudBigDataApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(CloudIdentityAuthenticationApiModule.class)
               .add(CloudIdentityAuthenticationModule.class)
               .add(RegionModule.class)
               .add(CloudBigDataParserModule.class)
               .add(CloudBigDataHttpApiModule.class)
               .build());
      }

      @Override
      public CloudBigDataApiMetadata build() {
         return new CloudBigDataApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
