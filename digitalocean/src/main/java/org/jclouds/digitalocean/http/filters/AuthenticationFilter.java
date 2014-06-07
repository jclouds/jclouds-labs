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
package org.jclouds.digitalocean.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;

/**
 * Adds the authentication query parameters to the requests.
 */
@Singleton
public class AuthenticationFilter implements HttpRequestFilter {

   public static final String IDENTITY_PARAM = "client_id";
   public static final String CREDENTIAL_PARAM = "api_key";

   private final Supplier<Credentials> credentials;

   @Inject
   AuthenticationFilter(@Provider final Supplier<Credentials> credentials) {
      this.credentials = checkNotNull(credentials, "credential supplier cannot be null");
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials creds = credentials.get();
      return request.toBuilder().addQueryParam(IDENTITY_PARAM, creds.identity)
            .addQueryParam(CREDENTIAL_PARAM, creds.credential).build();
   }

}
