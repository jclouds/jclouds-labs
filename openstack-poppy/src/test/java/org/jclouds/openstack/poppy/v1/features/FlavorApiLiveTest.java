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
package org.jclouds.openstack.poppy.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.openstack.poppy.v1.domain.Flavor;
import org.jclouds.openstack.poppy.v1.internal.BasePoppyApiLiveTest;
import org.testng.annotations.Test;

/**
 * OpenStack Poppy Flavor API Live Tests
 */
@Test(groups = "live", testName = "FlavorApiLiveTest")
public class FlavorApiLiveTest extends BasePoppyApiLiveTest {

   /**
    * Tests retrieval of all Poppy Flavors.
    *
    * @throws Exception
    */
   public void testListFlavors() throws Exception {
      FlavorApi flavorApi = api.getFlavorApi();
      List<Flavor> flavors = flavorApi.list().toList();
      assertNotNull(flavors);
      assertFalse(flavors.isEmpty());

      for (Flavor flavor : flavors) {
         assertNotNull(flavor);
      }

   }

   /**
    * Tests retrieval of a single Poppy Flavor.
    *
    * @throws Exception
    */
   public void testGetFlavor() throws Exception {
      FlavorApi flavorApi = api.getFlavorApi();
      List<Flavor> flavors = flavorApi.list().toList();
      assertNotNull(flavors);

      for (Flavor flavor : flavors) {
         Flavor oneFlavor = flavorApi.get(flavor.getId());
         assertNotNull(oneFlavor);
         assertEquals(oneFlavor.getId(), flavor.getId());
         assertEquals(oneFlavor.getProviders(), flavor.getProviders());
         assertEquals(oneFlavor.getLinks(), flavor.getLinks());
      }
   }
}
