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
package org.jclouds.openstack.reddwarf.v1.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.jclouds.openstack.reddwarf.v1.domain.Instance;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiLiveTest;
import org.jclouds.openstack.reddwarf.v1.predicates.InstancePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "DatabaseApiLiveTest", singleThreaded = true)
public class DatabaseApiLiveTest extends BaseRedDwarfApiLiveTest {

   // zone to instance
   private static Map<String,List<Instance>> instancesToDelete = Maps.newHashMap();
   // not deleting databases. they will be deleted when instances are deleted

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      for (String zone : api.getConfiguredZones()) {
         // create instances
         List<Instance> instanceList = Lists.newArrayList();
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         Instance first = instanceApi.create("1", 1, "first_database_testing_" + zone);
         Instance second = instanceApi.create("1", 1, "second_database_testing_" + zone);
         instanceList.add(first);
         instanceList.add(second);
         InstancePredicates.awaitAvailable(instanceApi).apply(first);
         InstancePredicates.awaitAvailable(instanceApi).apply(second);        
         instancesToDelete.put(zone, instanceList);
         
         DatabaseApi databaseApiFirst = api.getDatabaseApiForInstanceInZone(first.getId(), zone);
         DatabaseApi databaseApiSecond = api.getDatabaseApiForInstanceInZone(second.getId(), zone);         
         databaseApiFirst.create("livetest_db1");
         databaseApiFirst.create("livetest_db2");
         databaseApiSecond.create("livetest_db3");
      }
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDown(){
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         for(Instance instance : instancesToDelete.get(zone)){
            if( !instanceApi.delete(instance.getId() ) )
               throw new RuntimeException("Could not delete a database instance after tests!");
         }
      }
      super.tearDown();
   }

   @Test
   public void testListDatabases() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instanceApi.list() ) {
            DatabaseApi databaseApi = api.getDatabaseApiForInstanceInZone(instance.getId(), zone);
            if(!instance.getName().contains("database_testing"))continue;
            assertTrue(databaseApi.list().size() >=1);
            for(String database : databaseApi.list()){
               assertNotNull(database);      
            }
         }  
      }   
   }
   
   @Test
   public void testDeleteDatabases() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instanceApi.list() ) {
            DatabaseApi databaseApi = api.getDatabaseApiForInstanceInZone(instance.getId(), zone);
            if(!instance.getName().contains("database_testing"))continue;
            assertTrue(databaseApi.list().size() >=1);
            for(String database : databaseApi.list()){
               assertNotNull(database);
               assertTrue(database.equals("livetest_db1") || database.equals("livetest_db2") || database.equals("livetest_db3") );
               assertEquals(instanceApi.get(instance.getId()).getStatus(), Instance.Status.ACTIVE);
               assertTrue(databaseApi.delete(database));
               assertEquals(instanceApi.get(instance.getId()).getStatus(), Instance.Status.ACTIVE);
               assertTrue(databaseApi.create(database));
               assertEquals(instanceApi.get(instance.getId()).getStatus(), Instance.Status.ACTIVE);
            }
         }  
      }   
   }
}
