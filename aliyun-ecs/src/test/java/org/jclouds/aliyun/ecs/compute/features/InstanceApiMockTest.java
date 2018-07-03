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

import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiMockTest;
import org.jclouds.aliyun.ecs.domain.AvailableZone;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.InstanceStatus;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.aliyun.ecs.domain.options.CreateInstanceOptions;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "InstanceApiMockTest", singleThreaded = true)
public class InstanceApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testListInstances() throws InterruptedException {
      server.enqueue(jsonResponse("/instances-first.json"));
      server.enqueue(jsonResponse("/instances-last.json"));

      Iterable<Instance> instances = api.instanceApi().list(TEST_REGION).concat();
      assertEquals(size(instances), 20); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);
      assertSent(server, "GET", "DescribeInstances");
      assertSent(server, "GET", "DescribeInstances", 2);
   }

   public void testListInstancesReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<Instance> instances = api.instanceApi().list(TEST_REGION).concat();
      assertTrue(isEmpty(instances));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeInstances");
   }

   public void testListInstanceTypes() throws InterruptedException {
      server.enqueue(jsonResponse("/instanceTypes.json"));

      List<InstanceType> instanceTypes = api.instanceApi().listTypes();
      assertEquals(size(instanceTypes), 308);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeInstanceTypes");
   }

   public void testListInstanceTypesReturns404() throws InterruptedException {
      server.enqueue(response404());
      List<InstanceType> instanceTypes = api.instanceApi().listTypes();
      assertTrue(isEmpty(instanceTypes));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeInstanceTypes");
   }

   public void testListInstanceTypesByAvailableZone() throws InterruptedException {
      server.enqueue(jsonResponse("/availableZones.json"));

      List<AvailableZone> availableZones = api.instanceApi().listInstanceTypesByAvailableZone(TEST_REGION);
      assertEquals(size(availableZones), 2);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeAvailableResource");
   }

   public void testListInstanceTypesByAvailableZoneReturns404() throws InterruptedException {
      server.enqueue(response404());
      List<AvailableZone> availableZones = api.instanceApi().listInstanceTypesByAvailableZone(TEST_REGION);
      assertTrue(isEmpty(availableZones));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeAvailableResource");
   }

   public void testCreateInstance() throws InterruptedException {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
                      "{" +
                      "    \"RequestId\": \"04F0F334-1335-436C-A1D7-6C044FE73368\"," +
                      "    \"InstanceId\": \"i-instance1\"" +
                      "}"));
      api.instanceApi().create(TEST_REGION,
              "test",
              "SecurityGroupId",
              "Hostname",
              "InstanceType"
      );
      assertSent(server, "POST", "CreateInstance");
   }

   public void testCreateInstanceWithInstanceName() throws InterruptedException {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
                      "{" +
                      "    \"RequestId\": \"04F0F334-1335-436C-A1D7-6C044FE73368\"," +
                      "    \"InstanceId\": \"i-instance1\"" +
                      "}"));
      api.instanceApi().create(TEST_REGION,
              "test",
              "SecurityGroupId",
              "Hostname",
              "InstanceType",
              CreateInstanceOptions.Builder.instanceName("jclouds")
      );
      assertSent(server, "POST", "CreateInstance");
   }

   public void testAllocatePublicIpAddress() throws InterruptedException {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
                      "{" +
                      "    \"RequestId\": \"F2EF6A3B-E345-46B9-931E-0EA094818567\"," +
                      "    \"IpAddress\": \"10.1.149.159\"" +
                      "}"));
      api.instanceApi().create(TEST_REGION,
              "test",
              "SecurityGroupId",
              "Hostname",
              "InstanceType"
      );
      assertSent(server, "POST", "CreateInstance");
   }

   public void testGetStatus() throws Exception {
      server.enqueue(jsonResponse("/instanceStatus.json"));
      Iterable<InstanceStatus> instanceStatuses = api.instanceApi().listInstanceStatus("12345").concat();
      assertSent(server, "GET", "DescribeInstanceStatus");
      assertNotNull(instanceStatuses);
   }

   public void testGetStatusReturns404() throws InterruptedException {
      server.enqueue(response404());
      Iterable<InstanceStatus> instanceStatuses = api.instanceApi().listInstanceStatus("12345").concat();
      assertTrue(Iterables.isEmpty(instanceStatuses));
      assertSent(server, "GET", "DescribeInstanceStatus");
   }

   public void testDeleteInstance() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200).setBody(
              "{" +
              "    \"RequestId\": \"928E2273-5715-46B9-A730-238DC996A533\"" +
              "}"));
      api.instanceApi().delete("instanceId");
      assertSent(server, "POST", "DeleteInstance");
   }

   public void testDeleteInstanceReturns404() throws Exception {
      server.enqueue(response404());
      api.instanceApi().delete("instanceId");
      assertSent(server, "POST", "DeleteInstance");
   }

}
