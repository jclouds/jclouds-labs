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
package org.jclouds.abiquo;

import static org.jclouds.abiquo.config.AbiquoProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.abiquo.config.AbiquoAuthenticationModule;
import org.jclouds.abiquo.config.AbiquoHttpApiModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Abiquo API.
 */
@AutoService(ApiMetadata.class)
public class AbiquoApiMetadata extends BaseHttpApiMetadata<AbiquoApi> {

   public AbiquoApiMetadata() {
      this(new Builder());
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   protected AbiquoApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      // By default the provided credential is not a token
      properties.setProperty(CREDENTIAL_TYPE, "password");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<AbiquoApi, Builder> {

      protected Builder() {
         id("abiquo")
               .name("Abiquo API")
               .identityName("API Username")
               .credentialName("API Password")
               .documentation(URI.create("http://wiki.abiquo.com/display/ABI32/Api+Reference"))
               .defaultEndpoint("http://localhost/api")
               .version("3.2")
               // .view(ComputeServiceContext.class)
               .defaultProperties(AbiquoApiMetadata.defaultProperties())
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(AbiquoHttpApiModule.class,
                           AbiquoAuthenticationModule.class));
      }

      @Override
      public AbiquoApiMetadata build() {
         return new AbiquoApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

}
