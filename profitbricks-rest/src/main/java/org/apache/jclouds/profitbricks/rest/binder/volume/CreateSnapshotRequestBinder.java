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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.ws.rs.core.MediaType;
import org.apache.jclouds.profitbricks.rest.binder.BaseProfitBricksRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;

public class CreateSnapshotRequestBinder extends BaseProfitBricksRequestBinder<Volume.Request.CreateSnapshotPayload> {

   protected final Multimap<String, String> requestBuilder;
   private String dataCenterId;
   private String volumeId;

   CreateSnapshotRequestBinder() {
      super("snapshot");
      this.requestBuilder = HashMultimap.create();
   }

   @Override
   protected String createPayload(Volume.Request.CreateSnapshotPayload payload) {

      checkNotNull(payload.dataCenterId(), "dataCenterId");
      checkNotNull(payload.volumeId(), "volumeId");
      
      dataCenterId = payload.dataCenterId();
      volumeId = payload.volumeId();
      
      if (payload.name() != null)
         requestBuilder.put("name",  payload.name());
      
      if (payload.description() != null)
         requestBuilder.put("description",  payload.description());
     
      return "";
   }

   @Override
   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {
      
      fromRequest = super.createRequest(fromRequest, payload);
      
      Builder<?> reqBuilder = fromRequest.toBuilder();
                  
      reqBuilder.addFormParams(requestBuilder);
      reqBuilder.replacePath(String.format("/rest/datacenters/%s/volumes/%s/create-snapshot", dataCenterId, volumeId));
      
      R req = (R) reqBuilder.build();
      req.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      
      return req;
   }

}
