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
package org.jclouds.shipyard.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.shipyard.ShipyardApi;
import org.jclouds.shipyard.domain.roles.RoleInfo;
import org.jclouds.shipyard.internal.BaseShipyardMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.shipyard.features.RolesApi} class.
 */
@Test(groups = "unit", testName = "RolesApiMockTest")
public class RolesApiMockTest extends BaseShipyardMockTest {

   public void testListRoles() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody(payloadFromResource("/roles.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         assertEquals(api.listRoles().size(), 2);
         assertSent(server, "GET", "/api/roles");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }

   public void testGetRole() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody(payloadFromResource("/role.json")));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         RoleInfo role = api.getRole("admin");
         assertNotNull(role);
         assertEquals(role.name(), "admin");
         assertSent(server, "GET", "/api/roles/admin");
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testGetNonExistentRole() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(500).setBody("role does not exist"));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         RoleInfo role = api.getRole("NonExistentRole");
         assertNull(role);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testCreateRole() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         api.createRole("admin");
         assertSent(server, "POST", "/api/roles", new String(payloadFromResource("/role-delete-create.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testDeleteRole() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         boolean removed = api.deleteRole("admin");
         assertTrue(removed);
         assertSent(server, "DELETE", "/api/roles", new String(payloadFromResource("/role-delete-create.json")));
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
   
   public void testDeleteNonExistentRole() throws Exception {
      MockWebServer server = mockShipyardWebServer();
      server.enqueue(new MockResponse().setResponseCode(500).setBody("role does not exist"));
      ShipyardApi shipyardApi = api(server.getUrl("/"));
      RolesApi api = shipyardApi.rolesApi();
      try {
         boolean removed = api.deleteRole("NonExistentRole");
         assertFalse(removed);
      } finally {
         shipyardApi.close();
         server.shutdown();
      }
   }
}
