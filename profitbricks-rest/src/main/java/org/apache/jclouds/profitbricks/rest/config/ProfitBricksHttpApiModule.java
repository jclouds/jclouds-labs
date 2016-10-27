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
package org.apache.jclouds.profitbricks.rest.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.compute.config.ProfitBricksComputeServiceContextModule.ComputeConstants;
import org.apache.jclouds.profitbricks.rest.domain.RequestStatus;
import org.apache.jclouds.profitbricks.rest.handlers.ProfitBricksHttpErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.common.base.Predicate;
import com.google.inject.Provides;

@ConfiguresHttpApi
public class ProfitBricksHttpApiModule extends HttpApiModule<ProfitBricksApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ProfitBricksHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ProfitBricksHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ProfitBricksHttpErrorHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }

   @Provides
   @Singleton
   Predicate<URI> provideRequestCompletedPredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new RequestCompletedPredicate(api), constants.pollTimeout(), constants.pollPeriod(),
            constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   private static class RequestCompletedPredicate implements Predicate<URI> {
      private final ProfitBricksApi api;

      private RequestCompletedPredicate(ProfitBricksApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(URI uri) {
         RequestStatus status = api.getRequestStatus(checkNotNull(uri, "uri"));
         switch (status.metadata().status()) {
            case DONE:
            case FAILED:
               return true;
            default:
               return false;
         }
      }

   }
}
