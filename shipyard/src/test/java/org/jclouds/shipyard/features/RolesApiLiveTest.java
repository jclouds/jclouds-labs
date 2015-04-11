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

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import org.jclouds.shipyard.domain.roles.RoleInfo;
import org.jclouds.shipyard.internal.BaseShipyardApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "RolesApiLiveTest", singleThreaded = true)
public class RolesApiLiveTest extends BaseShipyardApiLiveTest {

   private final String roleName = "jclouds-shipyard-test-" + UUID.randomUUID().toString().replaceAll("-", "");
   
   @AfterClass (alwaysRun = true)
   protected void tearDown() {
      api().deleteRole(roleName);
   }
   
   public void testCreateRole() throws Exception {
     api().createRole(roleName);
   }
   
   @Test (dependsOnMethods = "testCreateRole")
   public void testGetRole() throws Exception {
      RoleInfo role = api().getRole(roleName);
      assertNotNull(role, "Role was not set");
      assertTrue(role.name().equals(roleName), "Found Role name does not match expected name: found=" + role.name() + ", expected=" + roleName);
   }
   
   @Test (dependsOnMethods = "testGetRole")
   public void testListRoles() throws Exception {
      List<RoleInfo> possibleRoles = api().listRoles();
      assertNotNull(possibleRoles, "possibleRoles was not set");
      assertTrue(possibleRoles.size() > 0, "Expected at least 1 Role but list was empty");
      RoleInfo possibleRole = Iterables.find(possibleRoles, new Predicate<RoleInfo>() {
         @Override
         public boolean apply(RoleInfo arg0) {
            return arg0.name().equals(roleName);
         }
      }, null);
      assertNotNull(possibleRole, "Expected but could not find Role amongst " + possibleRoles.size() + " found");
   }
   
   @Test (dependsOnMethods = "testListRoles")
   public void testRemoveRole() throws Exception {
      boolean removed = api().deleteRole(UUID.randomUUID().toString().replaceAll("-", ""));
      assertTrue(removed);
   }
   
   @Test (dependsOnMethods = "testRemoveRole")
   public void testRemoveNonExistentRole() throws Exception {
      boolean removed = api().deleteRole(UUID.randomUUID().toString().replaceAll("-", ""));
      assertTrue(removed);
   }
   
   @Test (dependsOnMethods = "testRemoveNonExistentRole")
   public void testGetNonExistentRole() throws Exception {
      RoleInfo role = api().getRole("jclouds-shipyard-test-" + UUID.randomUUID().toString().replaceAll("-", ""));
      assertNull(role, "Role was expected to be NULL but was not");
   }
   
   private RolesApi api() {
      return api.rolesApi();
   }
}
