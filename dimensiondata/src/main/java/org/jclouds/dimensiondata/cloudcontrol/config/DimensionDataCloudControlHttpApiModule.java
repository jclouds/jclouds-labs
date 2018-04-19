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
package org.jclouds.dimensiondata.cloudcontrol.config;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import org.jclouds.collect.Memoized;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.handlers.DimensionDataCloudControlErrorHandler;
import org.jclouds.dimensiondata.cloudcontrol.suppliers.RegionsToApiEndpoints;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

@ConfiguresHttpApi
public class DimensionDataCloudControlHttpApiModule extends HttpApiModule<DimensionDataCloudControlApi> {

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(RegionIdToURISupplier.class).to(RegionsToApiEndpoints.class).in(Scopes.SINGLETON);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(DimensionDataCloudControlErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(DimensionDataCloudControlErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(DimensionDataCloudControlErrorHandler.class);
   }

   @Provides
   @Singleton
   @Memoized
   public final Supplier<String> getOrganisationIdForAccount(
         final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, final DimensionDataCloudControlApi api) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier
            .create(authException, new OrganisationIdForAccount(api), seconds, TimeUnit.SECONDS);
   }

}
