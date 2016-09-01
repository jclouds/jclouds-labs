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
import org.apache.jclouds.oneandone.rest.domain.PublicIp;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "PublicIpApiMockTest", singleThreaded = true)
public class PublicIpApiMockTest extends BaseOneAndOneApiMockTest {

   private PublicIpApi publicIpApi() {
      return api.publicIpApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/list.json"))
      );

      List<PublicIp> publicIps = publicIpApi().list();

      assertNotNull(publicIps);
      assertEquals(publicIps.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<PublicIp> publicIps = publicIpApi().list();

      assertEquals(publicIps.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<PublicIp> publicIps = publicIpApi().list(options);

      assertNotNull(publicIps);
      assertEquals(publicIps.size(), 3);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<PublicIp> publicIps = publicIpApi().list(options);

      assertEquals(publicIps.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips?q=New");
   }

   public void testGet() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/get.json"))
      );
      PublicIp result = publicIpApi().get("publicipId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips/publicipId");
   }

   public void testGet404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      PublicIp result = publicIpApi().get("publicipId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/public_ips/publicipId");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/get.json"))
      );

      PublicIp response = publicIpApi().create(PublicIp.CreatePublicIp.create("reverseDns", "datacentarId", Types.IPType.IPV4));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/public_ips", "{\"reverse_dns\":\"reverseDns\",\"datacenter_id\":\"datacentarId\",\"type\":\"IPV4\"}");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/get.json"))
      );
      PublicIp response = publicIpApi().update("publicipId", PublicIp.UpdatePublicIp.create("reverseDns"));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/public_ips/publicipId", "{\"reverse_dns\":\"reverseDns\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/publicip/get.json"))
      );
      PublicIp response = publicIpApi().delete("publicipId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/public_ips/publicipId");
   }

   @Test
   public void testDelete404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      PublicIp response = publicIpApi().delete("publicipId");

      assertEquals(response, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/public_ips/publicipId");
   }
}
