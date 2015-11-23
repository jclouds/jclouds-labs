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
package org.apache.jclouds.profitbricks.rest;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.net.URI;
import java.util.Properties;
import org.apache.jclouds.profitbricks.rest.config.ProfitBricksHttpApiModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import static org.jclouds.reflect.Reflection2.typeToken;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

public class ProfitBricksApiMetadata extends BaseHttpApiMetadata<ProfitBricksApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ProfitBricksApiMetadata() {
      this(new Builder());
   }

   protected ProfitBricksApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ProfitBricksApi, Builder> {

      protected Builder() {
         id("profitbricks-rest")
            .name("ProfitBricks REST API")
            .identityName("API Username")
            .credentialName("API Password")
            .documentation(URI.create("https://devops.profitbricks.com/api/rest/"))
            .defaultEndpoint("https://api.profitbricks.com/rest/")
            .defaultProperties(ProfitBricksApiMetadata.defaultProperties())
            .view(typeToken(ComputeServiceContext.class))
            .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(OkHttpCommandExecutorServiceModule.class)
               .add(ProfitBricksHttpApiModule.class)
               .build());
      }

      @Override
      public ProfitBricksApiMetadata build() {
         return new ProfitBricksApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
