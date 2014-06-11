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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.jclouds.cloudsigma2.CloudSigma2Api;
import org.jclouds.cloudsigma2.domain.ServerDrive;
import org.jclouds.cloudsigma2.domain.ServerInfo;
import org.jclouds.cloudsigma2.domain.ServerStatus;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.all.JustProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;

@Singleton
public class ServerInfoToNodeMetadata implements Function<ServerInfo, NodeMetadata> {

   private final ServerDriveToVolume serverDriveToVolume;
   private final NICToAddress nicToAddress;
   private final Map<ServerStatus, NodeMetadata.Status> serverStatusToNodeStatus;
   private final GroupNamingConvention groupNamingConventionWithPrefix;
   private final GroupNamingConvention groupNamingConventionWithoutPrefix;
   private final Map<String, Credentials> credentialStore;
   private final JustProvider locations;
   private final CloudSigma2Api api;

   @Inject
   public ServerInfoToNodeMetadata(ServerDriveToVolume serverDriveToVolume, NICToAddress nicToAddress,
                                   Map<ServerStatus, NodeMetadata.Status> serverStatusToNodeStatus,
                                   GroupNamingConvention.Factory groupNamingConvention,
                                   Map<String, Credentials> credentialStore,
                                   JustProvider locations, CloudSigma2Api api) {
      this.serverDriveToVolume = checkNotNull(serverDriveToVolume, "serverDriveToVolume");
      this.nicToAddress = checkNotNull(nicToAddress, "nicToAddress");
      this.serverStatusToNodeStatus = checkNotNull(serverStatusToNodeStatus, "serverStatusToNodeStatus");
      this.groupNamingConventionWithPrefix = checkNotNull(groupNamingConvention, "groupNamingConvention").create();
      this.groupNamingConventionWithoutPrefix = groupNamingConvention.createWithoutPrefix();
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.locations = checkNotNull(locations, "locations");
      this.api = checkNotNull(api, "api");
   }

   @Override
   public NodeMetadata apply(ServerInfo serverInfo) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      builder.ids(serverInfo.getUuid());
      builder.name(serverInfo.getName());
      builder.group(groupNamingConventionWithoutPrefix.extractGroup(serverInfo.getName()));
      builder.location(getOnlyElement(locations.get()));

      builder.hardware(new HardwareBuilder().ids(serverInfo.getUuid()).processor(new Processor(1, serverInfo.getCpu()))
            .ram(serverInfo.getMemory().intValue())
            .volumes(Iterables.transform(serverInfo.getDrives(), serverDriveToVolume)).build());

      builder.tags(readTags(serverInfo));
      builder.userMetadata(serverInfo.getMeta());
      builder.imageId(extractImageId(serverInfo));
      builder.status(serverStatusToNodeStatus.get(serverInfo.getStatus()));
      builder.publicAddresses(filter(transform(serverInfo.getNics(), nicToAddress), notNull()));

      // CloudSigma does not provide a way to get the credentials.
      // Try to return them from the credential store
      Credentials credentials = credentialStore.get("node#" + serverInfo.getUuid());
      if (credentials instanceof LoginCredentials) {
         builder.credentials(LoginCredentials.class.cast(credentials));
      }

      return builder.build();
   }

   private static String extractImageId(ServerInfo serverInfo) {
      String imageId = serverInfo.getMeta().get("image_id");

      if (imageId == null) {
         ServerDrive serverBootDrive = null;
         for (ServerDrive serverDrive : serverInfo.getDrives()) {
            if (serverDrive.getBootOrder() != null
                  && (serverBootDrive == null || serverDrive.getBootOrder() < serverBootDrive.getBootOrder())) {
               serverBootDrive = serverDrive;
            }
         }
         if (serverBootDrive != null) {
            imageId = serverBootDrive.getDriveUuid();
         }
      }

      return imageId;
   }

   private Iterable<String> readTags(ServerInfo serverInfo) {
      return transform(serverInfo.getTags(), new Function<Tag, String>() {
         @Override
         public String apply(Tag input) {
            Tag tag = api.getTagInfo(input.getUuid());
            if (tag.getName() == null) {
               return input.getUuid();
            }
            String tagWithoutPrefix = groupNamingConventionWithPrefix.groupInSharedNameOrNull(tag.getName());
            return tagWithoutPrefix != null ? tagWithoutPrefix : tag.getName();
         }
      });
   }

}
