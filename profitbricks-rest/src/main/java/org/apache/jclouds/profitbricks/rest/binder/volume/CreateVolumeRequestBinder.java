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
package org.apache.jclouds.profitbricks.rest.binder.volume;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;

public class CreateVolumeRequestBinder extends BaseProfitBricksRequestBinder<Volume.Request.CreatePayload> {

   protected final Map<String, Object> requestBuilder;
   final Json jsonBinder;
   
   private String dataCenterId;

   @Inject
   CreateVolumeRequestBinder(Json jsonBinder) {
      super("volume");
      this.jsonBinder = jsonBinder;
      this.requestBuilder = new HashMap<String, Object>();
   }

   @Override
   protected String createPayload(Volume.Request.CreatePayload payload) {
      
      checkNotNull(payload.dataCenterId(), "dataCenterId");

      dataCenterId = payload.dataCenterId();
      
      Map<String, Object> properties = new HashMap<String, Object>();
      
      properties.put("size",  payload.size());
      
      if (payload.name() != null)
         properties.put("name", payload.name());
      
      if (payload.bus() != null)
         properties.put("bus", payload.bus());
      
      if (payload.type() != null)
         properties.put("type", payload.type());
      
      if (payload.imagePassword() != null)
         properties.put("imagePassword", payload.imagePassword());
      
      if (payload.image() != null)
         properties.put("image", payload.image());
      else if (payload.licenceType() != null)
         properties.put("licenceType", payload.licenceType());
      
      requestBuilder.put("properties", properties);
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/datacenters/%s/volumes", dataCenterId)).build();
      return super.createRequest(request, payload);
   }

}
