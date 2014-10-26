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
package org.jclouds.openstack.poppy.v1.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jclouds.openstack.poppy.v1.PoppyApi;
import org.jclouds.openstack.poppy.v1.domain.Flavor;
import org.jclouds.openstack.poppy.v1.internal.BasePoppyApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code FlavorApi}
 */
@Test(groups = "unit", testName = "FlavorApiMockTest")
public class FlavorApiMockTest extends BasePoppyApiMockTest {

   public void testListFlavors() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_flavor_list_response.json"))));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         FlavorApi api = poppyApi.getFlavorApi();

         List<Flavor> flavors = api.list().toList();

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/flavors");

         assertThat(flavors).isNotEmpty();

      } finally {
         server.shutdown();
      }
   }

   public void testGetFlavor() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_flavor_get_response.json"))));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         FlavorApi api = poppyApi.getFlavorApi();

         Flavor oneFlavor  = api.get("cdn");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/flavors/cdn");

         assertThat(oneFlavor).isNotNull();

      } finally {
         server.shutdown();
      }
   }

   public void testGetFlavorFailOn404() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         FlavorApi api = poppyApi.getFlavorApi();

         Flavor oneFlavor  = api.get("cdn");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/flavors/cdn");

         assertThat(oneFlavor).isNull();

      } finally {
         server.shutdown();
      }
   }
}
