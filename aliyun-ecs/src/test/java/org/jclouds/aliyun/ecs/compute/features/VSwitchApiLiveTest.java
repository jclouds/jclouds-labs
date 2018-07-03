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
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.aliyun.ecs.domain.VSwitch;
import org.jclouds.aliyun.ecs.domain.VSwitchRequest;
import org.jclouds.aliyun.ecs.domain.options.CreateVSwitchOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVSwitchesOptions;
import org.jclouds.aliyun.ecs.features.VSwitchApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jclouds.aliyun.ecs.domain.options.PaginationOptions.Builder.pageSize;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "VSwitchApiLiveTest")
public class VSwitchApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public static final String VSWITCH_NAME = "jclouds-vswitch";
   public static final String DEFAULT_CIDR_BLOCK = "172.16.1.0/24";
   private String vpcId;
   private String vSwitchId;

   @BeforeClass
   public void setUp() {
      VPCRequest preRequisite = api.vpcApi().create(TEST_REGION);
      vpcId = preRequisite.getVpcId();
      VSwitchRequest vpcRequest = api().create(
              TEST_ZONE,
              DEFAULT_CIDR_BLOCK,
              vpcId,
              CreateVSwitchOptions.Builder.vSwitchName(VSWITCH_NAME));
      assertNotNull(vpcRequest.getRequestId());
      assertNotNull(vpcRequest.getVSwitchId());
      vSwitchId = vpcRequest.getVSwitchId();
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (vSwitchId != null) {
         assertNotNull(api().delete(TEST_REGION, vSwitchId));
      }
      if (vpcId != null) {
         assertNotNull(api.vpcApi().delete(TEST_REGION, vpcId));
      }
   }

   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(TEST_REGION).concat(), new Predicate<VSwitch>() {
         @Override
         public boolean apply(VSwitch input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All vSwitches must have at least the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some vSwitch to be returned");
   }

   public void testListWithOptions() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(TEST_REGION, ListVSwitchesOptions.Builder.vSwitchId(vSwitchId)
            .paginationOptions(pageSize(50)))
            .firstMatch(new Predicate<VSwitch>() {
               @Override
               public boolean apply(VSwitch input) {
                  found.incrementAndGet();
                  return !isNullOrEmpty(input.id());
               }
            }).isPresent(), "All vSwitches must have at least the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some vSwitch to be returned");
   }

   private VSwitchApi api() {
      return api.vSwitchApi();
   }
}
