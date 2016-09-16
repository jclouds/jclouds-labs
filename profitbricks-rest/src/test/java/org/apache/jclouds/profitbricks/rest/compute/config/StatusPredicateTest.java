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
package org.apache.jclouds.profitbricks.rest.compute.config;

import com.google.common.base.Predicate;
import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.concurrent.TimeUnit;
import org.apache.jclouds.profitbricks.rest.compute.config.ProfitBricksComputeServiceContextModule.DataCenterProvisioningStatePredicate;
import org.apache.jclouds.profitbricks.rest.compute.config.ProfitBricksComputeServiceContextModule.ServerStatusPredicate;
import org.apache.jclouds.profitbricks.rest.compute.config.ProfitBricksComputeServiceContextModule.SnapshotProvisioningStatePredicate;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.jclouds.util.Predicates2;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test class for {@link DataCenterProvisioningStatePredicate} and
 * {@link ServerStatusPredicate}
 */
@Test(groups = "unit", testName = "ProvisioningStatusPollingPredicateTest", singleThreaded = true)
public class StatusPredicateTest extends BaseProfitBricksApiMockTest {

   @Test
   public void testDataCenterPredicate() throws Exception {

      String payloadInProcess = stringFromResource("/compute/predicate/datacenter-inprocess.json");
      String payloadAvailable = stringFromResource("/compute/predicate/datacenter.json");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      Predicate<String> waitUntilAvailable = Predicates2.retry(
              new DataCenterProvisioningStatePredicate(api, State.AVAILABLE),
              30l, 1l, TimeUnit.SECONDS);

      String id = "datacenter-id,aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      waitUntilAvailable.apply(id);

      DataCenter dataCenter = api.dataCenterApi().getDataCenter(id);
      State finalState = dataCenter.metadata().state();
      assertEquals(finalState, State.AVAILABLE);

   }

   @Test
   public void testServerPredicate() throws Exception {

      String payloadInProcess = stringFromResource("/compute/predicate/server-inprocess.json");
      String payloadAvailable = stringFromResource("/compute/predicate/server.json");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      Predicate<ServerRef> waitUntilAvailable = Predicates2.retry(
              new ServerStatusPredicate(api, Server.Status.RUNNING),
              30l, 1l, TimeUnit.SECONDS);

      waitUntilAvailable.apply(ServerRef.create("datacenter-id", "server-id"));
      Server remoteServer = api.serverApi().getServer("datacenter-id", "server-id");
      assertEquals(remoteServer.properties().vmState(), Server.Status.RUNNING);

   }

   @Test
   public void testSnapshotPredicate() throws Exception {

      String payloadInProcess = stringFromResource("/compute/predicate/snapshot-inprocess.json");
      String payloadAvailable = stringFromResource("/compute/predicate/snapshot.json");

      // wait 3 times
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadInProcess));
      server.enqueue(new MockResponse().setBody(payloadAvailable));

      server.enqueue(new MockResponse().setBody(payloadAvailable));

      Predicate<String> waitUntilAvailable = Predicates2.retry(
              new SnapshotProvisioningStatePredicate(api, State.AVAILABLE),
              30l, 1l, TimeUnit.SECONDS);

      String id = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";

      waitUntilAvailable.apply(id);
      Snapshot snapshot = api.snapshotApi().get(id);
      assertEquals(snapshot.metadata().state().toString(), State.AVAILABLE.toString());

   }

}
