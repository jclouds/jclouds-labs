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

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "OperatingSystemToOsFamilyTest")
public class OperatingSystemToOsFamilyTest {

   private OperatingSystemToOsFamily operatingSystemToOsFamily;

   @BeforeMethod
   public void setUp() throws Exception {
      operatingSystemToOsFamily = new OperatingSystemToOsFamily();
   }

   @Test
   public void apply_Centos() {
      assertEquals(OsFamily.CENTOS, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("CENTOS532").displayName("").family("").build()));
   }

   @Test
   public void apply_Suse() {
      assertEquals(OsFamily.SUSE, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("SUSE1032").displayName("").family("").build()));
   }

   @Test
   public void apply_Sles() {
      assertEquals(OsFamily.SUSE, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("SLES1164").displayName("").family("").build()));
   }

   @Test
   public void apply_Solaris() {
      assertEquals(OsFamily.SOLARIS, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("SOLARIS1032").displayName("").family("").build()));
   }

   @Test
   public void apply_Linux() {
      assertEquals(OsFamily.LINUX, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("OTHER24XLINUX32").displayName("").family("").build()));
   }

   @Test
   public void apply_RedHat() {
      assertEquals(OsFamily.RHEL, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("REDHAT632").displayName("").family("").build()));
   }

   @Test
   public void apply_Ubuntu() {
      assertEquals(OsFamily.UBUNTU, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("UBUNTUX64").displayName("").family("").build()));
   }

   @Test
   public void apply_Windows() {
      assertEquals(OsFamily.WINDOWS, operatingSystemToOsFamily
            .apply(OperatingSystem.builder().id("WIN2003S64").displayName("").family("WINDOWS").build()));
   }

   @Test
   public void apply_Unrecognized() {
      assertEquals(OsFamily.UNRECOGNIZED,
            operatingSystemToOsFamily.apply(OperatingSystem.builder().id("XXX").displayName("").family("").build()));
   }

}
