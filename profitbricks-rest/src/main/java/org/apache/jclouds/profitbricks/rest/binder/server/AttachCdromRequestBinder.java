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

import com.google.inject.Inject;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import com.google.common.base.Supplier;
import java.net.URI;
import org.jclouds.location.Provider;
import static com.google.common.base.Preconditions.checkNotNull;

public class AttachCdromRequestBinder extends BaseProfitBricksRequestBinder<Server.Request.AttachCdromPayload> {

   String dataCenterId;
   String serverId;

   @Inject
   AttachCdromRequestBinder(Json jsonBinder, @Provider Supplier<URI> endpointSupplier) {
      super("cdrom", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(Server.Request.AttachCdromPayload payload) {
      
      checkNotNull(payload, "payload");
      
      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.serverId(), "serverId");
      checkNotNull(payload.imageId(), "imageId");

      dataCenterId = payload.dataCenterId();
      serverId = payload.serverId();
      
      formMap.put("id", payload.imageId());
      return jsonBinder.toJson(formMap);
   }
   
   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      return super.createRequest(genRequest(String.format("datacenters/%s/servers/%s/cdroms", dataCenterId, serverId), fromRequest), payload);
   }

}
