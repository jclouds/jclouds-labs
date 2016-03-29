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
package org.apache.jclouds.profitbricks.rest.binder.lan;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Lan;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;

public class CreateLanRequestBinder extends BaseProfitBricksRequestBinder<Lan.Request.CreatePayload> {

   private String dataCenterId;

   @Inject
   CreateLanRequestBinder(Json jsonBinder,  @Provider Supplier<URI> endpointSupplier) {
      super("lan", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(Lan.Request.CreatePayload payload) {

      dataCenterId = payload.dataCenterId();
      
      Map<String, Object> properties = new HashMap<String, Object>();
      
      if (payload.name() != null)
         properties.put("name", payload.name());
      
      if (payload.isPublic() != null)
         properties.put("public", payload.isPublic());
      
      requestBuilder.put("properties", properties);
      
      if (payload.nics() != null) {
         Map<String, Object> entities = new HashMap<String, Object>();
         entities.put("nics", payload.nics());
         properties.put("entities", entities);
      }
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      return super.createRequest(genRequest(String.format("datacenters/%s/lans", dataCenterId), fromRequest), payload);
   }

}
