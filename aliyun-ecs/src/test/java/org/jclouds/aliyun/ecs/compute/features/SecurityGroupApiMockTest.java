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

import com.google.common.collect.ImmutableMap;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiMockTest;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.aliyun.ecs.domain.options.ListSecurityGroupsOptions.Builder.paginationOptions;
import static org.jclouds.aliyun.ecs.domain.options.PaginationOptions.Builder.pageNumber;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "SecurityGroupApiMockTest", singleThreaded = true)
public class SecurityGroupApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testListSecurityGroups() throws InterruptedException {
      server.enqueue(jsonResponse("/securitygroups-first.json"));
      server.enqueue(jsonResponse("/securitygroups-last.json"));
      Iterable<SecurityGroup> securitygroups = api.securityGroupApi().list(TEST_REGION).concat();
      assertEquals(size(securitygroups), 7); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);
      assertSent(server, "GET", "DescribeSecurityGroups", ImmutableMap.of("RegionId", TEST_REGION));
      assertSent(server, "GET", "DescribeSecurityGroups", ImmutableMap.of("RegionId", TEST_REGION), 2);
   }

   public void testListSecurityGroupsReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<SecurityGroup> securitygroups = api.securityGroupApi().list(TEST_REGION).concat();
      assertTrue(isEmpty(securitygroups));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeSecurityGroups", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testListSecurityGroupsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/securitygroups-first.json"));
      Iterable<SecurityGroup> securitygroups = api.securityGroupApi().list(TEST_REGION, paginationOptions(pageNumber(1).pageSize(5)));
      assertEquals(size(securitygroups), 5);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeSecurityGroups", ImmutableMap.of("RegionId", TEST_REGION), 1);
   }

   public void testListSecurityGroupsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<SecurityGroup> securitygroups = api.securityGroupApi().list(TEST_REGION, paginationOptions(pageNumber(2)));
      assertTrue(isEmpty(securitygroups));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeSecurityGroups", ImmutableMap.of("RegionId", TEST_REGION), 2);
   }

}
