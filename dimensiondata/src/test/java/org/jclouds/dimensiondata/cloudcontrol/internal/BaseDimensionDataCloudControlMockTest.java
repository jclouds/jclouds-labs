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

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlProviderMetadata;
import org.jclouds.json.Json;
import org.jclouds.rest.ApiContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for all DimensionDataCloudController mock tests.
 */
public class BaseDimensionDataCloudControlMockTest {

   private static final String DEFAULT_ENDPOINT = new DimensionDataCloudControlProviderMetadata().getEndpoint();

   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(sameThreadExecutor()));

   protected MockWebServer server;
   protected DimensionDataCloudControlApi api;
   protected ApiContext<DimensionDataCloudControlApi> ctx;
   private Json json;

   // So that we can ignore formatting.
   private final JsonParser parser = new JsonParser();

   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      ctx = ContextBuilder.newBuilder(DimensionDataCloudControlProviderMetadata.builder().build()).credentials("", "")
            .endpoint(url("")).modules(modules).overrides(overrides()).build();
      json = ctx.utils().injector().getInstance(Json.class);
      api = ctx.getApi();
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
   }

   protected Properties overrides() {
      return new Properties();
   }

   protected String url(String path) {
      return server.getUrl(path).toString();
   }

   protected MockResponse jsonResponse(String resource) {
      return new MockResponse().addHeader("Content-Type", "application/json").setBody(stringFromResource(resource));
   }

   /**
    * Mocked standard Dimension Data Unexpected Error 400 response
    *
    * @return {@link MockResponse}
    */
   protected MockResponse responseUnexpectedError() {
      return new MockResponse().setResponseCode(400).setStatus("HTTP/1.1 400 Bad Request")
            .setBody("content: [{\"operation\":\"OPERATION\",\"responseCode\":\"UNEXPECTED_ERROR\"}]");
   }

   /**
    * Mocked standard Dimension Data Resource Not Found 400 response
    *
    * @return {@link MockResponse}
    */
   protected MockResponse responseResourceNotFound() {
      return new MockResponse().setResponseCode(400).setStatus("HTTP/1.1 400 Bad Request")
            .setBody("content: [{\"operation\":\"OPERATION\",\"responseCode\":\"RESOURCE_NOT_FOUND\"}]");
   }

   /**
    * Mocked OK 200 Response
    *
    * @return {@link MockResponse}
    */
   protected MockResponse response200() {
      return new MockResponse().setResponseCode(200);
   }

   protected String stringFromResource(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8)
               .replace(DEFAULT_ENDPOINT, url(""));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected RecordedRequest assertSent(String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Basic Og==");
      return request;
   }

   protected void assertBodyContains(RecordedRequest recordedRequest, String expectedText) {
      assertThat(recordedRequest.getUtf8Body()).contains(expectedText);
   }
}
