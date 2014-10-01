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
package org.jclouds.azurecompute;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.net.URI;
import java.util.Properties;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.config.AzureComputeHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import static org.jclouds.azurecompute.config.AzureComputeProperties.SUBSCRIPTION_ID;

/**
 * Implementation of {@link ApiMetadata} for Microsoft Service Management Service API
 */
public class AzureManagementApiMetadata extends BaseHttpApiMetadata<AzureComputeApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public AzureManagementApiMetadata() {
      this(new Builder());
   }

   protected AzureManagementApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<AzureComputeApi, Builder> {

      protected Builder() {
         id("azurecompute")
         .name("Microsoft Azure Service Management Service API")
         .version("2012-03-01")
         .identityName("Path to Management Certificate .p12 file, or PEM string")
         .credentialName("Password to Management Certificate")
         .defaultEndpoint("https://management.core.windows.net/${" + SUBSCRIPTION_ID + "}")
         .endpointName("Service Management Endpoint ending in your Subscription Id")
         .documentation(URI.create("http://msdn.microsoft.com/en-us/library/ee460799"))
         .defaultProperties(AzureManagementApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(AzureComputeServiceContextModule.class)
               .add(AzureComputeHttpApiModule.class).build());
      }

      @Override
      public AzureManagementApiMetadata build() {
         return new AzureManagementApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
