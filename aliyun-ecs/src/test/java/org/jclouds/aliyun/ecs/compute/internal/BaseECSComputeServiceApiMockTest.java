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
package org.jclouds.aliyun.ecs.compute.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.ContextBuilder;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.ECSComputeServiceProviderMetadata;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.json.Json;
import org.jclouds.rest.ApiContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BaseECSComputeServiceApiMockTest {

   private static final String DEFAULT_ENDPOINT = new ECSComputeServiceProviderMetadata().getEndpoint();
   protected static final String TEST_REGION = Regions.EU_CENTRAL_1.getName();
   protected static final String TEST_ZONE = TEST_REGION + "a";

   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(newDirectExecutorService()));
   protected MockWebServer server;
   protected ECSComputeServiceApi api;
   private Json json;
   private ApiContext<ECSComputeServiceApi> ctx;

   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      ctx = ContextBuilder.newBuilder("alibaba-ecs").credentials("user", "password").endpoint(url("")).modules(modules)
            .overrides(overrides()).build();
      json = ctx.utils().injector().getInstance(Json.class);
      api = ctx.getApi();
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
   }

   protected Properties overrides() {
      Properties properties = new Properties();
      properties.put(PROPERTY_MAX_RETRIES, "0"); // Do not retry
      return properties;
   }

   protected String url(String path) {
      return server.getUrl(path).toString();
   }

   protected MockResponse jsonResponse(String resource) {
      return new MockResponse().addHeader("Content-Type", "application/json").setBody(stringFromResource(resource));
   }

   protected MockResponse response404() {
      return new MockResponse().setStatus("HTTP/1.1 404 Not Found");
   }

   protected MockResponse response204() {
      return new MockResponse().setStatus("HTTP/1.1 204 No Content");
   }

   protected String stringFromResource(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8)
               .replace(DEFAULT_ENDPOINT, url(""));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected <T> T objectFromResource(String resourceName, Class<T> type) {
      String text = stringFromResource(resourceName);
      return json.fromJson(text, type);
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String action) throws InterruptedException {
      return assertSent(server, method, action, ImmutableMap.<String, String>of(), null);
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String action, Integer page) throws InterruptedException {
      return assertSent(server, method, action, ImmutableMap.<String, String>of(), page);
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String action, Map<String, String> queryParameters) throws InterruptedException {
      return assertSent(server, method, action, queryParameters, null);
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String action, Map<String, String> queryParameters, Integer page) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertEquals(request.getMethod(), method);
      Map<String, String> requestQueryParameters = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(request.getPath());
      assertEquals(requestQueryParameters.get("Action"), action);

      for (Map.Entry<String, String> keyAndValue : queryParameters.entrySet()) {
         assertTrue(requestQueryParameters.containsKey(keyAndValue.getKey()));
         assertTrue(requestQueryParameters.containsValue(keyAndValue.getValue()));
         assertEquals(requestQueryParameters.get(keyAndValue.getKey()), keyAndValue.getValue());
      }
      if (page != null) assertEquals(Integer.valueOf(requestQueryParameters.get("pageNumber")), page);
      assertEquals(request.getHeader("Accept"), "application/json");
      return request;
   }

}
