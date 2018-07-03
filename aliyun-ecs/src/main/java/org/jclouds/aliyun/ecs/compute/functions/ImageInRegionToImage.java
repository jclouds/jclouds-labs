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
package org.jclouds.aliyun.ecs.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.jclouds.aliyun.ecs.compute.functions.internal.OperatingSystems;
import org.jclouds.aliyun.ecs.domain.regionscoped.ImageInRegion;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;
import static org.jclouds.compute.domain.OperatingSystem.builder;

public class ImageInRegionToImage implements Function<ImageInRegion, Image> {

   private final Supplier<Set<? extends Location>> locations;

   private static final Map<String, OsFamily> OTHER_OS_MAP = ImmutableMap.<String, OsFamily>builder()
         .put("Aliyun", OsFamily.LINUX).build();

   private static Optional<OsFamily> findInStandardFamilies(final String platform) {
      return tryFind(asList(OsFamily.values()), new Predicate<OsFamily>() {
         @Override
         public boolean apply(OsFamily input) {
            return platform.toUpperCase().startsWith(input.name());
         }
      });
   }

   private static Optional<OsFamily> findInOtherOSMap(final String label) {
      return tryFind(OTHER_OS_MAP.keySet(), new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            return label.contains(input);
         }
      }).transform(new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(String input) {
            return OTHER_OS_MAP.get(input);
         }
      });
   }

   @Inject
   ImageInRegionToImage(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Image apply(ImageInRegion from) {
      ImageBuilder builder = new ImageBuilder();
      builder.id(RegionAndId.slashEncodeRegionAndId(from.regionId(), from.image().id()));
      builder.providerId(from.image().id());
      builder.name(from.image().name());
      builder.description(from.image().description());
      builder.status(from.image().status() == org.jclouds.aliyun.ecs.domain.Image.Status.AVAILABLE ?
              Image.Status.AVAILABLE : Image.Status.PENDING);

      OsFamily family = findInStandardFamilies(from.image().platform())
              .or(findInOtherOSMap(from.image().platform()))
              .or(OsFamily.UNRECOGNIZED);

      String osVersion = OperatingSystems.version().apply(from.image());

      builder.operatingSystem(
            builder().name(from.image().osName()).family(family)
                    .description(from.image().description())
                    .version(osVersion)
                  .is64Bit("x86_64".equals(from.image().architecture()) ? true : false).build());

      builder.location(from(locations.get()).firstMatch(LocationPredicates.idEquals(from.regionId())).orNull());
      return builder.build();
   }

}
