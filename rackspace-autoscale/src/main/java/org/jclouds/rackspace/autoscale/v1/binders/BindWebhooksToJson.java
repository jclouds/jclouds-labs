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

import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.autoscale.v1.domain.CreateWebhook;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Bind parameters to a webhook
 */
public class BindWebhooksToJson implements MapBinder {

   private final BindToJsonPayload jsonBinder;

   @Inject
   private BindWebhooksToJson(BindToJsonPayload jsonBinder) {
      this.jsonBinder = jsonBinder;
   }

   @SuppressWarnings("unchecked")
   @Override
   // This binding will potentially get refactored, as right now it is very close, but not completely the same as the create call.
   // Refactoring will depend on whether this call will change any further.
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ImmutableList.Builder<Map<String, Object>> webhookListBuilder = ImmutableList.builder();
      for (CreateWebhook webhook : (List<CreateWebhook>)postParams.get("webhooks") ) {
         ImmutableMap.Builder<String, Object> webhookMap = ImmutableMap.builder();
         webhookMap.put("name", webhook.getName());
         if (!webhook.getMetadata().isEmpty()) {
            webhookMap.put("metadata", webhook.getMetadata());
         }
         webhookListBuilder.add((Map<String, Object>)webhookMap.build());
      }
      return jsonBinder.bindToRequest(request, webhookListBuilder.build());
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("Create webhook is a POST operation");
   }
}
