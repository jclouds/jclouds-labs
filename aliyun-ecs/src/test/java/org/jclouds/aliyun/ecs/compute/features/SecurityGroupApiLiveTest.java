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
package org.jclouds.aliyun.ecs.compute.features;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiLiveTest;
import org.jclouds.aliyun.ecs.domain.IpProtocol;
import org.jclouds.aliyun.ecs.domain.Permission;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.features.SecurityGroupApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "SecurityGroupApiLiveTest")
public class SecurityGroupApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public static final String TEST_PORT_RANGE = "8081/8085";
   public static final String INTERNET = "0.0.0.0/0";

   private String securityGroupId;

   @BeforeClass
   public void setUp() {
      SecurityGroupRequest request = api().create(TEST_REGION,
            CreateSecurityGroupOptions.Builder
                  .securityGroupName("jclouds-test")
      );
      securityGroupId = request.getSecurityGroupId();
   }

   @AfterClass
   public void tearDown() {
      if (securityGroupId != null) {
         api().delete(TEST_REGION, securityGroupId);
      }
   }

   public void testAddRules() {
      api().addInboundRule(TEST_REGION, securityGroupId, IpProtocol.TCP, TEST_PORT_RANGE, INTERNET);
   }

   @Test(groups = "live", dependsOnMethods = "testAddRules")
   public void testGet() {
      Permission permission = Iterables.getOnlyElement(api().get(TEST_REGION, securityGroupId));
      checkPermission(permission);
   }

   @Test(groups = "live", dependsOnMethods = "testGet")
   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(TEST_REGION).concat(), new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All security groups must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some security group to be returned");
   }

   private SecurityGroupApi api() {
      return api.securityGroupApi();
   }

   private void checkPermission(Permission permission) {
      assertNotNull(permission.ipProtocol());
      assertNotNull(permission.portRange());
      assertNotNull(permission.sourceCidrIp());
   }
}
