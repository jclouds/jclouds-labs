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
package org.jclouds.abiquo.domain.admin;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.config.Privilege;
import org.jclouds.abiquo.domain.enterprise.Role;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Live integration tests for the {@link Role} domain class.
 */
@Test(groups = "api", testName = "RoleLiveApiTest")
public class RoleLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      Role role = Role.builder(env.context.getApiContext()).name("dummyRoleUpdateRole").blocked(false).build();
      role.save();

      role.setName("UPDATED_ROLE");
      role.update();

      // Recover the updated role
      RoleDto updated = env.adminApi.getRole(role.getId());

      assertEquals(updated.getName(), "UPDATED_ROLE");

      role.delete();
   }

   public void testCreateRepeated() {
      Role repeated = Role.Builder.fromRole(env.role).build();

      try {
         repeated.save();
         fail("Should not be able to create roles with the same name");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "ROLE-7");
      }
   }

   public void testCreateEnterpriseRole() {
      final Role entRole = Role.Builder.fromRole(env.role).build();
      entRole.setName(entRole.getName() + "enterprise");
      entRole.setEnterprise(env.enterprise);
      entRole.save();

      find(env.enterprise.listRoles(), new Predicate<Role>() {
         @Override
         public boolean apply(Role input) {
            return input.getName().equals(entRole.getName());
         }
      });
   }

   public void testAddPrivilege() {
      PrivilegeDto dto = env.configApi.getPrivilege(8);
      final Privilege privilege = DomainWrapper.wrap(env.context.getApiContext(), Privilege.class, dto);
      List<Privilege> privileges = Lists.newArrayList(env.role.listPrivileges());
      privileges.add(privilege);

      env.role.setPrivileges(privileges);
      env.role.update();

      find(env.role.listPrivileges(), new Predicate<Privilege>() {
         @Override
         public boolean apply(Privilege input) {
            return input.getName().equals(privilege.getName());
         }
      });
   }
}
