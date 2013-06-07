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

import static com.google.common.base.Preconditions.checkArgument;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jclouds.openstack.reddwarf.v1.domain.Instance;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiLiveTest;
import org.jclouds.openstack.reddwarf.v1.predicates.InstancePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseRedDwarfApiLiveTest {

    private static Map<String,List<Instance>> created = Maps.newHashMap();
    
    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setup() {
        super.setup();
        for (String zone : api.getConfiguredZones()) {
            List<Instance> zoneList = Lists.newArrayList();
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);
            zoneList.add(instanceApi.create("1", 1, "first_instance_testing"));
            Instance second = instanceApi.create("1", 1, "second_instance_testing");
            InstancePredicates.awaitAvailable(instanceApi).apply(second);
            instanceApi.enableRoot(second.getId());
            zoneList.add(second);            
            created.put(zone, zoneList);
        }
    }
    
    @Override
    @AfterClass(groups = { "integration", "live" })
    public void tearDown(){
        for (String zone : api.getConfiguredZones()) {
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);
            for(Instance instance : created.get(zone)){
                if( !instanceApi.delete(instance.getId() ) )
                    throw new RuntimeException("Could not delete a database instance after tests!");
            }
        }
        super.tearDown();
    }

    private void checkInstance(Instance instance) {
        assertNotNull(instance.getId(), "Id cannot be null for " + instance);
        checkArgument(instance.getSize() > 0, "Size must not be 0");
    }

    @Test
    public void testListInstances() {
        for (String zone : api.getConfiguredZones()) {
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);
            FluentIterable<Instance> response = instanceApi.list(); 
            assertTrue(response.size() > 0 );
            for (Instance instance : response) {
                checkInstance(instance);
            }  
        }   
    }    

    @Test
    public void testGetInstance() {
        for (String zone : api.getConfiguredZones()) {
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);           
            for (Instance instance : instanceApi.list()) {
                Instance instanceFromGet = instanceApi.get(instance.getId());
                assertNotNull(instanceFromGet.getHostname());
                assertNull(instance.getHostname());
                assertEquals(instanceFromGet.getId(), instance.getId());
                assertEquals(instanceFromGet.getName(), instance.getName());
                assertEquals(instanceFromGet.getStatus(), instance.getStatus());
                assertEquals(instanceFromGet.getFlavor(), instance.getFlavor());
                assertEquals(instanceFromGet.getSize(), instance.getSize());
                assertEquals(instanceFromGet.getLinks(), instance.getLinks());
            }
        }
    }

    @Test
    public void testGetInstanceWhenNotFound() {
        for (String zone : api.getConfiguredZones()) {
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);
            assertNull(instanceApi.get("9999"));
        }
    }   
    
    @Test
    public void testGetRootStatus() {
        for (String zone : api.getConfiguredZones()) {
            InstanceApi instanceApi = api.getInstanceApiForZone(zone);
            Iterator<Instance> iterator = instanceApi.list().iterator();
            Instance first, second;
            do{
               first = iterator.next(); 
            } while(!first.getName().contains("instance_testing"));
            do{
               second = iterator.next(); 
            } while(!second.getName().contains("instance_testing"));
            assertTrue(instanceApi.isRooted(first.getId()) || instanceApi.isRooted(second.getId()));
        }
    }
}
