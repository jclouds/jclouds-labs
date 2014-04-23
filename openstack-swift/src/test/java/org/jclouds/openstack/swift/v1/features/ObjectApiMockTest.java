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
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_RETRY_DELAY_START;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newByteSourcePayload;
import static org.jclouds.openstack.swift.v1.features.ContainerApiMockTest.containerResponse;
import static org.jclouds.openstack.swift.v1.options.ListContainerOptions.Builder.marker;
import static org.jclouds.openstack.swift.v1.options.PutOptions.Builder.metadata;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_ACL_ANYBODY_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_REMOVE_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.openstack.swift.v1.CopyObjectException;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.swift.v1.options.PutOptions;
import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Provides mock tests for the {@link ObjectApi}.
 * 
 * @author Adrian Cole
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "ObjectApiMockTest")
public class ObjectApiMockTest extends BaseOpenStackMockTest<SwiftApi> {
   SimpleDateFormatDateService dates = new SimpleDateFormatDateService();

   protected ImmutableList<SwiftObject> parsedObjectsForUrl(String baseUri) {
      baseUri += "v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer";
      return ImmutableList.of(
            SwiftObject.builder()
                  .name("test_obj_1")
                  .uri(URI.create(baseUri + "/test_obj_1"))
                  .etag("4281c348eaf83e70ddce0e07221c3d28")
                  .payload(payload(14, "application/octet-stream"))
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build(),
            SwiftObject.builder()
                  .name("test_obj_2")
                  .uri(URI.create(baseUri + "/test_obj_2"))
                  .etag("b039efe731ad111bc1b0ef221c3849d0")
                  .payload(payload(64l, "application/octet-stream"))
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build());
   }

   public void testList() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ)
            .setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApiForRegionAndContainer("DFW", "myContainer").list();
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));
         assertEquals(objects.getContainer().getName(), "myContainer");
         assertTrue(objects.getContainer().getAnybodyRead().get());

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/?format=json");
      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ)
            .setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApiForRegionAndContainer("DFW", "myContainer").list(new ListContainerOptions());
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));
         assertEquals(objects.getContainer().getName(), "myContainer");
         assertTrue(objects.getContainer().getAnybodyRead().get());

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/?format=json");
      } finally {
         server.shutdown();
      }
   }
   
   public void testListOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse().setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApiForRegionAndContainer("DFW", "myContainer").list(marker("test"));
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/?format=json&marker=test");
      } finally {
         server.shutdown();
      }
   }

   public void testReplace() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse()
            .setResponseCode(201)
            .addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(
               api.getObjectApiForRegionAndContainer("DFW", "myContainer").put("myObject",
                     newByteSourcePayload(ByteSource.wrap("swifty".getBytes())), metadata(metadata)), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         RecordedRequest replace = server.takeRequest();
         assertRequest(replace, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");

         assertEquals(new String(replace.getBody()), "swifty");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replace.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testReplace408Retry() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 1
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 2
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 3

      // Finally success
      server.enqueue(addCommonHeaders(new MockResponse()
            .setResponseCode(201)
            .addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b")));

      try {
         Properties overrides = new Properties();
         overrides.setProperty(PROPERTY_MAX_RETRIES, 5 + "");

         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift", overrides);
         assertEquals(
               api.getObjectApiForRegionAndContainer("DFW", "myContainer").put("myObject",
                     new ByteSourcePayload(ByteSource.wrap("swifty".getBytes())), PutOptions.Builder.metadata(metadata)), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 5);
         assertAuthentication(server);
         RecordedRequest replace = server.takeRequest();
         // This should take a while.
         assertRequest(replace, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");

         assertEquals(new String(replace.getBody()), "swifty");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replace.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   /** upper-cases first char, and lower-cases rest!! **/
   public void testHeadKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            // note silly casing
            .addHeader(OBJECT_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         SwiftObject object = api.getObjectApiForRegionAndContainer("DFW", "myContainer").getWithoutBody("myObject");
         assertEquals(object.getName(), "myObject");
         assertEquals(object.getETag(), "8a964ee2a5e88be344f36c22562a6486");
         assertEquals(object.getLastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.getMetadata().entrySet()) {
            assertEquals(object.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(object.getPayload().getContentMetadata().getContentLength(), new Long(4));
         assertEquals(object.getPayload().getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         assertEquals(Strings2.toStringAndClose(object.getPayload().openStream()), "");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");
      } finally {
         server.shutdown();
      }
   }

   public void get() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            // note silly casing
            .addHeader(OBJECT_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         SwiftObject object = api.getObjectApiForRegionAndContainer("DFW", "myContainer").get("myObject", tail(1));
         assertEquals(object.getName(), "myObject");
         assertEquals(object.getETag(), "8a964ee2a5e88be344f36c22562a6486");
         assertEquals(object.getLastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.getMetadata().entrySet()) {
            assertEquals(object.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(object.getPayload().getContentMetadata().getContentLength(), new Long(4));
         assertEquals(object.getPayload().getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         // note MWS doesn't process Range header at the moment
         assertEquals(Strings2.toStringAndClose(object.getPayload().openStream()), "ABCD");

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

   @Test(expectedExceptions = HttpResponseException.class, timeOut = 20000)
   public void testReplaceTimeout() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      // Typically we would enqueue a response for the put. However, in this case, test the timeout by not providing one.

      try {
         Properties overrides = new Properties();

         overrides.setProperty(PROPERTY_SO_TIMEOUT, 5000 + ""); // This time-outs the connection
         overrides.setProperty(PROPERTY_MAX_RETRIES, 0 + ""); // 0 retries == 1 try. Semantics.
         overrides.setProperty(PROPERTY_RETRY_DELAY_START, 0 + ""); // exponential backoff already working for this call. This is the delay BETWEEN attempts.

         final SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift", overrides);
         
         api.getObjectApiForRegionAndContainer("DFW", "myContainer").put("myObject", new ByteSourcePayload(ByteSource.wrap("swifty".getBytes())), PutOptions.Builder.metadata(metadata));

         fail("testReplaceTimeout test should have failed with an HttpResponseException.");
      } finally {
         server.shutdown();
      }
   }

   public void updateMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            .addHeader(OBJECT_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApiForRegionAndContainer("DFW", "myContainer").updateMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateMetadataContentType() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            .addHeader(OBJECT_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApiForRegionAndContainer("DFW", "myContainer").updateMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getHeaders("Content-Type").get(0), "", "updateMetadata should send an empty content-type header, but sent "
               + replaceRequest.getHeaders("Content-Type").get(0).toString());

         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void deleteMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApiForRegionAndContainer("DFW", "myContainer").deleteMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader(OBJECT_REMOVE_METADATA_PREFIX + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   public void delete() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getObjectApiForRegionAndContainer("DFW", "myContainer").delete("myObject");

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
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getObjectApiForRegionAndContainer("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }
   
   public void copyObject() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bar/foo.txt")));
      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApiForRegionAndContainer("DFW", "foo")
            .copy("bar.txt", "bar", "foo.txt"));
              
         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         
         RecordedRequest copyRequest = server.takeRequest();
         assertEquals(copyRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/foo/bar.txt HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }
   
   @Test(expectedExceptions = CopyObjectException.class)
   public void copyObjectFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bogus/foo.txt")));
      
      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         // the following line will throw the CopyObjectException
         api.getObjectApiForRegionAndContainer("DFW", "foo").copy("bar.txt", "bogus", "foo.txt"); 
      } finally {
         server.shutdown();
      }  
   }
   
   private static final Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   public static MockResponse objectResponse() {
      return new MockResponse()
            .addHeader("Last-Modified", "Fri, 12 Jun 2010 13:40:18 GMT")
            .addHeader("ETag", "8a964ee2a5e88be344f36c22562a6486")
            // TODO: MWS doesn't allow you to return content length w/o content
            // on HEAD!
            .setBody("ABCD".getBytes(US_ASCII))
            .addHeader("Content-Length", "4").addHeader("Content-Type", "text/plain; charset=UTF-8");
   }

   private static final byte[] NO_CONTENT = new byte[] {};

   private static Payload payload(long bytes, String contentType) {
      Payload payload = newByteSourcePayload(ByteSource.wrap(NO_CONTENT));
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      return payload;
   }
}
