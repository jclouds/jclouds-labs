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
package org.jclouds.fujitsu.fgcp.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.util.Predicates2.retry;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.compute.functions.ResourceIdToFirewallId;
import org.jclouds.fujitsu.fgcp.compute.functions.ResourceIdToSystemId;
import org.jclouds.fujitsu.fgcp.compute.predicates.ServerStarted;
import org.jclouds.fujitsu.fgcp.compute.predicates.ServerStopped;
import org.jclouds.fujitsu.fgcp.compute.strategy.VServerMetadata.Builder;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.domain.VServerWithVNICs;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * Defines the connection between the {@link org.jclouds.fujitsu.fgcp.FGCPApi}
 * implementation and the jclouds {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class FGCPComputeServiceAdapter implements
      ComputeServiceAdapter<VServerMetadata, ServerType, DiskImage, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final FGCPApi api;
   protected Predicate<String> serverStopped = null;
   protected Predicate<String> serverStarted = null;
   protected Predicate<String> serverCreated = null;
   protected ResourceIdToFirewallId toFirewallId = null;
   protected ResourceIdToSystemId toSystemId = null;

   @Inject
   public FGCPComputeServiceAdapter(FGCPApi api, ServerStopped serverStopped,
         ServerStarted serverStarted, Timeouts timeouts,
         ResourceIdToFirewallId toFirewallId,
         ResourceIdToSystemId toSystemId) {
      this.api = checkNotNull(api, "api");
      this.serverStopped = retry(checkNotNull(serverStopped), timeouts.nodeSuspended);
      this.serverStarted = retry(checkNotNull(serverStarted), timeouts.nodeRunning);
      this.serverCreated = retry(checkNotNull(serverStopped), timeouts.nodeRunning);
      this.toFirewallId = checkNotNull(toFirewallId, "ResourceIdToFirewallId");
      this.toSystemId = checkNotNull(toSystemId, "ResourceIdToSystemId");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeAndInitialCredentials<VServerMetadata> createNodeWithGroupEncodedIntoName(
         String group, String name, Template template) {
      String id = api.getVirtualSystemApi().createServer(name,
            template.getHardware().getName(), template.getImage().getId(),
            template.getLocation().getId());

      // wait until fully created (i.e. transitions to stopped status)
      checkState(serverCreated.apply(id), "node %s not reaching STOPPED state after creation", id);
      resumeNode(id);
      // don't wait until fully started, template "optionToNotBlock" takes care of that
      VServerMetadata server = getNode(id);

      // do we need this?
      server.setTemplate(template);
      String user = template.getImage().getOperatingSystem().getFamily() == OsFamily.WINDOWS ? "Administrator" : "root";

      return new NodeAndInitialCredentials<VServerMetadata>(server, id, LoginCredentials.builder().identity(user)
            .password(server.getInitialPassword()).build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<ServerType> listHardwareProfiles() {
      return api.getVirtualDCApi().listServerTypes();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<DiskImage> listImages() {
      return api.getVirtualDCApi().listDiskImages();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DiskImage getImage(String id) {
      return api.getDiskImageApi().get(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      // see SystemAndNetworkSegmentToLocationSupplier
      return ImmutableSet.<Location> of();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VServerMetadata getNode(String id) {
      Builder builder = VServerMetadata.builder();
      builder.id(id);

      try {
         VServerWithDetails server = api.getVirtualServerApi().getDetails(id);
         // skip FWs and SLBs
         if (isFWorSLB(server)) {
            return null;
         }
         VServerStatus status = api.getVirtualServerApi().getStatus(id);
         logger.trace("Node %s [%s] - %s", id, status, server);
         builder.serverWithDetails(server);
         builder.status(status);
         builder.initialPassword(api.getVirtualServerApi().getInitialPassword(id));

         // mapped public ips?
//       String fwId = toFirewallId.apply(id);
       // futures.add(asyncApi.getBuiltinServerApi().getConfiguration(fwId,
       // BuiltinServerConfiguration.FW_RULE));
      } catch (ResourceNotFoundException e) {
         return null;
      }

      return builder.build();
   }

   private boolean isFWorSLB(VServer server) {
      String serverType = server.getType();
      return "firewall".equals(serverType) || "slb".equals(serverType);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<VServerMetadata> listNodes() {
      ImmutableSet.Builder<VServerMetadata> servers = ImmutableSet.<VServerMetadata> builder();

      Set<VSystem> systems = api.getVirtualDCApi().listVirtualSystems();
      for (VSystem system : systems) {

         VSystemWithDetails systemDetails = api.getVirtualSystemApi().getDetails(system.getId());

         for (VServerWithVNICs server : systemDetails.getServers()) {

            // skip FWs and SLBs
            if (!isFWorSLB(server)) {
               servers.add(getNode(server.getId()));
            }
         }
      }

      return servers.build();
   }

   @Override
   public Iterable<VServerMetadata> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VServerMetadata>() {

         @Override
         public boolean apply(VServerMetadata server) {
            return contains(ids, server.getId());
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyNode(String id) {
      // ensure it is stopped first
      suspendNode(id);
      checkState(serverStopped.apply(id), "could not stop %s before destroying it", id);
      api.getVirtualServerApi().destroy(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rebootNode(String id) {
      suspendNode(id);
      checkState(serverStopped.apply(id), "could not stop %s before restarting it", id);
      resumeNode(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void resumeNode(String id) {
      try {
         api.getVirtualServerApi().start(id);
      } catch (IllegalStateException ise) {
         if (!(ise.getMessage().contains("ALREADY_STARTED") || ise.getMessage().contains("STARTING"))) {
            throw ise;
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void suspendNode(String id) {
      try {
         api.getVirtualServerApi().stop(id);
      } catch (IllegalStateException ise) {
         if (ise.getMessage().contains("ALREADY_STOPPED") || ise.getMessage().contains("STOPPING")) {
            logger.trace("suspendNode({0}) - {1}", id, ise.getMessage());
            // ignore as it has/will reach the desired destination state
         } else if (ise.getMessage().contains("STARTING")) {
            // wait till running, then try to stop again
            logger.trace("suspendNode({0}) - {1} - waiting to reach RUNNING state", id, ise.getMessage());
            checkState(serverStarted.apply(id), "starting %s didn't reach RUNNING state", id);
            logger.trace("suspendNode({0}) - now RUNNING, trying to stop again", id);
            try {
               api.getVirtualServerApi().stop(id);
            } catch (IllegalStateException e) {
               if (e.getMessage().contains("ALREADY_STOPPED") || e.getMessage().contains("STOPPING")) {
                  logger.trace("suspendNode({0}) - {1}", id, e.getMessage());
                  // ignore as it has/will reach the desired destination state
               } else {
                  throw e;
               }
            }
         } else {
            throw ise;
         }
      } catch (RuntimeException e) {
         logger.error(e, "suspendNode({0}) - exception occurred!", id);
         throw e;
      }
   }
}
