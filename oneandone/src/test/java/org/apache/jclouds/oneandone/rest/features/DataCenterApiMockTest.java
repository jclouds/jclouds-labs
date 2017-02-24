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
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DataCenterApiMockTest", singleThreaded = true)
public class DataCenterApiMockTest extends BaseOneAndOneApiMockTest {

   private DataCenterApi dataCenterApi() {
      return api.dataCenterApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/datacenter/list.json"))
      );

      List<DataCenter> dataCenter = dataCenterApi().list();

      assertNotNull(dataCenter);
      assertEquals(dataCenter.size(), 4);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<DataCenter> dataCenter = dataCenterApi().list();

      assertNotNull(dataCenter);
      assertEquals(dataCenter.size(), 0);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/datacenter/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<DataCenter> dataCenter = dataCenterApi().list(options);

      assertNotNull(dataCenter);
      assertEquals(dataCenter.size(), 4);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<DataCenter> dataCenter = dataCenterApi().list(options);

      assertNotNull(dataCenter);
      assertEquals(dataCenter.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/datacenter/get.json"))
      );
      DataCenter result = dataCenterApi().get("datacenterId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenterId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      DataCenter result = dataCenterApi().get("datacenterId");

      assertNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/datacenters/datacenterId");
   }
}
