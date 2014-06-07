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

import static org.jclouds.Constants.PROPERTY_MAX_REDIRECTS;
import static org.jclouds.abiquo.config.AbiquoProperties.ASYNC_TASK_MONITOR_DELAY;
import static org.jclouds.abiquo.config.AbiquoProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.abiquo.compute.config.AbiquoComputeServiceContextModule;
import org.jclouds.abiquo.config.AbiquoAuthenticationModule;
import org.jclouds.abiquo.config.AbiquoHttpApiModule;
import org.jclouds.concurrent.config.ScheduledExecutorServiceModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Abiquo API.
 */
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
      // By default redirects will be handled in the domain objects
      properties.setProperty(PROPERTY_MAX_REDIRECTS, "0");
      // The default polling delay between AsyncTask monitor requests
      properties.setProperty(ASYNC_TASK_MONITOR_DELAY, "5000");
      // By default the provided credential is not a token
      properties.setProperty(CREDENTIAL_TYPE, "password");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<AbiquoApi, Builder> {
      private static final String DOCUMENTATION_ROOT = "http://community.abiquo.com/display/ABI"
            + CharMatcher.DIGIT.retainFrom(AbiquoApi.API_VERSION);

      protected Builder() {
         id("abiquo")
               .name("Abiquo API")
               .identityName("API Username")
               .credentialName("API Password")
               .documentation(URI.create(DOCUMENTATION_ROOT + "/API+Reference"))
               .defaultEndpoint("http://localhost/api")
               .version(AbiquoApi.API_VERSION)
               .buildVersion(AbiquoApi.BUILD_VERSION)
               .view(AbiquoContext.class)
               .defaultProperties(AbiquoApiMetadata.defaultProperties())
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(AbiquoHttpApiModule.class,
                           AbiquoAuthenticationModule.class, AbiquoComputeServiceContextModule.class,
                           ScheduledExecutorServiceModule.class));
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
