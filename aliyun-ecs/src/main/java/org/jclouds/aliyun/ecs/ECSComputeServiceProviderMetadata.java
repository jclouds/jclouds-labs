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
package org.jclouds.aliyun.ecs;

import com.google.auto.service.AutoService;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import java.net.URI;
import java.util.Properties;

@AutoService(ProviderMetadata.class)
public class ECSComputeServiceProviderMetadata extends BaseProviderMetadata {

   public ECSComputeServiceProviderMetadata() {
      super(builder());
   }

   public ECSComputeServiceProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static Properties defaultProperties() {
      final Properties properties = ECSServiceApiMetadata.defaultProperties();
      return properties;
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("aliyun-ecs")
               .name("Alibaba Elastic Compute Service")
               .apiMetadata(new ECSServiceApiMetadata())
               .homepage(URI.create("https://www.alibabacloud.com"))
               .console(URI.create("https://ecs.console.aliyun.com"))
               .endpoint("https://ecs.aliyuncs.com")
               .iso3166Codes("US-CA", "US-VA", "DE", "JP", "ID-JK", "SG", "IN", "AU-NSW", "MY", "CN-HE", "CN-SH", "CN-ZJ", "CN-GD", "HK", "AE-DU")
               .defaultProperties(ECSServiceApiMetadata.defaultProperties());
      }

      @Override
      public ECSComputeServiceProviderMetadata build() {
         return new ECSComputeServiceProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}


