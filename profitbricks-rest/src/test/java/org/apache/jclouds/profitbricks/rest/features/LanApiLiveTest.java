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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.apache.jclouds.profitbricks.rest.domain.DataCenter;
import org.apache.jclouds.profitbricks.rest.domain.Lan;
import org.apache.jclouds.profitbricks.rest.domain.State;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "LanApiLiveTest")
public class LanApiLiveTest extends BaseProfitBricksLiveTest {
   
   private DataCenter dataCenter;
   private Lan testLan;
  
   @BeforeClass
   public void setupTest() {
      dataCenter = createDataCenter();
   }
   
   @AfterClass(alwaysRun = true)
   public void teardownTest() {
      if (dataCenter != null)
         deleteDataCenter(dataCenter.id());
   }
     
   @Test
   public void testCreateLan() {
      assertNotNull(dataCenter);
            
      testLan = lanApi().create(
              Lan.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-lan")
              .build());

      assertRequestCompleted(testLan);
      assertNotNull(testLan);
      assertEquals(testLan.properties().name(), "jclouds-lan");
      assertLanAvailable(testLan);
   }
   

   @Test(dependsOnMethods = "testCreateLan")
   public void testGetLan() {
      Lan lan = lanApi().get(dataCenter.id(), testLan.id());

      assertNotNull(lan);
      assertEquals(lan.id(), testLan.id());
   }

   @Test(dependsOnMethods = "testCreateLan")
   public void testList() {
      List<Lan> lans = lanApi().list(dataCenter.id());

      assertNotNull(lans);
      assertFalse(lans.isEmpty());
      assertTrue(Iterables.any(lans, new Predicate<Lan>() {
         @Override public boolean apply(Lan input) {
            return input.id().equals(testLan.id());
         }
      }));
   }
   
   @Test(dependsOnMethods = "testCreateLan")
   public void testUpdateLan() {
      assertDataCenterAvailable(dataCenter);
      
      Lan updated = api.lanApi().update(
              Lan.Request.updatingBuilder()
              .dataCenterId(testLan.dataCenterId())
              .id(testLan.id())
              .isPublic(false)
              .build());

      assertRequestCompleted(updated);
      assertLanAvailable(updated);
      
      Lan lan = lanApi().get(dataCenter.id(), testLan.id());
      
      assertEquals(lan.properties().isPublic(), false);
   }
   
   @Test(dependsOnMethods = "testUpdateLan")
   public void testDeleteLan() {
      URI uri = lanApi().delete(testLan.dataCenterId(), testLan.id());
      assertRequestCompleted(uri);
      assertLanRemoved(testLan);
   }
   
   private void assertLanAvailable(Lan lan) {
      assertPredicate(new Predicate<Lan>() {
         @Override
         public boolean apply(Lan testLan) {
            Lan lan = lanApi().get(testLan.dataCenterId(), testLan.id());
            
            if (lan == null || lan.metadata() == null)
               return false;
            
            return lan.metadata().state() == State.AVAILABLE;
         }
      }, lan);
   }
   
   private void assertLanRemoved(Lan lan) {
      assertPredicate(new Predicate<Lan>() {
         @Override
         public boolean apply(Lan testLan) {
            return lanApi().get(testLan.dataCenterId(), testLan.id()) == null;
         }
      }, lan);
   }
     
   private LanApi lanApi() {
      return api.lanApi();
   }
   
}
