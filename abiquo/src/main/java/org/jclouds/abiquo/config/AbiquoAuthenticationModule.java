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
package org.jclouds.abiquo.config;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.abiquo.config.AbiquoProperties.CREDENTIAL_TYPE;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.abiquo.functions.auth.GetTokenFromApi;
import org.jclouds.abiquo.functions.auth.GetTokenFromCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Abiquo authentication.
 */
public class AbiquoAuthenticationModule extends AbstractModule {

   /** The name of the authentication token. */
   public static final String AUTH_TOKEN_NAME = "auth";

   @Provides
   @Singleton
   protected Map<String, Function<Credentials, String>> authenticationMethods(GetTokenFromApi tokenFromApi,
         GetTokenFromCredentials tokenFromCredentials) {
      return ImmutableMap.of("password", tokenFromApi, "token", tokenFromCredentials);
   }

   @Provides
   @Singleton
   protected Function<Credentials, String> authenticationMethodForCredentialType(
         Map<String, Function<Credentials, String>> authenticationMethods, @Named(CREDENTIAL_TYPE) String credentialType) {
      checkArgument(authenticationMethods.containsKey(credentialType), "credential type %s not in supported list: %s",
            credentialType, authenticationMethods.keySet());
      return authenticationMethods.get(credentialType);
   }

   // Abiquo authentication tokens have 30 minutes life time
   @Provides
   @Singleton
   protected LoadingCache<Credentials, String> provideTokenCache(Function<Credentials, String> getToken) {
      return CacheBuilder.newBuilder().expireAfterWrite(29, TimeUnit.MINUTES).build(CacheLoader.from(getToken));
   }

   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideTokenSupplier(final LoadingCache<Credentials, String> cache,
         @Provider final Supplier<Credentials> creds) {
      return new Supplier<String>() {
         @Override
         public String get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }

   @Override
   protected void configure() {
      // Nothing to do here
   }

}
