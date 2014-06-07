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
package org.jclouds.abiquo;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.features.BaseAbiquoApiTest;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code AbiquoApi}.
 */
@Test(groups = "unit", testName = "AbiquoApiTest")
public class AbiquoApiTest extends BaseAbiquoApiTest<AbiquoApi> {
   public void testGet() throws SecurityException, NoSuchMethodException, IOException {
      RESTLink link = new RESTLink("edit", "http://foo/bar");
      link.setType(DatacentersDto.BASE_MEDIA_TYPE);

      Invokable<?, ?> method = method(AbiquoApi.class, "get", RESTLink.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(link)));

      assertRequestLineEquals(request, "GET http://foo/bar HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, IdentityFunction.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
