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

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
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

   private final JustProvider provider;

   @Inject
   OSImageToImage(JustProvider provider) {
      this.provider = provider;
   }

   @Override
   public Image apply(OSImage image) {

      ImageBuilder builder = new ImageBuilder()
            .id(image.name())
            .name(image.label())
            .description(image.description())
            .status(Image.Status.AVAILABLE)
            .uri(image.mediaLink())
            .providerId(image.name())
            .location(createLocation(image.location()));

      OperatingSystem.Builder osBuilder = osFamily().apply(image);
      return builder.operatingSystem(osBuilder.build()).build();
   }

   private Location createLocation(String input) {
      if (input == null) return null;
      return new LocationBuilder().id(input).scope(LocationScope.REGION).description(input).parent(
               Iterables.getOnlyElement(provider.get())).metadata(ImmutableMap.<String, Object>of("name", input))
               .build();
   }

   public static Function<OSImage, OperatingSystem.Builder> osFamily() {
      return new Function<OSImage, OperatingSystem.Builder>() {
         @Override
         public OperatingSystem.Builder apply(final OSImage image) {

            final String label = image.label();
            checkNotNull(label, "label");
            OsFamily family = OsFamily.UNRECOGNIZED;

            if (label.contains(CENTOS))
               family = OsFamily.CENTOS;
            else if (label.contains(OPENLOGIC))
               family = OsFamily.CENTOS;
            else if (label.contains(SUSE))
               family = OsFamily.SUSE;
            else if (label.contains(UBUNTU))
               family = OsFamily.UBUNTU;
            else if (label.contains(WINDOWS))
               family = OsFamily.WINDOWS;
            else if (label.contains(ORACLE_lINUX))
               family = OsFamily.OEL;

            String version = UNRECOGNIZED;
            //ex: CoreOS Alpha -> Alpha
            if (label.contains(COREOS))
               version = label.replace("CoreOS ", "");
               //openSUSE 13.1 -> 13.1
            else if (label.contains(OPENSUSE))
               version = label.replace("openSUSE ", "");
               //SUSE Linux Enterprise Server 11 SP3 (Premium Image) -> 11 SP3(Premium Image)
            else if (label.contains(SUSE))
               version = label.replace("SUSE ", "");
               //Ubuntu Server 12.04 LTS -> 12.04 LTS
            else if (label.contains(UBUNTU))
               version = label.replace("Ubuntu Server ", "");
            else if (label.contains(SQL_SERVER))
               version = label;
            else if (label.contains(CENTOS))
               version = label;
            else if (label.contains(WINDOWS))
               version = label;
            else if (label.equals(ORACLE_lINUX))
               version = label;
            if (family != OsFamily.UNRECOGNIZED) {
               return OperatingSystem.builder().family(family).version(version)
                     .description(image.description() + "");
            } else if (family == OsFamily.UNRECOGNIZED && image.os() == OSImage.Type.WINDOWS) {
               return OperatingSystem.builder().family(OsFamily.WINDOWS).version(version)
                     .description(image.description() + "");
            }
            return OperatingSystem.builder().family(OsFamily.LINUX).version(version).description(image.description());
         }
      };
   }
}
