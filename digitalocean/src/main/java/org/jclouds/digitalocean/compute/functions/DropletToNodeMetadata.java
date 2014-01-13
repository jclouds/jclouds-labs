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
package org.jclouds.digitalocean.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * Transforms an {@link Droplet} to the jclouds portable model.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class DropletToNodeMetadata implements Function<Droplet, NodeMetadata> {

   private final Supplier<Map<String, ? extends Image>> images;
   private final Supplier<Map<String, ? extends Hardware>> hardwares;
   private final Supplier<Set<? extends Location>> locations;
   private final Function<Droplet.Status, Status> toPortableStatus;
   private final GroupNamingConvention groupNamingConvention;
   private final Map<String, Credentials> credentialStore;

   @Inject
   DropletToNodeMetadata(Supplier<Map<String, ? extends Image>> images,
         Supplier<Map<String, ? extends Hardware>> hardwares, @Memoized Supplier<Set<? extends Location>> locations,
         Function<Droplet.Status, Status> toPortableStatus, GroupNamingConvention.Factory groupNamingConvention,
         Map<String, Credentials> credentialStore) {
      this.images = checkNotNull(images, "images cannot be null");
      this.hardwares = checkNotNull(hardwares, "hardwares cannot be null");
      this.locations = checkNotNull(locations, "locations cannot be null");
      this.toPortableStatus = checkNotNull(toPortableStatus, "toPortableStatus cannot be null");
      this.groupNamingConvention = checkNotNull(groupNamingConvention, "groupNamingConvention cannot be null")
            .createWithoutPrefix();
      this.credentialStore = checkNotNull(credentialStore, "credentialStore cannot be null");
   }

   @Override
   public NodeMetadata apply(Droplet input) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(String.valueOf(input.getId()));
      builder.name(input.getName());
      builder.hostname(input.getName());
      builder.group(groupNamingConvention.extractGroup(input.getName()));

      builder.hardware(hardwares.get().get(String.valueOf(input.getSizeId())));

      final String regionIdPattern = input.getRegionId() + "/";
      builder.location(find(locations.get(), new Predicate<Location>() {
         @Override
         public boolean apply(Location location) {
            return location.getDescription().startsWith(regionIdPattern);
         }
      }));

      Image image = images.get().get(String.valueOf(input.getImageId()));
      builder.imageId(image.getId());
      builder.operatingSystem(image.getOperatingSystem());

      builder.status(toPortableStatus.apply(input.getStatus()));
      builder.backendStatus(input.getStatus().name());

      if (input.getIp() != null) {
         builder.publicAddresses(ImmutableSet.of(input.getIp()));
      }
      if (input.getPrivateIp() != null) {
         builder.privateAddresses(ImmutableSet.of(input.getPrivateIp()));
      }

      // DigitalOcean does not provide a way to get the credentials.
      // Try to return them from the credential store
      Credentials credentials = credentialStore.get("node#" + input.getId());
      if (credentials instanceof LoginCredentials) {
         builder.credentials(LoginCredentials.class.cast(credentials));
      }

      return builder.build();
   }
}
