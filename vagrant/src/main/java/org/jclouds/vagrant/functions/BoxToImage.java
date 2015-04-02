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
package org.jclouds.vagrant.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.vagrant.internal.BoxConfig;
import org.jclouds.vagrant.reference.VagrantConstants;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import vagrant.api.domain.Box;

public class BoxToImage implements Function<Box, Image> {
   private BoxConfig.Factory boxConfigFactory;

   @Inject
   BoxToImage(BoxConfig.Factory boxConfigFactory) {
      this.boxConfigFactory = checkNotNull(boxConfigFactory, "boxConfigFactory");
   }

   @Override
   public Image apply(Box input) {
      OperatingSystem os = new OperatingSystem(inferOsFamily(input), input.getName(), input.getVersion(), null, input.getName(), true);
      return new ImageBuilder()
            .ids(input.getName())
            .name(input.getName())
            .version(input.getVersion())
            .operatingSystem(os)
            .status(Status.AVAILABLE)
            // Overriden by AddDefaultCredentialsToImage
            //.defaultCredentials()
            .userMetadata(ImmutableMap.of(VagrantConstants.USER_META_PROVIDER, input.getProvider()))
            .build();
   }

   private OsFamily inferOsFamily(Box input) {
      String name = input.getName().toUpperCase();
      for (OsFamily family : OsFamily.values()) {
         if (name.contains(family.name())) {
            return family;
         }
      }

      BoxConfig configParser = boxConfigFactory.newInstance(input);
      Optional<String> guest = configParser.getKey(VagrantConstants.KEY_VM_GUEST);
      if (guest.isPresent() && guest.get().equals(VagrantConstants.VM_GUEST_WINDOWS)) {
         return OsFamily.WINDOWS;
      }
      return OsFamily.UNRECOGNIZED;
   }

}
