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
import java.util.zip.ZipInputStream;
import org.apache.jclouds.oneandone.rest.domain.Vpn;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VpnApiMockTest", singleThreaded = true)
public class VpnApiMockTest extends BaseOneAndOneApiMockTest {

   private VpnApi vpnApi() {
      return api.vpnApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/list.json"))
      );

      List<Vpn> vpns = vpnApi().list();

      assertNotNull(vpns);
      assertEquals(vpns.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<Vpn> vpns = vpnApi().list();

      assertEquals(vpns.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<Vpn> vpns = vpnApi().list(options);

      assertNotNull(vpns);
      assertEquals(vpns.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<Vpn> vpns = vpnApi().list(options);

      assertEquals(vpns.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/get.json"))
      );
      Vpn result = vpnApi().get("vpnId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns/vpnId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      Vpn result = vpnApi().get("vpnId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns/vpnId");
   }

   public void testGetConfiguration() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/configuration.json"))
      );
      ZipInputStream result = vpnApi().getConfiguration("vpnId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns/vpnId/configuration_file");
   }

   public void testGetConfiguration404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      ZipInputStream result = vpnApi().getConfiguration("vpnId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/vpns/vpnId/configuration_file");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/get.json"))
      );

      Vpn response = vpnApi().create(Vpn.CreateVpn.create("name", "desc", null));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/vpns", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/get.json"))
      );
      Vpn response = vpnApi().update("vpnId", Vpn.UpdateVpn.create("name", "desc"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/vpns/vpnId", "{\"name\":\"name\",\"description\":\"desc\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/vpn/get.json"))
      );
      Vpn response = vpnApi().delete("vpnId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/vpns/vpnId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      Vpn response = vpnApi().delete("vpnId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/vpns/vpnId");
   }
}
