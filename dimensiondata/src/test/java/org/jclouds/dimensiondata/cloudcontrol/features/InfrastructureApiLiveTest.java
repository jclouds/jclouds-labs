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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.internal.BaseDimensionDataCloudControlApiLiveTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters.Builder.datacenterId;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "InfrastructureApiLiveTest", singleThreaded = true)
public class InfrastructureApiLiveTest extends BaseDimensionDataCloudControlApiLiveTest {

   @Test
   public void testListDatacenters() {
      FluentIterable<Datacenter> datacenters = getDatacenters();
      assertTrue(!datacenters.isEmpty());
      for (Datacenter datacenter : datacenters) {
         assertNotNull(datacenter);
      }
   }

   private FluentIterable<Datacenter> getDatacenters() {
      FluentIterable<Datacenter> datacenters = api().listDatacenters().concat();
      assertNotNull(datacenters);
      return datacenters;
   }

   @Test
   public void testListOperatingSystems() {
      Set<String> datacenterIds = new HashSet<String>();
      for (Datacenter dc : getDatacenters()) {
         datacenterIds.add(dc.id());
      }
      ImmutableList<OperatingSystem> operatingSystems = api().listOperatingSystems(datacenterId(datacenterIds))
            .toList();
      assertNotNull(operatingSystems);
      assertTrue(!operatingSystems.isEmpty());
      for (OperatingSystem operatingSystem : operatingSystems) {
         assertNotNull(operatingSystem);
      }
   }

   private InfrastructureApi api() {
      return api.getInfrastructureApi();
   }

}
