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
import org.jclouds.aliyun.ecs.domain.Region;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.domain.Zone;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "RegionAndZoneApiMockTest", singleThreaded = true)
public class RegionAndZoneApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testListRegions() throws InterruptedException {
      server.enqueue(jsonResponse("/regions.json"));
      List<Region> regions = api.regionAndZoneApi().describeRegions();
      assertEquals(size(regions), 18);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeRegions");
   }

   public void testListRegionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      List<Region> regions = api.regionAndZoneApi().describeRegions();
      assertTrue(isEmpty(regions));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeRegions");
   }

   public void testListZones() throws InterruptedException {
      server.enqueue(jsonResponse("/zones.json"));
      List<Zone> zones = api.regionAndZoneApi().describeZones(Regions.EU_CENTRAL_1.getName());
      assertEquals(size(zones), 2);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeZones", ImmutableMap.of("RegionId", Regions.EU_CENTRAL_1.getName()));
   }

   public void testListZonesReturns404() throws InterruptedException {
      server.enqueue(response404());
      List<Zone> zones = api.regionAndZoneApi().describeZones(Regions.EU_CENTRAL_1.getName());
      assertTrue(isEmpty(zones));
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeZones", ImmutableMap.of("RegionId", Regions.EU_CENTRAL_1.getName()));
   }

}
