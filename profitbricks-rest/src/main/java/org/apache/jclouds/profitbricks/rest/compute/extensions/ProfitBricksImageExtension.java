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
package org.apache.jclouds.profitbricks.rest.compute.extensions;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.find;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import static org.apache.jclouds.profitbricks.rest.config.ProfitBricksComputeProperties.POLL_PREDICATE_SNAPSHOT;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.domain.zonescoped.DataCenterAndId;
import org.apache.jclouds.profitbricks.rest.util.Trackables;
import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;
import org.jclouds.logging.Logger;

public class ProfitBricksImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ProfitBricksApi client;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Set<? extends Location>> locations;
   private final Predicate<String> snapshotAvailablePredicate;
   private final Trackables trackables;

   @Inject
   ProfitBricksImageExtension(ProfitBricksApi client,
           @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
           @Memoized Supplier<Set<? extends Location>> locations,
           @Named(POLL_PREDICATE_SNAPSHOT) Predicate<String> snapshotAvailablePredicate,
           Trackables trackables) {
      this.client = client;
      this.userExecutor = userExecutor;
      this.locations = locations;
      this.snapshotAvailablePredicate = snapshotAvailablePredicate;
      this.trackables = trackables;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(id);
      Server server = client.serverApi().getServer(datacenterAndId.getDataCenter(), datacenterAndId.getId());
      if (server == null) {
         throw new IllegalArgumentException("Cannot find server with id: " + id);
      }
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      final DataCenterAndId datacenterAndId = DataCenterAndId.fromSlashEncoded(cloneTemplate.getSourceNodeId());

      final Server server = client.serverApi().getServer(datacenterAndId.getDataCenter(), datacenterAndId.getId());
      List<Volume> volumes = client.volumeApi().getList(server.dataCenterId());

      final Volume volume = Iterables.getOnlyElement(volumes);

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            Snapshot snapshot = client.volumeApi().createSnapshot(Volume.Request.createSnapshotBuilder()
                    .dataCenterId(datacenterAndId.getDataCenter())
                    .volumeId(volume.id())
                    .name(cloneTemplate.getName())
                    .description(cloneTemplate.getName())
                    .build());

            trackables.waitUntilRequestCompleted(snapshot);
            logger.info(">> Registered new snapshot %s, waiting for it to become available.", snapshot.id());

            final Image image = new ImageBuilder()
                    .location(find(locations.get(), idEquals(snapshot.properties().location().getId())))
                    .id(snapshot.id())
                    .providerId(snapshot.id())
                    .name(cloneTemplate.getName())
                    .description(cloneTemplate.getName())
                    .operatingSystem(OperatingSystem.builder().description(cloneTemplate.getName()).build())
                    .status(Image.Status.PENDING).build();

            if (snapshotAvailablePredicate.apply(image.getId())) {
               return image;
            }
            throw new UncheckedTimeoutException("Image was not created within the time limit: " + image);
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         URI deleteJob = client.snapshotApi().delete(id);
         trackables.waitUntilRequestCompleted(deleteJob);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

}
