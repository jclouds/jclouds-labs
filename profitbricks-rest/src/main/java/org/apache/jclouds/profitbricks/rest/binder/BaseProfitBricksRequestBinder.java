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
package org.apache.jclouds.profitbricks.rest.binder;

import java.util.Map;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.util.HashMap;
import org.jclouds.json.Json;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import java.net.URI;
import com.google.common.base.Supplier;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseProfitBricksRequestBinder<T> implements MapBinder {

   protected final Supplier<URI> endpointSupplier;
   protected final String paramName;
   protected final Map<String, Object> requestBuilder;
   protected final Json jsonBinder;

   @Inject
   protected BaseProfitBricksRequestBinder(String paramName, Json jsonBinder, Supplier<URI> endpointSupplier) {
      this.paramName = checkNotNull(paramName, "Initialize 'paramName' in constructor");
      this.jsonBinder = jsonBinder;
      this.requestBuilder = new HashMap<String, Object>();
      this.endpointSupplier = endpointSupplier;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkNotNull(request, "request");

      Object obj = checkNotNull(postParams.get(paramName), "Param '%s' cannot be null.", paramName);
      T payload = (T) obj;

      return createRequest(request, createPayload(payload));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   protected abstract String createPayload(T payload);

   protected static String formatIfNotEmpty(String pattern, Object param) {
      return Strings.isNullOrEmpty(nullableToString(param)) ? "" : String.format(pattern, param);
   }

   protected static String nullableToString(Object object) {
      return object == null ? "" : object.toString();
   }

   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {
      MutableContentMetadata metadata = new BaseMutableContentMetadata();
      metadata.setContentType("application/vnd.profitbricks.resource+json");
      metadata.setContentLength(Long.valueOf(payload.getBytes().length));

      fromRequest.setPayload(payload);
      fromRequest.getPayload().setContentMetadata(metadata);
      return fromRequest;
   }
   
   protected <R extends HttpRequest> R genRequest(String path, R fromRequest) {          
      R request = (R) fromRequest.toBuilder()
         .replacePath(endpointSupplier.get().getPath() + path)
         .build();
      
      return request;
   }
   
   protected void putIfPresent(Map<String, Object> list, String key, Object value) {
      if (value != null)
         list.put(key, value);
   }
}
