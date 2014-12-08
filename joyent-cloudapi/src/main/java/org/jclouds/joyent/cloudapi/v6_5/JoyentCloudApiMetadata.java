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
package org.jclouds.joyent.cloudapi.v6_5;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.joyent.cloudapi.v6_5.compute.config.JoyentCloudComputeServiceContextModule;
import org.jclouds.joyent.cloudapi.v6_5.config.DatacentersAreZonesModule;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudHttpApiModule;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudProperties;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class JoyentCloudApiMetadata extends BaseHttpApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public JoyentCloudApiMetadata() {
      this(new Builder());
   }

   protected JoyentCloudApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      // auth fail sometimes happens, as the rc.local script that injects the
      // authorized key executes after ssh has started.
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(JoyentCloudProperties.AUTOGENERATE_KEYS, "true");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<JoyentCloudApi, Builder> {

      protected Builder() {
         super(JoyentCloudApi.class);
         id("joyent-cloudapi")
         .name("Joyent Cloud API")
         .identityName("username")
         .credentialName("password")
         .documentation(URI.create("http://cloudApi.joyent.org/cloudApiapi.html"))
         .version("~6.5")
         .defaultEndpoint("https://api.joyentcloud.com")
         .defaultProperties(JoyentCloudApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>> builder()
                                     .add(DatacentersAreZonesModule.class)
                                     .add(JoyentCloudHttpApiModule.class)
                                     .add(JoyentCloudComputeServiceContextModule.class).build());
      }

      @Override
      public JoyentCloudApiMetadata build() {
         return new JoyentCloudApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
