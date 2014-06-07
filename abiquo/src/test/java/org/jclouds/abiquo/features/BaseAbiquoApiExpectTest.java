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
package org.jclouds.abiquo.features;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoApiMetadata;
import org.jclouds.abiquo.functions.auth.GetTokenFromApi;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * Base class for Abiquo expect tests.
 */
public abstract class BaseAbiquoApiExpectTest<S> extends BaseRestApiExpectTest<S> {
   protected final String tokenAuth = "auth=mock-token";

   public BaseAbiquoApiExpectTest() {
      provider = "abiquo";
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new AbiquoApiMetadata();
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(GetTokenFromApi.class).to(MockTokenFromApi.class);
         }
      };
   }

   @Override
   public S createClient(final Function<HttpRequest, HttpResponse> fn, final Module module, final Properties props) {
      return clientFrom(createInjector(fn, module, props).getInstance(AbiquoApi.class));
   }

   protected abstract S clientFrom(AbiquoApi api);

   protected String normalize(final String mediatType) {
      return MediaType.valueOf(mediatType).toString();
   }

   @Singleton
   private static class MockTokenFromApi extends GetTokenFromApi {
      @Inject
      public MockTokenFromApi(ProviderMetadata provider, HttpClient http) {
         super(provider, http);
      }

      @Override
      public String apply(Credentials input) {
         return "mock-token";
      }
   }

}
