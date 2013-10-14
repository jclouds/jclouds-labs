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
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * @author Jeremy Daggett
 */
@Test
public class AccountApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   /** upper-cases first char, and lower-cases rest!! **/
   public void getKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(accountResponse() //
            // note silly casing
            .addHeader("X-Account-Meta-Apiname", "swift") //
            .addHeader("X-Account-Meta-Apiversion", "v1.1"));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         Account account = api.accountApiInRegion("DFW").get();
         assertEquals(account.containerCount(), 3l);
         assertEquals(account.objectCount(), 42l);
         assertEquals(account.bytesUsed(), 323479l);
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(account.metadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(server.takeRequest().getRequestLine(),
               "HEAD /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/ HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void updateMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(accountResponse() //
            .addHeader("X-Account-Meta-ApiName", "swift") //
            .addHeader("X-Account-Meta-ApiVersion", "v1.1"));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.accountApiInRegion("DFW").updateMetadata(metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/ HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader("x-account-meta-" + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void updateTemporaryUrlKey() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(accountResponse());

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.accountApiInRegion("DFW").updateTemporaryUrlKey("foobar"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/ HTTP/1.1");
         assertEquals(replaceRequest.getHeader("X-Account-Meta-Temp-URL-Key"), "foobar");
      } finally {
         server.shutdown();
      }
   }

   public void deleteMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(accessRackspace));
      server.enqueue(accountResponse());

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.accountApiInRegion("DFW").deleteMetadata(metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/ HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader("x-remove-account-meta-" + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   private final static Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   public static MockResponse accountResponse() {
      return new MockResponse() //
            .addHeader("X-Account-Container-Count", "3") //
            .addHeader("X-Account-Object-Count", "42") //
            .addHeader("X-Account-Bytes-Used", "323479");
   }
}
