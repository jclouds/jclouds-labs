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
package org.jclouds.abiquo.functions.auth;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.jclouds.abiquo.config.AbiquoAuthenticationModule.AUTH_TOKEN_NAME;
import static org.jclouds.http.filters.BasicAuthentication.basic;

import java.net.URI;
import java.util.Collection;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Cookie;

import org.jclouds.abiquo.config.AbiquoProperties;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.net.HttpHeaders;

/**
 * Requests a new authentication token.
 */
@Singleton
public class GetTokenFromApi implements Function<Credentials, String> {

   /** Information of the provider. */
   private final ProviderMetadata provider;

   /** The raw HTTP client used to request the authentication token. */
   private final HttpClient http;

   @Resource
   @Named(AbiquoProperties.ABIQUO_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public GetTokenFromApi(ProviderMetadata provider, HttpClient http) {
      this.provider = checkNotNull(provider, "provider must not be null");
      this.http = checkNotNull(http, "http must not be null");
   }

   @Override
   public String apply(Credentials input) {
      logger.info(">> Requesting an authentication token for user: %s...", input.identity);

      HttpResponse response = http.invoke(HttpRequest.builder() //
            .method("GET") //
            .endpoint(URI.create(provider.getEndpoint() + "/login")) //
            .addHeader(AUTHORIZATION, basic(input.identity, input.credential)) //
            .build());

      Optional<Cookie> token = readAuthenticationToken(response);
      if (!token.isPresent()) {
         throw new AuthorizationException("Could not obtain a new authentication token");
      }

      return token.get().getValue();
   }

   @VisibleForTesting
   static Optional<Cookie> readAuthenticationToken(final HttpResponse response) {
      Collection<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
      return tryFind(transform(cookies, cookie()), new Predicate<Cookie>() {
         @Override
         public boolean apply(Cookie input) {
            return input.getName().equals(AUTH_TOKEN_NAME);
         }
      });

   }

   private static Function<String, Cookie> cookie() {
      return new Function<String, Cookie>() {
         @Override
         public Cookie apply(String input) {
            return Cookie.valueOf(input);
         }
      };
   }

}
