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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontrol.domain.BaseImage;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;

import javax.inject.Singleton;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class BaseImageToImage implements Function<BaseImage, Image> {

   private final Supplier<Set<Location>> locations;
   private final Function<org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem, OsFamily> operatingSystemToOsFamily;
   private static final Pattern OS_VERSION_EXTRACT_PATTERN = Pattern.compile("[A-Z]+(\\w+)(32|64)");

   @Inject
   BaseImageToImage(@Memoized final Supplier<Set<Location>> locations,
         Function<org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem, OsFamily> operatingSystemToOsFamily) {
      this.locations = locations;
      this.operatingSystemToOsFamily = operatingSystemToOsFamily;
   }

   @Override
   public Image apply(final BaseImage input) {
      OsFamily osFamily;
      if (input.guest() != null) {
         osFamily = operatingSystemToOsFamily.apply(input.guest().operatingSystem());
      } else {
         osFamily = OsFamily.UNRECOGNIZED;
      }
      String osVersion;
      if (input.guest() != null) {
         osVersion = parseVersion(input.guest().operatingSystem());
      } else {
         osVersion = null;
      }
      boolean is64Bit = input.guest() != null && is64bit(input.guest().operatingSystem());

      OperatingSystem os = OperatingSystem.builder().name(input.name()).description(input.description())
            .family(osFamily).version(osVersion).is64Bit(is64Bit).build();

      return new ImageBuilder().ids(input.id()).name(input.name()).description(input.description())
            .status(Image.Status.AVAILABLE).operatingSystem(os).location(
                  FluentIterable.from(locations.get()).firstMatch(LocationPredicates.idEquals(input.datacenterId()))
                        .orNull()).build();
   }

   private boolean is64bit(final org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem operatingSystem) {
      return operatingSystem.id().endsWith("64");
   }

   String parseVersion(final org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem operatingSystem) {
      Matcher matcher = OS_VERSION_EXTRACT_PATTERN.matcher(operatingSystem.id());
      return matcher.matches() ? matcher.group(1) : "unknown";
   }

}

