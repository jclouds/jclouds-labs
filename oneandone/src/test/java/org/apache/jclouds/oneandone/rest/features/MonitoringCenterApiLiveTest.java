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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.MonitoringCenter;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericDateQueryOptions;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "MonitoringCenterApiLiveTest")
public class MonitoringCenterApiLiveTest extends BaseOneAndOneLiveTest {

   private Server currentServer;

   private MonitoringCenterApi monitoringCenterApi() {

      return api.monitoringCenterApi();
   }

   @BeforeClass
   public void setupTest() throws InterruptedException {
      currentServer = createServer("Monitoring Center jclouds server");
      assertNodeAvailable(currentServer);
   }

   @Test
   public void testList() {
      List<MonitoringCenter> result = monitoringCenterApi().list();

      assertNotNull(result);
      Assert.assertTrue(result.size() >= 1);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(1, 1, null, null, null);
      List<MonitoringCenter> resultWithQuery = monitoringCenterApi().list(options);

      assertNotNull(resultWithQuery);
      Assert.assertTrue(resultWithQuery.size() >= 1);
   }

   @Test
   public void testGetCustomPeriod() {
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      Calendar cal = Calendar.getInstance();
      Date end = cal.getTime();
      cal.add(Calendar.DATE, -2);
      Date start = cal.getTime();
      options.customPeriod(start, end);
      MonitoringCenter result = monitoringCenterApi().get(currentServer.id(), options);
      assertNotNull(result);
   }

   @Test
   public void testGetFixedPeriod() {
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      options.fixedPeriods(Types.PeriodType.LAST_7D);
      MonitoringCenter result = monitoringCenterApi().get(currentServer.id(), options);
      assertNotNull(result);
   }
   
    @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      assertNodeAvailable(currentServer);
      deleteServer(currentServer.id());
   }
}
