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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.features.NetworkApi;
import org.jclouds.dimensiondata.cloudcontrol.features.ServerApi;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static java.lang.String.format;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_DELETED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.config.DimensionDataCloudControlComputeServiceContextModule.SERVER_STOPPED_PREDICATE;
import static org.jclouds.dimensiondata.cloudcontrol.utils.DimensionDataCloudControlResponseUtils.generateFirewallRuleName;

@Singleton
public class CleanupServer implements Function<String, Boolean> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DimensionDataCloudControlApi api;
   private final Timeouts timeouts;
   private Predicate<String> serverStoppedPredicate;
   private Predicate<String> serverDeletedPredicate;

   @Inject
   CleanupServer(final DimensionDataCloudControlApi api, final Timeouts timeouts,
         @Named(SERVER_STOPPED_PREDICATE) final Predicate<String> serverStoppedPredicate,
         @Named(SERVER_DELETED_PREDICATE) final Predicate<String> serverDeletedPredicate) {
      this.api = api;
      this.timeouts = timeouts;
      this.serverStoppedPredicate = serverStoppedPredicate;
      this.serverDeletedPredicate = serverDeletedPredicate;
   }

   @Override
   public Boolean apply(final String serverId) {
      final ServerApi serverApi = api.getServerApi();
      Server server = serverApi.getServer(serverId);

      if (server == null) {
         return true;
      }

      if (server.state().isFailed()) {
         rollbackOperation(format("Server(%s) not deleted as it is in state(%s).", serverId, server.state()));
      }

      if (!server.state().isNormal()) {
         return false;
      }

      String networkDomainId = server.networkInfo().networkDomainId();
      final String internalIp = server.networkInfo().primaryNic().privateIpv4();

      // delete nat rules associated to the server, if any
      final NetworkApi networkApi = api.getNetworkApi();
      List<NatRule> natRulesToBeDeleted = networkApi.listNatRules(networkDomainId).concat()
            .filter(new Predicate<NatRule>() {
               @Override
               public boolean apply(NatRule natRule) {
                  return natRule.internalIp().equals(internalIp);
               }
            }).toList();

      for (final NatRule natRule : natRulesToBeDeleted) {

         attemptDeleteNatRule(serverId, networkApi, natRule);

         Optional<PublicIpBlock> optionalPublicIpBlock = networkApi.listPublicIPv4AddressBlocks(networkDomainId)
               .concat().firstMatch(new Predicate<PublicIpBlock>() {
                  @Override
                  public boolean apply(PublicIpBlock input) {
                     return input.baseIp().equals(natRule.externalIp());
                  }
               });
         if (optionalPublicIpBlock.isPresent()) {
            attemptDeletePublicIpBlock(serverId, networkApi, optionalPublicIpBlock.get());
         }
      }

      List<FirewallRule> firewallRulesToBeDeleted = networkApi.listFirewallRules(networkDomainId).concat()
            .filter(new Predicate<FirewallRule>() {
               @Override
               public boolean apply(FirewallRule firewallRule) {
                  return firewallRule.name().equals(generateFirewallRuleName(serverId));
               }
            }).toList();

      for (FirewallRule firewallRule : firewallRulesToBeDeleted) {
         attemptDeleteFirewallRule(serverId, networkApi, firewallRule);
      }

      serverApi.powerOffServer(serverId);
      String message = format("Server(%s) not terminated within %d ms.", serverId, timeouts.nodeTerminated);
      if (!serverStoppedPredicate.apply(serverId)) {
         throw new IllegalStateException(message);
      }
      serverApi.deleteServer(serverId);
      String deleteFailureMessage = format("Server(%s) not deleted within %d ms.", serverId, timeouts.nodeTerminated);

      if (!serverDeletedPredicate.apply(serverId)) {
         throw new IllegalStateException(deleteFailureMessage);
      }
      return true;
   }

   private void attemptDeleteFirewallRule(final String serverId, final NetworkApi networkApi,
         final FirewallRule firewallRule) {
      try {
         if (firewallRule.state().isNormal()) {
            networkApi.deleteFirewallRule(firewallRule.id());
            if (firewallRule.destination() != null && firewallRule.destination().portList() != null) {
               try {
                  networkApi.deletePortList(firewallRule.destination().portList().id());
               } catch (Throwable t) {
                  logger.warn(t, format(
                        "Failed to delete PortList(%s) associated with FirewallRule(%s) and with Server(%s). Due to - (%s)",
                        firewallRule.destination().portList().id(), firewallRule.id(), serverId, t.getMessage()));
               }
            }
         } else {
            logger.warn(
                  format("Server(%s) has an associated FirewallRule(%s) that was not deleted as it is in state(%s).",
                        serverId, firewallRule.id(), firewallRule.state()));
         }
      } catch (Throwable t) {
         logger.warn(t,
               format("Failed to delete FirewallRule(%s) associated with Server(%s). Due to - (%s)", firewallRule.id(),
                     serverId, t.getMessage()));
      }
   }

   private void attemptDeletePublicIpBlock(final String serverId, final NetworkApi networkApi,
         final PublicIpBlock publicIpBlock) {
      try {
         if (publicIpBlock.state().isNormal()) {
            networkApi.removePublicIpBlock(publicIpBlock.id());
         } else {
            logger.warn(format("Server(%s) has an associated IpBlock(%s) that was not deleted as it was in state(%s).",
                  serverId, publicIpBlock.id(), publicIpBlock.state()));
         }
      } catch (Throwable t) {
         logger.warn(t,
               format("Failed to delete IpBlock(%s) associated with Server(%s). Due to - (%s)", publicIpBlock.id(),
                     serverId, t.getMessage()));
      }
   }

   private void attemptDeleteNatRule(final String serverId, final NetworkApi networkApi, final NatRule natRule) {
      try {
         if (natRule.state().isNormal()) {
            networkApi.deleteNatRule(natRule.id());
         } else {
            logger.warn(format("Server(%s) has an associated NatRule(%s) that was not deleted as it was in state(%s).",
                  serverId, natRule.id(), natRule.state()));
         }
      } catch (Throwable t) {
         logger.warn(t,
               format("Failed to delete NatRule(%s) associated with Server(%s). Due to - (%s)", natRule.id(), serverId,
                     t.getMessage()));
      }
   }

   private void rollbackOperation(final String message) {
      throw new IllegalStateException(message);
   }
}
