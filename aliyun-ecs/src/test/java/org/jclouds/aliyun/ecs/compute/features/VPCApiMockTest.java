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
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.VPC;
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.collect.IterableWithMarker;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.aliyun.ecs.domain.options.ListVPCsOptions.Builder.paginationOptions;
import static org.jclouds.aliyun.ecs.domain.options.PaginationOptions.Builder.pageNumber;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "VPCApiMockTest", singleThreaded = true)
public class VPCApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testListVPCs() throws InterruptedException {
      server.enqueue(jsonResponse("/vpcs-first.json"));
      server.enqueue(jsonResponse("/vpcs-last.json"));
      Iterable<VPC> vpcs = api.vpcApi().list(TEST_REGION).concat();
      assertEquals(size(vpcs), 2); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);
      assertSent(server, "GET", "DescribeVpcs", ImmutableMap.of("RegionId", TEST_REGION));
      assertSent(server, "GET", "DescribeVpcs", ImmutableMap.of("RegionId", TEST_REGION), 2);
   }

   public void testListVPCsReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<VPC> vpcs = api.vpcApi().list(TEST_REGION).concat();
      assertTrue(isEmpty(vpcs));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeVpcs", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testListVPCsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/vpcs-first.json"));
      IterableWithMarker<VPC> vpcs = api.vpcApi().list(TEST_REGION, paginationOptions(pageNumber(1).pageSize(5)));
      assertEquals(size(vpcs), 1);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeVpcs", ImmutableMap.of("RegionId", TEST_REGION), 1);
   }

   public void testListVPCsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<VPC> vpcs = api.vpcApi().list(TEST_REGION, paginationOptions(pageNumber(2)));
      assertTrue(isEmpty(vpcs));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeVpcs", ImmutableMap.of("RegionId", TEST_REGION), 2);
   }

   public void testCreateVPC() throws InterruptedException {
      server.enqueue(jsonResponse("/vpc-create-res.json"));
      VPCRequest vpcRequest = api.vpcApi().create(TEST_REGION);
      assertEquals(vpcRequest, objectFromResource("/vpc-create-res.json", VPCRequest.class));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "CreateVpc", ImmutableMap.of("RegionId", TEST_REGION));
   }

   public void testDeleteVPC() throws InterruptedException {
      server.enqueue(jsonResponse("/vpc-delete-res.json"));
      Request delete = api.vpcApi().delete(TEST_REGION, "vpc-123456789");
      assertEquals(delete, objectFromResource("/vpc-delete-res.json", Request.class));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "DeleteVpc", ImmutableMap.of("RegionId", TEST_REGION, "VpcId", "vpc-123456789"));
   }

}
