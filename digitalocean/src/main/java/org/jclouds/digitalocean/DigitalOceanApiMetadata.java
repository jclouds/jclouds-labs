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
package org.jclouds.digitalocean;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.digitalocean.compute.config.DigitalOceanComputeServiceContextModule;
import org.jclouds.digitalocean.config.DigitalOceanHttpApiModule;
import org.jclouds.digitalocean.config.DigitalOceanHttpApiModule.DigitalOceanHttpCommandExecutorServiceModule;
import org.jclouds.digitalocean.config.DigitalOceanParserModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link BaseHttpApiMetadata} for the DigitalOcean API
 */
public class DigitalOceanApiMetadata extends BaseHttpApiMetadata<DigitalOceanApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DigitalOceanApiMetadata() {
      this(new Builder());
   }

   protected DigitalOceanApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<DigitalOceanApi, Builder> {

      protected Builder() {
         super(DigitalOceanApi.class);
         id("digitalocean")
            .name("DigitalOcean API")
            .identityName("Client Id")
            .credentialName("API Key")
            .documentation(URI.create("https://cloud.digitalocean.com/api_access"))
            .defaultEndpoint("https://api.digitalocean.com")
            .defaultProperties(DigitalOceanApiMetadata.defaultProperties())
            .view(ComputeServiceContext.class)
            .defaultModules(ImmutableSet.<Class<? extends Module>> of(
                     DigitalOceanHttpApiModule.class,
                     DigitalOceanHttpCommandExecutorServiceModule.class,
                     DigitalOceanParserModule.class,
                     DigitalOceanComputeServiceContextModule.class));
      }

      @Override
      public DigitalOceanApiMetadata build() {
         return new DigitalOceanApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         return this;
      }
   }
}
