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
package org.apache.jclouds.profitbricks.rest.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.IpBlock;
import org.apache.jclouds.profitbricks.rest.domain.Location;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IpBlockApiMockTest", singleThreaded = true)
public class IpBlockApiMockTest extends BaseProfitBricksApiMockTest {

   private IpBlockApi ipBlockApi() {
      return api.ipBlockApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/ipblock/list.json"))
      );

      List<IpBlock> list = ipBlockApi().list();

      assertNotNull(list);
      assertEquals(list.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks");
   }

   @Test
   public void testListWith404() throws InterruptedException {
      server.enqueue(response404());
      List<IpBlock> list = ipBlockApi().list();
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks");
   }

   @Test
   public void testListWithDepth() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/ipblock/list.depth-5.json"))
      );

      List<IpBlock> list = ipBlockApi().list(new DepthOptions().depth(5));

      assertNotNull(list);
      assertEquals(list.size(), 2);
      assertEquals(list.get(0).properties().name(), "jclouds-block");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks?depth=5");
   }

   @Test
   public void testListWith404WithDepth() throws InterruptedException {
      server.enqueue(response404());
      List<IpBlock> list = ipBlockApi().list(new DepthOptions().depth(5));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks?depth=5");
   }

   @Test
   public void testGetIpBlock() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/ipblock/get.json"));
      response.setHeader("Content-Type", "application/json");

      server.enqueue(response);

      IpBlock ipblock = ipBlockApi().get("some-id");

      assertNotNull(ipblock);
      assertEquals(ipblock.properties().name(), "jclouds-block");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks/some-id");
   }

   public void testGetIpBlockWith404() throws InterruptedException {
      server.enqueue(response404());

      IpBlock ipblock = ipBlockApi().get("some-id");

      assertEquals(ipblock, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ipblocks/some-id");
   }

   @Test
   public void testCreate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/ipblock/get.json"))
      );

      IpBlock.PropertiesRequest properties = IpBlock.PropertiesRequest.create("jclouds-block", Location.US_LAS.getId(), 2);
      IpBlock ipblock = ipBlockApi().create(
              IpBlock.Request.creatingBuilder()
              .properties(properties)
              .build());

      assertNotNull(ipblock);
      assertNotNull(ipblock.id());

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/ipblocks",
              "{\"properties\":{\n"
              + "\"name\":\"jclouds-block\",\n"
              + "\"location\":\"us/las\",\n"
              + "\"size\":2}\n"
              + "}"
      );
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody("")
      );

      ipBlockApi().delete("some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/ipblocks/some-id");
   }

}
