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
package org.jclouds.abiquo.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.domain.enterprise.options.UserOptions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * Expect tests for the {@link EnterpriseApi} class.
 */
@Test(groups = "unit", testName = "EnterpriseApiExpectTest")
public class EnterpriseApiExpectTest extends BaseAbiquoApiExpectTest<EnterpriseApi> {

   public void testListUsersWithoutPagination() {
      EnterpriseApi api = requestsSendResponses(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises/1/users")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UsersDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/users-page.xml", normalize(UsersDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises/1/users")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UsersDto.MEDIA_TYPE)) //
                  .addQueryParam("numResults", "2") //
                  .addQueryParam("page", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/users-lastpage.xml",
                              normalize(UsersDto.MEDIA_TYPE))) //
                  .build());

      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.addLink(new RESTLink("users", "http://localhost/api/admin/enterprises/1/users"));

      PagedIterable<UserDto> result = api.listUsers(enterprise);
      List<UserDto> all = result.concat().toList();

      assertEquals(all.size(), 3);
      assertEquals(all.get(0).getId().intValue(), 1);
      assertEquals(all.get(1).getId().intValue(), 2);
      assertEquals(all.get(2).getId().intValue(), 3);
   }

   public void testListUsersWithPagination() {
      EnterpriseApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises/1/users")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(UsersDto.MEDIA_TYPE)) //
                  .addQueryParam("page", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/users-lastpage.xml",
                              normalize(UsersDto.MEDIA_TYPE))) //
                  .build());

      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.addLink(new RESTLink("users", "http://localhost/api/admin/enterprises/1/users"));

      UserOptions options = UserOptions.builder().page(1).build();
      PaginatedCollection<UserDto, UsersDto> result = api.listUsers(enterprise, options);

      assertEquals(result.size(), 1);
      assertEquals(result.getTotalSize().intValue(), 3);
      assertEquals(result.get(0).getId().intValue(), 3);
      assertNotNull(result.searchLink("first"));
      assertNotNull(result.searchLink("last"));
   }

   public void testListEnterprises() {
      EnterpriseApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EnterprisesDto.MEDIA_TYPE)) //
                  .addQueryParam("limit", "1") //
                  .addQueryParam("has", "text") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/enterprises-page.xml",
                              normalize(EnterprisesDto.MEDIA_TYPE))) //
                  .build());

      EnterpriseOptions options = EnterpriseOptions.builder().limit(1).has("text").build();
      PaginatedCollection<EnterpriseDto, EnterprisesDto> result = api.listEnterprises(options);

      assertEquals(result.size(), 1);
      assertEquals(result.getTotalSize().intValue(), 2);
      assertEquals(result.get(0).getId().intValue(), 1);
      assertNotNull(result.searchLink("first"));
      assertNotNull(result.searchLink("last"));
   }

   public void testListEnterprisesByDatacenterWithOptions() {
      EnterpriseApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/action/enterprises")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EnterprisesDto.MEDIA_TYPE)) //
                  .addQueryParam("limit", "1") //
                  .addQueryParam("has", "text") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/enterprises-page.xml",
                              normalize(EnterprisesDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("enterprises", "http://localhost/api/admin/datacenters/1/action/enterprises"));

      EnterpriseOptions options = EnterpriseOptions.builder().limit(1).has("text").build();
      PaginatedCollection<EnterpriseDto, EnterprisesDto> result = api.listEnterprises(datacenter, options);

      assertEquals(result.size(), 1);
      assertEquals(result.getTotalSize().intValue(), 2);
      assertEquals(result.get(0).getId().intValue(), 1);
      assertNotNull(result.searchLink("first"));
      assertNotNull(result.searchLink("last"));
   }

   public void testListEnterprisesReturns2xx() {
      EnterpriseApi api = requestsSendResponses(
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EnterprisesDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/enterprises-page.xml",
                              normalize(EnterprisesDto.MEDIA_TYPE))) //
                  .build(),
            HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost/api/admin/enterprises")) //
                  .addHeader("Cookie", tokenAuth) //
                  .addHeader("Accept", normalize(EnterprisesDto.MEDIA_TYPE)) //
                  .addQueryParam("startwith", "1") //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/enterprises-lastpage.xml",
                              normalize(EnterprisesDto.MEDIA_TYPE))) //
                  .build());

      PagedIterable<EnterpriseDto> result = api.listEnterprises();
      List<EnterpriseDto> all = result.concat().toList();

      assertEquals(all.size(), 2);
      assertEquals(all.get(0).getId().intValue(), 1);
      assertEquals(all.get(1).getId().intValue(), 2);
   }

   @Override
   protected EnterpriseApi clientFrom(AbiquoApi api) {
      return api.getEnterpriseApi();
   }
}
