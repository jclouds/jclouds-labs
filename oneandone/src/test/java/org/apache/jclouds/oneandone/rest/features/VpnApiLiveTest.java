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
package org.apache.jclouds.oneandone.rest.features;

import java.util.List;
import java.util.zip.ZipInputStream;
import org.apache.jclouds.oneandone.rest.domain.Vpn;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "VpnApiLiveTest")
public class VpnApiLiveTest extends BaseOneAndOneLiveTest {
   
   private Vpn currentVpn;
   private List<Vpn> vpns;
   
   private VpnApi vpnApi() {
      return api.vpnApi();
   }
   
   @BeforeClass
   public void setupTest() {
      currentVpn = vpnApi().create(Vpn.CreateVpn.create("jclouds vpn", "description", null));
   }
   
   @Test
   public void testList() {
      vpns = vpnApi().list();
      
      assertNotNull(vpns);
      assertFalse(vpns.isEmpty());
      Assert.assertTrue(vpns.size() > 0);
   }
   
   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jclouds", null);
      List<Vpn> resultWithQuery = vpnApi().list(options);
      
      assertNotNull(resultWithQuery);
      assertFalse(resultWithQuery.isEmpty());
      Assert.assertTrue(resultWithQuery.size() > 0);
   }
   
   @Test
   public void testGet() {
      Vpn result = vpnApi().get(currentVpn.id());
      
      assertNotNull(result);
      assertEquals(result.id(), currentVpn.id());
   }
   
   @Test
   public void testGetConfiguration() throws InterruptedException {
      assertVPNAvailable(currentVpn);
      ZipInputStream result = vpnApi().getConfiguration(currentVpn.id());
      assertNotNull(result);
   }
   
   @Test(dependsOnMethods = "testGetConfiguration")
   public void testUpdate() throws InterruptedException {
      String updatedName = "updatejclouds VPN";
      
      Vpn updateResult = vpnApi().update(currentVpn.id(), Vpn.UpdateVpn.create(updatedName, "desc"));
      
      assertNotNull(updateResult);
      assertEquals(updateResult.name(), updatedName);
      
   }
   
   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      vpnApi().delete(currentVpn.id());
   }
}
