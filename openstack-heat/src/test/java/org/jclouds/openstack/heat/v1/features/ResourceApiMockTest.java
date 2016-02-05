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
package org.jclouds.openstack.heat.v1.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jclouds.openstack.heat.v1.HeatApi;
import org.jclouds.openstack.heat.v1.internal.BaseHeatApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code ResourceApi}
 */
@Test(groups = "unit", testName = "ResourceApiMockTest")
public class ResourceApiMockTest extends BaseHeatApiMockTest {

   public void testListTypes() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/resource_type_list_response.json"))));

      try {
         HeatApi heatApi = api(server.getUrl("/").toString(), "openstack-heat", overrides);
         ResourceApi api = heatApi.getResourceApi("RegionOne");

         List<String> resourceTypes = api.listTypes();

         /*
          * Check request
          */
         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/resource_types");

         /*
          * Check response
          */
         assertThat(resourceTypes).isNotEmpty();
         assertThat(resourceTypes.size()).isEqualTo(20);

      } finally {
         server.shutdown();
      }
   }

}
