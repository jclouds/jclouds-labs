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
import com.google.common.base.Supplier;
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
import org.jclouds.http.Uris;
import org.jclouds.json.Json;
import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.rest.ApiContext;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Iterables.size;
import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Base class for all DimensionDataCloudController mock tests.
 */
public class BaseDimensionDataCloudControlMockTest implements IHookable {

   private static final DimensionDataCloudControlProviderMetadata PROVIDER_METADATA = new DimensionDataCloudControlProviderMetadata();
   private static final String DEFAULT_ENDPOINT = PROVIDER_METADATA.getEndpoint();
   protected static final String VERSION = PROVIDER_METADATA.getApiMetadata().getVersion();

   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(newDirectExecutorService()));

   protected MockWebServer server;
   protected DimensionDataCloudControlApi api;
   protected ApiContext<DimensionDataCloudControlApi> ctx;
   private Json json;
   private int assertedRequestCount;
   protected Set<String> datacenters;

   // So that we can ignore formatting.
   private final JsonParser parser = new JsonParser();

   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      ctx = ContextBuilder.newBuilder(DimensionDataCloudControlProviderMetadata.builder().build()).credentials("", "")
            .endpoint(url("/caas/")).modules(modules).overrides(new Properties()).build();
      json = ctx.utils().injector().getInstance(Json.class);
      api = ctx.getApi();
      applyAdditionalServerConfig();
      assertedRequestCount = 0;
      datacenters = getZones();
   }

   private Set<String> getZones() {
      final String region = ctx.utils().injector().getInstance(ImplicitRegionIdSupplier.class).get();
      final Map<String, Supplier<Set<String>>> regionToZoneMap = ctx.utils().injector()
            .getInstance(RegionIdToZoneIdsSupplier.class).get();
      return regionToZoneMap.get(region).get();
   }

   /**
    * Applies any additional configuration required by test classes to the mock web server.
    */
   protected void applyAdditionalServerConfig() {
   }

   @Override
   public void run(IHookCallBack callBack, ITestResult testResult) {
      callBack.runTestMethod(testResult);
      if (testResult.isSuccess()) {
         ensureAllRequestsWereAsserted();
      }
   }

   private void ensureAllRequestsWereAsserted() {
      int unAssertedRequestCount = server.getRequestCount() - assertedRequestCount;
      if (unAssertedRequestCount > 0) {
         StringBuilder messageBuilder = new StringBuilder(
               String.format("There were %s un-asserted requests: ", unAssertedRequestCount));
         while (unAssertedRequestCount > 0) {
            try {
               messageBuilder.append('\n').append(server.takeRequest().toString());
            } catch (InterruptedException e) {
               messageBuilder.append('\n').append("Error obtaining request from mock web server: ")
                     .append(e.toString());
            }
            unAssertedRequestCount--;
         }
         fail(messageBuilder.toString());
      }
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
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

   protected MockResponse response404() {
      return new MockResponse().setStatus("HTTP/1.1 404 Not Found");
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
      assertedRequestCount++;
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Basic Og==");
      return request;
   }

   protected void assertBodyContains(RecordedRequest recordedRequest, String expectedText) {
      assertThat(recordedRequest.getUtf8Body()).contains(expectedText);
   }

   /**
    * Consumes the supplied iterable, checking that it contained the expected number of items, and that the expected
    * number of additional page requests were made in doing so.
    *
    * @param iterable                         the iterable to be consumed.
    * @param expectedSize                     the number of items expected in the iterable.
    * @param expectedAdditionalPagesRequested the number of additional page requests that should be made.
    * @param <T>                              the type of item contained in the iterable.
    */
   protected <T> void consumeIterableAndAssertAdditionalPagesRequested(Iterable<T> iterable, int expectedSize,
         int expectedAdditionalPagesRequested) {
      int initialRequestCount = server.getRequestCount();
      assertEquals(size(iterable), expectedSize);
      assertEquals(server.getRequestCount() - initialRequestCount, expectedAdditionalPagesRequested);
   }

   protected Uris.UriBuilder addPageNumberToUriBuilder(Uris.UriBuilder uriBuilder, int pageNumber, boolean clearQuery) {
      if (clearQuery) {
         uriBuilder.clearQuery();
      }
      return uriBuilder.addQuery("pageNumber", Integer.toString(pageNumber));
   }

   protected Uris.UriBuilder addZonesToUriBuilder(String zoneQueryParameter, Uris.UriBuilder uriBuilder) {
      for (String datacenter : datacenters) {
         uriBuilder.addQuery(zoneQueryParameter, datacenter);
      }
      return uriBuilder;
   }

   public byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
