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
package org.apache.jclouds.profitbricks.rest.binder.nic;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import java.net.URI;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Nic;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;

public class UpdateNicRequestBinder extends BaseProfitBricksRequestBinder<Nic.Request.UpdatePayload> {

   private String dataCenterId;
   private String serverId;
   private String nicId;

   @Inject
   UpdateNicRequestBinder(Json jsonBinder,  @Provider Supplier<URI> endpointSupplier) {
      super("nic", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(Nic.Request.UpdatePayload payload) {
            
      checkNotNull(payload, "payload");
      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.serverId(), "serverId");
      checkNotNull(payload.id(), "id");
      
      dataCenterId = payload.dataCenterId();
      serverId = payload.serverId();
      nicId = payload.id();
            
      requestBuilder.put("lan",  payload.lan());
      
      if (payload.name() != null)
         requestBuilder.put("name", payload.name());
      
      if (payload.ips() != null && !payload.ips().isEmpty())
         requestBuilder.put("ips", payload.ips());
      
      if (payload.dhcp() != null)
         requestBuilder.put("dhcp", payload.dhcp());
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/datacenters/%s/servers/%s/nics/%s", dataCenterId, serverId, nicId)).build();
      return super.createRequest(request, payload);
   }

}
