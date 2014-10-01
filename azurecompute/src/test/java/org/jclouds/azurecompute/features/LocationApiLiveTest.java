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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.List;
import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Test(groups = "live", testName = "LocationApiLiveTest")
public class LocationApiLiveTest extends BaseAzureComputeApiLiveTest {

   @Test
   protected void testList() {
      List<Location> response = api().list();

      for (Location location : response) {
         checkLocation(location);
      }

   }

   private Predicate<String> knownServices = Predicates
         .in(Arrays.asList("Compute", "Storage", "PersistentVMRole", "HighMemory"));

   private void checkLocation(Location location) {
      checkNotNull(location.getName(), "Name cannot be null for a Location.");
      checkNotNull(location.getDisplayName(), "DisplayName cannot be null for Location %s", location.getName());
      checkNotNull(location.getAvailableServices(), "AvailableServices cannot be null for Location %s",
            location.getName());
      checkState(Iterables.all(location.getAvailableServices(), knownServices),
            "AvailableServices in Location %s didn't match %s: %s", location.getName(), knownServices,
            location.getAvailableServices());
   }

   private LocationApi api() {
      return api.getLocationApi();
   }
}
