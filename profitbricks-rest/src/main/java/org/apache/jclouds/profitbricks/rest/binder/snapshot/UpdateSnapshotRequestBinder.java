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
package org.apache.jclouds.profitbricks.rest.binder.snapshot;

import com.google.inject.Inject;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import java.net.URI;
import org.jclouds.location.Provider;

public class UpdateSnapshotRequestBinder extends BaseProfitBricksRequestBinder<Snapshot.Request.UpdatePayload> {

   private String snapshotId;

   @Inject
   UpdateSnapshotRequestBinder(Json jsonBinder, @Provider Supplier<URI> endpointSupplier) {
      super("snapshot", jsonBinder, endpointSupplier);
   }

   @Override
   protected String createPayload(Snapshot.Request.UpdatePayload payload) {

      checkNotNull(payload.id(), "snapshotId");
      
      snapshotId = payload.id();

      if (payload.name() != null)
        formMap.put("name", payload.name());

      if (payload.description() != null)
        formMap.put("description", payload.description());

      if (payload.licenceType() != null)
        formMap.put("licenceType", payload.licenceType());

      if (payload.cpuHotPlug() != null)
        formMap.put("cpuHotPlug", payload.cpuHotPlug());

      if (payload.cpuHotUnplug() != null)
        formMap.put("cpuHotUnplug", payload.cpuHotUnplug());

      if (payload.ramHotPlug() != null)
        formMap.put("ramHotPlug", payload.ramHotPlug());

      if (payload.ramHotUnplug() != null)
        formMap.put("ramHotUnplug", payload.ramHotUnplug());

      if (payload.nicHotPlug() != null)
        formMap.put("nicHotPlug", payload.nicHotPlug());

      if (payload.nicHotUnplug() != null)
        formMap.put("nicHotUnplug", payload.nicHotUnplug());

      if (payload.discVirtioHotPlug() != null)
        formMap.put("discVirtioHotPlug", payload.discVirtioHotPlug());

      if (payload.discVirtioHotUnplug() != null)
        formMap.put("discVirtioHotUnplug", payload.discVirtioHotUnplug());

      if (payload.discScsiHotPlug() != null)
        formMap.put("discScsiHotPlug", payload.discScsiHotPlug());

      if (payload.discScsiHotUnplug() != null)
        formMap.put("discScsiHotUnplug", payload.discScsiHotUnplug());
      
      return jsonBinder.toJson(formMap);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/snapshots/%s", snapshotId)).build();
      return super.createRequest(request, payload);
   }

}
