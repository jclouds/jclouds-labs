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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.jclouds.azurecompute.domain.Disk;
import org.jclouds.azurecompute.domain.Image;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.domain.OSType;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "DiskApiLiveTest")
public class DiskApiLiveTest extends BaseAzureComputeApiLiveTest {

   private ImmutableSet<String> locations;
   private ImmutableSet<String> images;

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();

      locations = ImmutableSet.copyOf(transform(api.getLocationApi().list(),
               new Function<Location, String>() {
                  @Override
                  public String apply(Location in) {
                     return in.getName();
                  }
               }));
      images = ImmutableSet.copyOf(transform(api.getImageApi().list(), new Function<Image, String>() {
         @Override
         public String apply(Image in) {
            return in.name();
         }
      }));
   }

   @Test
   protected void testList() {
      List<Disk> response = api().list();

      for (Disk disk : response) {
         checkDisk(disk);
      }
   }

   private void checkDisk(Disk disk) {
      checkNotNull(disk.getName(), "Name cannot be null for Disk %s", disk.getLabel());
      checkNotNull(disk.getOS(), "OS cannot be null for Disk: %s", disk);
      assertNotEquals(disk.getOS(), OSType.UNRECOGNIZED, "Status cannot be UNRECOGNIZED for Disk: " + disk);

      checkNotNull(disk.getAttachedTo(), "While AttachedTo can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getAttachedTo().isPresent()) {
         // TODO: verify you can lookup the role
      }

      checkNotNull(disk.getLogicalSizeInGB(),
               "While LogicalSizeInGB can be null for Disk, its Optional wrapper cannot: %s", disk);

      if (disk.getLogicalSizeInGB().isPresent())
         assertTrue(disk.getLogicalSizeInGB().get() > 0, "LogicalSizeInGB should be positive, if set" + disk.toString());

      checkNotNull(disk.getMediaLink(), "While MediaLink can be null for Disk, its Optional wrapper cannot: %s", disk);

      if (disk.getMediaLink().isPresent())
         assertTrue(ImmutableSet.of("http", "https").contains(disk.getMediaLink().get().getScheme()),
                  "MediaLink should be an http(s) url" + disk.toString());

      checkNotNull(disk.getLabel(), "While Label can be null for Disk, its Optional wrapper cannot: %s",
               disk);

      checkNotNull(disk.getDescription(), "While Description can be null for Disk, its Optional wrapper cannot: %s",
               disk);

      checkNotNull(disk.getLocation(), "While Location can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getLocation().isPresent()) {
         assertTrue(locations.contains(disk.getLocation().get()),
                  "Location not in " + locations + " :" + disk.toString());
      }

      checkNotNull(disk.getSourceImage(), "While SourceImage can be null for Disk, its Optional wrapper cannot: %s",
               disk);
      if (disk.getSourceImage().isPresent()) {
         assertTrue(images.contains(disk.getSourceImage().get()),
                  "SourceImage not in " + images + " :" + disk.toString());
      }

      checkNotNull(disk.getAffinityGroup(),
               "While AffinityGroup can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getAffinityGroup().isPresent()) {
         // TODO: list getAffinityGroups and check if there
      }
   }

   private DiskApi api() {
      return api.getDiskApi();
   }
}
