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
package org.jclouds.abiquo.functions.auth;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.abiquo.functions.auth.GetTokenFromApi.readAuthenticationToken;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.Cookie;

import org.easymock.EasyMock;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;

/**
 * Unit tests for the {@link GetTokenFromApi} function.
 */
@Test(groups = "unit", testName = "GetTokenFromApiTest")
public class GetTokenFromApiTest {

   public void testGetTokenWithoutHeaders() {
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      expect(response.getHeaders()).andReturn(ImmutableMultimap.<String, String> of());
      replay(response);

      Optional<Cookie> token = readAuthenticationToken(response);
      assertFalse(token.isPresent());

      verify(response);
   }

   public void testGetTokenWithOtherHeaders() {
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      expect(response.getHeaders()).andReturn(ImmutableMultimap.<String, String> of("Accept", "application/xml"));
      replay(response);

      Optional<Cookie> token = readAuthenticationToken(response);
      assertFalse(token.isPresent());

      verify(response);
   }

   public void testGetTokenWithOtherCookie() {
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      expect(response.getHeaders()).andReturn(ImmutableMultimap.<String, String> of(HttpHeaders.SET_COOKIE, "foo=bar"));
      replay(response);

      Optional<Cookie> token = readAuthenticationToken(response);
      assertFalse(token.isPresent());

      verify(response);
   }

   public void testGetTokenWithAuthenticationCookie() {
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      expect(response.getHeaders()).andReturn(
            ImmutableMultimap.<String, String> of(HttpHeaders.SET_COOKIE, "auth=the-token"));
      replay(response);

      Optional<Cookie> token = readAuthenticationToken(response);
      assertTrue(token.isPresent());
      assertEquals(token.get().getName(), "auth");
      assertEquals(token.get().getValue(), "the-token");

      verify(response);
   }
}
