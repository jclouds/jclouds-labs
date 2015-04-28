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
package org.jclouds.jdbc;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.jdbc.config.JdbcBlobStoreContextModule;

import java.net.URI;

/**
 * Implementation of {@link ApiMetadata} for jclouds Jdbc BlobStore
 */
@AutoService(ApiMetadata.class)
public class JdbcApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public JdbcApiMetadata() {
      super(new Builder());
   }

   protected JdbcApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("jdbc")
         .name("Jdbc BlobStore")
         .identityName("Unused")
         .credentialName("unused")
         .defaultEndpoint("http://localhost/transient")
         .defaultIdentity("unused")
         .defaultCredential("unused")
         .version("1")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/blobstore-guide"))
         .view(BlobStoreContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(JdbcBlobStoreContextModule.class));
      }

      @Override
      public JdbcApiMetadata build() {
         return new JdbcApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
