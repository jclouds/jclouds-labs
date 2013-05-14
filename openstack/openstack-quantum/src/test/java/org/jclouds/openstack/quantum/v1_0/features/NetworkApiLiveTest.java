/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.quantum.v1_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests NetworkApi
 *
 * @author Adam Lowe
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "NetworkApiLiveTest", singleThreaded = true)
public class NetworkApiLiveTest extends BaseQuantumApiLiveTest {

   public void testListNetworks() {
      for (String zoneId : api.getConfiguredZones()) {
         Set<? extends Reference> ids = api.getNetworkApiForZone(zoneId).listReferences().toSet();
         Set<? extends Network> networks = api.getNetworkApiForZone(zoneId).list().toSet();
         assertNotNull(ids);
         assertEquals(ids.size(), networks.size());
         for (Network network : networks) {
            assertNotNull(network.getName());
            assertTrue(ids.contains(Reference.builder().id(network.getId()).build()));
         }
      }
   }

   public void testCreateUpdateAndDeleteNetwork() {
      for (String zoneId : api.getConfiguredZones()) {
         NetworkApi netApi = api.getNetworkApiForZone(zoneId);
         Reference net = netApi.create("jclouds-test");
         assertNotNull(net);

         Network network = netApi.get(net.getId());
         NetworkDetails details = netApi.getDetails(net.getId());
         
         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-test");
         }
         
         assertTrue(details.getPorts().isEmpty());

         assertTrue(netApi.rename(net.getId(), "jclouds-live-test"));
         
         // Grab the updated metadata
         network = netApi.get(net.getId());
         details = netApi.getDetails(net.getId());

         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-live-test");
         }

         assertTrue(details.getPorts().isEmpty());

         Reference net2 = netApi.create("jclouds-test2");
         assertNotNull(net2);
        
         assertTrue(netApi.delete(net.getId()));
         assertTrue(netApi.delete(net2.getId()));
      }
   }   
}
