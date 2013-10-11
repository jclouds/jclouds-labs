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

import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test
public class StaticLargeObjectApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   public void replaceManifest() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().addHeader(HttpHeaders.ETAG, "\"abcd\""));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(
               api.staticLargeObjectApiInRegionForContainer("DFW", "myContainer").replaceManifest(
                     "myObject",
                     ImmutableList
                           .<Segment> builder()
                           .add(Segment.builder().path("/mycontainer/objseg1").etag("0228c7926b8b642dfb29554cd1f00963")
                                 .sizeBytes(1468006).build())
                           .add(Segment.builder().path("/mycontainer/pseudodir/seg-obj2")
                                 .etag("5bfc9ea51a00b790717eeb934fb77b9b").sizeBytes(1572864).build())
                           .add(Segment.builder().path("/other-container/seg-final")
                                 .etag("b9c3da507d2557c1ddc51f27c54bae51").sizeBytes(256).build()).build(),
                     ImmutableMap.of("MyFoo", "Bar")), "abcd");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=put HTTP/1.1");
         assertEquals(replaceRequest.getHeader("x-object-meta-myfoo"), "Bar");
         assertEquals(
               new String(replaceRequest.getBody()),
         "[{\"path\":\"/mycontainer/objseg1\",\"etag\":\"0228c7926b8b642dfb29554cd1f00963\",\"size_bytes\":1468006}," +
          "{\"path\":\"/mycontainer/pseudodir/seg-obj2\",\"etag\":\"5bfc9ea51a00b790717eeb934fb77b9b\",\"size_bytes\":1572864}," +
          "{\"path\":\"/other-container/seg-final\",\"etag\":\"b9c3da507d2557c1ddc51f27c54bae51\",\"size_bytes\":256}]");
      } finally {
         server.shutdown();
      }
   }

   public void delete() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.staticLargeObjectApiInRegionForContainer("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=delete HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void alreadyDeleted() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.staticLargeObjectApiInRegionForContainer("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=delete HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }
}
