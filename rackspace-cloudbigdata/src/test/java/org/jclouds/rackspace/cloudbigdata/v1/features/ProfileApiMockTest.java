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
package org.jclouds.rackspace.cloudbigdata.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jclouds.rackspace.cloudbigdata.v1.CloudBigDataApi;
import org.jclouds.rackspace.cloudbigdata.v1.domain.CreateProfile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.Profile;
import org.jclouds.rackspace.cloudbigdata.v1.domain.ProfileSSHKey;
import org.jclouds.rackspace.cloudbigdata.v1.internal.BaseCloudBigDataApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests ProfileApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test
public class ProfileApiMockTest extends BaseCloudBigDataApiMockTest {

   public void testCreateProfile() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/profile_create_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ProfileApi api = cbdApi.getProfileApiForZone("ORD");

         CreateProfile createProfile = CreateProfile.builder()
               .username("john.doe")
               .password("j0Hnd03")
               .sshKeys(ImmutableList.of(ProfileSSHKey.builder().name("t@test")
                     .publicKey("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCtUFnkFrqDDCgEqW1akQkpMOX\n" + 
                           "Owwvg73PLn5Z5QgvxjvJhRCg9ZTR/OWXpWcYqFVNagH4Zs8NOb9921TyQ+ydMnatOM\n" + 
                           "haxMh1ZwTgaUcvndOF8fY+kcERiw1l0iT95w42F8IdUH42Z+8KihZM8gVsbMS6qYTi\n" + 
                           "OM29WHX7y37wuJIzqf3N2TiVXrqfjwugvY/bZ+47EUn78uk6aPZYJGXdDgaFqnIXUV\n" + 
                           "N+hRFYXgKnU0Ui0aQkuYwnAW8KmanLoNU2xodrb6/XqWnSAAmwl7aoGKFunQsT6xDW\n" + 
                           "yQk+ncUHUcdofDUqgd3lXmHGrTmQW97vqexDEnhsJ+AwbLGD5dukr t@test")
                           .build()))
                           .credentialsUsername("jdoe")
                           .credentialsApiKey("df23gkh34h52gkdgfakgf")
                           .build();

         Profile profile = api.create(createProfile);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/profile", "/profile_create_request.json");

         /*
          * Check response
          */
         assertNotNull(profile);
         assertEquals(profile.getUsername(),"john.doe");
         assertEquals(profile.getUserId(),"12346");
         assertEquals(profile.getTenantId(),"123456");
         assertEquals(profile.getSSHKeys().get(0).getName(),"t@test");
         assertEquals(profile.getSSHKeys().get(0).getPublicKey(),"ssh-rsa .....");
         assertEquals(profile.getCredentialsUsername(),"jdoe");
         assertNull(profile.getCredentialsApiKey());
         assertEquals(profile.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/123456/profile"));
      } finally {
         server.shutdown();
      }
   }

   public void testCreateProfileFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/profile_create_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ProfileApi api = cbdApi.getProfileApiForZone("ORD");

         CreateProfile createProfile = CreateProfile.builder()
               .username("john.doe")
               .password("j0Hnd03")
               .sshKeys(ImmutableList.of(ProfileSSHKey.builder().name("t@test")
                     .publicKey("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCtUFnkFrqDDCgEqW1akQkpMOX\n" + 
                           "Owwvg73PLn5Z5QgvxjvJhRCg9ZTR/OWXpWcYqFVNagH4Zs8NOb9921TyQ+ydMnatOM\n" + 
                           "haxMh1ZwTgaUcvndOF8fY+kcERiw1l0iT95w42F8IdUH42Z+8KihZM8gVsbMS6qYTi\n" + 
                           "OM29WHX7y37wuJIzqf3N2TiVXrqfjwugvY/bZ+47EUn78uk6aPZYJGXdDgaFqnIXUV\n" + 
                           "N+hRFYXgKnU0Ui0aQkuYwnAW8KmanLoNU2xodrb6/XqWnSAAmwl7aoGKFunQsT6xDW\n" + 
                           "yQk+ncUHUcdofDUqgd3lXmHGrTmQW97vqexDEnhsJ+AwbLGD5dukr t@test")
                           .build()))
                           .credentialsUsername("jdoe")
                           .credentialsApiKey("df23gkh34h52gkdgfakgf")
                           .build();

         Profile profile = api.create(createProfile);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v1.0/888888/profile", "/profile_create_request.json");

         /*
          * Check response
          */
         assertNull(profile);
      } finally {
         server.shutdown();
      }
   }

   public void testGetProfile() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/profile_get_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ProfileApi api = cbdApi.getProfileApiForZone("ORD");
         Profile profile = api.get();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/profile");

         /*
          * Check response
          */
         assertNotNull(profile);
         assertEquals(profile.getUsername(),"john.doe");
         assertEquals(profile.getUserId(),"12346");
         assertEquals(profile.getTenantId(),"123456");
         assertEquals(profile.getSSHKeys().get(0).getName(),"t@test");
         assertEquals(profile.getSSHKeys().get(0).getPublicKey(),"ssh-rsa .....");
         assertEquals(profile.getCredentialsUsername(),"jdoe");
         assertNull(profile.getCredentialsApiKey());
         assertEquals(profile.getLinks().get(0).getHref(), new URI("https://dfw.bigdata.api.rackspacecloud.com/v1.0/123456/profile"));
      } finally {
         server.shutdown();
      }
   }

   public void testGetProfileFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/profile_get_response.json"))));

      try {
         CloudBigDataApi cbdApi = api(server.getUrl("/").toString(), "rackspace-cloudbigdata", overrides);
         ProfileApi api = cbdApi.getProfileApiForZone("ORD");
         Profile profile = api.get();

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1.0/888888/profile");

         /*
          * Check response
          */
         assertNull(profile);
      } finally {
         server.shutdown();
      }
   }
}
