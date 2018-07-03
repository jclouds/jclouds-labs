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
import org.jclouds.aliyun.ecs.domain.VPC;
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.aliyun.ecs.domain.options.CreateVPCOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVPCsOptions;
import org.jclouds.aliyun.ecs.features.VPCApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jclouds.aliyun.ecs.domain.options.PaginationOptions.Builder.pageSize;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "VPCApiLiveTest")
public class VPCApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public static final String VPC_NAME = "jclouds-vpc";

   private String vpcId;

   @BeforeClass
   public void setUp() {
      VPCRequest vpcRequest = api().create(TEST_REGION, CreateVPCOptions.Builder.vpcName(VPC_NAME));
      assertNotNull(vpcRequest.getRequestId());
      assertNotNull(vpcRequest.getVpcId());
      vpcId = vpcRequest.getVpcId();
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (vpcId != null) {
         assertNotNull(api().delete(TEST_REGION, vpcId));
      }
   }

   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(TEST_REGION).concat(), new Predicate<VPC>() {
         @Override
         public boolean apply(VPC input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All vpcs must have at least the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some vpc to be returned");
   }

   public void testListWithOptions() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(TEST_REGION, ListVPCsOptions.Builder.vpcId(vpcId)
                  .paginationOptions(pageSize(50)))
            .firstMatch(new Predicate<VPC>() {
               @Override
               public boolean apply(VPC input) {
                  found.incrementAndGet();
                  return !isNullOrEmpty(input.id());
               }
            }).isPresent(), "All vpcs must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some image to be returned");
   }

   private VPCApi api() {
      return api.vpcApi();
   }
}
