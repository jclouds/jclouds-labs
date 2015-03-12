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
package org.jclouds.azurecompute.features;

import com.google.common.collect.ImmutableList;
import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecompute.domain.CreateProfileParams;
import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpoint;
import org.jclouds.azurecompute.domain.ProfileDefinitionEndpointParams;
import org.jclouds.azurecompute.domain.ProfileDefinitionParams;
import org.jclouds.azurecompute.domain.UpdateProfileParams;
import org.jclouds.azurecompute.xml.ListProfileDefinitionsHandlerTest;
import org.jclouds.azurecompute.xml.ListProfilesHandlerTest;
import org.jclouds.azurecompute.xml.ProfileDefinitionHandlerTest;
import org.jclouds.azurecompute.xml.ProfileHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "TrafficManagerApiMockTest")
public class TrafficManagerApiMockTest extends BaseAzureComputeApiMockTest {

   public void listDefWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/listprofiledefinitions.xml"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertEquals(api.listDefinitions("myprofile"), ListProfileDefinitionsHandlerTest.expected());
         assertSent(server, "GET", "/services/WATM/profiles/myprofile/definitions");
      } finally {
         server.shutdown();
      }
   }

   public void listDefWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertTrue(api.listDefinitions("myprofile").isEmpty());
         assertSent(server, "GET", "/services/WATM/profiles/myprofile/definitions");
      } finally {
         server.shutdown();
      }
   }

   public void getDefWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/profiledefinition.xml"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertEquals(api.getDefinition("myprofile"), ProfileDefinitionHandlerTest.expected());
         assertSent(server, "GET", "/services/WATM/profiles/myprofile/definitions/1");
      } finally {
         server.shutdown();
      }
   }

   public void getDefWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertNull(api.getDefinition("myprofile"));
         assertSent(server, "GET", "/services/WATM/profiles/myprofile/definitions/1");
      } finally {
         server.shutdown();
      }
   }

   public void listProfWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/listprofiles.xml"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertEquals(api.listProfiles(), ListProfilesHandlerTest.expected());
         assertSent(server, "GET", "/services/WATM/profiles");
      } finally {
         server.shutdown();
      }
   }

   public void listProfWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertTrue(api.listProfiles().isEmpty());
         assertSent(server, "GET", "/services/WATM/profiles");
      } finally {
         server.shutdown();
      }
   }

   public void getProfWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/profile.xml"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertEquals(api.getProfile("myprofile"), ProfileHandlerTest.expected());
         assertSent(server, "GET", "/services/WATM/profiles/myprofile");
      } finally {
         server.shutdown();
      }
   }

   public void getProfWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertNull(api.getProfile("myprofile"));
         assertSent(server, "GET", "/services/WATM/profiles/myprofile");
      } finally {
         server.shutdown();
      }
   }

   public void checkDNSPrefixAvailability() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/checkdnsprefixavailability.xml"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertTrue(api.checkDNSPrefixAvailability("jclouds.trafficmanager.net"));
         assertSent(server, "GET", "/services/WATM/operations/isavailable/jclouds.trafficmanager.net");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertEquals(api.delete("myprofile"), "request-1");
         assertSent(server, "DELETE", "/services/WATM/profiles/myprofile");

      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();
         assertNull(api.delete("myprofile"));
         assertSent(server, "DELETE", "/services/WATM/profiles/myprofile");
      } finally {
         server.shutdown();
      }
   }

   public void createProfile() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();

         final CreateProfileParams params = CreateProfileParams.builder()
                 .domain("jclouds.trafficmanager.net").name("jclouds").build();

         assertEquals(api.createProfile(params), "request-1");
         assertSent(server, "POST", "/services/WATM/profiles", "/createprofileparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void updateProfile() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();

         final UpdateProfileParams params = UpdateProfileParams.builder()
                 .status(ProfileDefinition.Status.ENABLED).build();

         assertEquals(api.updateProfile("myprofile", params), "request-1");
         assertSent(server, "PUT", "/services/WATM/profiles/myprofile", "/updateprofileparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void createDefinition() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final TrafficManagerApi api = api(server.getUrl("/")).getTrafficManaerApi();

         final ImmutableList.Builder<ProfileDefinitionEndpointParams> endpoints
                 = ImmutableList.<ProfileDefinitionEndpointParams>builder();

         endpoints.add(ProfileDefinitionEndpointParams.builder()
                 .domain("jclouds1.cloudapp.net")
                 .status(ProfileDefinition.Status.ENABLED)
                 .type(ProfileDefinitionEndpoint.Type.CLOUDSERVICE)
                 .weight(1).build());

         endpoints.add(ProfileDefinitionEndpointParams.builder()
                 .domain("jclouds2.cloudapp.net")
                 .status(ProfileDefinition.Status.ENABLED)
                 .type(ProfileDefinitionEndpoint.Type.CLOUDSERVICE)
                 .weight(1).build());

         final ProfileDefinitionParams params = ProfileDefinitionParams.builder()
                 .ttl(300)
                 .lb(ProfileDefinition.LBMethod.ROUNDROBIN)
                 .path("/")
                 .port(80)
                 .protocol(ProfileDefinition.Protocol.HTTP)
                 .endpoints(endpoints.build())
                 .build();

         assertEquals(api.createDefinition("myprofile", params), "request-1");
         assertSent(server, "POST", "/services/WATM/profiles/myprofile/definitions", "/profiledefinitioncsparams.xml");
      } finally {
         server.shutdown();
      }
   }
}
