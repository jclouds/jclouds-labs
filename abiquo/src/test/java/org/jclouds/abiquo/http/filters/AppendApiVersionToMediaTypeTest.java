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
package org.jclouds.abiquo.http.filters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;

import org.jclouds.abiquo.AbiquoApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Unit tests for the {@link AppendApiVersionToMediaType} filter.
 */
@Test(groups = "unit", testName = "AppendApiVersionToMediaTypeTest")
public class AppendApiVersionToMediaTypeTest {

   private AppendApiVersionToMediaType filter;

   private AbiquoApiMetadata metadata;

   @BeforeMethod
   public void setup() {
      metadata = new AbiquoApiMetadata();
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(metadata.getVersion());
         }
      });
      filter = injector.getInstance(AppendApiVersionToMediaType.class);
   }

   public void testAppendVersionToNonPayloadHeadersWithoutHeaders() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      assertTrue(filtered.getHeaders().get(HttpHeaders.ACCEPT).isEmpty());
   }

   public void testAppendVersionToNonPayloadHeadersWithStandardMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> contentType = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(contentType.size(), 1);
      assertEquals(contentType.iterator().next(), "application/json");
   }

   public void testAppendVersionToNonPayloadHeadersWithVersionInMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> contentType = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(contentType.size(), 1);
      assertEquals(contentType.iterator().next(), "application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");
   }

   public void testAppendVersionToNonPayloadHeadersWithoutVersionInMediaType() {
      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .build();

      HttpRequest filtered = filter.appendVersionToNonPayloadHeaders(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());
   }

   public void testAppendVersionToPayloadHeadersWithoutPayload() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).build();

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertNull(filtered.getPayload());
   }

   public void testAppendVersionToPayloadHeadersWithStandardPayload() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(), "application/json");
   }

   public void testAppendVersionToPayloadHeadersWithDefaultPayload() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(), "application/unknown");
   }

   public void testAppendVersionToPayloadHeadersWithVersionInPayload() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json;version=1.8.5");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=1.8.5");
   }

   public void testAppendVersionToPayloadHeadersWithoutVersionInPayload() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).payload(payload)
            .build();

      HttpRequest filtered = filter.appendVersionToPayloadHeaders(request);

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());
   }

   public void testFilterWithAcceptAndContentTypeWithVersion() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=2.1-SNAPSHOT");
   }

   public void testFilterWithAcceptAndContentTypeWithoutVersion() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());
   }

   public void testFilterWithversionInAccept() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json;version=1.8.5");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(), "application/vnd.abiquo.virtualmachine+json;version=1.8.5");

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());
   }

   public void testFilterWithversionInContentType() {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[0]));
      payload.getContentMetadata().setContentType("application/vnd.abiquo.virtualmachine+json;version=1.8.5");

      Multimap<String, String> headers = LinkedHashMultimap.<String, String> create();
      headers.put(HttpHeaders.ACCEPT, "application/vnd.abiquo.virtualmachine+json");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://foo")).headers(headers)
            .payload(payload).build();

      HttpRequest filtered = filter.filter(request);

      Collection<String> accept = filtered.getHeaders().get(HttpHeaders.ACCEPT);
      assertEquals(accept.size(), 1);
      assertEquals(accept.iterator().next(),
            "application/vnd.abiquo.virtualmachine+json;version=" + metadata.getVersion());

      assertEquals(filtered.getPayload().getContentMetadata().getContentType(),
            "application/vnd.abiquo.virtualmachine+json;version=1.8.5");
   }
}
