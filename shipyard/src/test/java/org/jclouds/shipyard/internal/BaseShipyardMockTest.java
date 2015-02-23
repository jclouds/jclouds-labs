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
package org.jclouds.shipyard.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Strings2.toStringAndClose;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.shipyard.ShipyardApi;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for all Shipyard mock tests.
 */
public class BaseShipyardMockTest {
   private final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor()));

   protected String provider;
   private final JsonParser parser = new JsonParser();

   public BaseShipyardMockTest() {
      provider = "shipyard";
   }

   public ShipyardApi api(URL url) {
      return ContextBuilder.newBuilder(provider)
            .credentials("shipyard-service-key", "")
            .endpoint(url.toString())
            .modules(modules)
            .overrides(setupProperties())
            .buildApi(ShipyardApi.class);
   }

   protected Properties setupProperties() {
      Properties properties = new Properties();
      properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
      return properties;
   }

   public static MockWebServer mockShipyardWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }

   public byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader("X-Service-Key").equals("shipyard-service-key"));
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      return request;
   }
   
   protected RecordedRequest assertSentIgnoreServiceKey(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
      return request;
   }
   
   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json) throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);
      assertEquals(request.getHeader("Content-Type"), APPLICATION_JSON);
      assertEquals(parser.parse(new String(request.getBody(), UTF_8)), parser.parse(json));
      return request;
   }
}
