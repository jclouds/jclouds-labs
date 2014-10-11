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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.USER;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;

@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "UserApiExpectTest")
public class UserApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private static String user = "7212e451-76e1-4631-b2de-ba1dfd8080e4";
   private static URI userHref = URI.create(endpoint + "/user/" + user);
   
   private static String org = "7212e451-76e1-4631-b2de-asdasdasd";
   private static URI orgAdminHref = URI.create(endpoint + "/admin/org/" + org);
   
   private HttpRequest add = HttpRequest.builder()
            .method("POST")
            .endpoint(orgAdminHref + "/users")
            .addHeader("Accept", USER)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/user/addUserSource.xml", VCloudDirectorMediaType.USER))
            .build();

   private HttpResponse addResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/addUser.xml", USER + ";version=1.5"))
            .build();
    
   @Test
   public void testAddUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, add, addResponse);
      assertEquals(api.getUserApi().addUserToOrg(addUserSource(), orgAdminHref), addUser());
   }

   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(userHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/user.xml", ORG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getUserApi().get(userHref), user());
   }

   HttpRequest edit = HttpRequest.builder()
            .method("PUT")
            .endpoint(userHref)
            .addHeader("Accept", USER)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/user/editUserSource.xml", USER))
            .build();

   HttpResponse editResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/editUser.xml", USER))
            .build();

   @Test
   public void testEditUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, edit, editResponse);
      assertEquals(api.getUserApi().edit(userHref, editUserSource()), editUser());
   }

   HttpRequest unlock = HttpRequest.builder()
            .method("POST")
            .endpoint(userHref + "/action/unlock")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

   HttpResponse unlockResponse = HttpResponse.builder()
            .statusCode(204)
            .build();

   @Test
   public void testUnlockUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, unlock, unlockResponse);
      api.getUserApi().unlock(userHref);
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUnlockUserHrefNotFound() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, unlock,  HttpResponse.builder()
               .statusCode(403)
               .payload(payloadFromResourceWithContentType("/org/error400.xml", ERROR))
               .build());
      api.getUserApi().unlock(userHref);
   }

   HttpRequest remove = HttpRequest.builder()
            .method("DELETE")
            .endpoint(userHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse removeResponse = HttpResponse.builder()
            .statusCode(200)
            .build();
      
   @Test
   public void testRemoveUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, remove, removeResponse);
      api.getUserApi().remove(userHref);
   }

   public static final User addUserSource() {
      return User.builder()
            .name("test")
            .fullName("testFullName")
            .emailAddress("test@test.com")
            .telephone("555-1234")
            .isEnabled(false)
            .im("testIM")
            .isAlertEnabled(false)
            .alertEmailPrefix("testPrefix")
            .alertEmail("testAlert@test.com")
            .isExternal(false)
            .isGroupRole(false)
            .role(Reference.builder()
               .type("application/vnd.vmware.admin.role+xml")
               .name("vApp User")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/role/ff1e0c91-1288-3664-82b7-a6fa303af4d1"))
               .build())
            .password("password")
            .groups(ImmutableList.<Reference>of())
            .build();
   }
   
   public static final User addUser() {
      return addUserSource().toBuilder()
         .id("urn:vcloud:user:b37223f3-8792-477a-820f-334998f61cd6")
         .type("application/vnd.vmware.admin.user+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.user+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
            .build())
         .isLocked(false)
         .isDefaultCached(false)
         .storedVmQuota(0)
         .deployedVmQuota(0)
         .password(null)
         .build();
   }
   
   public static final User user() {
      return addUser().toBuilder()
         .nameInSource("test")
         .build();
   }

   public static final User editUserSource() {
      return user().toBuilder()
         .fullName("new" + user().getFullName())
         .emailAddress("new" + user().getEmailAddress())
         .telephone("1-" + user().getTelephone())
         .isEnabled(true)
         .im("new" + user().getIM())
         .isAlertEnabled(true)
         .alertEmailPrefix("new" + user().getAlertEmailPrefix())
         .alertEmail("new" + user().getAlertEmail())
         .storedVmQuota(1)
         .deployedVmQuota(1)
         .password("newPassword")
         .build();
   }
   
   public static final User editUser() {
      return editUserSource().toBuilder()
         .password(null)
         .build();
   }
 
}
