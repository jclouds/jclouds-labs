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
public abstract class VmTools {

   public enum Type {
      VMWARE_TOOLS, OPEN_VM_TOOLS
   }

   public enum RunningStatus {
      STARTING, NOT_RUNNING, RUNNING
   }

   public enum VersionStatus {
      CURRENT, NEED_UPGRADE, NOT_INSTALLED, UNMANAGED
   }

   VmTools() {
   }

   public abstract Type type();

   @Nullable
   public abstract VersionStatus versionStatus();

   @Nullable
   public abstract RunningStatus runningStatus();

   @Nullable
   public abstract Integer apiVersion();

   @SerializedNames({ "type", "versionStatus", "runningStatus", "apiVersion" })
   public static VmTools create(Type type, VersionStatus versionStatus, RunningStatus runningStatus,
         Integer apiVersion) {
      return builder().type(type).versionStatus(versionStatus).runningStatus(runningStatus).apiVersion(apiVersion)
            .build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder versionStatus(VersionStatus versionStatus);

      public abstract Builder runningStatus(RunningStatus runningStatus);

      public abstract Builder apiVersion(Integer apiVersion);

      public abstract Builder type(Type type);

      public abstract VmTools build();
   }

   public static Builder builder() {
      return new AutoValue_VmTools.Builder();
   }
}
