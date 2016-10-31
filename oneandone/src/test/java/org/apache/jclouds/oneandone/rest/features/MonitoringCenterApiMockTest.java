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

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.jclouds.oneandone.rest.domain.MonitoringCenter;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericDateQueryOptions;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "MonitoringCenterApiMockTest", singleThreaded = true)
public class MonitoringCenterApiMockTest extends BaseOneAndOneApiMockTest {

   private MonitoringCenterApi monitoringCenterApi() {
      return api.monitoringCenterApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringcenters/list.json"))
      );

      List<MonitoringCenter> result = monitoringCenterApi().list();

      assertNotNull(result);
      assertEquals(result.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<MonitoringCenter> result = monitoringCenterApi().list();

      assertEquals(result.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringcenters/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<MonitoringCenter> result = monitoringCenterApi().list(options);

      assertEquals(result.size(), 2);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<MonitoringCenter> result = monitoringCenterApi().list(options);

      assertEquals(result.size(), 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center?q=New");
   }

   public void testGetCustomPeriod() throws InterruptedException, ParseException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringcenters/get.json"))
      );
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      String startStr = "11-11-2012";
      String endStr = "11-11-2013";
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date end = dateFormat.parse(endStr);
      Date start = dateFormat.parse(startStr);
      options.customPeriod(start, end);

      MonitoringCenter result = monitoringCenterApi().get("serverId", options);
      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center/serverId?period=CUSTOM&start_date=2012-11-11T00%3A00%3A00Z&end_date=2013-11-11T00%3A00%3A00Z");
   }

   public void testGetCustomPeriod404() throws InterruptedException, ParseException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      String startStr = "11-11-2012";
      String endStr = "11-11-2013";
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date end = dateFormat.parse(endStr);
      Date start = dateFormat.parse(startStr);
      options.customPeriod(start, end);
      MonitoringCenter result = monitoringCenterApi().get("serverId", options);

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center/serverId?period=CUSTOM&start_date=2012-11-11T00%3A00%3A00Z&end_date=2013-11-11T00%3A00%3A00Z");
   }

   public void testGetFixedPeriod() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/monitoringcenters/get.json"))
      );
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      options.fixedPeriods(Types.PeriodType.LAST_24H);
      MonitoringCenter result = monitoringCenterApi().get("serverId", options);

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center/serverId?period=LAST_24H");
   }

   public void testGetFixedPeriod404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericDateQueryOptions options = new GenericDateQueryOptions();
      options.fixedPeriods(Types.PeriodType.LAST_24H);
      MonitoringCenter result = monitoringCenterApi().get("serverId", options);

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/monitoring_center/serverId?period=LAST_24H");
   }
}
