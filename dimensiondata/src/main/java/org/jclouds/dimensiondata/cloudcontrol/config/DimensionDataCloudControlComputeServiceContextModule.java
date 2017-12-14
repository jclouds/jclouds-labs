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
package org.jclouds.dimensiondata.cloudcontrol.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.State;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.domain.VmTools;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerApi;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataProperties.OPERATION_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

public class DimensionDataCloudControlComputeServiceContextModule extends AbstractModule {

   @Resource
   private Logger logger = Logger.NULL;

   public static final String VLAN_DELETED_PREDICATE = "VLAN_DELETED_PREDICATE";
   public static final String VLAN_NORMAL_PREDICATE = "VLAN_NORMAL_PREDICATE";
   public static final String NETWORK_DOMAIN_DELETED_PREDICATE = "NETWORK_DOMAIN_DELETED_PREDICATE";
   public static final String NETWORK_DOMAIN_NORMAL_PREDICATE = "NETWORK_DOMAIN_NORMAL_PREDICATE";
   public static final String SERVER_STARTED_PREDICATE = "SERVER_STARTED_PREDICATE";
   public static final String SERVER_STOPPED_PREDICATE = "SERVER_STOPPED_PREDICATE";
   public static final String SERVER_DELETED_PREDICATE = "SERVER_DELETED_PREDICATE";
   public static final String SERVER_NORMAL_PREDICATE = "SERVER_NORMAL_PREDICATE";
   public static final String VM_TOOLS_RUNNING_PREDICATE = "VM_TOOLS_RUNNING_PREDICATE";

   @Override
   protected void configure() {

   }

   @Provides
   @Named(VLAN_DELETED_PREDICATE)
   protected Predicate<String> provideVlanDeletedPredicate(final DimensionDataCloudControlApi api,
         @Named(OPERATION_TIMEOUT) final Long operationTimeout, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new VlanState(api.getNetworkApi(), State.DELETED), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VLAN_NORMAL_PREDICATE)
   protected Predicate<String> provideVlanNormalPredicate(final DimensionDataCloudControlApi api,
         @Named(OPERATION_TIMEOUT) final Long operationTimeout, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new VlanState(api.getNetworkApi(), State.NORMAL), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(NETWORK_DOMAIN_DELETED_PREDICATE)
   protected Predicate<String> provideNetworkDomainDeletedPredicate(final DimensionDataCloudControlApi api,
         @Named(OPERATION_TIMEOUT) final Long operationTimeout, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new NetworkDomainState(api.getNetworkApi(), State.DELETED), operationTimeout,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(NETWORK_DOMAIN_NORMAL_PREDICATE)
   protected Predicate<String> provideNetworkDomainNormalPredicate(final DimensionDataCloudControlApi api,
         @Named(OPERATION_TIMEOUT) final Long operationTimeout, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new NetworkDomainState(api.getNetworkApi(), State.NORMAL), operationTimeout,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(SERVER_STARTED_PREDICATE)
   protected Predicate<String> provideServerStartedPredicate(final DimensionDataCloudControlApi api,
         final ComputeServiceConstants.Timeouts timeouts, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerStatus(api.getServerApi(), true, true), timeouts.nodeRunning, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(SERVER_STOPPED_PREDICATE)
   @VisibleForTesting
   public Predicate<String> provideServerStoppedPredicate(final DimensionDataCloudControlApi api,
         final ComputeServiceConstants.Timeouts timeouts, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerStatus(api.getServerApi(), false, true), timeouts.nodeSuspended,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(SERVER_DELETED_PREDICATE)
   @VisibleForTesting
   public Predicate<String> provideServerDeletedPredicate(final DimensionDataCloudControlApi api,
         final ComputeServiceConstants.Timeouts timeouts, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerState(api.getServerApi(), State.DELETED), timeouts.nodeTerminated,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(SERVER_NORMAL_PREDICATE)
   protected Predicate<String> provideServerNormalPredicate(final DimensionDataCloudControlApi api,
         final ComputeServiceConstants.Timeouts timeouts, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerState(api.getServerApi(), State.NORMAL), timeouts.nodeRunning,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VM_TOOLS_RUNNING_PREDICATE)
   protected Predicate<String> provideVMToolsRunningPredicate(final DimensionDataCloudControlApi api,
         @Named(OPERATION_TIMEOUT) final Long operationTimeout, final ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new VMToolsRunningStatus(api.getServerApi()), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   private class VlanState implements Predicate<String> {

      private final State state;
      private final NetworkApi networkApi;

      private VlanState(final NetworkApi networkApi, final State state) {
         this.networkApi = networkApi;
         this.state = state;
      }

      @Override
      public boolean apply(final String vlanId) {
         checkNotNull(vlanId, "vlanId");
         logger.trace("looking for state on vlan %s", vlanId);
         final Vlan vlan = networkApi.getVlan(vlanId);
         final boolean isDeleted = (vlan == null) && (state == State.DELETED);
         return isDeleted || ((vlan != null) && vlan.state() == state);
      }
   }

   private class NetworkDomainState implements Predicate<String> {

      private final State state;
      private final NetworkApi networkApi;

      private NetworkDomainState(final NetworkApi networkApi, final State state) {
         this.networkApi = networkApi;
         this.state = state;
      }

      @Override
      public boolean apply(final String networkDomainId) {
         checkNotNull(networkDomainId, "networkDomainId");
         logger.trace("looking for state on network domain %s", networkDomainId);
         final NetworkDomain networkDomain = networkApi.getNetworkDomain(networkDomainId);
         final boolean isDeleted = networkDomain == null && state == State.DELETED;
         return isDeleted || (networkDomain != null && networkDomain.state() == state);
      }
   }

   private class ServerStatus implements Predicate<String> {

      private final ServerApi api;
      private final boolean started;
      private final boolean deployed;

      private ServerStatus(final ServerApi api, final boolean started, final boolean deployed) {
         this.api = api;
         this.started = started;
         this.deployed = deployed;
      }

      @Override
      public boolean apply(final String serverId) {
         checkNotNull(serverId, "serverId");
         logger.trace("looking for start status on Server %s", serverId);
         final Server server = api.getServer(serverId);

         // perhaps request isn't available, yet
         if (server == null)
            return false;
         logger.trace("Looking for Server %s to be started: %s currently: %s", server.id(), started, server.started());
         if (server.state().isFailed()) {
            throw new IllegalStateException(String.format("Server %s is in FAILED state", server.id()));
         }
         return server.started() == started && server.deployed() == deployed;
      }
   }

   private class ServerState implements Predicate<String> {

      private final ServerApi api;
      private final State state;

      private ServerState(final ServerApi api, final State state) {
         this.api = api;
         this.state = state;
      }

      @Override
      public boolean apply(final String serverId) {
         checkNotNull(serverId, "serverId");
         logger.trace("looking for state on Server %s", serverId);
         final Server server = api.getServer(serverId);

         if (server == null && state == State.DELETED) {
            return true;
         }

         if (server.state().isFailed()) {
            throw new IllegalStateException(String.format("Server %s is in FAILED state", server.id()));
         } else {
            return server.state() == state;
         }
      }
   }

   private class VMToolsRunningStatus implements Predicate<String> {

      private final ServerApi api;

      private VMToolsRunningStatus(final ServerApi api) {
         this.api = api;
      }

      @Override
      public boolean apply(final String serverId) {
         checkNotNull(serverId, "serverId");
         logger.trace("looking for guest tools state on Server %s", serverId);
         final Server server = api.getServer(serverId);
         if (server == null) {
            throw new IllegalStateException(String.format("Server %s is not found", serverId));
         }
         final VmTools vmTools = server.guest().vmTools();
         return vmTools != null && vmTools.runningStatus() == VmTools.RunningStatus.RUNNING;
      }
   }
}
