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
package org.jclouds.cloudsigma2.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.cloudsigma2.config.CloudSigma2Properties.TIMEOUT_DRIVE_CLONED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.compute.functions.LibraryDriveToImage;
import org.jclouds.cloudsigma2.compute.functions.NICToAddress;
import org.jclouds.cloudsigma2.compute.functions.ServerDriveToVolume;
import org.jclouds.cloudsigma2.compute.functions.ServerInfoToNodeMetadata;
import org.jclouds.cloudsigma2.compute.options.CloudSigma2TemplateOptions;
import org.jclouds.cloudsigma2.compute.strategy.CloudSigma2ComputeServiceAdapter;
import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.NIC;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.ServerStatus;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatement;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class CloudSigma2ComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<ServerInfo, Hardware, LibraryDrive, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<ServerInfo, Hardware, LibraryDrive, Location>>() {
      }).to(CloudSigma2ComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<ServerInfo, NodeMetadata>>() {
      }).to(ServerInfoToNodeMetadata.class);
      bind(new TypeLiteral<Function<LibraryDrive, Image>>() {
      }).to(LibraryDriveToImage.class);
      bind(new TypeLiteral<Function<ServerDrive, Volume>>() {
      }).to(ServerDriveToVolume.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<NIC, String>>() {
      }).to(NICToAddress.class);

      bind(TemplateOptions.class).to(CloudSigma2TemplateOptions.class);
      bind(NodeAndTemplateOptionsToStatement.class).to(NodeAndTemplateOptionsToStatementWithoutPublicKey.class);
   }

   @VisibleForTesting
   public static final Map<ServerStatus, NodeMetadata.Status> serverStatusToNodeStatus = ImmutableMap
         .<ServerStatus, NodeMetadata.Status>builder().put(ServerStatus.RUNNING, NodeMetadata.Status.RUNNING)
         .put(ServerStatus.STARTING, NodeMetadata.Status.PENDING)
         .put(ServerStatus.STOPPING, NodeMetadata.Status.PENDING)
         .put(ServerStatus.STOPPED, NodeMetadata.Status.SUSPENDED)
         .put(ServerStatus.PAUSED, NodeMetadata.Status.SUSPENDED)
         .put(ServerStatus.UNAVAILABLE, NodeMetadata.Status.SUSPENDED)
         .put(ServerStatus.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   @Provides
   @Singleton
   protected Map<ServerStatus, NodeMetadata.Status> provideStatusMap() {
      return serverStatusToNodeStatus;
   }

   @VisibleForTesting
   public static final Map<DriveStatus, Image.Status> driveStatusToImageStatus = ImmutableMap
         .<DriveStatus, Image.Status>builder().put(DriveStatus.MOUNTED, Image.Status.AVAILABLE)
         .put(DriveStatus.UNMOUNTED, Image.Status.UNRECOGNIZED).put(DriveStatus.COPYING, Image.Status.PENDING)
         .put(DriveStatus.UNAVAILABLE, Image.Status.ERROR).build();

   @Provides
   @Singleton
   protected Map<DriveStatus, Image.Status> provideImageStatusMap() {
      return driveStatusToImageStatus;
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_DRIVE_CLONED)
   protected Predicate<DriveInfo> provideDriveClonedPredicate(final CloudSigma2Api api,
                                                              @Named(TIMEOUT_DRIVE_CLONED) long driveClonedTimeout) {
      return retry(new DriveClonedPredicate(api), driveClonedTimeout);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<String> provideServerStoppedPredicate(final CloudSigma2Api api, Timeouts timeouts,
                                                             PollPeriod pollPeriod) {
      return retry(new ServerStatusPredicate(api, ServerStatus.STOPPED), timeouts.nodeSuspended,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class DriveClonedPredicate implements Predicate<DriveInfo> {

      private final CloudSigma2Api api;

      public DriveClonedPredicate(CloudSigma2Api api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      public boolean apply(DriveInfo input) {
         DriveInfo drive = api.getDriveInfo(input.getUuid());
         switch (drive.getStatus()) {
            case COPYING:
            case UNAVAILABLE:
               return false;
            case MOUNTED:
            case UNMOUNTED:
               return true;
            default:
               throw new IllegalStateException("Resource is in invalid status: " + drive.getStatus());
         }
      }
   }

   @VisibleForTesting
   static class ServerStatusPredicate implements Predicate<String> {

      private final CloudSigma2Api api;
      private final ServerStatus status;

      public ServerStatusPredicate(CloudSigma2Api api, ServerStatus status) {
         this.api = checkNotNull(api, "api");
         this.status = checkNotNull(status, "status");
      }

      @Override
      public boolean apply(String input) {
         ServerInfo serverInfo = api.getServerInfo(input);
         return status.equals(serverInfo.getStatus());
      }
   }
}
