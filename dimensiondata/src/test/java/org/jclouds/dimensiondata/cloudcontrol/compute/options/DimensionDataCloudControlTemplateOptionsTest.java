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
package org.jclouds.dimensiondata.cloudcontrol.compute.options;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "DimensionDataCloudControlTemplateOptionsTest")
public class DimensionDataCloudControlTemplateOptionsTest {

   private DimensionDataCloudControlTemplateOptions templateOptions;
   private String networkDomainName;
   private String defaultPrivateIPv4BaseAddress;
   private int defaultPrivateIPv4PrefixSize;

   @BeforeMethod
   public void setUp() throws Exception {
      networkDomainName = "networkDomainName";
      defaultPrivateIPv4BaseAddress = "defaultPrivateIPv4BaseAddress";
      defaultPrivateIPv4PrefixSize = 100;
      templateOptions = DimensionDataCloudControlTemplateOptions.Builder.networkDomainName(networkDomainName)
            .defaultPrivateIPv4BaseAddress(defaultPrivateIPv4BaseAddress)
            .defaultPrivateIPv4PrefixSize(defaultPrivateIPv4PrefixSize);
   }

   @Test
   public void testBuilder() throws Exception {
      assertEquals(networkDomainName, templateOptions.getNetworkDomainName());
      assertEquals(defaultPrivateIPv4BaseAddress, templateOptions.getDefaultPrivateIPv4BaseAddress());
      assertEquals(defaultPrivateIPv4PrefixSize, templateOptions.getDefaultPrivateIPv4PrefixSize().intValue());
   }

   @Test
   public void testEquals() throws Exception {
      assertTrue(templateOptions.equals(
            DimensionDataCloudControlTemplateOptions.Builder.networkDomainName(networkDomainName)
                  .defaultPrivateIPv4BaseAddress(defaultPrivateIPv4BaseAddress)
                  .defaultPrivateIPv4PrefixSize(defaultPrivateIPv4PrefixSize)));
   }
}
