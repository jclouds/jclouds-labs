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
package org.apache.jclouds.oneandone.rest.features;

import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.ServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServerApplianceApiLiveTest")
public class ServerApplianceApiLiveTest extends BaseOneAndOneLiveTest {

   private ServerAppliance currentAppliance;
   private List<ServerAppliance> appliances;

   private ServerApplianceApi serverApplianceApi() {

      return api.serverApplianceApi();
   }

   @Test
   public void testList() {
      appliances = serverApplianceApi().list();
      Assert.assertTrue(appliances.size() > 0);
      currentAppliance = appliances.get(0);
   }

   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "iso", null);
      List<ServerAppliance> resultWithQuery = serverApplianceApi().list(options);

      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   @Test(dependsOnMethods = "testList")
   public void testGet() {
      SingleServerAppliance result = serverApplianceApi().get(currentAppliance.id());

      assertEquals(result.id(), currentAppliance.id());
   }

}
