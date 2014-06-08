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
package org.jclouds.openstack.glance.v1_0.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;
import java.util.Collections;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Test(groups = "unit", testName = "GlanceErrorHandlerTest")
public class GlanceErrorHandlerTest {

   @Test
   public void test300UnintendedVersionNegotiation() {
      assertCodeMakes("GET", URI
         .create("https://glance.jclouds.org:9292/"),
         300, "Multiple Choices", "", HttpResponseException.class);
   }

   @Test
   public void test300VersionNegotiation() {
      assertCodeMakes("GET", Multimaps.forMap(Collections.singletonMap("Is-Version-Negotiation-Request", "true")),
         URI.create("https://glance.jclouds.org:9292/"),
         300, "Multiple Choices", "", null);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode,
            String message, String content, Class<? extends Exception> expected) {
      assertCodeMakes(method, Multimaps.forMap(Collections.EMPTY_MAP), uri, statusCode, message, "text/plain", content, expected);
   }

   private void assertCodeMakes(String method, Multimap<String, String> headers, URI uri, int statusCode,
            String message, String content, Class<? extends Exception> expected) {
      assertCodeMakes(method, headers, uri, statusCode, message, "text/plain", content, expected);
   }

   private void assertCodeMakes(String method, Multimap<String, String> headers, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected) {

      GlanceErrorHandler function = new GlanceErrorHandler();

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method(method).headers(headers).endpoint(uri).build();
      HttpResponse response = HttpResponse.builder().statusCode(statusCode).message(message).payload(content).build();
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      if (expected != null) {
         command.setException(classEq(expected));
      }

      replay(command);

      function.handleError(command, response);

      verify(command);
   }

   public static Exception classEq(final Class<? extends Exception> in) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("classEq(");
            buffer.append(in);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return arg.getClass() == in;
         }

      });
      return null;
   }


}
