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
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SnapshotApiMockTest", singleThreaded = true)
public class SnapshotApiMockTest extends BaseProfitBricksApiMockTest {

   @Test
   public void testGetList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/snapshot/list.json"))
      );

      List<Snapshot> list = snapshotApi().list();

      assertNotNull(list);
      assertEquals(list.size(), 9);
      assertEquals(list.get(0).properties().name(), "snapshot desc...");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots");
   }

   @Test
   public void testGetListWithDepth() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/snapshot/list-depth-5.json"))
      );

      List<Snapshot> list = snapshotApi().list(new DepthOptions().depth(5));

      assertNotNull(list);
      assertEquals(list.size(), 3);
      assertEquals(list.get(0).properties().name(), "test snapshot");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots?depth=5");
   }

   @Test
   public void testGetListWith404() throws InterruptedException {
      server.enqueue(response404());
      List<Snapshot> list = snapshotApi().list();
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots");
   }

   @Test
   public void testGetListWithDepth404() throws InterruptedException {
      server.enqueue(response404());
      List<Snapshot> list = snapshotApi().list(new DepthOptions().depth(5));
      assertTrue(list.isEmpty());
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots?depth=5");
   }

   @Test
   public void testGetSnapshot() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/snapshot/get.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");

      server.enqueue(response);

      Snapshot snapshot = snapshotApi().get("some-id");

      assertNotNull(snapshot);
      assertEquals(snapshot.properties().name(), "snapshot desc...");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots/some-id");
   }

   @Test
   public void testGetSnapshotWithDepth() throws InterruptedException {
      MockResponse response = new MockResponse();
      response.setBody(stringFromResource("/snapshot/get-depth-5.json"));
      response.setHeader("Content-Type", "application/vnd.profitbricks.resource+json");

      server.enqueue(response);

      Snapshot snapshot = snapshotApi().get("some-id", new DepthOptions().depth(5));

      assertNotNull(snapshot);
      assertEquals(snapshot.properties().name(), "test snapshot 2");

      assertEquals(this.server.getRequestCount(), 1);
      assertSent(this.server, "GET", "/snapshots/some-id?depth=5");
   }

   public void testGetSnapshotWith404() throws InterruptedException {
      server.enqueue(response404());

      Snapshot snapshot = snapshotApi().get("some-id");

      assertEquals(snapshot, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots/some-id");
   }

   public void testGetSnapshotWithDepth404() throws InterruptedException {
      server.enqueue(response404());

      Snapshot snapshot = snapshotApi().get("some-id", new DepthOptions().depth(5));

      assertEquals(snapshot, null);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/snapshots/some-id?depth=5");
   }

   @Test
   public void testUpdate() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/snapshot/get.json"))
      );

      api.snapshotApi().update(
              Snapshot.Request.updatingBuilder()
              .id("some-id")
              .name("new-snapshot-name")
              .description("description...")
              .build());

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/rest/snapshots/some-id", "{\"name\": \"new-snapshot-name\", \"description\": \"description...\"}");
   }

   @Test
   public void testDelete() throws InterruptedException {
      server.enqueue(response204());

      snapshotApi().delete("some-id");
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/snapshots/some-id");
   }

   @Test
   public void testDeleteWith404() throws InterruptedException {
      server.enqueue(response404());

      snapshotApi().delete("some-id");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/snapshots/some-id");
   }

   private SnapshotApi snapshotApi() {
      return api.snapshotApi();
   }

}
