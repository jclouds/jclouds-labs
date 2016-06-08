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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.jclouds.openstack.poppy.v1.PoppyApi;
import org.jclouds.openstack.poppy.v1.domain.Caching;
import org.jclouds.openstack.poppy.v1.domain.CreateService;
import org.jclouds.openstack.poppy.v1.domain.Domain;
import org.jclouds.openstack.poppy.v1.domain.LogDelivery;
import org.jclouds.openstack.poppy.v1.domain.Origin;
import org.jclouds.openstack.poppy.v1.domain.Restriction;
import org.jclouds.openstack.poppy.v1.domain.RestrictionRule;
import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.openstack.poppy.v1.domain.UpdateService;
import org.jclouds.openstack.poppy.v1.internal.BasePoppyApiMockTest;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code ServiceApi}
 */
@Test(groups = "unit", testName = "ServiceApiMockTest")
public class ServiceApiMockTest extends BasePoppyApiMockTest {

   public void testCreateService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(202))
                        .setHeader(HttpHeaders.LOCATION, "https://poppycdn.org/v1.0/services/123123"));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         CreateService options = CreateService.builder()
               .name("mywebsite.com")
               .domains(ImmutableList.of(
                     Domain.builder().domain("www.mywebsite.com").build(),
                     Domain.builder().domain("blog.mywebsite.com").build()))
               .origins(ImmutableList.of(
                     Origin.builder().origin("mywebsite.com").port(80).sslEnabled(false).build()))
               .restrictions(ImmutableList.of(
                     Restriction.builder()
                           .name("website only")
                           .rules(ImmutableList.of(
                                 RestrictionRule.builder().name("mywebsite.com").httpHost("www.mywebsite.com").build()))
                           .build()))
               .caching(ImmutableList.of(
                     Caching.builder().name("default").ttl(3600).build()))
               .flavorId("cdn")
               .logDelivery(LogDelivery.builder().enabled(false).build())
               .build();

         URI uri = api.create(options);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", BASE_URI + "/services",
               "/poppy_service_create_request.json");

         assertThat(uri).isEqualTo(URI.create("https://poppycdn.org/v1.0/services/123123"));

      } finally {
         server.shutdown();
      }
   }

   public void testGetService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_service_get_response.json"))));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         Service oneService = api.get("96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/services/96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");

         assertThat(oneService).isNotNull();

      } finally {
         server.shutdown();
      }
   }

   public void testListPagedService() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_service_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_service_list_response_paged2.json"))));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         // Note: Lazy! Have to actually look at the collection.
         List<Service> services = api.list().concat().toList();
         assertEquals(services.size(), 4);
         // look at last element
         assertEquals(services.get(3).getId(), "96737ae3-cfc1-4c72-be88-5d0e7cc9a3f1_2");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/123123/services");
         assertRequest(server.takeRequest(), "GET", "/v1.0/123123/services?marker=96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0&limit=20");

         /*
          * Check response
          */
         assertNotNull(services);
         assertEquals(services.get(0).getId(), "96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");
         assertEquals(services.get(3).getId(), "96737ae3-cfc1-4c72-be88-5d0e7cc9a3f1_2");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageService() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_service_list_response_paged1.json"))));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         PaginatedCollection<Service> services = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/123123/services?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(services);
         assertEquals(services.first().get().getId(), "96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");
      } finally {
         server.shutdown();
      }
   }

   public void testPatchService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200).setBody(stringFromResource("/poppy_service_get_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(202))
            .setHeader(HttpHeaders.LOCATION, "https://poppycdn.org/v1.0/services/123123"));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         Service toUpdate = api.get("345345");
         UpdateService.Builder updatable = toUpdate.toUpdatableService();
         UpdateService target = updatable.name("the name has been updated to this one").build();
         URI uri = api.update("345345", toUpdate, target);

         assertThat(server.getRequestCount()).isEqualTo(3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", BASE_URI + "/services/345345");
         assertRequest(server.takeRequest(), "PATCH", BASE_URI + "/services/345345", "/poppy_service_patch_response.json");

         assertThat(uri).isEqualTo(URI.create("https://poppycdn.org/v1.0/services/123123"));

      } finally {
         server.shutdown();
      }
   }

   public void testDeleteService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         boolean result = api.delete("96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", BASE_URI + "/services/96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");

         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteServiceAsset() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         boolean result = api.deleteAsset("96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0", "/images/1.jpg");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE",
               BASE_URI + "/services/96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0/assets?url=/images/1.jpg");

         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteAllServiceAssets() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(200)));

      try {
         PoppyApi poppyApi = api(server.getUrl("/").toString(), "openstack-poppy", overrides);
         ServiceApi api = poppyApi.getServiceApi();

         boolean result = api.deleteAssets("96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE",
               BASE_URI + "/services/96737ae3-cfc1-4c72-be88-5d0e7cc9a3f0/assets?all=true");

         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

}
