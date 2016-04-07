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
package org.apache.jclouds.profitbricks.rest.features;

import com.google.common.base.Predicate;
import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.ids.ServerRef;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

@Test(groups = "live", testName = "NicApiLiveTest")
public class NicApiLiveTest extends BaseProfitBricksLiveTest {
   
   private DataCenter dataCenter;
   private Server testServer;
   private Nic testNic;
  
   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
      assertDataCenterAvailable(dataCenter);
            
      testServer = api.serverApi().createServer(
         Server.Request.creatingBuilder()
            .dataCenterId(dataCenter.id())
            .name("jclouds-node")
            .cores(1)
            .ram(1024)
            .build());
      
      assertNodeAvailable(ServerRef.create(dataCenter.id(), testServer.id()));
   }
   
   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null)
         deleteDataCenter(dataCenter.id());
   }
     
   @Test
   public void testCreateNic() {
      assertNotNull(dataCenter);
            
      testNic = nicApi().create(
              Nic.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .serverId(testServer.id())
              .name("jclouds-nic")
              .lan(1)
              .build());

      assertNotNull(testNic);
      assertEquals(testNic.properties().name(), "jclouds-nic");
      assertNicAvailable(testNic);
   }
   

   @Test(dependsOnMethods = "testCreateNic")
   public void testGetNic() {
      Nic nic = nicApi().get(dataCenter.id(), testServer.id(), testNic.id());

      assertNotNull(nic);
      assertEquals(nic.id(), testNic.id());
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testList() {
      List<Nic> nics = nicApi().list(dataCenter.id(), testServer.id());

      assertNotNull(nics);
      assertFalse(nics.isEmpty());
      assertEquals(nics.size(), 1);
   }
   
   @Test(dependsOnMethods = "testGetNic")
   public void testUpdateNic() {
      assertDataCenterAvailable(dataCenter);
      
      nicApi().update(
              Nic.Request.updatingBuilder()
              .dataCenterId(testNic.dataCenterId())
              .serverId(testServer.id())
              .id(testNic.id())
              .name("apache-nic")
              .build());

      assertNicAvailable(testNic);
      
      Nic nic = nicApi().get(dataCenter.id(), testServer.id(), testNic.id());
      
      assertEquals(nic.properties().name(), "apache-nic");
   }
   

   @Test(dependsOnMethods = "testUpdateNic", alwaysRun = true)
   public void testDeleteNic() {
      nicApi().delete(testNic.dataCenterId(), testServer.id(), testNic.id());
      assertNicRemoved(testNic);
   } 
      
   private void assertNicRemoved(Nic nic) {
      assertPredicate(new Predicate<Nic>() {
         @Override
         public boolean apply(Nic testNic) {
            return nicApi().get(testNic.dataCenterId(), testNic.serverId(), testNic.id()) == null;
         }
      }, nic);
   }
     
   private NicApi nicApi() {
      return api.nicApi();
   }
   
}
