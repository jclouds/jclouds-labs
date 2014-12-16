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
package org.jclouds.azurecompute.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class BaseAzureComputeApiMockTest {
   private final Set<Module> modules = ImmutableSet
         .<Module>of(new ExecutorServiceModule(sameThreadExecutor()));

   protected String provider;
   private final String identity;
   private final String credential;

   public BaseAzureComputeApiMockTest() {
      provider = "azurecompute";
      // self-signed dummy cert:
      // keytool -genkey -alias test -keyalg RSA -keysize 1024 -validity 5475 -dname "CN=localhost" -keystore azure-test.p12 -storepass azurepass -storetype pkcs12
      identity = this.getClass().getResource("/azure-test.p12").getFile();
      credential = "azurepass";
   }

   public AzureComputeApi api(URL url) {
      Properties properties = new Properties();
      //properties.setProperty(SUBSCRIPTION_ID, "1234-1234-1234");
      return ContextBuilder.newBuilder(provider).credentials(identity, credential).endpoint(url.toString())
            .modules(modules).overrides(properties).buildApi(AzureComputeApi.class);
   }

   protected static MockWebServer mockAzureManagementServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }

   protected MockResponse xmlResponse(String resource) {
      return new MockResponse().addHeader("Content-Type", "application/xml").setBody(stringFromResource(resource));
   }

   protected MockResponse requestIdResponse(String requestId) {
      return new MockResponse().addHeader("x-ms-request-id", requestId);
   }

   protected String stringFromResource(String resourceName) {
      try {
         return Strings2.toStringAndClose(getClass().getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
      assertThat(request.getHeader("x-ms-version")).isEqualTo("2014-10-01");
      assertThat(request.getHeader("Accept")).isEqualTo("application/xml");
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String resource)
         throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);
      assertThat(new String(request.getBody(), UTF_8)).isEqualTo(stringFromResource(resource).trim());
      return request;
   }
}
