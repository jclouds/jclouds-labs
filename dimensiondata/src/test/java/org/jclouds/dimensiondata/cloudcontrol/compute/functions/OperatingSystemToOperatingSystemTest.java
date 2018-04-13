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
package org.jclouds.dimensiondata.cloudcontrol.compute.functions;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(testName = "OperatingSystemToOperatingSystemTest")
public class OperatingSystemToOperatingSystemTest {

   private OperatingSystemToOsFamily operatingSystemToOsFamily;

   public void testApply(){

      String id = "Windows10x64";
      String name = "testWindowsOS";
      String family = "Windows";

      org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem operatingSystem = org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem.builder().id(id).displayName(name).family(family).build();

      operatingSystemToOsFamily = EasyMock.createNiceMock(OperatingSystemToOsFamily.class);
      expect(operatingSystemToOsFamily.apply(operatingSystem)).andReturn(OsFamily.WINDOWS);

      EasyMock.replay(operatingSystemToOsFamily);

      OperatingSystem result = new OperatingSystemToOperatingSystem(operatingSystemToOsFamily).apply(operatingSystem);

      assertNotNull(result);
      assertEquals(result.getName(), operatingSystem.displayName());
      assertEquals(result.getFamily(), OsFamily.WINDOWS);
   }
}
