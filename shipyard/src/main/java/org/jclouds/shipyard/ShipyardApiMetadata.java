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
package org.jclouds.shipyard;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.shipyard.config.ShipyardHttpApiModule;

import java.net.URI;
import java.util.Properties;

@AutoService(ApiMetadata.class)
public class ShipyardApiMetadata extends BaseHttpApiMetadata<ShipyardApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ShipyardApiMetadata() {
      this(new Builder());
   }

   protected ShipyardApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      return BaseHttpApiMetadata.defaultProperties();
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ShipyardApi, Builder> {

      protected Builder() {
         super(ShipyardApi.class);
         id("shipyard")
           .name("Shipyard Remote Docker Management API")
           .identityName("<shipyard-service-key>")
           .credentialName("not used")
           .documentation(URI.create("http://shipyard-project.com/docs/api/"))
           .version("2.0.4")
           .defaultEndpoint("https://127.0.0.1:8080")
           .defaultProperties(ShipyardApiMetadata.defaultProperties())
           .defaultModules(ImmutableSet.<Class<? extends Module>>of(
                   ShipyardHttpApiModule.class));
      }

      @Override
      public ShipyardApiMetadata build() {
         return new ShipyardApiMetadata(this);
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
