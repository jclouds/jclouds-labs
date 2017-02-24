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
package org.apache.jclouds.oneandone.rest.config;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.inject.Provides;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_PREDICATE_PRIVATE_NETWORK;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.handlers.OneAndOneHttpErrorHandler;
import org.apache.jclouds.oneandone.rest.ids.ServerPrivateNetworkRef;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import static org.jclouds.util.Predicates2.retry;

@ConfiguresHttpApi
public class OneAndOneHttpApiModule extends HttpApiModule<OneAndOneApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(OneAndOneHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(OneAndOneHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(OneAndOneHttpErrorHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }

   @Provides
   @Named(POLL_PREDICATE_PRIVATE_NETWORK)
   Predicate<ServerPrivateNetworkRef> providePrivateNetworkReadyPredicate(final OneAndOneApi api, ComputeServiceConstants.Timeouts timeouts, ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new PrivateNetworkReadyPredicate(
              api),
              timeouts.nodeRunning, pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod, TimeUnit.SECONDS);
   }

   static class PrivateNetworkReadyPredicate implements Predicate<ServerPrivateNetworkRef> {

      private final OneAndOneApi api;

      public PrivateNetworkReadyPredicate(OneAndOneApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(ServerPrivateNetworkRef networkRef) {
         checkNotNull(networkRef, "ServerPrivateNetworkRef");
         PrivateNetwork server = api.serverApi().getPrivateNetwork(networkRef.serverId(), networkRef.privateNetworkId());
         return server.state() != Types.GenericState.ACTIVE;
      }
   }
}
