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
package org.jclouds.azurecompute.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.location.predicates.LocationPredicates;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

public class OSImageToImage implements Function<OSImage, Image> {

   private static final String UNRECOGNIZED = "UNRECOGNIZED";

   private static final String UBUNTU = "Ubuntu";

   private static final String WINDOWS = "Windows";

   private static final String OPENLOGIC = "openLogic";

   private static final String CENTOS = "CentOS";

   private static final String COREOS = "CoreOS";

   private static final String OPENSUSE = "openSUSE";

   private static final String SUSE = "SUSE";

   private static final String SQL_SERVER = "SQL Server";

   private static final String ORACLE_lINUX = "Oracle Linux";

   public static String toGeoName(final String name, final String location) {
      return name + "/" + location;
   }

   public static String[] fromGeoName(final String geoName) {
      final String[] parts = checkNotNull(geoName, "geoName").split("/");
      return (parts.length == 1) ? new String[]{geoName, null} : parts;
   }

   private final Supplier<Set<? extends org.jclouds.domain.Location>> locations;

   @Inject
   OSImageToImage(@Memoized final Supplier<Set<? extends org.jclouds.domain.Location>> locations) {

      this.locations = locations;
   }

   @Override
   public Image apply(final OSImage image) {
      final ImageBuilder builder = new ImageBuilder()
              .id(image.name())
              .name(image.label())
              .description(image.description())
              .status(Image.Status.AVAILABLE)
              .uri(image.mediaLink())
              .providerId(image.name())
              .location(FluentIterable.from(locations.get())
                      .firstMatch(LocationPredicates.idEquals(image.location())).orNull());

      final OperatingSystem.Builder osBuilder = osFamily().apply(image);
      return builder.operatingSystem(osBuilder.build()).build();
   }

   public static Function<OSImage, OperatingSystem.Builder> osFamily() {
      return new Function<OSImage, OperatingSystem.Builder>() {
         @Override
         public OperatingSystem.Builder apply(final OSImage image) {
            checkNotNull(image.label(), "label");
            final String label = Splitter.on('/').split(image.label()).iterator().next();

            boolean is64Bit = false;

            OsFamily family = OsFamily.UNRECOGNIZED;
            if (label.contains(CENTOS)) {
               family = OsFamily.CENTOS;
               is64Bit = image.name().contains("x64");
            } else if (label.contains(OPENLOGIC)) {
               family = OsFamily.CENTOS;
            } else if (label.contains(SUSE)) {
               family = OsFamily.SUSE;
            } else if (label.contains(UBUNTU)) {
               family = OsFamily.UBUNTU;
               is64Bit = image.name().contains("amd64");
            } else if (label.contains(WINDOWS)) {
               family = OsFamily.WINDOWS;
               is64Bit = true;
            } else if (label.contains(ORACLE_lINUX)) {
               family = OsFamily.OEL;
            }

            String version = UNRECOGNIZED;
            //ex: CoreOS Alpha -> Alpha
            if (label.contains(COREOS)) {
               version = label.replace("CoreOS ", "");
            } //openSUSE 13.1 -> 13.1
            else if (label.contains(OPENSUSE)) {
               version = label.replace("openSUSE ", "");
            } //SUSE Linux Enterprise Server 11 SP3 (Premium Image) -> 11 SP3(Premium Image)
            else if (label.contains(SUSE)) {
               version = label.replace("SUSE ", "");
            } //Ubuntu Server 12.04 LTS -> 12.04 LTS
            else if (label.contains(UBUNTU)) {
               version = label.replace("Ubuntu Server ", "");
            } else if (label.contains(SQL_SERVER)) {
               version = label;
            } else if (label.contains(CENTOS)) {
               version = label;
            } else if (label.contains(WINDOWS)) {
               version = label;
            } else if (label.equals(ORACLE_lINUX)) {
               version = label;
            }

            return OperatingSystem.builder().
                    family(family != OsFamily.UNRECOGNIZED
                                    ? family
                                    : image.os() == OSImage.Type.WINDOWS
                                            ? OsFamily.WINDOWS
                                            : OsFamily.LINUX).
                    version(version).
                    is64Bit(is64Bit).
                    description(image.description() + "");
         }
      };
   }
}
