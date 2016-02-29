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
package org.apache.jclouds.profitbricks.rest.binder.image;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;

public class UpdateImageRequestBinder extends BaseProfitBricksRequestBinder<Image.Request.UpdatePayload> {

   final Map<String, Object> requestBuilder;
   final Json jsonBinder;
   
   private String imageId;

   @Inject
   UpdateImageRequestBinder(Json jsonBinder) {
      super("image");
      this.jsonBinder = jsonBinder;
      this.requestBuilder = new HashMap<String, Object>();
   }

   @Override
   protected String createPayload(Image.Request.UpdatePayload payload) {

      checkNotNull(payload, "payload");
      checkNotNull(payload.id(), "imageId");
      
      imageId = payload.id();

      if (payload.name() != null)
        requestBuilder.put("name", payload.name());

      if (payload.description() != null)
        requestBuilder.put("description", payload.description());

      if (payload.licenceType() != null)
        requestBuilder.put("licenceType", payload.licenceType());

      if (payload.cpuHotPlug() != null)
        requestBuilder.put("cpuHotPlug", payload.cpuHotPlug());

      if (payload.cpuHotUnplug() != null)
        requestBuilder.put("cpuHotUnplug", payload.cpuHotUnplug());

      if (payload.ramHotPlug() != null)
        requestBuilder.put("ramHotPlug", payload.ramHotPlug());

      if (payload.ramHotUnplug() != null)
        requestBuilder.put("ramHotUnplug", payload.ramHotUnplug());

      if (payload.nicHotPlug() != null)
        requestBuilder.put("nicHotPlug", payload.nicHotPlug());

      if (payload.nicHotUnplug() != null)
        requestBuilder.put("nicHotUnplug", payload.nicHotUnplug());

      if (payload.discVirtioHotPlug() != null)
        requestBuilder.put("discVirtioHotPlug", payload.discVirtioHotPlug());

      if (payload.discVirtioHotUnplug() != null)
        requestBuilder.put("discVirtioHotUnplug", payload.discVirtioHotUnplug());

      if (payload.discScsiHotPlug() != null)
        requestBuilder.put("discScsiHotPlug", payload.discScsiHotPlug());

      if (payload.discScsiHotUnplug() != null)
        requestBuilder.put("discScsiHotUnplug", payload.discScsiHotUnplug());
      
      return jsonBinder.toJson(requestBuilder);
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {              
      R request = (R) fromRequest.toBuilder().replacePath(String.format("/rest/images/%s", imageId)).build();
      return super.createRequest(request, payload);
   }

}
