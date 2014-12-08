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
package org.jclouds.cloudsigma2;

import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.PROPERTY_DELETE_DRIVES;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.PROPERTY_VNC_PASSWORD;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.TIMEOUT_DRIVE_CLONED;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudsigma2.compute.config.CloudSigma2ComputeServiceContextModule;
import org.jclouds.cloudsigma2.config.CloudSigma2HttpApiModule;
import org.jclouds.cloudsigma2.config.CloudSigma2ParserModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Cloud Sigma API
 */
@AutoService(ApiMetadata.class)
public class CloudSigma2ApiMetadata extends BaseHttpApiMetadata<CloudSigma2Api> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudSigma2ApiMetadata() {
      this(new Builder());
   }

   protected CloudSigma2ApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_VNC_PASSWORD, "IL9vs34d");
      properties.setProperty(TIMEOUT_DRIVE_CLONED, "60000");
      properties.setProperty(PROPERTY_DELETE_DRIVES, "true");
      properties.setProperty(TEMPLATE, "imageNameMatches=Ubuntu.*[Cc]loud [Ii]mage.*,loginUser=ubuntu");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<CloudSigma2Api, Builder> {

      protected Builder() {
         super(CloudSigma2Api.class);
         id("cloudsigma2")
               .name("CloudSigma API")
               .identityName("Email")
               .credentialName("Password")
               .documentation(URI.create("http://cloudsigma.com/en/platform-details/the-api"))
               .version("2.0")
               .defaultEndpoint("https://zrh.cloudsigma.com/api/2.0")
               .defaultProperties(CloudSigma2ApiMetadata.defaultProperties())
               .view(ComputeServiceContext.class)
               .defaultModules(ImmutableSet.<Class<? extends Module>>of(
                     CloudSigma2HttpApiModule.class,
                     CloudSigma2ParserModule.class,
                     CloudSigma2ComputeServiceContextModule.class));
      }

      @Override
      public CloudSigma2ApiMetadata build() {
         return new CloudSigma2ApiMetadata(this);
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
