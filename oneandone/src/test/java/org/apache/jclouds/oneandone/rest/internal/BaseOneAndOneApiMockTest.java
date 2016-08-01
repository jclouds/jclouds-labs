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
package org.apache.jclouds.oneandone.rest.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import org.apache.jclouds.oneandone.rest.OneAndOneApi;
import org.apache.jclouds.oneandone.rest.OneAndOneProviderMetadata;
import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.json.Json;
import org.jclouds.rest.ApiContext;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseOneAndOneApiMockTest {

   private static final OneAndOneProviderMetadata METADATA = new OneAndOneProviderMetadata();
   protected static final String AUTH_HEADER = "token";

   private static final String DEFAULT_ENDPOINT = METADATA.getEndpoint();
   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(sameThreadExecutor()));
   protected MockWebServer server;
   protected OneAndOneApi api;
   private Json json;

   private final JsonParser parser = new JsonParser();

   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      ApiContext<OneAndOneApi> ctx = ContextBuilder.newBuilder("oneandone")
              .credentials("token", "password")
              .endpoint(url(""))
              .modules(modules)
              .overrides(overrides())
              .build();
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

   protected String stringFromResource(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8)
                 .replace(DEFAULT_ENDPOINT, url(""));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {

      RecordedRequest request = server.takeRequest();

      assertEquals(request.getMethod(), method);
      assertEquals(request.getPath(), path);
      assertEquals(request.getHeader("X-TOKEN"), AUTH_HEADER);
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json)
           throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);

      String expectedContentType = "application/json";

      assertEquals(request.getHeader("Content-Type"), expectedContentType);
      assertEquals(parser.parse(new String(request.getBody(), Charsets.UTF_8)), parser.parse(json));
      return request;
   }
}
