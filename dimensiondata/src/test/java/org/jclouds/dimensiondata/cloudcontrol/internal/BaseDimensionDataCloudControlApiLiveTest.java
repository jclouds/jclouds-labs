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
package org.jclouds.dimensiondata.cloudcontrol.internal;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApiMetadata;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.testng.annotations.Test;

@Test(groups = "live")
public class BaseDimensionDataCloudControlApiLiveTest extends BaseApiLiveTest<DimensionDataCloudControlApi> {

   protected static final String NETWORK_DOMAIN_ID = System
         .getProperty("networkDomainId", "690de302-bb80-49c6-b401-8c02bbefb945");
   protected static final String VLAN_ID = System.getProperty("vlanId", "6b25b02e-d3a2-4e69-8ca7-9bab605deebd");
   protected static final String IMAGE_ID = System.getProperty("imageId", "4c02126c-32fc-4b4c-9466-9824c1b5aa0f");
   protected static final String DATACENTER = System.getProperty("datacenter", "NW20-EPC-LAB04");

   public BaseDimensionDataCloudControlApiLiveTest() {
      provider = "dimensiondata-cloudcontrol";
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new DimensionDataCloudControlApiMetadata();
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

}
