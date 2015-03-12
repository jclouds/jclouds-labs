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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListAffinityGroupsHandlerTest;
import org.jclouds.azurecompute.domain.CreateAffinityGroupParams;
import org.jclouds.azurecompute.domain.UpdateAffinityGroupParams;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "AffinityGroupApiMockTest")
public class AffinityGroupApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/affinityGroups.xml"));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertEquals(api.list(), ListAffinityGroupsHandlerTest.expected());

         assertSent(server, "GET", "/affinitygroups");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertTrue(api.list().isEmpty());

         assertSent(server, "GET", "/affinitygroups");
      } finally {
         server.shutdown();
      }
   }

   public void testRead() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/affinityGroup.xml"));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertEquals(api.get("Test1"), ListAffinityGroupsHandlerTest.expected().get(0));

         assertSent(server, "GET", "/affinitygroups/Test1");
      } finally {
         server.shutdown();
      }
   }

   public void testNullRead() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertNull(api.get("Test1"));

         assertSent(server, "GET", "/affinitygroups/Test1");
      } finally {
         server.shutdown();
      }
   }

   public void testAdd() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         final CreateAffinityGroupParams params = CreateAffinityGroupParams.builder().name("mygroup").label("foo").
                 location("West Europe").build();

         assertEquals(api.add(params), "request-1");

         assertSent(server, "POST", "/affinitygroups", "/createaffinitygroupparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdate() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-2"));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         final UpdateAffinityGroupParams params = UpdateAffinityGroupParams.builder().label("foo").
                 description("mygroup description").build();

         assertEquals(api.update("mygroup", params), "request-2");

         assertSent(server, "PUT", "/affinitygroups/mygroup", "/updateaffinitygroupparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-3"));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertEquals(api.delete("mygroup"), "request-3");

         assertSent(server, "DELETE", "/affinitygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }

   public void testNullDelete() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final AffinityGroupApi api = api(server.getUrl("/")).getAffinityGroupApi();

         assertNull(api.delete("mygroup"));

         assertSent(server, "DELETE", "/affinitygroups/mygroup");
      } finally {
         server.shutdown();
      }
   }
}
