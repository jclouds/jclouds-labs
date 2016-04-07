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
package org.apache.jclouds.profitbricks.rest.binder.firewall;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.FirewallRule;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;

public class CreateFirewallRuleRequestBinder extends BaseProfitBricksRequestBinder<FirewallRule.Request.CreatePayload> {

   private String dataCenterId;
   private String serverId;
   private String nicId;

   @Inject
   CreateFirewallRuleRequestBinder(Json jsonBinder,  @Provider Supplier<URI> endpointSupplier) {
      super("firewallRule", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(FirewallRule.Request.CreatePayload payload) {
            
      checkNotNull(payload, "payload");
      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.serverId(), "serverId");
      checkNotNull(payload.nicId(), "nicId");      
      
      dataCenterId = payload.dataCenterId();
      serverId = payload.serverId();
      nicId = payload.nicId();
      
      Map<String, Object> properties = new HashMap<String, Object>();
      
      properties.put("protocol",  payload.protocol());
      
      putIfPresent(properties, "name", payload.name());
      putIfPresent(properties, "sourceMac", payload.sourceMac());
      putIfPresent(properties, "sourceIp", payload.sourceIp());
      putIfPresent(properties, "targetIp", payload.targetIp());
      putIfPresent(properties, "icmpCode", payload.icmpCode());
      putIfPresent(properties, "portRangeStart", payload.portRangeStart());
      putIfPresent(properties, "portRangeEnd", payload.portRangeEnd());
            
      requestBuilder.put("properties", properties);
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {
      return super.createRequest(genRequest(String.format("datacenters/%s/servers/%s/nics/%s/firewallrules", dataCenterId, serverId, nicId), fromRequest), payload);
   }

}
