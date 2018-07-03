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
import org.jclouds.aliyun.ecs.domain.Region;
import org.jclouds.aliyun.ecs.domain.Zone;
import org.jclouds.aliyun.ecs.features.RegionAndZoneApi;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "RegionAndZoneApiLiveTest")
public class RegionAndZoneApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public void testListRegions() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().describeRegions(), new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.regionId());
         }
      }), "All regions must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some region to be returned");
   }

   public void testListZones() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().describeZones("eu-central-1"), new Predicate<Zone>() {
         @Override
         public boolean apply(Zone input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.zoneId());
         }
      }), "All zones must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some zone to be returned");
   }

   private RegionAndZoneApi api() {
      return api.regionAndZoneApi();
   }
}
