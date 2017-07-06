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
package org.jclouds.dimensiondata.cloudcontrol.domain.options;

import com.google.auto.value.AutoValue;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CloneServerOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   private final String description;
   private final String clusterId;
   private final boolean guestOsCustomization;

   public CloneServerOptions(String description, String clusterId, boolean guestOsCustomization) {
      this.description = description;
      this.clusterId = clusterId;
      this.guestOsCustomization = guestOsCustomization;
   }

   @AutoValue
   abstract static class CloneServerRequest {

      abstract String id();

      abstract String imageName();

      @Nullable
      abstract String description();

      @Nullable
      abstract String clusterId();

      abstract boolean guestOsCustomization();

      @SerializedNames({ "id", "imageName", "description", "clusterId", "guestOsCustomization" })
      static CloneServerRequest create(String id, String imageName, String description, String clusterId,
            Boolean guestOsCustomization) {
         return new AutoValue_CloneServerOptions_CloneServerRequest(id, imageName, description, clusterId,
               guestOsCustomization);
      }

      CloneServerRequest() {
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      CloneServerRequest cloneServer = CloneServerRequest
            .create(checkNotNull(postParams.get("id"), "id parameter not present").toString(),
                  checkNotNull(postParams.get("imageName"), "imageName parameter not present").toString().toString(),
                  description, clusterId, guestOsCustomization);

      return bindToRequest(request, cloneServer);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

   public String getDescription() {
      return description;
   }

   public String getClusterId() {
      return clusterId;
   }

   public boolean isGuestOsCustomization() {
      return guestOsCustomization;
   }

   public static class Builder {
      private String description;
      private String clusterId;
      private boolean guestOsCustomization = false;

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder clusterId(String clusterId) {
         this.clusterId = clusterId;
         return this;
      }

      public Builder guestOsCustomization(boolean guestOsCustomization) {
         this.guestOsCustomization = guestOsCustomization;
         return this;
      }

      public CloneServerOptions build() {
         return new CloneServerOptions(description, clusterId, guestOsCustomization);
      }
   }

   public static CloneServerOptions.Builder builder() {
      return new CloneServerOptions.Builder();
   }

}
