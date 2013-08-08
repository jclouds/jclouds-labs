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

import java.net.URI;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.enterprise.options.UserOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.UsersDto;

/**
 * Expect tests for the {@link EnterpriseApi} class.
 * 
 * @author Carlos Garcia
 */
@Test(groups = "unit", testName = "EnterpriseApiExpectTest")
public class EnterpriseApiExpectTest extends BaseAbiquoApiExpectTest<EnterpriseApi> {

   private EnterpriseApi buildMockEnterpriseApi(String payloadFile, Builder<?> requestBuilder) {
      return requestSendsResponse(requestBuilder.build(),
            HttpResponse.builder().statusCode(200).payload(
                  payloadFromResourceWithContentType(payloadFile,
                        normalize(UsersDto.MEDIA_TYPE))) //
                        .build());
   }

   public Builder<?> getRequestBuilder() {
      return HttpRequest.builder() //
            .method("GET")
            .endpoint(URI.create("http://localhost/api/admin/enterprises/1/users"))
            .addHeader("Cookie", tokenAuth)
            .addHeader("Accept", normalize(UsersDto.MEDIA_TYPE));
   }

   public void testListUsersWithoutPagination() {
      EnterpriseApi api = buildMockEnterpriseApi("/payloads/usr-list.xml",
            getRequestBuilder());

      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.addLink(new RESTLink("users",
            "http://localhost/api/admin/enterprises/1/users"));

      UsersDto users = api.listUsers(enterprise);
      assertEquals(users.getCollection().size(), 3);
      assertEquals(users.getCollection().get(0).getNick(), "potter");
      assertEquals(users.getCollection().get(1).getNick(), "granger");
      assertEquals(users.getCollection().get(2).getNick(), "ron");
   }

   public void testListUsersWithPagination() {
      Builder<?> builder = getRequestBuilder();
      builder.addQueryParam("numResults", "2"); 
      builder.addQueryParam("page", "2");
      EnterpriseApi api = buildMockEnterpriseApi("/payloads/usr-list-page-2.xml", builder);

      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.addLink(new RESTLink("users",
            "http://localhost/api/admin/enterprises/1/users"));

      UsersDto users = api.listUsers(enterprise, 
            UserOptions.builder().limit(2).page(2).build());
      assertEquals(users.getCollection().size(), 1);
      assertEquals(users.getCollection().get(0).getNick(), "ron");
   }

   @Override
   protected EnterpriseApi clientFrom(AbiquoApi api) {
      return api.getEnterpriseApi();
   }

}
