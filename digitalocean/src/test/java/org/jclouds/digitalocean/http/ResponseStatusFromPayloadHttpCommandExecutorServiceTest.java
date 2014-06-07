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
package org.jclouds.digitalocean.http;

import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.digitalocean.http.ResponseStatusFromPayloadHttpCommandExecutorService.ACCESS_DENIED;
import static org.jclouds.digitalocean.http.ResponseStatusFromPayloadHttpCommandExecutorService.NOT_FOUND;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.features.ImageApi;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ResponseStatusFromPayloadHttpCommandExecutorService} class.
 */
@Test(groups = "unit", testName = "ResponseStatusFromPayloadHttpCommandExecutorServiceTest")
public class ResponseStatusFromPayloadHttpCommandExecutorServiceTest extends BaseDigitalOceanMockTest {

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty(PROPERTY_MAX_RETRIES, "1");
      return properties;
   }

   public void testAccessDenied() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/access-denied.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.list();
         fail("Request should have failed");
      } catch (Exception ex) {
         assertTrue(ex instanceof AuthorizationException, "Exception should be an AuthorizationException");
         assertEquals(ex.getMessage(), ACCESS_DENIED);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testNotFound() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/not-found.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.list();
         fail("Request should have failed");
      } catch (Exception ex) {
         assertTrue(ex instanceof ResourceNotFoundException, "Exception should be a ResourceNotFoundException");
         assertEquals(ex.getMessage(), NOT_FOUND);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testInternalServerError() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/error.json")));
      // Response to be sent for the retried request
      server.enqueue(new MockResponse().setBody(payloadFromResource("/error.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.list();
         fail("Request should have failed after retrying");
      } catch (Exception ex) {
         assertTrue(ex instanceof HttpResponseException, "Exception should be an HttpResponseException");
         HttpResponseException exception = HttpResponseException.class.cast(ex);
         assertEquals(exception.getResponse().getStatusCode(), 500);
         assertEquals(exception.getMessage(), "No Image Found");
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
