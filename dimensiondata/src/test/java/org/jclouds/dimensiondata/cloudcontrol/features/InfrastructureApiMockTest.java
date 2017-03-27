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
package org.jclouds.dimensiondata.cloudcontrol.features;

import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseAccountAwareCloudControlMockTest;
import org.jclouds.http.Uris;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.util.Set;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link InfrastructureApi} class.
 */
@Test(groups = "unit", testName = "InfrastructureApiMockTest", singleThreaded = true)
public class InfrastructureApiMockTest extends BaseAccountAwareCloudControlMockTest {

   public void testListDatacenters() throws Exception {
      server.enqueue(jsonResponse("/datacenters.json"));
      Iterable<Datacenter> datacenters = api.getInfrastructureApi().listDatacenters().concat();

      assertEquals(size(datacenters), 2); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(HttpMethod.GET, expectedListDatacentersUriBuilder().toString());
   }

   public void testListDatacentersWithPagination() throws Exception {
      server.enqueue(jsonResponse("/datacenters-page1.json"));
      server.enqueue(jsonResponse("/datacenters-page2.json"));
      Iterable<Datacenter> datacenters = api.getInfrastructureApi().listDatacenters().concat();

      consumeIterableAndAssertAdditionalPagesRequested(datacenters, 2, 1);

      assertSent(HttpMethod.GET, expectedListDatacentersUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListDatacentersUriBuilder(), 2).toString());
   }

   public void testListDatacenters404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getInfrastructureApi().listDatacenters().concat().isEmpty());
      assertSent(HttpMethod.GET, expectedListDatacentersUriBuilder().toString());
   }

   private Uris.UriBuilder expectedListDatacentersUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/infrastructure/datacenter");
      Set<String> zones = ctx.utils().injector().getInstance(ZoneIdsSupplier.class).get();
      for (String zone : zones) {
         uriBuilder.addQuery("id", zone);
      }
      return uriBuilder;
   }

   public void testListOperatingSystems() throws Exception {
      server.enqueue(jsonResponse("/operatingSystems.json"));
      Iterable<OperatingSystem> operatingSystems = api.getInfrastructureApi().listOperatingSystems("NA9").concat();

      assertEquals(size(operatingSystems), 33);
      assertEquals(server.getRequestCount(), 2);

      assertSent(HttpMethod.GET, expectedListOperatingSystemsUriBuilder().toString());
   }

   public void testListOperatingSystemsWithPagination() throws Exception {
      server.enqueue(jsonResponse("/operatingSystems-page1.json"));
      server.enqueue(jsonResponse("/operatingSystems-page2.json"));
      Iterable<OperatingSystem> operatingSystems = api.getInfrastructureApi().listOperatingSystems("NA9").concat();

      consumeIterableAndAssertAdditionalPagesRequested(operatingSystems, 33, 1);

      assertSent(HttpMethod.GET, expectedListOperatingSystemsUriBuilder().toString());
      assertSent(HttpMethod.GET, addPageNumberToUriBuilder(expectedListOperatingSystemsUriBuilder(), 2).toString());

   }

   public void testListOperatingSystems404() throws Exception {
      server.enqueue(response404());
      assertTrue(api.getInfrastructureApi().listOperatingSystems("NA9").concat().isEmpty());
      assertSent(HttpMethod.GET, expectedListOperatingSystemsUriBuilder().toString());
   }

   private Uris.UriBuilder expectedListOperatingSystemsUriBuilder() {
      Uris.UriBuilder uriBuilder = Uris
            .uriBuilder("/caas/2.4/6ac1e746-b1ea-4da5-a24e-caf1a978789d/infrastructure/operatingSystem");
      uriBuilder.addQuery("datacenterId", "NA9");
      return uriBuilder;
   }

}
