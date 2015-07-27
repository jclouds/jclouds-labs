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

package org.jclouds.etcd;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.etcd.config.EtcdHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class EtcdApiMetadata extends BaseHttpApiMetadata<EtcdApi> {

   public static final String API_VERSION = "v2";
   public static final String BUILD_VERSION = "2.1.1";

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public EtcdApiMetadata() {
      this(new Builder());
   }

   protected EtcdApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<EtcdApi, Builder> {

      protected Builder() {
         super(EtcdApi.class);
         id("etcd").
         name("Etcd API").
         identityName("Optional Username").
         credentialName("Optional Password").
         defaultIdentity("N/A").
         defaultCredential("N/A").
         documentation(URI.create("https://github.com/coreos/etcd/blob/master/Documentation/api.md")).
         version(API_VERSION).
         buildVersion(BUILD_VERSION).
         defaultEndpoint("http://127.0.0.1:2379").
         defaultProperties(EtcdApiMetadata.defaultProperties()).
         defaultModules(ImmutableSet.<Class<? extends Module>> of(EtcdHttpApiModule.class));
      }

      @Override
      public EtcdApiMetadata build() {
         return new EtcdApiMetadata(this);
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
