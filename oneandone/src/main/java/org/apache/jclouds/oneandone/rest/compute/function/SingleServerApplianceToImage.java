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
package org.apache.jclouds.oneandone.rest.compute.function;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;
import java.util.Map;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;

public class SingleServerApplianceToImage implements Function<SingleServerAppliance, Image> {

   private static final Map<String, OsFamily> OTHER_OS_MAP = ImmutableMap.<String, OsFamily>builder()
           .put("Others", OsFamily.FREEBSD)
           .put("Other", OsFamily.FREEBSD)
           .build();

   @Override
   public Image apply(SingleServerAppliance from) {
      OsFamily osFamily = findInStandardFamilies(from.osVersion()).or(findInOtherOSMap(from.osVersion())).or(OsFamily.UNRECOGNIZED);
      OperatingSystem os = OperatingSystem.builder()
              .description(osFamily.value())
              .family(osFamily)
              .version(parseVersion(from.os()))
              .is64Bit(is64Bit(from.osArchitecture()))
              .build();

      return new ImageBuilder()
              .ids(from.id())
              .name(from.name())
              .status(Image.Status.AVAILABLE)
              .operatingSystem(os)
              .build();
   }

   static String parseVersion(String from) {
      if (from != null) {
         String[] split = from.toLowerCase().split("^\\D*(?=\\d)");

         if (split.length >= 2) {
            return split[1];
         }
      }
      return "non";
   }

   static boolean is64Bit(int architecture) {
      return architecture != 32;

   }

   private static Optional<OsFamily> findInStandardFamilies(final String osFamily) {
      if (osFamily == null) {
         return Optional.absent();
      }
      return tryFind(asList(OsFamily.values()), new Predicate<OsFamily>() {
         @Override
         public boolean apply(OsFamily input) {
            return osFamily.toLowerCase().contains(input.value().toLowerCase());
         }
      });
   }

   private static Optional<OsFamily> findInOtherOSMap(final String label) {
      if (label == null) {
         return Optional.absent();
      }
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
}
