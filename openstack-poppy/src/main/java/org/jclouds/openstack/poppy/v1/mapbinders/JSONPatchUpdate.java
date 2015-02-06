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
package org.jclouds.openstack.poppy.v1.mapbinders;

import java.io.IOException;
import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.rest.MapBinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.google.inject.Inject;

/**
 * This will create a JSONPatch out of a Service and an UpdateService.
 *
 * User side:
 * Get a Service with api.get(service_id)
 * Get a UpdateService builder by using Service.toUpdatableService()
 *    This step will provide an interface that exposes the updatable JSON values to the user.
 * Use the UpdateService.Builder instance to modify and build() a new UpdateService.
 * Send the original Service and the new UpdateService to the api.update method.
 *
 * jclouds side:
 * Convert the Service to UpdateService, but don't change it (this is the source).
 * Serialize both source and target to String
 * Diff to create JSONPatch using dependency.
 * Send the JSONPatch in the request.
 *
 * JSONPatch RFC:
 * https://tools.ietf.org/html/rfc6902
 */
public class JSONPatchUpdate implements MapBinder {
   @Inject
   Json json;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String jsonPatch = null;
      Service service = (Service) postParams.get("service");

      //Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

      String targetService = json.toJson(postParams.get("updateService"));
      String sourceService = json.toJson(service.toUpdatableService().build());

      ObjectMapper mapper = new ObjectMapper();
      try {
         jsonPatch = JsonDiff.asJson(mapper.readTree(sourceService), mapper.readTree(targetService)).toString();
      } catch (IOException e) {
         throw new RuntimeException("Could not create a JSONPatch", e);
      }

      return bindToRequest(request, (Object) jsonPatch);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      request.setPayload((String) input);
      request.getPayload().getContentMetadata().setContentType("application/json");
      return request;
   }
}
