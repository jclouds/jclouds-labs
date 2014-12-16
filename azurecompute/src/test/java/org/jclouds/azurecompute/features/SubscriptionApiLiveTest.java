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
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SubscriptionApiLiveTest")
public class SubscriptionApiLiveTest extends BaseAzureComputeApiLiveTest {

   @Test public void testList() {
      for (RoleSize roleSize : api().listRoleSizes()) {
         checkLocation(roleSize);
      }
   }

   private void checkLocation(RoleSize roleSize) {
      assertNotNull(roleSize.name(), "Name cannot be null for a Location.");
      assertNotNull(roleSize.label(), "Label cannot be null for: " + roleSize);
      assertNotNull(roleSize.cores(), "Cores cannot be null for: " + roleSize.name());
   }

   private SubscriptionApi api() {
      return api.getSubscriptionApi();
   }
}
