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
package org.jclouds.cloudsigma2.compute.strategy;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

import org.jclouds.Constants;
import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.compute.options.CloudSigma2TemplateOptions;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.jclouds.cloudsigma2.domain.FirewallAction;
import org.jclouds.cloudsigma2.domain.FirewallDirection;
import org.jclouds.cloudsigma2.domain.FirewallIpProtocol;
import org.jclouds.cloudsigma2.domain.FirewallPolicy;
import org.jclouds.cloudsigma2.domain.FirewallRule;
import org.jclouds.cloudsigma2.domain.IPConfiguration;
import org.jclouds.cloudsigma2.domain.IPConfigurationType;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.MediaType;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.ServerStatus;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.PROPERTY_DELETE_DRIVES;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.PROPERTY_VNC_PASSWORD;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.TIMEOUT_DRIVE_CLONED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

@Singleton
public class CloudSigma2ComputeServiceAdapter implements
      ComputeServiceAdapter<ServerInfo, Hardware, LibraryDrive, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudSigma2Api api;
   private final ListeningExecutorService userExecutor;
   private final String defaultVncPassword;
   private final Predicate<DriveInfo> driveCloned;
   private final Predicate<String> serverStopped;
   private final boolean destroyDrives;
   private final GroupNamingConvention groupNamingConvention;

   @Inject
   public CloudSigma2ComputeServiceAdapter(CloudSigma2Api api,
                                           @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService
                                                 userExecutor,
                                           @Named(PROPERTY_VNC_PASSWORD) String defaultVncPassword,
                                           @Named(TIMEOUT_DRIVE_CLONED) Predicate<DriveInfo> driveCloned,
                                           @Named(TIMEOUT_NODE_SUSPENDED) Predicate<String> serverStopped,
                                           @Named(PROPERTY_DELETE_DRIVES) boolean destroyDrives,
                                           GroupNamingConvention.Factory groupNamingConvention) {
      this.api = checkNotNull(api, "api");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.defaultVncPassword = checkNotNull(defaultVncPassword, "defaultVncPassword");
      this.driveCloned = checkNotNull(driveCloned, "driveCloned");
      this.serverStopped = checkNotNull(serverStopped, "serverStopped");
      this.destroyDrives = destroyDrives;
      this.groupNamingConvention = checkNotNull(groupNamingConvention, "groupNamingConvention").create();
   }

   @Override
   public NodeAndInitialCredentials<ServerInfo> createNodeWithGroupEncodedIntoName(String group, String name,
                                                                                   Template template) {
      CloudSigma2TemplateOptions options = template.getOptions().as(CloudSigma2TemplateOptions.class);
      Image image = template.getImage();
      Hardware hardware = template.getHardware();

      DriveInfo drive = api.getLibraryDrive(image.getProviderId());

      if (!drive.getMedia().equals(MediaType.CDROM)) {
         logger.debug(">> cloning library drive %s...", image.getProviderId());

         drive = api.cloneLibraryDrive(image.getProviderId(), null);
         driveCloned.apply(drive);

         // Refresh the drive object and verify the clone operation didn't time out
         drive = api.getDriveInfo(drive.getUuid());
         DriveStatus status = drive.getStatus();

         if (DriveStatus.UNMOUNTED != status) {
            if (destroyDrives) {
               // Rollback the cloned drive, if needed
               logger.error(">> clone operation failed. Rolling back drive (%s)...", drive);
               destroyDrives(ImmutableList.of(drive.getUuid()));
            }
            throw new IllegalStateException("Resource is in invalid status: " + status);
         }

         logger.debug(">> drive cloned (%s)...", drive);
      }

      ImmutableList.Builder<FirewallRule> firewallRulesBuilder = ImmutableList.builder();
      for (int port : options.getInboundPorts()) {
         firewallRulesBuilder.add(new FirewallRule.Builder().action(FirewallAction.ACCEPT)
               .ipProtocol(FirewallIpProtocol.TCP).direction(FirewallDirection.IN).destinationPort("" + port).build());
      }

      List<NIC> nics = null;
      try {
         logger.debug(">> creating firewall policies...");
         FirewallPolicy firewallPolicy = api.createFirewallPolicy(new FirewallPolicy.Builder().rules(
               firewallRulesBuilder.build()).build());
         nics = configureNICs(options, firewallPolicy);
      } catch (Exception ex) {
         if (destroyDrives) {
            logger.debug(">> rolling back the cloned drive...", drive.getUuid());
            destroyDrives(ImmutableList.of(drive.getUuid()));
         }
         throw propagate(ex);
      }

      List<Tag> tagIds = configureTags(options);

      // Cloud init images expect the public key in the server metadata
      Map<String, String> metadata = Maps.newLinkedHashMap();
      metadata.put("image_id", image.getProviderId());
      if (!Strings.isNullOrEmpty(options.getPublicKey())) {
         metadata.put("ssh_public_key", options.getPublicKey());
      }
      metadata.putAll(options.getUserMetadata());

      ServerInfo serverInfo = null;
      try {
         logger.debug(">> creating server...");

         serverInfo = api.createServer(new ServerInfo.Builder()
               .name(name)
               .cpu((int) hardware.getProcessors().get(0).getSpeed())
               .memory(BigInteger.valueOf(hardware.getRam()).multiply(BigInteger.valueOf(1024 * 1024)))
               .drives(ImmutableList.of(drive.toServerDrive(1, "0:1", options.getDeviceEmulationType())))
               .nics(nics)
               .meta(metadata)
               .tags(tagIds)
               .vncPassword(Optional.fromNullable(options.getVncPassword()).or(defaultVncPassword)).build());

         api.startServer(serverInfo.getUuid());

         return new NodeAndInitialCredentials<ServerInfo>(serverInfo, serverInfo.getUuid(), LoginCredentials.builder()
               .build());
      } catch (Exception ex) {
         try {
            if (serverInfo != null) {
               logger.debug(">> rolling back the server...");
               api.deleteServer(serverInfo.getUuid());
            }
         } finally {
            try {
               if (destroyDrives) {
                  logger.debug(">> rolling back the cloned drive...");
                  destroyDrives(ImmutableList.of(drive.getUuid()));
               }
            } finally {
               deleteTags(tagIds);
            }
         }
         throw propagate(ex);
      }
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      // Return a hardcoded list of hardware profiles until
      // https://issues.apache.org/jira/browse/JCLOUDS-482 is fixed
      Builder<Hardware> hardware = ImmutableSet.builder();
      Builder<Integer> ramSetBuilder = ImmutableSet.builder();
      Builder<Double> cpuSetBuilder = ImmutableSet.builder();
      for (int i = 1; i < 65; i++) {
         ramSetBuilder.add(i * 1024);
      }
      for (int i = 1; i < 41; i++) {
         cpuSetBuilder.add((double) i * 1000);
      }
      for (int ram : ramSetBuilder.build()) {
         for (double cpu : cpuSetBuilder.build()) {
            hardware.add(new HardwareBuilder().ids(String.format("cpu=%f,ram=%d", cpu, ram))
                  .processor(new Processor(1, cpu)).ram(ram)
                  .volumes(ImmutableList.<Volume>of(new VolumeImpl(null, true, false))).build());
         }
      }
      return hardware.build();
   }

   @Override
   public Iterable<LibraryDrive> listImages() {
      return api.listLibraryDrives().concat();
   }

   @Override
   public LibraryDrive getImage(String uuid) {
      return api.getLibraryDrive(uuid);
   }

   @Override
   public Iterable<Location> listLocations() {
      // Nothing to return here. Each provider will configure the locations
      return ImmutableSet.<Location>of();
   }

   @Override
   public ServerInfo getNode(String uuid) {
      return api.getServerInfo(uuid);
   }

   @Override
   public void destroyNode(String uuid) {
      ServerInfo server = api.getServerInfo(uuid);

      if (ServerStatus.RUNNING == server.getStatus()) {
         api.stopServer(uuid);
         waitUntilServerIsStopped(uuid);
      }

      deleteTags(server.getTags());

      List<String> driveIds = transform(server.getDrives(), new Function<ServerDrive, String>() {
         @Override
         public String apply(ServerDrive input) {
            return input.getDriveUuid();
         }
      });

      logger.debug(">> deleting server...");
      api.deleteServer(uuid);

      if (destroyDrives) {
         logger.debug(">> deleting server drives...");
         destroyDrives(driveIds);
      }
   }

   @Override
   public void rebootNode(String uuid) {
      api.stopServer(uuid);
      waitUntilServerIsStopped(uuid);
      api.startServer(uuid);
   }

   @Override
   public void resumeNode(String uuid) {
      api.startServer(uuid);
   }

   @Override
   public void suspendNode(String uuid) {
      api.stopServer(uuid);
   }

   @Override
   public Iterable<ServerInfo> listNodes() {
      return api.listServersInfo().concat();
   }

   @Override
   public Iterable<ServerInfo> listNodesByIds(final Iterable<String> uuids) {
      // Only fetch the requested nodes. Do it in parallel.
      ListenableFuture<List<ServerInfo>> futures = allAsList(transform(uuids,
            new Function<String, ListenableFuture<ServerInfo>>() {
               @Override
               public ListenableFuture<ServerInfo> apply(final String input) {
                  return userExecutor.submit(new Callable<ServerInfo>() {
                     @Override
                     public ServerInfo call() throws Exception {
                        return api.getServerInfo(input);
                     }
                  });
               }
            }));

      return getUnchecked(futures);
   }

   private void waitUntilServerIsStopped(String uuid) {
      serverStopped.apply(uuid);
      ServerInfo server = api.getServerInfo(uuid);
      checkState(server.getStatus() == ServerStatus.STOPPED, "Resource is in invalid status: %s", server.getStatus());
   }

   private List<NIC> configureNICs(CloudSigma2TemplateOptions options, FirewallPolicy firewallPolicy) {
      ImmutableList.Builder<NIC> nics = ImmutableList.builder();
      for (String network : options.getNetworks()) {
         VLANInfo vlan = api.getVLANInfo(network);
         checkArgument(vlan != null, "network %s not found", network);
         nics.add(new NIC.Builder().vlan(vlan).firewallPolicy(firewallPolicy).model(options.getNicModel()).build());
      }

      // If no network has been specified, assign an IP from the DHCP
      if (options.getNetworks().isEmpty()) {
         logger.debug(">> no networks configured. Will assign an IP from the DHCP...");
         NIC nic = new NIC.Builder().firewallPolicy(firewallPolicy).model(options.getNicModel())
               .ipV4Configuration(new IPConfiguration.Builder().configurationType(IPConfigurationType.DHCP).build())
               .build();
         nics.add(nic);
      }

      return nics.build();
   }

   private List<Tag> configureTags(CloudSigma2TemplateOptions options) {
      ImmutableList.Builder<Tag> builder = ImmutableList.builder();
      for (String tagName : options.getTags()) {
         String nameWithPrefix = groupNamingConvention.sharedNameForGroup(tagName);
         builder.add(new Tag.Builder().name(nameWithPrefix).build());
      }

      List<Tag> tags = builder.build();
      builder = ImmutableList.builder();

      if (!tags.isEmpty()) {
         logger.debug(">> creating tags...");
         builder.addAll(api.createTags(tags));
      }

      return builder.build();
   }

   private void deleteTags(List<Tag> tags) {
      logger.debug(">> deleting server tags...");
      Iterable<Tag> customTags = filter(tags, new Predicate<Tag>() {
         @Override
         public boolean apply(Tag input) {
            // Only delete the tags jclouds has set
            Tag tag = api.getTagInfo(input.getUuid());
            return groupNamingConvention.groupInSharedNameOrNull(tag.getName()) != null;
         }
      });

      for (Tag tag : customTags) {
         try {
            // Try to delete the tags but don't fail if the can't be deleted
            api.deleteTag(tag.getUuid());
         } catch (Exception ex) {
            logger.warn(ex, ">> could not delete tag: %s", tag);
         }
      }
   }

   private void destroyDrives(List<String> driveIds) {
      try {
         // Try to delete the drives but don't fail if the can't be deleted, as the server has been already removed.
         api.deleteDrives(driveIds);
      } catch (Exception ex) {
         logger.warn(ex, ">> could not delete drives: [%s]", Joiner.on(',').join(driveIds));
      }
   }
}
