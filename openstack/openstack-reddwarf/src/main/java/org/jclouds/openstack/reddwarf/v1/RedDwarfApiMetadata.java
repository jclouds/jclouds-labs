/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.reddwarf.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.reddwarf.v1.config.RedDwarfHttpApiModule;
import org.jclouds.openstack.reddwarf.v1.config.RedDwarfParserModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for RedDwarf API
 * 
 * @author Zack Shoylev
 */
public class RedDwarfApiMetadata extends BaseHttpApiMetadata<RedDwarfApi> {
      
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public RedDwarfApiMetadata() {
      this(new Builder());
   }

   protected RedDwarfApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.DATABASE_SERVICE);      
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<RedDwarfApi, Builder> {

      protected Builder() {         
          id("openstack-reddwarf")
         .name("OpenStack RedDwarf API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .endpointName("Keystone base URL ending in /v2.0/")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.0")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(RedDwarfApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(AuthenticationApiModule.class)
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(ZoneModule.class)
                                     .add(RedDwarfParserModule.class)
                                     .add(RedDwarfHttpApiModule.class)
                                     .build());
      }
      
      @Override
      public RedDwarfApiMetadata build() {
         return new RedDwarfApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
