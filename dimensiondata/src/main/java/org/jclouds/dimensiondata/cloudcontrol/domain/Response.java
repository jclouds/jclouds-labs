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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Response {

   Response() {
   }

   public abstract String operation();

   public abstract String responseCode();

   public abstract String message();

   @Nullable
   public abstract List<Property> info();

   @Nullable
   public abstract List<Property> warning();

   @Nullable
   public abstract List<Property> error();

   public abstract String requestId();

   @SerializedNames({ "operation", "responseCode", "message", "info", "warning", "error", "requestId" })
   public static Response create(String operation, String responseCode, String message, List<Property> info,
         List<Property> warning, List<Property> error, String requestId) {
      return builder().operation(operation).responseCode(responseCode).message(message).info(info).warning(warning)
            .error(error).requestId(requestId).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder operation(String operation);

      public abstract Builder responseCode(String responseCode);

      public abstract Builder message(String message);

      public abstract Builder info(List<Property> info);

      public abstract Builder warning(List<Property> warning);

      public abstract Builder error(List<Property> error);

      public abstract Builder requestId(String requestId);

      abstract Response autoBuild();

      abstract List<Property> warning();

      abstract List<Property> error();

      abstract List<Property> info();

      public Response build() {
         warning(warning() != null ? ImmutableList.copyOf(warning()) : null);
         error(error() != null ? ImmutableList.copyOf(error()) : null);
         info(info() != null ? ImmutableList.copyOf(info()) : null);
         return autoBuild();
      }
   }

   public static Builder builder() {
      return new AutoValue_Response.Builder();
   }
}
