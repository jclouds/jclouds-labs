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
package org.jclouds.azurecompute.features;

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.domain.AffinityGroup;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.internal.AbstractAzureComputeApiLiveTest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Test(groups = "live", testName = "OSImageApiLiveTest")
public class OSImageApiLiveTest extends AbstractAzureComputeApiLiveTest {

   private ImmutableSet<String> locations;

   private ImmutableSet<String> groups;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      locations = ImmutableSet.copyOf(transform(api.getLocationApi().list(), new Function<Location, String>() {

         @Override
         public String apply(final Location location) {
            return location.name();
         }
      }));
      groups = ImmutableSet.copyOf(transform(api.getAffinityGroupApi().list(), new Function<AffinityGroup, String>() {

         @Override
         public String apply(final AffinityGroup group) {
            return group.name();
         }
      }));
   }

   public void testList() {
      for (OSImage osImage : api().list()) {
         checkOSImage(osImage);
      }
   }

   private void checkOSImage(final OSImage osImage) {
      assertNotNull(osImage.label(), "Label cannot be null for " + osImage);
      assertNotNull(osImage.name(), "Name cannot be null for " + osImage);
      assertNotNull(osImage.os(), "OS cannot be null for " + osImage);
      assertTrue(osImage.logicalSizeInGB() > 0, "LogicalSizeInGB should be positive, if set" + osImage);

      if (osImage.category() != null) {
         assertNotEquals("", osImage.category().trim(), "Invalid Category for " + osImage);
      }

      if (osImage.mediaLink() != null) {
         assertTrue(ImmutableSet.of("http", "https").contains(osImage.mediaLink().getScheme()),
                 "MediaLink should be an http(s) url" + osImage);
      }

      ImmutableSet<String> osImageLocations = ImmutableSet.copyOf(Splitter.on(';').splitToList(osImage.location()));
      assertFalse(Sets.intersection(osImageLocations, locations).isEmpty(),
              "No shared locations between " + locations + " and " + osImageLocations);

      // Ex. Dirty data in RightScale eula field comes out as an empty string.
      assertFalse(osImage.eula().contains(""));
      if (osImage.affinityGroup() != null) {
         assertTrue(locations.contains(osImage.affinityGroup()), "No " + osImage.affinityGroup() + " in " + locations);
      }
   }

   private OSImageApi api() {
      return api.getOSImageApi();
   }
}
