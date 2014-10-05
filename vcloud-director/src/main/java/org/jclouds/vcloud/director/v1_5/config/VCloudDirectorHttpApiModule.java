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
package org.jclouds.vcloud.director.v1_5.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.internal.ApiContextImpl;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.annotations.Login;
import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.features.OrgApi;
import org.jclouds.vcloud.director.v1_5.features.TaskApi;
import org.jclouds.vcloud.director.v1_5.features.VAppApi;
import org.jclouds.vcloud.director.v1_5.features.VmApi;
import org.jclouds.vcloud.director.v1_5.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.vcloud.director.v1_5.handlers.VCloudDirectorErrorHandler;
import org.jclouds.vcloud.director.v1_5.loaders.LoginUserInOrgWithPassword;
import org.jclouds.vcloud.director.v1_5.loaders.ResolveEntity;
import org.jclouds.vcloud.director.v1_5.login.SessionApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

@ConfiguresHttpApi
public class VCloudDirectorHttpApiModule extends HttpApiModule<VCloudDirectorApi> {
   @Override
   protected void configure() {
      bind(new TypeLiteral<ApiContext<VCloudDirectorAdminApi>>() {
      }).to(new TypeLiteral<ApiContextImpl<VCloudDirectorAdminApi>>() {
      });
      
      // Bind apis that are used directly in Functions, Predicates and other circumstances
      bindHttpApi(binder(), OrgApi.class);
      bindHttpApi(binder(), SessionApi.class);
      bindHttpApi(binder(), TaskApi.class);
      bindHttpApi(binder(), VAppApi.class);
      bindHttpApi(binder(), VmApi.class);
      
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(InvalidateSessionAndRetryOn401AndLogoutOnClose.class);
      
      super.configure();
      bindHttpApi(binder(),  VCloudDirectorAdminApi.class);
   }
   
   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(VCloudDirectorErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(VCloudDirectorErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(VCloudDirectorErrorHandler.class);
   }
   
   @Provides
   @Login
   protected Supplier<URI> loginUrl(@Provider Supplier<URI> provider) {
      // TODO: technically, we should implement version api, but this will work
      return Suppliers.compose(new Function<URI, URI>() {
         
         @Override
         public URI apply(URI arg0) {
            return URI.create(arg0.toASCIIString() + "/sessions");
         }
         
      }, provider);
   }
   
   @Provides
   protected Supplier<Session> currentSession(Supplier<SessionWithToken> in) {
      return Suppliers.compose(new Function<SessionWithToken, Session>() {
         
         @Override
         public Session apply(SessionWithToken arg0) {
            return arg0.getSession();
         }
         
      }, in);
      
   }
   
   @Provides
   @Singleton
   @org.jclouds.vcloud.director.v1_5.annotations.Session
   protected Supplier<String> sessionToken(Supplier<SessionWithToken> in) {
      return Suppliers.compose(new Function<SessionWithToken, String>() {
         
         @Override
         public String apply(SessionWithToken arg0) {
            return arg0.getToken();
         }
         
      }, in);
      
   }

   @Provides
   @Singleton
   LoadingCache<String, Entity> resolveEntityCache(ResolveEntity loader, @Named(PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(loader);
   }

   @Provides
   @Singleton
   LoadingCache<Credentials, SessionWithToken> provideSessionWithTokenCache(LoginUserInOrgWithPassword loader,
         @Named(PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(loader);
   }
   
   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<SessionWithToken> provideSessionWithTokenSupplier(
         final LoadingCache<Credentials, SessionWithToken> cache, @Provider final Supplier<Credentials> creds) {
      return new Supplier<SessionWithToken>() {
         @Override
         public SessionWithToken get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }
}
