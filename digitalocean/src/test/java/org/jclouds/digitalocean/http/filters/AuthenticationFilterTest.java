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
package org.jclouds.digitalocean.http.filters;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.digitalocean.http.filters.AuthenticationFilter.CREDENTIAL_PARAM;
import static org.jclouds.digitalocean.http.filters.AuthenticationFilter.IDENTITY_PARAM;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.testng.Assert.assertEquals;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;

/**
 * Unit tests for the {@link AuthenticationFilter} class.
 */
@Test(groups = "unit", testName = "AuthenticationFilterTest")
public class AuthenticationFilterTest {

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "credential supplier cannot be null")
   public void testFilterWithoutCredentials() {
      new AuthenticationFilter(null);
   }

   public void testFilterWithCredentials() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost/foo").build();
      Credentials credentials = LoginCredentials.builder().identity("foo").credential("bar").build();
      AuthenticationFilter filter = new AuthenticationFilter(Suppliers.ofInstance(credentials));

      HttpRequest filtered = filter.filter(request);

      String queryLine = filtered.getRequestLine().trim()
            .substring(filtered.getRequestLine().indexOf('?') + 1, filtered.getRequestLine().lastIndexOf(' '));
      Multimap<String, String> params = queryParser().apply(queryLine);

      assertEquals(getOnlyElement(params.get(IDENTITY_PARAM)), "foo");
      assertEquals(getOnlyElement(params.get(CREDENTIAL_PARAM)), "bar");
   }
}
