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
package org.jclouds.abiquo.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.config.AbiquoAuthenticationModule.AUTH_TOKEN_NAME;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.config.Authentication;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.common.base.Supplier;
import com.google.common.net.HttpHeaders;

/**
 * Authenticates using Basic Authentication or a generated token from previous
 * API sessions.
 */
@Singleton
public class AbiquoAuthentication implements HttpRequestFilter {
   private final Supplier<String> authTokenProvider;

   @Inject
   public AbiquoAuthentication(@Authentication Supplier<String> authTokenProvider) {
      this.authTokenProvider = checkNotNull(authTokenProvider, "authTokenProvider must not be null");
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return request.toBuilder().replaceHeader(HttpHeaders.COOKIE, tokenAuth(authTokenProvider.get())).build();
   }

   private static String tokenAuth(final String token) {
      return AUTH_TOKEN_NAME + "=" + checkNotNull(token, "missing authentication token");
   }
}
