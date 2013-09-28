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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * @author Adrian Cole
 */
@Test
public class ContainerApiMockTest extends BaseSwiftMockTest {

   String containerList = "" //
         + "[\n" //
         + "  {\"name\":\"test_container_1\", \"count\":2, \"bytes\":78},\n" //
         + "  {\"name\":\"test_container_2\", \"count\":1, \"bytes\":17}\n" //
         + "]";

   public void listFirstPage() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setBody(containerList));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         ImmutableList<Container> containers = api.containerApiInRegion("DFW").listFirstPage().toList();
         assertEquals(containers, ImmutableList.of(//
               Container.builder() //
                     .name("test_container_1") //
                     .objectCount(2) //
                     .bytesUsed(78).build(), //
               Container.builder() //
                     .name("test_container_2") //
                     .objectCount(1) //
                     .bytesUsed(17).build()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/?format=json HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void listAt() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setBody(containerList));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         ImmutableList<Container> containers = api.containerApiInRegion("DFW").listAt("test").toList();
         assertEquals(containers, ImmutableList.of(//
               Container.builder() //
                     .name("test_container_1") //
                     .objectCount(2) //
                     .bytesUsed(78).build(), //
               Container.builder() //
                     .name("test_container_2") //
                     .objectCount(1) //
                     .bytesUsed(17).build()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/?format=json&marker=test HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void createIfAbsent() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(201));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.containerApiInRegion("DFW").createIfAbsent("myContainer", new CreateContainerOptions()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest createRequest = server.takeRequest();
         assertEquals(createRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void createPublicRead() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(201));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.containerApiInRegion("DFW").createIfAbsent("myContainer", new CreateContainerOptions().publicRead()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest createRequest = server.takeRequest();
         assertEquals(createRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         assertEquals(createRequest.getHeader("x-container-read"), ".r:*,.rlistings");
      } finally {
         server.shutdown();
      }
   }

   public void alreadyCreated() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(202));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertFalse(api.containerApiInRegion("DFW").createIfAbsent("myContainer", new CreateContainerOptions()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest createRequest = server.takeRequest();
         assertEquals(createRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   /** upper-cases first char, and lower-cases rest!! **/
   public void getKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(containerResponse() //
            // note silly casing
            .addHeader("X-Container-Meta-Apiname", "swift") //
            .addHeader("X-Container-Meta-Apiversion", "v1.1"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         Container container = api.containerApiInRegion("DFW").get("myContainer");
         assertEquals(container.name(), "myContainer");
         assertEquals(container.objectCount(), 42l);
         assertEquals(container.bytesUsed(), 323479l);
         for (Entry<String, String> entry : container.metadata().entrySet()) {
            assertEquals(container.metadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "HEAD /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void updateMetadata() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(containerResponse() //
            .addHeader("X-Container-Meta-ApiName", "swift") //
            .addHeader("X-Container-Meta-ApiVersion", "v1.1"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.containerApiInRegion("DFW").updateMetadata("myContainer", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader("x-container-meta-" + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void deleteMetadata() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(containerResponse());

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.containerApiInRegion("DFW").deleteMetadata("myContainer", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader("x-remove-container-meta-" + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   public void deleteIfEmpty() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.containerApiInRegion("DFW").deleteIfEmpty("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void alreadyDeleted() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertFalse(api.containerApiInRegion("DFW").deleteIfEmpty("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void deleteWhenNotEmpty() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(409));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         api.containerApiInRegion("DFW").deleteIfEmpty("myContainer");

      } finally {
         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         server.shutdown();
      }
   }

   private final static Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   public static MockResponse containerResponse() {
      return new MockResponse() //
            .addHeader("X-Container-Object-Count", "42") //
            .addHeader("X-Container-Bytes-Used", "323479");
   }
}
