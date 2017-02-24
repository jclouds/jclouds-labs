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
import org.apache.jclouds.oneandone.rest.domain.ServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerApplianceApiMockTest", singleThreaded = true)
public class ServerApplianceApiMockTest extends BaseOneAndOneApiMockTest {

   private ServerApplianceApi serverApplianceApi() {
      return api.serverApplianceApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/serverappliance/list.json"))
      );

      List<ServerAppliance> networks = serverApplianceApi().list();

      assertNotNull(networks);
      assertEquals(networks.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<ServerAppliance> networks = serverApplianceApi().list();

      assertEquals(networks.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/serverappliance/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<ServerAppliance> publicIps = serverApplianceApi().list(options);

      assertNotNull(publicIps);
      assertEquals(publicIps.size(), 4);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<ServerAppliance> publicIps = serverApplianceApi().list(options);

      assertEquals(publicIps.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances?q=New");
   }

   @Test
   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/serverappliance/get.json"))
      );
      SingleServerAppliance result = serverApplianceApi().get("serverApplianceId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances/serverApplianceId");
   }

   @Test
   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      SingleServerAppliance result = serverApplianceApi().get("serverApplianceId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/server_appliances/serverApplianceId");
   }
}
