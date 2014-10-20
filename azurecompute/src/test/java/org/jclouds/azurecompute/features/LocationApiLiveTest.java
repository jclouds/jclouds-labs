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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jclouds.azurecompute.domain.Location;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "LocationApiLiveTest")
public class LocationApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final List<String> KNOWN_SERVICES = Arrays
         .asList("Compute", "Storage", "PersistentVMRole", "HighMemory");

   @Test public void testList() {
      for (Location location : api().list()) {
         checkLocation(location);
      }
   }

   private void checkLocation(Location location) {
      assertNotNull(location.name(), "Name cannot be null for a Location.");
      assertNotNull(location.displayName(), "DisplayName cannot be null for: " + location);
      assertNotNull(location.availableServices(), "AvailableServices cannot be null for: " + location.name());
      assertTrue(KNOWN_SERVICES.containsAll(location.availableServices()),
            "AvailableServices in " + location + " didn't match: " + KNOWN_SERVICES);
   }

   private LocationApi api() {
      return api.getLocationApi();
   }
}
