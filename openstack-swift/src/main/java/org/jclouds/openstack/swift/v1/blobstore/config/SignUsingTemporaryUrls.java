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
package org.jclouds.openstack.swift.v1.blobstore.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.date.TimeStamp;
import org.jclouds.location.Region;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.TemporaryUrlSigner;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedTemporaryUrlBlobSigner;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class SignUsingTemporaryUrls extends AbstractModule {

   @Override
   protected void configure() {
      bind(BlobRequestSigner.class).to(RegionScopedTemporaryUrlBlobSigner.class);
   }

   @Provides
   @TimeStamp
   protected Long unixEpochTimestamp() {
      return System.currentTimeMillis() / 1000;
   }

   @Provides
   @Singleton
   Supplier<TemporaryUrlSigner> regionScopedTemporaryUrlSigner(final SwiftApi api,
         @Region final Supplier<String> defaultRegion, @Named(PROPERTY_SESSION_INTERVAL) final long seconds) {
      return Suppliers.memoize(new Supplier<TemporaryUrlSigner>() {

         @Override
         public TemporaryUrlSigner get() {
            return TemporaryUrlSigner.checkApiEvery(api.accountApiInRegion(defaultRegion.get()), seconds);
         }
      });
   }

   @Provides
   @Singleton
   @Region
   Supplier<URI> storageUrl(@Region final Supplier<String> defaultRegion,
         @Region final Supplier<Map<String, Supplier<URI>>> regionToUris) {
      return Suppliers.memoize(new Supplier<URI>() {

         @Override
         public URI get() {
            return regionToUris.get().get(defaultRegion.get()).get();
         }
      });
   }
}
