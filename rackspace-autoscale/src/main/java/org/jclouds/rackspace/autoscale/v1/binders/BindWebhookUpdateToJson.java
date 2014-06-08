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
package org.jclouds.rackspace.autoscale.v1.binders;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Bind parameters to a webhook
 */
public class BindWebhookUpdateToJson implements MapBinder {

   private final BindToJsonPayload jsonBinder;

   @Inject
   private BindWebhookUpdateToJson(BindToJsonPayload jsonBinder) {
      this.jsonBinder = jsonBinder;
   }

   // This binding will potentially get refactored, as right now it is very close, but not completely the same as the create call.
   // Refactoring will depend on whether this call will change any further.
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ImmutableMap.Builder<String, Object> webhook = ImmutableMap.builder();
      webhook.put("name", postParams.get("name"));
      ImmutableMap<String, Object> metadata = postParams.get("metadata") != null ?
            ImmutableMap.copyOf((Map<String, Object>) postParams.get("metadata")) : ImmutableMap.<String, Object>of();
      webhook.put("metadata", metadata);
      return jsonBinder.bindToRequest(request, webhook.build());
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("Create webhook is a POST operation");
   }
}
