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
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;

public class CreateVolumeRequestBinder extends BaseProfitBricksRequestBinder<Volume.Request.CreatePayload> {

   private String dataCenterId;

   @Inject
   CreateVolumeRequestBinder(Json jsonBinder, @Provider Supplier<URI> endpointSupplier) {
      super("volume", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(Volume.Request.CreatePayload payload) {
      
      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.type(), "type");

      dataCenterId = payload.dataCenterId();
      
      Map<String, Object> properties = new HashMap<String, Object>();
      
      properties.put("type", payload.type());
      
      properties.put("size",  payload.size());
      
      if (payload.name() != null)
         properties.put("name", payload.name());
      
      if (payload.bus() != null)
         properties.put("bus", payload.bus());
      
      if (payload.sshKeys() != null)
         properties.put("sshKeys", payload.sshKeys());
      
      if (payload.imagePassword() != null)
         properties.put("imagePassword", payload.imagePassword());
      
      if (payload.imageAlias() != null)
         properties.put("imageAlias", payload.imageAlias());
      
      if (payload.image() != null)
         properties.put("image", payload.image());
      else if (payload.licenceType() != null)
         properties.put("licenceType", payload.licenceType());
      
      requestBuilder.put("properties", properties);
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      return super.createRequest(genRequest(String.format("datacenters/%s/volumes", dataCenterId), fromRequest), payload);
   }

}
