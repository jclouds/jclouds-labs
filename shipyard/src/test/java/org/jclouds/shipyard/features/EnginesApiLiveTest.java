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

import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import org.jclouds.http.HttpResponseException;
import org.jclouds.shipyard.domain.engines.AddEngine;
import org.jclouds.shipyard.domain.engines.EngineInfo;
import org.jclouds.shipyard.domain.engines.EngineSettingsInfo;
import org.jclouds.shipyard.internal.BaseShipyardApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

@Test(groups = "live", testName = "EnginesApiLiveTest", singleThreaded = true)
public class EnginesApiLiveTest extends BaseShipyardApiLiveTest {

   protected static final String engineName = "jclouds-shipard-live-test";
   protected String engineID = null;
   
   @BeforeClass
   protected void init() throws Exception {
      String dockerEndpoint = System.getProperty("test.shipyard.docker.endpoint");
      EngineSettingsInfo info = EngineSettingsInfo.create(engineName, dockerEndpoint, 1, 1024, Lists.newArrayList(engineName));
      AddEngine additionalEngine = AddEngine.create("local", "", "", "", info);
      api().addEngine(additionalEngine);
   }
   
   @AfterClass (alwaysRun = true)
   protected void tearDown() {
      assertNotNull(engineID, "Expected engineID to be set but was not");
      api().removeEngine(engineID);
   }
   
   public void testGetAllEngines() throws Exception {
     List<EngineInfo> engines = api().listEngines();
     assertTrue(engines.size() >= 1, "Shipyard did not contain at least 1 Engine which was expected");
     boolean engineFound = false;
     for (EngineInfo engine : api().listEngines()) {
        if (engine.engine().id().equals(engineName)) {
           engineID = engine.id();
           engineFound = true;
        }
     }
     assertTrue(engineFound, "Expected but could not find Engine amongst " + engines.size() + " found");
   }
   
   @Test (dependsOnMethods = "testGetAllEngines")
   public void testGetEngine() throws Exception {
      assertNotNull(engineID, "Expected engineID to be set but was not");
      EngineInfo engine = api().getEngine(engineID);
      assertTrue(engine.engine().id().equals(engineName), "Expected Engine name " + engineName + " but found " + engine.engine().id());
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testAddNonExistentEngine() throws Exception {
      EngineSettingsInfo info = EngineSettingsInfo.create("local", "http://www.test-jclouds-shipyard:9999", 1, 1024, Lists.newArrayList("default"));
      AddEngine additionalEngine = AddEngine.create("local", "", "", "", info);
      api().addEngine(additionalEngine);
   }
   
   @Test (expectedExceptions = HttpResponseException.class)
   public void testRemoveNonExistentEngine() throws Exception {
      api().removeEngine("1234567890-shipyard-jclouds");
   }
   
   private EnginesApi api() {
      return api.enginesApi();
   }
}
