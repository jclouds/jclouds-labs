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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.aliyun.ecs.compute.config.ECSServiceContextModule;
import org.jclouds.aliyun.ecs.config.ECSComputeServiceHttpApiModule;
import org.jclouds.aliyun.ecs.config.ECSComputeServiceParserModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.reflect.Reflection2.typeToken;

public class ECSServiceApiMetadata extends BaseHttpApiMetadata<ECSComputeServiceApi> {

   public static final String DEFAULT_API_VERSION = "2014-05-26";

   public ECSServiceApiMetadata() {
      this(new Builder());
   }

   protected ECSServiceApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put(TEMPLATE, "osFamily=CENTOS,os64Bit=true,osVersionMatches=7.*");
      properties.put(TIMEOUT_NODE_RUNNING, 900000); // 15 mins
      properties.put(TIMEOUT_NODE_SUSPENDED, 900000); // 15 mins
      return properties;
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ECSComputeServiceApi, Builder> {

      protected Builder() {
         id("alibaba-ecs")
                 .name("Alibaba Elastic Compute Service API")
                 .identityName("user name")
                 .credentialName("user password")
                 .version(DEFAULT_API_VERSION)
                 .documentation(URI.create("https://www.alibabacloud.com/help"))
                 .defaultEndpoint("https://ecs.aliyuncs.com")
                 .defaultProperties(ECSServiceApiMetadata.defaultProperties())
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                         .add(ECSComputeServiceHttpApiModule.class)
                         .add(ECSComputeServiceParserModule.class)
                         .add(ECSServiceContextModule.class)
                         .build());
      }

      @Override
      public ECSServiceApiMetadata build() {
         return new ECSServiceApiMetadata(this);
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
