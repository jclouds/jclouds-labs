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
package org.apache.jclouds.profitbricks.rest.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static java.lang.String.format;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_NIC;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_SERVER;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.util.ComputeServiceUtils.getPortRangesFromList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.compute.concurrent.ProvisioningJob;
import org.apache.jclouds.profitbricks.rest.compute.concurrent.ProvisioningManager;
import org.apache.jclouds.profitbricks.rest.compute.function.ProvisionableToImage;
import org.apache.jclouds.profitbricks.rest.compute.strategy.TemplateWithDataCenter;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.Lan;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.Provisionable;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.VolumeType;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.DataCenterAndId;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.ServerInDataCenter;
import org.apache.jclouds.profitbricks.rest.features.ServerApi;
import org.apache.jclouds.profitbricks.rest.ids.NicRef;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.ids.VolumeRef;
import org.apache.jclouds.profitbricks.rest.util.Trackables;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.PasswordGenerator;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

@Singleton
public class ProfitBricksComputeServiceAdapter implements ComputeServiceAdapter<ServerInDataCenter, Hardware, Provisionable, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ProfitBricksApi api;
   private final Predicate<String> waitDcUntilAvailable;
   private final Predicate<VolumeRef> waitVolumeUntilAvailable;
   private final Predicate<ServerRef> waitServerUntilAvailable;
   private final Predicate<ServerRef> waitServerUntilRunning;
   private final Predicate<ServerRef> waitServerUntilSuspended;
   private final Predicate<NicRef> waitNICUntilAvailable;
   private final Trackables trackables;
   private final ListeningExecutorService executorService;
   private final ProvisioningJob.Factory jobFactory;
   private final ProvisioningManager provisioningManager;
   private final PasswordGenerator.Config passwordGenerator;
   private List<DataCenter> datacetners;

   private static final Integer DEFAULT_LAN_ID = 1;

   @Inject
   ProfitBricksComputeServiceAdapter(ProfitBricksApi api,
           @Named(POLL_PREDICATE_DATACENTER) Predicate<String> waitDcUntilAvailable,
           @Named(TIMEOUT_NODE_RUNNING) Predicate<VolumeRef> waitVolumeUntilAvailable,
           @Named(PROPERTY_USER_THREADS) ListeningExecutorService executorService,
           @Named(POLL_PREDICATE_SERVER) Predicate<ServerRef> waitServerUntilAvailable,
           @Named(TIMEOUT_NODE_RUNNING) Predicate<ServerRef> waitServerUntilRunning,
           @Named(TIMEOUT_NODE_SUSPENDED) Predicate<ServerRef> waitServerUntilSuspended,
           @Named(POLL_PREDICATE_NIC) Predicate<NicRef> waitNICUntilAvailable,
           Trackables trackables,
           ProvisioningJob.Factory jobFactory,
           ProvisioningManager provisioningManager,
           PasswordGenerator.Config passwordGenerator) {
      this.api = api;
      this.waitDcUntilAvailable = waitDcUntilAvailable;
      this.waitVolumeUntilAvailable = waitVolumeUntilAvailable;
      this.waitServerUntilAvailable = waitServerUntilAvailable;
      this.waitNICUntilAvailable = waitNICUntilAvailable;
      this.waitServerUntilSuspended = waitServerUntilSuspended;
      this.waitServerUntilRunning = waitServerUntilRunning;
      this.trackables = trackables;
      this.executorService = executorService;
      this.jobFactory = jobFactory;
      this.provisioningManager = provisioningManager;
      this.passwordGenerator = passwordGenerator;
      this.datacetners = ImmutableList.of();
   }

   @Override
   public NodeAndInitialCredentials<ServerInDataCenter> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      checkArgument(template instanceof TemplateWithDataCenter, "This implementation requires a TemplateWithDataCenter");
      return createNodeWithGroupEncodedIntoName(group, name, TemplateWithDataCenter.class.cast(template));
   }

   protected NodeAndInitialCredentials<ServerInDataCenter> createNodeWithGroupEncodedIntoName(String group, String name, TemplateWithDataCenter template) {
      checkArgument(template.getLocation().getScope() == LocationScope.ZONE, "Template must use a ZONE-scoped location");
      final String dataCenterId = template.getDataCenter().id();
      Hardware hardware = template.getHardware();
      TemplateOptions options = template.getOptions();
      final String loginUser = isNullOrEmpty(options.getLoginUser()) ? "root" : options.getLoginUser();
      final String pubKey = options.getPublicKey();
      final String password = options.hasLoginPassword() ? options.getLoginPassword() : passwordGenerator.generate();
      final org.jclouds.compute.domain.Image image = template.getImage();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      // provision all volumes based on hardware
      List<? extends Volume> volumes = hardware.getVolumes();
      List<String> volumeIds = Lists.newArrayListWithExpectedSize(volumes.size());

      int i = 1;
      for (final Volume volume : volumes) {
         try {
            logger.trace("<< provisioning volume '%s'", volume);
            final org.apache.jclouds.profitbricks.rest.domain.Volume.Request.CreatePayload.Builder request = org.apache.jclouds.profitbricks.rest.domain.Volume.Request.creatingBuilder();
            if (i == 1) {
               request.image(image.getId());
               // we don't need to pass password to the API if we're using a snapshot
               Provisionable.Type provisionableType = Provisionable.Type.fromValue(
                       image.getUserMetadata().get(ProvisionableToImage.KEY_PROVISIONABLE_TYPE));
               if (provisionableType == Provisionable.Type.IMAGE) {
                  if (pubKey != null) {
                     request.sshKeys(new HashSet<String>(Arrays.asList(pubKey)));
                  } else {
                     request.imagePassword(password);
                  }
               }

            }
            request.dataCenterId(dataCenterId).
                    name(format("%s-disk-%d", name, i++)).
                    size(volume.getSize().intValue()).
                    type(VolumeType.HDD);

            org.apache.jclouds.profitbricks.rest.domain.Volume vol = (org.apache.jclouds.profitbricks.rest.domain.Volume) provisioningManager
                    .provision(jobFactory.create(dataCenterId, new Supplier<Object>() {
                       @Override
                       public Object get() {
                          return api.volumeApi().createVolume(request.build());
                       }
                    }));

            volumeIds.add(vol.id());
            logger.trace(">> provisioning complete for volume. returned id='%s'", vol.id());
         } catch (Exception ex) {
            if (i - 1 == 1) // if first volume (one with image) provisioning fails; stop method
            {
               throw Throwables.propagate(ex);
            }
            logger.warn(ex, ">> failed to provision volume. skipping..");
         }
      }

      String volumeBootDeviceId = Iterables.get(volumeIds, 0); // must have atleast 1
      waitVolumeUntilAvailable.apply(VolumeRef.create(dataCenterId, volumeBootDeviceId));

      // provision server
      final Server server;
      Double cores = ComputeServiceUtils.getCores(hardware);

      Server.BootVolume bootVolume = Server.BootVolume.create(volumeBootDeviceId);

      try {
         final Server.Request.CreatePayload serverRequest = Server.Request.creatingBuilder()
                 .dataCenterId(dataCenterId)
                 .name(name)
                 .bootVolume(bootVolume)
                 .cores(cores.intValue())
                 .ram(hardware.getRam())
                 .build();

         logger.trace("<< provisioning server '%s'", serverRequest);

         server = (Server) provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

            @Override
            public Object get() {
               return api.serverApi().createServer(serverRequest);
            }
         }));
         logger.trace(">> provisioning complete for server. returned id='%s'", server.id());

      } catch (Exception ex) {
         logger.error(ex, ">> failed to provision server. rollbacking..");
         destroyVolumes(volumeIds, dataCenterId);
         throw Throwables.propagate(ex);
      }

      waitServerUntilAvailable.apply(ServerRef.create(dataCenterId, server.id()));
      waitDcUntilAvailable.apply(dataCenterId);

      //attach bootVolume to Server
      org.apache.jclouds.profitbricks.rest.domain.Volume volume = api.serverApi().attachVolume(Server.Request.attachVolumeBuilder()
              .dataCenterId(dataCenterId)
              .serverId(server.id())
              .volumeId(bootVolume.id())
              .build());

      trackables.waitUntilRequestCompleted(volume);
      waitServerUntilAvailable.apply(ServerRef.create(dataCenterId, server.id()));
      waitDcUntilAvailable.apply(dataCenterId);

      //fetch an existing lan and creat if non was found
      Lan lan = null;

      List<Lan> lans = api.lanApi().list(dataCenterId);
      if (lans != null && !lans.isEmpty()) {
         lan = FluentIterable.from(lans).firstMatch(new Predicate<Lan>() {
            @Override
            public boolean apply(Lan input) {
               input = api.lanApi().get(dataCenterId, input.id(), new DepthOptions().depth(3));
               return input.properties().isPublic();
            }
         }).orNull();
      }
      if (lan == null) {
         logger.warn("Could not find an existing lan Creating one....");
         lan = api.lanApi().create(Lan.Request.creatingBuilder()
                 .dataCenterId(dataCenterId)
                 .isPublic(Boolean.TRUE)
                 .name("lan " + name)
                 .build());
         trackables.waitUntilRequestCompleted(lan);
      }

      //add a NIC to the server
      int lanId = DEFAULT_LAN_ID;
      if (options.getNetworks() != null) {
         try {
            String networkId = Iterables.get(options.getNetworks(), 0);
            lanId = Integer.valueOf(networkId);
         } catch (Exception ex) {
            logger.warn("no valid network id found from options. using default id='%d'", DEFAULT_LAN_ID);
         }
      }

      Nic nic = api.nicApi().create(Nic.Request.creatingBuilder()
              .dataCenterId(dataCenterId)
              .name("jclouds" + name)
              .dhcp(Boolean.TRUE)
              .lan(lanId)
              .firewallActive(inboundPorts.length > 0)
              .serverId(server.id()).
              build());

      trackables.waitUntilRequestCompleted(nic);
      waitNICUntilAvailable.apply(NicRef.create(dataCenterId, server.id(), nic.id()));
      waitDcUntilAvailable.apply(dataCenterId);
      waitServerUntilAvailable.apply(ServerRef.create(dataCenterId, server.id()));

      Map<Integer, Integer> portsRange = getPortRangesFromList(inboundPorts);

      for (Map.Entry<Integer, Integer> range : portsRange.entrySet()) {
         FirewallRule rule = api.firewallApi().create(
                 FirewallRule.Request.creatingBuilder()
                 .dataCenterId(dataCenterId)
                 .serverId(server.id())
                 .nicId(nic.id())
                 .name(server.properties().name() + " jclouds-firewall")
                 .protocol(FirewallRule.Protocol.TCP)
                 .portRangeStart(range.getKey())
                 .portRangeEnd(range.getValue())
                 .build()
         );
         trackables.waitUntilRequestCompleted(rule);

      }

      //connect the rest of volumes to server;delete if fails
      final int volumeCount = volumeIds.size();
      for (int j = 1; j < volumeCount; j++) { // skip first; already connected
         final String volumeId = volumeIds.get(j);
         try {
            logger.trace("<< connecting volume '%s' to server '%s'", volumeId, server.id());
            provisioningManager.provision(jobFactory.create(group, new Supplier<Object>() {

               @Override
               public Object get() {
                  return api.serverApi().attachVolume(
                          Server.Request.attachVolumeBuilder()
                          .dataCenterId(dataCenterId)
                          .serverId(server.id())
                          .volumeId(volumeId)
                          .build()
                  );
               }
            }));

            logger.trace(">> volume connected.");
         } catch (Exception ex) {
            try {
               // delete unconnected volume
               logger.warn(ex, ">> failed to connect volume '%s'. deleting..", volumeId);
               destroyVolume(volumeId, dataCenterId);
               logger.warn(ex, ">> rolling back server..", server.id());
               destroyServer(server.id(), dataCenterId);
               throw ex;
            } catch (Exception ex1) {
               logger.error(ex, ">> failed to rollback");
            }
         }
      }
      waitDcUntilAvailable.apply(dataCenterId);
      waitServerUntilAvailable.apply(ServerRef.create(dataCenterId, server.id()));

      LoginCredentials serverCredentials = LoginCredentials.builder()
              .user(loginUser)
              .privateKey(pubKey)
              .password(password)
              .build();

      String serverInDataCenterId = DataCenterAndId.fromDataCenterAndId(dataCenterId, server.id()).slashEncode();
      ServerInDataCenter serverInDatacenter = getNode(serverInDataCenterId);

      return new NodeAndInitialCredentials<ServerInDataCenter>(serverInDatacenter, serverInDataCenterId, serverCredentials);
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      // Max [cores=48] [disk size per volume=2048GB] [ram=200704 MB]
      List<Hardware> hardwares = Lists.newArrayList();
      for (int core = 1; core <= 48; core++) {
         for (int ram : new int[]{1024, 2 * 1024, 4 * 1024, 8 * 1024,
            10 * 1024, 16 * 1024, 24 * 1024, 28 * 1024, 32 * 1024}) {
            for (float size : new float[]{10, 20, 30, 50, 80, 100, 150, 200, 250, 500}) {
               String id = String.format("cpu=%d,ram=%s,disk=%f", core, ram, size);
               hardwares.add(new HardwareBuilder()
                       .ids(id)
                       .ram(ram)
                       .hypervisor("kvm")
                       .name(id)
                       .processor(new Processor(core, 1d))
                       .volume(new VolumeImpl(size, true, true))
                       .build());
            }
         }
      }
      return hardwares;
   }

   @Override
   public Iterable<Provisionable> listImages() {
      // fetch images..
      ListenableFuture<List<Image>> images = executorService.submit(new Callable<List<Image>>() {

         @Override
         public List<Image> call() throws Exception {
            logger.trace("<< fetching images..");
            // Filter HDD types only, since JClouds doesn't have a concept of "CD-ROM" anyway
            Iterable<Image> filteredImages = Iterables.filter(api.imageApi().getList(new DepthOptions().depth(1)), new Predicate<Image>() {

               @Override
               public boolean apply(Image image) {
                  return image.properties().imageType() == Image.Type.HDD;
               }
            });
            logger.trace(">> images fetched.");

            return ImmutableList.copyOf(filteredImages);
         }

      });
      // and snapshots at the same time
      ListenableFuture<List<Snapshot>> snapshots = executorService.submit(new Callable<List<Snapshot>>() {

         @Override
         public List<Snapshot> call() throws Exception {
            logger.trace("<< fetching snapshots");
            List<Snapshot> remoteSnapshots = api.snapshotApi().list(new DepthOptions().depth(1));
            logger.trace(">> snapshots feched.");

            return remoteSnapshots;
         }

      });

      ImmutableList.Builder<Provisionable> provisionables = ImmutableList.builder();
      provisionables.addAll(getUnchecked(images));
      provisionables.addAll(getUnchecked(snapshots));

      return provisionables.build();
   }

   @Override
   public Provisionable getImage(String id) {
      // try search images
      logger.trace("<< searching for image with id=%s", id);
      Image image = api.imageApi().getImage(id);
      if (image != null) {
         logger.trace(">> found image [%s].", image.properties().name());
         return image;
      }
      // try search snapshots
      logger.trace("<< not found from images. searching for snapshot with id=%s", id);
      Snapshot snapshot = api.snapshotApi().get(id);
      if (snapshot != null) {
         logger.trace(">> found snapshot [%s]", snapshot.properties().name());
         return snapshot;
      }
      return null;
   }

   @Override
   public Iterable<Location> listLocations() {
      // Will never be called
      throw new UnsupportedOperationException("Locations are configured in jclouds properties");
   }

   @Override
   public ServerInDataCenter getNode(String id) {
      DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(id);
      logger.trace("<< searching for server with id=%s", id);

      Server server = api.serverApi().getServer(datacenterAndId.getDataCenter(), datacenterAndId.getId(), new DepthOptions().depth(3));
      if (server != null) {
         logger.trace(">> found server [%s]", server.properties().name());
      }
      return server == null ? null : new ServerInDataCenter(server, datacenterAndId.getDataCenter());
   }

   @Override
   public void destroyNode(String nodeId) {
      DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(nodeId);
      ServerApi serverApi = api.serverApi();
      Server server = serverApi.getServer(datacenterAndId.getDataCenter(), datacenterAndId.getId(), new DepthOptions().depth(5));
      if (server != null) {
         for (org.apache.jclouds.profitbricks.rest.domain.Volume volume : server.entities().volumes().items()) {
            destroyVolume(volume.id(), datacenterAndId.getDataCenter());
         }

         try {
            destroyServer(datacenterAndId.getId(), datacenterAndId.getDataCenter());
         } catch (Exception ex) {
            logger.warn(ex, ">> failed to delete server with id=%s", datacenterAndId.getId());
         }
      }
   }

   @Override
   public void rebootNode(final String id) {
      final DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(id);
      // Fail pre-emptively if not found
      final ServerInDataCenter node = getRequiredNode(id);
      provisioningManager.provision(jobFactory.create(datacenterAndId.getDataCenter(), new Supplier<Object>() {
         @Override
         public Object get() {
            URI requestStatusURI = api.serverApi().rebootServer(datacenterAndId.getDataCenter(), datacenterAndId.getId());
            trackables.waitUntilRequestCompleted(requestStatusURI);
            waitServerUntilRunning.apply(ServerRef.create(datacenterAndId.getDataCenter(), datacenterAndId.getId()));
            return node;
         }
      }));
   }

   @Override
   public void resumeNode(final String id) {
      final DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(id);
      final ServerInDataCenter node = getRequiredNode(id);
      if (node.getServer().properties().vmState() == Server.Status.RUNNING) {
         return;
      }

      provisioningManager.provision(jobFactory.create(datacenterAndId.getDataCenter(), new Supplier<Object>() {
         @Override
         public Object get() {
            URI requestStatusURI = api.serverApi().startServer(datacenterAndId.getDataCenter(), datacenterAndId.getId());
            trackables.waitUntilRequestCompleted(requestStatusURI);
            waitServerUntilRunning.apply(ServerRef.create(datacenterAndId.getDataCenter(), datacenterAndId.getId()));
            return node;
         }
      }));
   }

   @Override
   public void suspendNode(final String id) {
      final DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(id);
      final ServerInDataCenter node = getRequiredNode(id);
      // Intentionally didn't include SHUTDOWN (only achieved via UI; soft-shutdown). 
      // A SHUTOFF server is no longer billed, so we execute method for all other status
      if (node.getServer().properties().vmState() == Server.Status.SHUTOFF) {
         return;
      }
      provisioningManager.provision(jobFactory.create(datacenterAndId.getDataCenter(), new Supplier<Object>() {
         @Override
         public Object get() {
            URI requestStatusURI = api.serverApi().stopServer(datacenterAndId.getDataCenter(), datacenterAndId.getId());
            trackables.waitUntilRequestCompleted(requestStatusURI);
            waitServerUntilSuspended.apply(ServerRef.create(datacenterAndId.getDataCenter(), datacenterAndId.getId()));
            return node;
         }
      }));
   }

   @Override
   public Iterable<ServerInDataCenter> listNodes() {
      logger.trace("<< fetching servers..");
      datacetners = api.dataCenterApi().list();
      List<ServerInDataCenter> servers = new ArrayList<ServerInDataCenter>();
      for (DataCenter dataCenter : datacetners) {

         List<Server> serversInDataCenter = api.serverApi().getList(dataCenter.id(), new DepthOptions().depth(4));
         for (Server server : serversInDataCenter) {
            servers.add(new ServerInDataCenter(server, dataCenter.id()));
         }
      }
      logger.trace("<< fetching servers..");
      return servers;
   }

   @Override
   public Iterable<ServerInDataCenter> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<ServerInDataCenter>() {
         @Override
         public boolean apply(ServerInDataCenter server) {
            return contains(ids, server.slashEncode());
         }
      });
   }

   private void destroyServer(final String serverId, final String dataCenterId) {
      try {
         logger.trace("<< deleting server with id=%s", serverId);
         provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {
            @Override
            public Object get() {
               URI requestStatusURI = api.serverApi().deleteServer(dataCenterId, serverId);
               trackables.waitUntilRequestCompleted(requestStatusURI);
               return serverId;
            }
         }));
         logger.trace(">> server '%s' deleted.", serverId);
      } catch (Exception ex) {
         logger.warn(ex, ">> failed to delete server with id=%s", serverId);
      }
   }

   private void destroyVolumes(List<String> volumeIds, String dataCenterId) {
      for (String volumeId : volumeIds) {
         destroyVolume(volumeId, dataCenterId);
      }
   }

   private void destroyVolume(final String volumeId, final String dataCenterId) {
      try {
         logger.trace("<< deleting volume with id=%s", volumeId);
         provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

            @Override
            public Object get() {
               URI requestStatusURI = api.volumeApi().deleteVolume(dataCenterId, volumeId);
               trackables.waitUntilRequestCompleted(requestStatusURI);
               return volumeId;
            }
         }));
         logger.trace(">> volume '%s' deleted.", volumeId);
      } catch (Exception ex) {
         logger.warn(ex, ">> failed to delete volume with id=%s", volumeId);
      }
   }

   private ServerInDataCenter getRequiredNode(String nodeId) {
      ServerInDataCenter node = getNode(nodeId);
      if (node == null) {
         throw new ResourceNotFoundException("Node with id'" + nodeId + "' was not found.");
      }
      return node;
   }

}
