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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class VMwareTools {

   VMwareTools() {
   }

   @Nullable
   public abstract String versionStatus();

   @Nullable
   public abstract String runningStatus();

   @Nullable
   public abstract Integer apiVersion();

   @SerializedNames({ "versionStatus", "runningStatus", "apiVersion" })
   public static VMwareTools create(String versionStatus, String runningStatus, Integer apiVersion) {
      return builder().versionStatus(versionStatus).runningStatus(runningStatus).apiVersion(apiVersion).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder versionStatus(String versionStatus);

      public abstract Builder runningStatus(String runningStatus);

      public abstract Builder apiVersion(Integer apiVersion);

      public abstract VMwareTools build();
   }

   public static Builder builder() {
      return new AutoValue_VMwareTools.Builder();
   }
}
