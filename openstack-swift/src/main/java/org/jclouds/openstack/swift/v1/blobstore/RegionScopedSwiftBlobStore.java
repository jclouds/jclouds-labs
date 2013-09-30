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
package org.jclouds.openstack.swift.v1.blobstore;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.domain.Location;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToBlobMetadata;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToListContainerOptions;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToResourceMetadata;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class RegionScopedSwiftBlobStore extends BaseBlobStore {

   private final BlobToHttpGetOptions toGetOptions = new BlobToHttpGetOptions();
   private final ToListContainerOptions toListContainerOptions = new ToListContainerOptions();

   private final SwiftApi api;
   private final Supplier<Location> region;
   private final ToResourceMetadata toResourceMetadata;
   private final FetchBlobMetadata fetchMetadata;

   @Inject
   protected RegionScopedSwiftBlobStore(BlobStoreContext context, BlobUtils blobUtils, final Supplier<Location> region,
         SwiftApi api, FetchBlobMetadata fetchMetadata) {
      super(context, blobUtils, region, new Supplier<Set<? extends Location>>() {
         @Override
         public Set<? extends Location> get() {
            return ImmutableSet.of(region.get());
         }
      });
      this.api = api;
      this.toResourceMetadata = new ToResourceMetadata(region);
      this.region = region;
      this.fetchMetadata = fetchMetadata;
   }

   /** all commands are scoped to a region. */
   protected String regionId() {
      return region.get().getId();
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      // TODO: there may eventually be >10k containers..
      FluentIterable<StorageMetadata> containers = api.containerApiInRegion(regionId()).listFirstPage()
            .transform(toResourceMetadata);
      return new PageSetImpl<StorageMetadata>(containers, null);
   }

   @Override
   public boolean containerExists(String container) {
      Container val = api.containerApiInRegion(regionId()).get(container);
      containerCache.put(container, Optional.fromNullable(val));
      return val != null;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      checkArgument(location == null || location.equals(region.get()), "location must be null or %s", region.get());
      if (options.isPublicRead()) {
         return api.containerApiInRegion(regionId()).createIfAbsent(container, ANYBODY_READ);
      }
      return api.containerApiInRegion(regionId()).createIfAbsent(container, BASIC_CONTAINER);
   }

   private static final org.jclouds.openstack.swift.v1.options.CreateContainerOptions BASIC_CONTAINER = new org.jclouds.openstack.swift.v1.options.CreateContainerOptions();
   private static final org.jclouds.openstack.swift.v1.options.CreateContainerOptions ANYBODY_READ = new org.jclouds.openstack.swift.v1.options.CreateContainerOptions()
         .anybodyRead();

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ObjectApi objectApi = api.objectApiInRegionForContainer(regionId(), container);
      ObjectList objects = objectApi.list(toListContainerOptions.apply(options));
      if (objects == null) {
         containerCache.put(container, Optional.<Container> absent());
         return new PageSetImpl<StorageMetadata>(ImmutableList.<StorageMetadata> of(), null);
      } else {
         containerCache.put(container, Optional.of(objects.container()));
         List<MutableBlobMetadata> list = Lists.transform(objects, toBlobMetadata(container));
         int limit = Optional.fromNullable(options.getMaxResults()).or(10000);
         String marker = list.size() == limit ? list.get(limit - 1).getName() : null;
         PageSet<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(list, marker);
         if (options.isDetailed()) {
            return fetchMetadata.setContainerName(container).apply(pageSet);
         }
         return pageSet;
      }
   }

   @Override
   public boolean blobExists(String container, String name) {
      return blobMetadata(container, name) != null;
   }

   @Override
   public String putBlob(String container, Blob blob) {
      return putBlob(container, blob, PutOptions.NONE);
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
         throw new UnsupportedOperationException();
      }
      ObjectApi objectApi = api.objectApiInRegionForContainer(regionId(), container);
      return objectApi.replace(blob.getMetadata().getName(), blob.getPayload(), blob.getMetadata().getUserMetadata());
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      SwiftObject object = api.objectApiInRegionForContainer(regionId(), container).head(name);
      if (object == null) {
         return null;
      }
      return toBlobMetadata(container).apply(object);
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      ObjectApi objectApi = api.objectApiInRegionForContainer(regionId(), container);
      SwiftObject object = objectApi.get(name, toGetOptions.apply(options));
      if (object == null) {
         return null;
      }
      Blob blob = new BlobImpl(toBlobMetadata(container).apply(object));
      blob.setPayload(object.payload());
      return blob;
   }

   @Override
   public void removeBlob(String container, String name) {
      api.objectApiInRegionForContainer(regionId(), container).delete(name);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      api.containerApiInRegion(regionId()).deleteIfEmpty(container);
      containerCache.invalidate(container);
      return true;
   }

   protected final LoadingCache<String, Optional<Container>> containerCache = CacheBuilder.newBuilder().build(
         new CacheLoader<String, Optional<Container>>() {
            public Optional<Container> load(String container) {
               return Optional.fromNullable(api.containerApiInRegion(regionId()).get(container));
            }
         });

   protected Function<SwiftObject, MutableBlobMetadata> toBlobMetadata(String container) {
      return new ToBlobMetadata(containerCache.getUnchecked(container).get());
   }
}
