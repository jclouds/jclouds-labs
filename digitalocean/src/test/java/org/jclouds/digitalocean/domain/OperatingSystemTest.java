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
package org.jclouds.digitalocean.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link OperatingSystem} class.
 */
@Test(groups = "unit", testName = "OperatingSystemTest")
public class OperatingSystemTest {

   public void testParseStandard64bit() {
      OperatingSystem os = OperatingSystem.builder().from("Ubuntu 12.10 x64", "Ubuntu").build();

      assertEquals(os.getDistribution(), Distribution.UBUNTU);
      assertEquals(os.getVersion(), "12.10");
      assertEquals(os.getArch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseStandard() {
      OperatingSystem os = OperatingSystem.builder().from("Ubuntu 12.10 x32", "Ubuntu").build();

      assertEquals(os.getDistribution(), Distribution.UBUNTU);
      assertEquals(os.getVersion(), "12.10");
      assertEquals(os.getArch(), "x32");
      assertFalse(os.is64bit());

      os = OperatingSystem.builder().from("CentOS 6.5 x64", "CentOS").build();

      assertEquals(os.getDistribution(), Distribution.CENTOS);
      assertEquals(os.getVersion(), "6.5");
      assertEquals(os.getArch(), "x64");
      assertTrue(os.is64bit());

      os = OperatingSystem.builder().from("CentOS 6.5 x64", "Centos").build();

      assertEquals(os.getDistribution(), Distribution.CENTOS);
      assertEquals(os.getVersion(), "6.5");
      assertEquals(os.getArch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseNoArch() {
      OperatingSystem os = OperatingSystem.builder().from("Ubuntu 12.10", "Ubuntu").build();

      assertEquals(os.getDistribution(), Distribution.UBUNTU);
      assertEquals(os.getVersion(), "12.10");
      assertEquals(os.getArch(), "");
      assertFalse(os.is64bit());
   }

   public void testParseNoVersion() {
      OperatingSystem os = OperatingSystem.builder().from("Ubuntu x64", "Ubuntu").build();

      assertEquals(os.getDistribution(), Distribution.UBUNTU);
      assertEquals(os.getVersion(), "");
      assertEquals(os.getArch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseUnknownDistribution() {
      OperatingSystem os = OperatingSystem.builder().from("Ubuntu 12.04 x64", "Foo").build();

      assertEquals(os.getDistribution(), Distribution.UNRECOGNIZED);
      assertEquals(os.getVersion(), "12.04");
      assertEquals(os.getArch(), "x64");
      assertTrue(os.is64bit());
   }
}
