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
package org.jclouds.azurecompute.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListDisksHandlerTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "DiskApiMockTest")
public class DiskApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/disks.xml"));

      try {
         DiskApi api = api(server.getUrl("/")).getDiskApi();

         assertEquals(api.list(), ListDisksHandlerTest.expected());

         assertSent(server, "GET", "/services/disks");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         DiskApi api = api(server.getUrl("/")).getDiskApi();

         assertEquals(api.delete("my-disk"), "request-1");

         assertSent(server, "DELETE", "/services/disks/my-disk");
      } finally {
         server.shutdown();
      }
   }

}
