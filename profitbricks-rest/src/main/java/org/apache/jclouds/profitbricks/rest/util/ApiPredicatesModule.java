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
package org.apache.jclouds.profitbricks.rest.util;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Provides;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PERIOD;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_TIMEOUT;
import org.apache.jclouds.profitbricks.rest.domain.ProvisioningState;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import com.google.inject.AbstractModule;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_MAX_PERIOD;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.TIMEOUT_NODE_RUNNING;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.TIMEOUT_NODE_SUSPENDED;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import static org.jclouds.util.Predicates2.retry;

public class ApiPredicatesModule extends AbstractModule {

   @Override
   protected void configure() {}

   @Provides
   @Singleton
   @Named(POLL_PREDICATE_DATACENTER)
   Predicate<String> provideDataCenterAvailablePredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new DataCenterProvisioningStatePredicate(
              api, ProvisioningState.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   Predicate<ServerRef> provideServerRunningPredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new ServerStatusPredicate(
              api, Server.Status.RUNNING),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }
   
   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   Predicate<ServerRef> provideServerSuspendedPredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new ServerStatusPredicate(
              api, Server.Status.SHUTOFF),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }
   
   static class DataCenterProvisioningStatePredicate implements Predicate<String> {

      private final ProfitBricksApi api;
      private final ProvisioningState expectedState;

      public DataCenterProvisioningStatePredicate(ProfitBricksApi api, ProvisioningState expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "datacenter id");
         return api.dataCenterApi().getDataCenter(input).metadata().state().toString().equals(expectedState.toString());
      }

   }

   static class ServerStatusPredicate implements Predicate<ServerRef> {

      private final ProfitBricksApi api;
      private final Server.Status expectedStatus;

      public ServerStatusPredicate(ProfitBricksApi api, Server.Status expectedStatus) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedStatus = checkNotNull(expectedStatus, "expectedStatus must not be null");
      }

      @Override
      public boolean apply(ServerRef serverRef) {
         checkNotNull(serverRef, "serverRef");
         
         Server server = api.serverApi().getServer(serverRef.dataCenterId(), serverRef.serverId());
         
         if (server == null || server.properties().vmState() == null)
            return false;
         
         return server.properties().vmState() == expectedStatus;
      }

   }

   @Singleton
   public static class ComputeConstants {

      @Inject
      @Named(POLL_TIMEOUT)
      private String pollTimeout;

      @Inject
      @Named(POLL_PERIOD)
      private String pollPeriod;

      @Inject
      @Named(POLL_MAX_PERIOD)
      private String pollMaxPeriod;

      public long pollTimeout() {
         return Long.parseLong(pollTimeout);
      }

      public long pollPeriod() {
         return Long.parseLong(pollPeriod);
      }

      public long pollMaxPeriod() {
         return Long.parseLong(pollMaxPeriod);
      }
   }
}
