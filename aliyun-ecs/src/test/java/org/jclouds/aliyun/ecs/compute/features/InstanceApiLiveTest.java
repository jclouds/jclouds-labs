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
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Uninterruptibles;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiLiveTest;
import org.jclouds.aliyun.ecs.domain.AvailableZone;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.InstanceRequest;
import org.jclouds.aliyun.ecs.domain.InstanceStatus;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.VPC;
import org.jclouds.aliyun.ecs.domain.VPCRequest;
import org.jclouds.aliyun.ecs.domain.VSwitch;
import org.jclouds.aliyun.ecs.domain.VSwitchRequest;
import org.jclouds.aliyun.ecs.domain.options.CreateInstanceOptions;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.domain.options.ListInstancesOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVPCsOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVSwitchesOptions;
import org.jclouds.aliyun.ecs.domain.regionscoped.RegionAndId;
import org.jclouds.aliyun.ecs.features.InstanceApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jclouds.aliyun.ecs.compute.strategy.CreateResourcesThenCreateNodes.DEFAULT_CIDR_BLOCK;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   private String imageId = "ubuntu_16_0402_32_20G_alibase_20180409.vhd";
   private String hostname = "jclouds-test";
   private String instanceType = "ecs.t5-lc2m1.nano";
   private String vpcId;
   private String vSwitchId;
   private String securityGroupId;
   private String instanceId;
   private String slashEncodedInstanceId;

   @BeforeClass
   public void setUp() {
      VPCRequest vpcRequest = api.vpcApi().create(TEST_REGION);
      vpcId = vpcRequest.getVpcId();
      VPC vpc = api.vpcApi().list(TEST_REGION, ListVPCsOptions.Builder.vpcId(vpcId)).firstMatch(Predicates.notNull()).orNull();
      while (vpc == null || vpc.status() == VPC.Status.PENDING) {
         vpc = api.vpcApi().list(TEST_REGION, ListVPCsOptions.Builder.vpcId(vpcId)).firstMatch(Predicates.notNull()).orNull();
         Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
      }

      VSwitchRequest vSwitchRequest = api.vSwitchApi().create(TEST_ZONE, DEFAULT_CIDR_BLOCK, vpcId);
      vSwitchId = vSwitchRequest.getVSwitchId();
      VSwitch vSwitch = api.vSwitchApi().list(TEST_REGION, ListVSwitchesOptions.Builder.vSwitchId(vSwitchId)).firstMatch(Predicates.notNull()).orNull();
      while (vSwitch == null || vSwitch.status() == VSwitch.Status.PENDING) {
         vSwitch = api.vSwitchApi().list(TEST_REGION, ListVSwitchesOptions.Builder.vSwitchId(vSwitchId)).firstMatch(Predicates.notNull()).orNull();
         Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
      }

      SecurityGroupRequest request = api.securityGroupApi().create(TEST_REGION,
              CreateSecurityGroupOptions.Builder
                      .securityGroupName(InstanceApiLiveTest.class.getSimpleName())
              .vpcId(vpcId)
      );
      securityGroupId = request.getSecurityGroupId();
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (instanceId != null) {
         api().delete(instanceId);
      }

      if (securityGroupId != null) {
         api.securityGroupApi().delete(TEST_REGION, securityGroupId);
      }

      if (vSwitchId != null) {
         api.vSwitchApi().delete(TEST_REGION, vSwitchId);
      }

      if (vpcId != null) {
         api.vpcApi().delete(TEST_REGION, vpcId);
      }
   }

   public void testListInstanceTypeByAvailableZone() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().listInstanceTypesByAvailableZone(TEST_REGION), new Predicate<AvailableZone>() {
         @Override
         public boolean apply(AvailableZone input) {
            found.incrementAndGet();
            return !input.availableResources().isEmpty();
         }
      }), "All available zones must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some instance type to be returned");
   }

   public void testListInstanceType() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().listTypes(), new Predicate<InstanceType>() {
         @Override
         public boolean apply(InstanceType input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All instance types must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some instance type to be returned");
   }

   @Test(groups = "live", dependsOnMethods = "testListInstanceType")
   public void testListInstance() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(TEST_REGION).concat(), new Predicate<Instance>() {
         @Override
         public boolean apply(Instance input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All instances must have the 'id' field populated");
   }

   @Test(groups = "live")
   public void testCreate() {
      InstanceRequest instanceRequest = api().create(TEST_REGION, imageId, securityGroupId, hostname, instanceType,
            CreateInstanceOptions.Builder.vSwitchId(vSwitchId));
      instanceId = instanceRequest.getInstanceId();
      slashEncodedInstanceId = RegionAndId.slashEncodeRegionAndId(TEST_REGION, instanceId);
      assertNotNull(instanceId, "Instance id must not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testGet() {
      Instance instance = Iterables.getOnlyElement(api().list(TEST_REGION,
              ListInstancesOptions.Builder.instanceIds(instanceId)));
      assertNotNull(instance.id(), "Instance must not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListInstanceStatus() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().listInstanceStatus(TEST_REGION).concat(), new Predicate<InstanceStatus>() {
         @Override
         public boolean apply(InstanceStatus input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.instanceId());
         }
      }), "All instance status must have the 'instance id' field populated");
   }

   @Test(groups = "live", dependsOnMethods = "testGet")
   public void testStartInstance() {
      api().powerOn(instanceId);
      assertTrue(instanceRunningPredicate.apply(slashEncodedInstanceId));
   }

   @Test(groups = "live", dependsOnMethods = "testStartInstance")
   public void testStopInstance() {
      api().powerOff(instanceId);
      assertTrue(instanceSuspendedPredicate.apply(slashEncodedInstanceId));
   }

   private InstanceApi api() {
      return api.instanceApi();
   }
}
