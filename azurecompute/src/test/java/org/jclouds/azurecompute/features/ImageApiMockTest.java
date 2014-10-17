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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.azurecompute.domain.ImageParams;
import org.jclouds.azurecompute.domain.OSType;
import org.jclouds.azurecompute.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecompute.xml.ListImagesHandlerTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseAzureComputeApiMockTest {

   public void listWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/images.xml"));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         assertEquals(api.list(), ListImagesHandlerTest.expected());

         assertSent(server, "GET", "/services/images");
      } finally {
         server.shutdown();
      }
   }

   public void listWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         assertTrue(api.list().isEmpty());

         assertSent(server, "GET", "/services/images");
      } finally {
         server.shutdown();
      }
   }

   public void add() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         ImageParams params = ImageParams.builder().name("myimage").label("foo").os(OSType.LINUX)
               .mediaLink(URI.create("http://example.blob.core.windows.net/disks/mydisk.vhd")).build();

         assertEquals(api.add(params), "request-1");

         assertSent(server, "POST", "/services/images", "/imageparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void update() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         ImageParams params = ImageParams.builder().name("myimage").label("foo").os(OSType.LINUX)
               .mediaLink(URI.create("http://example.blob.core.windows.net/disks/mydisk.vhd")).build();

         assertEquals(api.update(params), "request-1");

         assertSent(server, "PUT", "/services/images/myimage", "/imageparams.xml");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         assertEquals(api.delete("myimage"), "request-1");

         assertSent(server, "DELETE", "/services/images/myimage");
      } finally {
         server.shutdown();
      }
   }

   public void deleteWhenNotFound() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         ImageApi api = api(server.getUrl("/")).getImageApi();

         assertNull(api.delete("myimage"));

         assertSent(server, "DELETE", "/services/images/myimage");
      } finally {
         server.shutdown();
      }
   }
}
