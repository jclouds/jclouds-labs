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
package org.jclouds.h2.jdbc;

import org.jclouds.h2.jdbc.config.H2JdbcBlobStoreContextModule;
import org.jclouds.jdbc.JdbcApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public class H2JdbcProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public H2JdbcProviderMetadata() {
      super(builder());
   }

   public H2JdbcProviderMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {
      protected Builder() {
         id("h2-jdbc")
               .name("H2 Jdbc")
               .apiMetadata(new JdbcApiMetadata()
                     .toBuilder()
                     .defaultModule(H2JdbcBlobStoreContextModule.class)
                     .build());
      }

      @Override
      public H2JdbcProviderMetadata build() {
         return new H2JdbcProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
