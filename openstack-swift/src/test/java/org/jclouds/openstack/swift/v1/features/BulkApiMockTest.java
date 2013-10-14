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
package org.jclouds.openstack.swift.v1.features;

import static org.jclouds.io.Payloads.newByteArrayPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.ExtractArchiveResponse;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

// TODO: cannot yet test bulk delete offline
@Test
public class BulkApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   public void extractArchive() throws Exception {
      GenericArchive files = ShrinkWrap.create(GenericArchive.class, "files.tar.gz");
      StringAsset content = new StringAsset("foo");
      for (int i = 0; i < 10; i++) {
         files.add(content, "/file" + i);
      }
      byte[] tarGz = ByteStreams.toByteArray(files.as(TarGzExporter.class).exportAsInputStream());

      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(new MockResponse().setResponseCode(201).setBody("{\"Number Files Created\": 10, \"Errors\": []}"));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ExtractArchiveResponse response = api.bulkApiInRegion("DFW").extractArchive("myContainer",
               newByteArrayPayload(tarGz), "tar.gz");
         assertEquals(response.created(), 10);
         assertTrue(response.errors().isEmpty());

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest extractRequest = server.takeRequest();
         assertEquals(extractRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer?extract-archive=tar.gz HTTP/1.1");
         assertEquals(extractRequest.getBody(), tarGz);
      } finally {
         server.shutdown();
      }
   }
}
