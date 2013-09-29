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

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.net.HttpHeaders.RANGE;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.openstack.swift.v1.options.ListContainerOptions.Builder.marker;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftMockTest;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test
public class ObjectApiMockTest extends BaseSwiftMockTest {
   SimpleDateFormatDateService dates = new SimpleDateFormatDateService();

   String objectList = "" //
         + "[\n" //
         + "   {\"name\":\"test_obj_1\",\n" //
         + "    \"hash\":\"4281c348eaf83e70ddce0e07221c3d28\",\n" //
         + "    \"bytes\":14,\n" //
         + "    \"content_type\":\"application\\/octet-stream\",\n" //
         + "    \"last_modified\":\"2009-02-03T05:26:32.612278\"},\n" //
         + "   {\"name\":\"test_obj_2\",\n" //
         + "    \"hash\":\"b039efe731ad111bc1b0ef221c3849d0\",\n" //
         + "    \"bytes\":64,\n" //
         + "    \"content_type\":\"application\\/octet-stream\",\n" //
         + "    \"last_modified\":\"2009-02-03T05:26:32.612278\"},\n" //
         + "]";

   protected ImmutableList<SwiftObject> parsedObjectsForUrl(String baseUri) {
      baseUri += "v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer";
      return ImmutableList.of(//
            SwiftObject.builder() //
                  .name("test_obj_1") //
                  .uri(URI.create(baseUri + "/test_obj_1")) //
                  .etag("4281c348eaf83e70ddce0e07221c3d28") //
                  .payload(payload(14, "application/octet-stream")) //
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build(), //
            SwiftObject.builder() //
                  .name("test_obj_2") //
                  .uri(URI.create(baseUri + "/test_obj_2")) //
                  .etag("b039efe731ad111bc1b0ef221c3849d0") //
                  .payload(payload(64l, "application/octet-stream")) //
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build());
   }

   public void list() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setBody(objectList));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         ImmutableList<SwiftObject> objects = api.objectApiInRegionForContainer("DFW", "myContainer")
               .list(new ListContainerOptions()).toList();
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/?format=json HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void listOptions() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setBody(objectList));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         ImmutableList<SwiftObject> objects = api.objectApiInRegionForContainer("DFW", "myContainer")
               .list(marker("test")).toList();
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/?format=json&marker=test HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void replace() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse() //
            .setResponseCode(201) //
            .addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertEquals(
               api.objectApiInRegionForContainer("DFW", "myContainer").replace("myObject",
                     newStringPayload("swifty"), metadata), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replace = server.takeRequest();
         assertEquals(replace.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         assertEquals(new String(replace.getBody()), "swifty");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replace.getHeader("x-object-meta-" + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   /** upper-cases first char, and lower-cases rest!! **/
   public void headKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(objectResponse() //
            // note silly casing
            .addHeader("X-Object-Meta-Apiname", "swift") //
            .addHeader("X-Object-Meta-Apiversion", "v1.1"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         SwiftObject object = api.objectApiInRegionForContainer("DFW", "myContainer").head("myObject");
         assertEquals(object.name(), "myObject");
         assertEquals(object.etag(), "8a964ee2a5e88be344f36c22562a6486");
         assertEquals(object.lastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.metadata().entrySet()) {
            assertEquals(object.metadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(object.payload().getContentMetadata().getContentLength(), new Long(4));
         assertEquals(object.payload().getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         assertEquals(Strings2.toStringAndClose(object.payload().getInput()), "");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "HEAD /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void get() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(objectResponse() //
            // note silly casing
            .addHeader("X-Object-Meta-Apiname", "swift") //
            .addHeader("X-Object-Meta-Apiversion", "v1.1"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         SwiftObject object = api.objectApiInRegionForContainer("DFW", "myContainer").get("myObject", tail(1));
         assertEquals(object.name(), "myObject");
         assertEquals(object.etag(), "8a964ee2a5e88be344f36c22562a6486");
         assertEquals(object.lastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.metadata().entrySet()) {
            assertEquals(object.metadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(object.payload().getContentMetadata().getContentLength(), new Long(4));
         assertEquals(object.payload().getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         // note MWS doesn't process Range header at the moment
         assertEquals(Strings2.toStringAndClose(object.payload().getInput()), "ABCD");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest get = server.takeRequest();
         assertEquals(get.getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         assertEquals(get.getHeader(RANGE), "bytes=-1");
      } finally {
         server.shutdown();
      }
   }

   public void updateMetadata() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(objectResponse() //
            .addHeader("X-Object-Meta-ApiName", "swift") //
            .addHeader("X-Object-Meta-ApiVersion", "v1.1"));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.objectApiInRegionForContainer("DFW", "myContainer").updateMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader("x-object-meta-" + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void deleteMetadata() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(objectResponse());

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         assertTrue(api.objectApiInRegionForContainer("DFW", "myContainer").deleteMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader("x-remove-object-meta-" + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   public void delete() throws Exception {
      MockWebServer server = mockSwiftServer();
      server.enqueue(new MockResponse().setBody(access));
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         SwiftApi api = swiftApi(server.getUrl("/").toString());
         api.objectApiInRegionForContainer("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
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
         api.objectApiInRegionForContainer("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   private final static Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   public static MockResponse objectResponse() {
      return new MockResponse() //
            .addHeader("Last-Modified", "Fri, 12 Jun 2010 13:40:18 GMT") //
            .addHeader("ETag", "8a964ee2a5e88be344f36c22562a6486") //
            // TODO: MWS doesn't allow you to return content length w/o content
            // on HEAD!
            .setBody("ABCD".getBytes(US_ASCII)) //
            .addHeader("Content-Length", "4").addHeader("Content-Type", "text/plain; charset=UTF-8");
   }

   private static final byte[] NO_CONTENT = new byte[] {};

   private static Payload payload(long bytes, String contentType) {
      Payload payload = Payloads.newByteArrayPayload(NO_CONTENT);
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      return payload;
   }
}
