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
package org.jclouds.digitalocean.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.compute.extensions.DigitalOceanImageExtension;
import org.jclouds.digitalocean.compute.functions.DropletStatusToStatus;
import org.jclouds.digitalocean.compute.functions.DropletToNodeMetadata;
import org.jclouds.digitalocean.compute.functions.ImageToImage;
import org.jclouds.digitalocean.compute.functions.RegionToLocation;
import org.jclouds.digitalocean.compute.functions.SizeToHardware;
import org.jclouds.digitalocean.compute.functions.TemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.digitalocean.compute.options.DigitalOceanTemplateOptions;
import org.jclouds.digitalocean.compute.strategy.DigitalOceanComputeServiceAdapter;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.domain.Location;
import org.jclouds.util.Predicates2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the compute service classes for the DigitalOcean API.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
public class DigitalOceanComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<Droplet, Size, Image, Region> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<Droplet, Size, Image, Region>>() {
      }).to(DigitalOceanComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Droplet, NodeMetadata>>() {
      }).to(DropletToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ImageToImage.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<Size, Hardware>>() {
      }).to(SizeToHardware.class);
      bind(new TypeLiteral<Function<Droplet.Status, Status>>() {
      }).to(DropletStatusToStatus.class);

      install(new LocationsFromComputeServiceAdapterModule<Droplet, Size, Image, Region>() {
      });

      bind(TemplateOptions.class).to(DigitalOceanTemplateOptions.class);
      bind(TemplateOptionsToStatement.class).to(TemplateOptionsToStatementWithoutPublicKey.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(DigitalOceanImageExtension.class);
   }

   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<Integer> provideDropletRunningPredicate(final DigitalOceanApi api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new EventDonePredicate(api), timeouts.nodeRunning, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<Integer> provideDropletSuspendedPredicate(final DigitalOceanApi api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new EventDonePredicate(api), timeouts.nodeSuspended, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<Integer> provideDropletTerminatedPredicate(final DigitalOceanApi api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new EventDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<Integer> provideImageAvailablePredicate(final DigitalOceanApi api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new EventDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class EventDonePredicate implements Predicate<Integer> {

      private final DigitalOceanApi api;

      public EventDonePredicate(DigitalOceanApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(Integer input) {
         Event event = api.getEventApi().get(input);
         switch (event.getStatus()) {
            case DONE:
               return true;
            case PENDING:
               return false;
            case ERROR:
            default:
               throw new IllegalStateException("Resource is in invalid status: " + event.getStatus().name());
         }
      }

   }
}
