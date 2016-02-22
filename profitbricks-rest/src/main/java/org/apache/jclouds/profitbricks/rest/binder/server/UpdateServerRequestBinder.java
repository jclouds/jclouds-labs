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

public class UpdateServerRequestBinder extends BaseProfitBricksRequestBinder<Server.Request.UpdatePayload> {

   final Map<String, Object> requestBuilder;
   final Json jsonBinder;
   String dataCenterId;
   String serverId;

   @Inject
   UpdateServerRequestBinder(Json jsonBinder) {
      super("server");
      this.jsonBinder = jsonBinder;
      this.requestBuilder = new HashMap<String, Object>();
   }

   @Override
   protected String createPayload(Server.Request.UpdatePayload payload) {

      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.id(), "serverId");
      
      dataCenterId = payload.dataCenterId();
      serverId = payload.id();
      
      requestBuilder.put("name",  payload.name());
      requestBuilder.put("ram",   payload.ram());
      requestBuilder.put("cores", payload.cores());
      
      if (payload.availabilityZone() != null)
         requestBuilder.put("availabilityzone", payload.availabilityZone());
      
      if (payload.licenceType() != null)
         requestBuilder.put("licencetype", payload.licenceType());
      
      if (payload.bootVolume() != null)
         requestBuilder.put("bootVolume", payload.bootVolume());
      else if (payload.bootCdrom() != null)
         requestBuilder.put("bootCdrom", payload.bootCdrom());
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/datacenters/%s/servers/%s", dataCenterId, serverId)).build();
      return super.createRequest(request, payload);
   }

}
