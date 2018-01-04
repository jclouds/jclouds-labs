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
package org.apache.jclouds.profitbricks.rest.compute.config;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.compute.ProfitBricksComputeServiceAdapter;
import org.apache.jclouds.profitbricks.rest.compute.concurrent.ProvisioningJob;
import org.apache.jclouds.profitbricks.rest.compute.concurrent.ProvisioningManager;
import org.apache.jclouds.profitbricks.rest.compute.extensions.ProfitBricksImageExtension;
import org.apache.jclouds.profitbricks.rest.compute.function.ProvisionableToImage;
import org.apache.jclouds.profitbricks.rest.compute.function.ServerInDataCenterToNodeMetadata;
import org.apache.jclouds.profitbricks.rest.compute.function.VolumeToVolume;
import org.apache.jclouds.profitbricks.rest.compute.strategy.AssignDataCenterToTemplate;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_MAX_PERIOD;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PERIOD;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_NIC;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_SERVER;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_SNAPSHOT;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_TIMEOUT;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.Provisionable;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.ServerInDataCenter;
import org.apache.jclouds.profitbricks.rest.ids.NicRef;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.ArbitraryCpuRamTemplateBuilderImpl;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.util.PasswordGenerator;

import static org.jclouds.util.Predicates2.retry;

public class ProfitBricksComputeServiceContextModule extends
        ComputeServiceAdapterContextModule<ServerInDataCenter, Hardware, Provisionable, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();

      install(new FactoryModuleBuilder().build(ProvisioningJob.Factory.class));

      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);

      bind(CreateNodesInGroupThenAddToSet.class).to(AssignDataCenterToTemplate.class).in(Scopes.SINGLETON);

      bind(new TypeLiteral<ComputeServiceAdapter<ServerInDataCenter, Hardware, Provisionable, Location>>() {
      }).to(ProfitBricksComputeServiceAdapter.class);

      bind(TemplateBuilderImpl.class).to(ArbitraryCpuRamTemplateBuilderImpl.class);

      bind(new TypeLiteral<Function<ServerInDataCenter, NodeMetadata>>() {
      }).to(ServerInDataCenterToNodeMetadata.class);

      bind(new TypeLiteral<Function<Provisionable, Image>>() {
      }).to(ProvisionableToImage.class);

      bind(new TypeLiteral<Function<org.apache.jclouds.profitbricks.rest.domain.Volume, Volume>>() {
      }).to(VolumeToVolume.class);

      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));

      bind(new TypeLiteral<ImageExtension>() {
      }).to(ProfitBricksImageExtension.class);
   }
   
   @Provides
   @Singleton
   protected PasswordGenerator.Config providePasswordGenerator() {
      return new PasswordGenerator()
            .lower().min(2).max(10).exclude("ilowyz".toCharArray())
            .upper().min(2).max(10).exclude("IOWYZ".toCharArray())
            .numbers().min(2).max(10).exclude("10".toCharArray())
            .symbols().count(0);
   }

   @Provides
   @Singleton
   @Named(POLL_PREDICATE_DATACENTER)
   Predicate<String> provideDataCenterAvailablePredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new DataCenterProvisioningStatePredicate(
              api, State.AVAILABLE),
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

   @Provides
   @Named(POLL_PREDICATE_SERVER)
   Predicate<ServerRef> provideServerAvailablePredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new ServerAvaiblablePredicate(
              api, State.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   ProvisioningManager provideProvisioningManager(Closer closer) {
      ProvisioningManager provisioningManager = new ProvisioningManager();
      closer.addToClose(provisioningManager);

      return provisioningManager;
   }

   @Provides
   @Singleton
   @Named(POLL_PREDICATE_SNAPSHOT)
   Predicate<String> provideSnapshotAvailablePredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new SnapshotProvisioningStatePredicate(
              api, State.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_RUNNING)
   Predicate<VolumeRef> provideVolumeAvailablePredicate(final ProfitBricksApi api, ComputeConstants constants) {
      return retry(new VolumeProvisoningStatusPredicate(
              api, State.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named(POLL_PREDICATE_NIC)
   Predicate<NicRef> provideNicAvailablePredicate(final ProfitBricksApi api, ProfitBricksComputeServiceContextModule.ComputeConstants constants) {
      return retry(new NicAvailable(
              api, State.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   static class DataCenterProvisioningStatePredicate implements Predicate<String> {

      private final ProfitBricksApi api;
      private final State expectedState;

      public DataCenterProvisioningStatePredicate(ProfitBricksApi api, State expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "datacenter id");
         DataCenter dataCenter = api.dataCenterApi().getDataCenter(input);
         return dataCenter.metadata().state() == expectedState;
      }

   }

   static class ServerAvaiblablePredicate implements Predicate<ServerRef> {

      private final ProfitBricksApi api;
      private final State expectedState;

      public ServerAvaiblablePredicate(ProfitBricksApi api, State expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(ServerRef serverRef) {

         checkNotNull(serverRef, "serverRef");
         //give time for the operation to actually start
         Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
         Server server = api.serverApi().getServer(serverRef.dataCenterId(), serverRef.serverId());

         if (server == null || server.metadata() == null) {
            return false;
         }
         return server.metadata().state().toString().equals(expectedState.toString());
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
         //give time for the operation to actually start
         Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
         Server server = api.serverApi().getServer(serverRef.dataCenterId(), serverRef.serverId());

         if (server == null || server.properties().vmState() == null) {
            return false;
         }

         return server.properties().vmState() == expectedStatus;
      }

   }

   static class SnapshotProvisioningStatePredicate implements Predicate<String> {

      private final ProfitBricksApi api;
      private final State expectedState;

      public SnapshotProvisioningStatePredicate(ProfitBricksApi api, State expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "snapshot id");
         return api.snapshotApi().get(input).metadata().state().toString().equals(expectedState.toString());
      }
   }

   static class VolumeProvisoningStatusPredicate implements Predicate<VolumeRef> {

      private final ProfitBricksApi api;
      private final State expectedState;

      public VolumeProvisoningStatusPredicate(ProfitBricksApi api, State expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(VolumeRef input) {
         checkNotNull(input, "Volume REF");
         org.apache.jclouds.profitbricks.rest.domain.Volume volume = api.volumeApi().getVolume(input.dataCenterId(), input.volumeId());
         if (volume == null || volume.metadata() == null || volume.metadata().state() == null) {
            return false;
         }
         return volume.metadata().state().toString().equals(expectedState.toString());
      }
   }

   static class NicAvailable implements Predicate<NicRef> {

      private final ProfitBricksApi api;
      private final State expectedState;

      public NicAvailable(ProfitBricksApi api, State expectedState) {
         this.api = checkNotNull(api, "api must not be null");
         this.expectedState = checkNotNull(expectedState, "expectedState must not be null");
      }

      @Override
      public boolean apply(NicRef input) {
         checkNotNull(input, "NicRef ");
         Nic nice = api.nicApi().get(input.dataCenterId(), input.serverId(), input.nicId());
         if (nice == null || nice.metadata() == null || nice.metadata().state() == null) {
            return false;
         }
         return nice.metadata().state().toString().equals(expectedState.toString());
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
