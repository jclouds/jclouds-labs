/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.compute.codec;

import com.google.common.collect.ImmutableSet;
import org.jclouds.ContextBuilder;
import org.jclouds.codec.ToLocation;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.representations.Hardware;
import org.jclouds.compute.representations.Image;
import org.jclouds.representations.Location;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


@Test
public class ComputeConversionsTest {

   private ComputeService getCompute() {
      ComputeServiceContext context = ContextBuilder.newBuilder("stub").name("test-stub").credentials("user", "pass").build(ComputeServiceContext.class);
      return context.getComputeService();
   }

   @Test
   void testToHardware() {
      assertNull(ToHardware.INSTANCE.apply(null));
      ComputeService compute = getCompute();
      Set<Hardware> hardwareSet = ImmutableSet.<Hardware>builder()
                                              .addAll(transform(compute.listHardwareProfiles(), ToHardware.INSTANCE))
                                              .build();
      assertFalse(hardwareSet.isEmpty());
      for (Hardware representation : hardwareSet) {
         assertTrue(representation.getRam() > 0);
      }
   }

   @Test
   void testToImage() {
      assertNull(ToImage.INSTANCE.apply(null));
      ComputeService compute = getCompute();
      Set<Image> imageSet = ImmutableSet.<Image>builder()
                                        .addAll(transform(compute.listImages(), ToImage.INSTANCE))
                                        .build();

      assertFalse(imageSet.isEmpty());
      for (Image representation : imageSet) {
         assertNotNull(representation.getId());
         assertNotNull(representation.getOperatingSystem());
      }
   }

   @Test
   void testToLocation() {
      assertNull(ToLocation.INSTANCE.apply(null));
      ComputeService compute = getCompute();
      Set<Location> locationSet = ImmutableSet.<Location>builder()
                                              .addAll(transform(compute.listAssignableLocations(), ToLocation.INSTANCE))
                                              .build();
      assertFalse(locationSet.isEmpty());

      for (Location representation : locationSet) {
         assertNotNull(representation.getId());
         assertNotNull(representation.getScope());
      }
   }
}
