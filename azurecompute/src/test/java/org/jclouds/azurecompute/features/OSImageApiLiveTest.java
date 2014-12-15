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

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "OSImageApiLiveTest")
public class OSImageApiLiveTest extends BaseAzureComputeApiLiveTest {

   private ImmutableSet<String> locations;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();

      locations = ImmutableSet.copyOf(transform(api.getLocationApi().list(), new Function<Location, String>() {
         public String apply(Location in) {
            return in.name();
         }
      }));
   }

   public void testList() {
      for (OSImage OSImage : api().list()) {
         checkOSImage(OSImage);
      }
   }

   private void checkOSImage(OSImage OSImage) {
      assertNotNull(OSImage.label(), "Label cannot be null for " + OSImage);
      assertNotNull(OSImage.name(), "Name cannot be null for " + OSImage);
      assertNotNull(OSImage.os(), "OS cannot be null for " + OSImage);
      assertTrue(OSImage.logicalSizeInGB() > 0, "LogicalSizeInGB should be positive, if set" + OSImage);

      if (OSImage.category() != null) {
         assertNotEquals("", OSImage.category().trim(), "Invalid Category for " + OSImage);
      }

      if (OSImage.mediaLink() != null) {
         assertTrue(ImmutableSet.of("http", "https").contains(OSImage.mediaLink().getScheme()),
               "MediaLink should be an http(s) url" + OSImage);
      }

      assertTrue(locations.contains(OSImage.location()), "Locations not in " + locations + " :" + OSImage);

      // Ex. Dirty data in RightScale eula field comes out as an empty string.
      assertFalse(OSImage.eula().contains(""));
      if (OSImage.affinityGroup() != null) {
         // TODO: list getAffinityGroups and check if there
      }
   }

   private OSImageApi api() {
      return api.getOSImageApi();
   }
}
