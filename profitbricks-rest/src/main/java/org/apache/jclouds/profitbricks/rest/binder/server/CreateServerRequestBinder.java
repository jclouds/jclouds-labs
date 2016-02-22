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
package org.apache.jclouds.profitbricks.rest.binder.server;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;

public class CreateServerRequestBinder extends BaseProfitBricksRequestBinder<Server.Request.CreatePayload> {

   final Map<String, Object> requestBuilder;
   final Json jsonBinder;
   String dataCenterId;

   @Inject
   CreateServerRequestBinder(Json jsonBinder) {
      super("server");
      this.jsonBinder = jsonBinder;
      this.requestBuilder = new HashMap<String, Object>();
   }

   @Override
   protected String createPayload(Server.Request.CreatePayload payload) {

      checkNotNull(payload.dataCenterId(), "dataCenterId");

      dataCenterId = payload.dataCenterId();
      
      Map<String, Object> properties = new HashMap<String, Object>();
      
      properties.put("name",  payload.name());
      properties.put("ram",   payload.ram());
      properties.put("cores", payload.cores());
      
      if (payload.availabilityZone() != null)
         properties.put("availabilityzone", payload.availabilityZone());
      
      if (payload.licenceType() != null)
         properties.put("licencetype", payload.licenceType());
      
      if (payload.bootVolume() != null)
         properties.put("bootVolume", payload.bootVolume());
      else if (payload.bootCdrom() != null)
         properties.put("bootCdrom", payload.bootCdrom());
      
      requestBuilder.put("properties", properties);
      
      Server.Entities entities = payload.entities();
      
      if (entities != null) {
         
         Map<String, Object> entitiesParams = new HashMap<String, Object>();
         
         if (entities.volumes() != null)
            entitiesParams.put("volumes", entities.volumes());
         
         if (entities.nics() != null)
            entitiesParams.put("nics", entities.nics());
         
         requestBuilder.put("entities", entitiesParams);
      }

      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/datacenters/%s/servers", dataCenterId)).build();
      return super.createRequest(request, payload);
   }

}
