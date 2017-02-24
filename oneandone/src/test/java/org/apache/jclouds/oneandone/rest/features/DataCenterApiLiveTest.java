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
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "DataCenterApiLiveTest")
public class DataCenterApiLiveTest extends BaseOneAndOneLiveTest {

   private DataCenter currentDataCenter;
   private List<DataCenter> dataCenters;

   private DataCenterApi dataCenterApi() {

      return api.dataCenterApi();
   }

   @Test
   public void testList() {
      dataCenters = dataCenterApi().list();
      assertNotNull(dataCenters);
      currentDataCenter = dataCenters.get(0);
      assertFalse(dataCenters.isEmpty());
      Assert.assertTrue(dataCenters.size() > 0);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "us", null);
      List<DataCenter> resultWithQuery = dataCenterApi().list(options);

      assertNotNull(resultWithQuery);
      assertFalse(resultWithQuery.isEmpty());
      Assert.assertTrue(resultWithQuery.size() > 0);
   }

   @Test(dependsOnMethods = "testList")
   public void testGet() {
      DataCenter result = dataCenterApi().get(currentDataCenter.id());

      assertNotNull(result);
      assertEquals(result.id(), currentDataCenter.id());
   }

}
