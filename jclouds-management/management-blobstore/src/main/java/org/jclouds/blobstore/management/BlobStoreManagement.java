/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.blobstore.management;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.codec.ToBlob;
import org.jclouds.blobstore.codec.ToBlobMetadata;
import org.jclouds.blobstore.codec.ToStorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.codec.ToLocation;
import org.jclouds.management.ViewMBean;
import org.jclouds.representations.Location;
import org.jclouds.blobstore.representations.Blob;
import org.jclouds.blobstore.representations.BlobMetadata;
import org.jclouds.blobstore.representations.StorageMetadata;
import org.jclouds.javax.annotation.Nullable;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;

public class BlobStoreManagement implements BlobStoreManagementMBean, ViewMBean<BlobStoreContext> {

   private final BlobStore blobStore;

   public BlobStoreManagement(BlobStoreContext context) {
      this.blobStore = context.getBlobStore();
   }

   @Override
   public Set<Location> listAssignableLocations() {
      return ImmutableSet.<Location>builder()
                         .addAll(transform(blobStore.listAssignableLocations(), ToLocation.INSTANCE))
                         .build();
   }

   @Override
   public Set<StorageMetadata> list() {
      return ImmutableSet.<StorageMetadata>builder()
                         .addAll(transform(blobStore.list(), ToStorageMetadata.INSTANCE))
                         .build();
   }

   @Override
   public Set<StorageMetadata> list(String container) {
      return ImmutableSet.<StorageMetadata>builder()
                         .addAll(transform(blobStore.list(container), ToStorageMetadata.INSTANCE))
                         .build();
   }

   @Override
   public Set<StorageMetadata> list(String container, String directory) {
      return ImmutableSet.<StorageMetadata>builder()
              .addAll(transform(blobStore.list(container, ListContainerOptions.Builder.inDirectory(directory)), ToStorageMetadata.INSTANCE))
              .build();
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      return ToBlobMetadata.INSTANCE.apply(blobStore.blobMetadata(container, name));
   }

   @Override
   public Blob getBlob(String container, String name) {
      return ToBlob.INSTANCE.apply(blobStore.getBlob(container, name));
   }

   @Override
   public boolean containerExists(String container) {
      return blobStore.containerExists(container);
   }

   @Override
   public boolean createContainerInLocation(@Nullable String locationId, String container) {
      Optional<? extends org.jclouds.domain.Location> location = tryFind(blobStore.listAssignableLocations(), new LocationPredicate(locationId));

      if (location.isPresent()) {
         return blobStore.createContainerInLocation(location.get(), container);
      } else {
         return false;
      }
   }

   @Override
   public void clearContainer(String container) {
      blobStore.clearContainer(container);
   }

   @Override
   public void deleteContainer(String container) {
      blobStore.deleteContainer(container);
   }

   @Override
   public boolean directoryExists(String container, String directory) {
      return blobStore.directoryExists(container, directory);
   }

   @Override
   public void createDirectory(String container, String directory) {
      blobStore.createDirectory(container, directory);
   }

   @Override
   public void deleteDirectory(String containerName, String name) {
      blobStore.deleteDirectory(containerName, name);
   }

   @Override
   public boolean blobExists(String container, String name) {
      return blobStore.blobExists(container, name);
   }

   @Override
   public void removeBlob(String container, String name) {
      blobStore.removeBlob(container, name);
   }

   @Override
   public long countBlobs(String container) {
      return blobStore.countBlobs(container);
   }

   @Override
   public String getType() {
      return "blobstore";
   }

   private static final class LocationPredicate implements Predicate<org.jclouds.domain.Location> {
      private final String id;

      private LocationPredicate(String id) {
         this.id = id;
      }

      @Override
      public boolean apply(@Nullable org.jclouds.domain.Location input) {
         return input.getId().equals(id);
      }
   }
}
