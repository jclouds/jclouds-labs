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
package org.jclouds.shipyard.features;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.http.HttpResponseException;
import org.jclouds.shipyard.domain.containers.ContainerInfo;
import org.jclouds.shipyard.domain.containers.DeployContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;


@Test(groups = "live", testName = "ContainersApiLiveTest", singleThreaded = true)
public class ContainersApiLiveTest extends EnginesApiLiveTest {
  
   private String containerName = "shipyard-jclouds-container-test";
   private String containerID = null;
   
   @BeforeClass
   protected void init() throws Exception {
      super.init();
      super.testGetAllEngines();
   }
   
   @AfterClass (alwaysRun = true)
   protected void tearDown() {
      super.tearDown();
   }
   
   public void testDeployContainer() throws Exception {

      DeployContainer deployContainer = DeployContainer.create("ubuntu:14.04.1", 
            containerName, 
            1, 
            512, 
            null,
            null, 
            null, 
            Lists.newArrayList(EnginesApiLiveTest.engineName), 
            Lists.newArrayList("/bin/bash"), 
            ImmutableMap.of("shipyard-jclouds", "test"), 
            null, 
            null, 
            null);
      
      List<ContainerInfo> container = api().deployContainer(deployContainer);
      assertNotNull(container, "Expected valid container but returned NULL");
      assertTrue(container.size() == 1, "Expected exactly 1 container removed and found " + container.size());
      assertTrue(container.get(0).name().endsWith(containerName), 
               "Expected name does not match actual name: requested=" + containerName + ", actual=" + container.get(0).name());
      assertTrue(container.get(0).engine().labels().contains(EnginesApiLiveTest.engineName), 
               "Expected label was not found in container: expected=" + EnginesApiLiveTest.engineName + ", found=" + container.get(0).engine().labels());
      containerID = container.get(0).id();
   }
   
   @Test (dependsOnMethods = "testDeployContainer")
   public void testDeployAlreadyExistentContainer() throws Exception {

      DeployContainer deployContainer = DeployContainer.create("ubuntu:14.04.1", 
            containerName, 
            1, 
            512, 
            null,
            null, 
            null, 
            Lists.newArrayList(EnginesApiLiveTest.engineName), 
            Lists.newArrayList("/bin/bash"), 
            ImmutableMap.of("shipyard-jclouds", "test"), 
            null, 
            null, 
            null);
      
      List<ContainerInfo> container = api().deployContainer(deployContainer);
      assertNull(container);
   }
   
   public void testDeployNonExistentContainer() throws Exception {

      DeployContainer deployContainer = DeployContainer.create("jclouds-shipyard-test:99.99.99", 
            containerName, 
            1, 
            512, 
            null,
            null, 
            null, 
            Lists.newArrayList(EnginesApiLiveTest.engineName), 
            Lists.newArrayList("/bin/bash"), 
            ImmutableMap.of("shipyard-jclouds", "test"), 
            null, 
            null, 
            null);
      
      List<ContainerInfo> container = api().deployContainer(deployContainer);
      assertNull(container);
   }
   
   @Test (dependsOnMethods = "testDeployAlreadyExistentContainer")
   public void testStopContainer() throws Exception {
      api().stopContainer(containerID);
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testStopNonExistentContainer() throws Exception {
      api().stopContainer("aaabbbccc111222333");
   }
   
   @Test (dependsOnMethods = "testStopContainer")
   public void testRestartContainer() throws Exception {
      api().restartContainer(containerID);
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testRestartNonExistentContainer() throws Exception {
      api().restartContainer("aaabbbccc111222333");
   }
   
   @Test (dependsOnMethods = "testRestartContainer")
   public void testDeleteContainer() throws Exception {
      api().deleteContainer(containerID);
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testDeleteNonExistentContainer() throws Exception {
      api().deleteContainer("aaabbbccc111222333");
   }
   
   private ContainersApi api() {
      return api.containersApi();
   }
}
