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
import com.google.inject.Inject;
import org.jclouds.dimensiondata.cloudcontrol.domain.CPU;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Options to customize server creation.
 */
public class CreateServerOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   private String description;
   private CPU cpu;
   private Integer memoryGb;
   private String primaryDns;
   private String secondaryDns;
   private String microsoftTimeZone;

   public CreateServerOptions() {

   }

   CreateServerOptions(String description, CPU cpu, Integer memoryGb, String primaryDns, String secondaryDns,
         String microsoftTimeZone) {
      this.description = description;
      this.cpu = cpu;
      this.memoryGb = memoryGb;
      this.primaryDns = primaryDns;
      this.secondaryDns = secondaryDns;
      this.microsoftTimeZone = microsoftTimeZone;
   }

   @AutoValue
   abstract static class ServerRequest {

      abstract String name();

      abstract String imageId();

      abstract Boolean start();

      abstract NetworkInfo networkInfo();

      abstract List<Disk> disks();

      @Nullable
      abstract String administratorPassword();

      @Nullable
      abstract String description();

      @Nullable
      abstract CPU cpu();

      @Nullable
      abstract Integer memoryGb();

      @Nullable
      abstract String primaryDns();

      @Nullable
      abstract String secondaryDns();

      @Nullable
      abstract String microsoftTimeZone();

      @SerializedNames({ "name", "imageId", "start", "networkInfo", "disk", "administratorPassword", "description",
            "cpu", "memoryGb", "primaryDns", "secondaryDns", "microsoftTimeZone" })
      static ServerRequest create(String name, String imageId, Boolean start, NetworkInfo networkInfo, List<Disk> disks,
            String administratorPassword, String description, CPU cpu, Integer memoryGb, String primaryDns,
            String secondaryDns, String microsoftTimeZone) {
         return new AutoValue_CreateServerOptions_ServerRequest(name, imageId, start, networkInfo, disks,
               administratorPassword, description, cpu, memoryGb, primaryDns, secondaryDns, microsoftTimeZone);
      }

      ServerRequest() {
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest Server = ServerRequest
            .create(checkNotNull(postParams.get("name"), "name parameter not present").toString(),
                  checkNotNull(postParams.get("imageId"), "imageId parameter not present").toString(),
                  Boolean.valueOf(checkNotNull(postParams.get("start"), "start parameter not present").toString()),
                  (NetworkInfo) checkNotNull(postParams.get("networkInfo"), "image parameter not present"),
                  (List<Disk>) checkNotNull(postParams.get("disk"), "disk parameter not present"),
                  (String) postParams.get("administratorPassword"), description, cpu, memoryGb, primaryDns,
                  secondaryDns, microsoftTimeZone);

      return bindToRequest(request, Server);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

   public String getDescription() {
      return description;
   }

   public CPU getCpu() {
      return cpu;
   }

   public Integer getMemoryGb() {
      return memoryGb;
   }

   public String getPrimaryDns() {
      return primaryDns;
   }

   public String getSecondaryDns() {
      return secondaryDns;
   }

   public String getMicrosoftTimeZone() {
      return microsoftTimeZone;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String description;
      private CPU cpu;
      private Integer memoryGb;
      private String primaryDns;
      private String secondaryDns;
      private String microsoftTimeZone;

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder cpu(CPU cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder memoryGb(Integer memoryGb) {
         this.memoryGb = memoryGb;
         return this;
      }

      public Builder primaryDns(String primaryDns) {
         this.primaryDns = primaryDns;
         return this;
      }

      public Builder secondaryDns(String secondaryDns) {
         this.secondaryDns = secondaryDns;
         return this;
      }

      public Builder microsoftTimeZone(String microsoftTimeZone) {
         this.microsoftTimeZone = microsoftTimeZone;
         return this;
      }

      public CreateServerOptions build() {
         return new CreateServerOptions(description, cpu, memoryGb, primaryDns, secondaryDns, microsoftTimeZone);
      }
   }
}
