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
package org.jclouds.openstack.marconi.v1;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.marconi.v1.config.MarconiHttpApiModule;
import org.jclouds.openstack.marconi.v1.config.MarconiTypeAdapters;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

/**
 * Implementation of {@link org.jclouds.apis.ApiMetadata} for Marconi 1.0 API
 */
public class MarconiApiMetadata extends BaseHttpApiMetadata<MarconiApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public MarconiApiMetadata() {
      this(new Builder());
   }

   protected MarconiApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.QUEUES);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);

      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<MarconiApi, Builder> {

      protected Builder() {
          id("openstack-marconi")
         .name("OpenStack Marconi Havana API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .documentation(URI.create("https://wiki.openstack.org/wiki/Marconi"))
         .version("1.0")
         .endpointName("Keystone base url ending in /v2.0/")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(MarconiApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(AuthenticationApiModule.class)
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(OkHttpCommandExecutorServiceModule.class)
                                     .add(ZoneModule.class)
                                     .add(MarconiTypeAdapters.class)
                                     .add(MarconiHttpApiModule.class).build());
      }
      
      @Override
      public MarconiApiMetadata build() {
         return new MarconiApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
