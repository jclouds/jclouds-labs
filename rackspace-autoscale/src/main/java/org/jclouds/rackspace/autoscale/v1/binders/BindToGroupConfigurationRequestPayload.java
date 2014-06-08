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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rackspace.autoscale.v1.domain.GroupConfiguration;
import org.jclouds.rest.MapBinder;

import com.google.common.collect.Maps;

public class BindToGroupConfigurationRequestPayload implements MapBinder {

   protected final Json jsonBinder;

   @Inject
   public BindToGroupConfigurationRequestPayload(Json jsonBinder) {
      this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      GroupConfiguration gc = (GroupConfiguration) postParams.get("groupConfiguration");
      Map<String, Object> gcMap = Maps.newHashMap();
      gcMap.put("name", gc.getName());
      gcMap.put("cooldown", gc.getCooldown());
      gcMap.put("minEntities", gc.getMinEntities());
      gcMap.put("maxEntities", gc.getMaxEntities());
      gcMap.put("metadata", gc.getMetadata());

      request.setPayload(jsonBinder.toJson(gcMap));
      request.getPayload().getContentMetadata().setContentType("application/json");
      return request;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("Illegal unwrap operation");
   }
}
