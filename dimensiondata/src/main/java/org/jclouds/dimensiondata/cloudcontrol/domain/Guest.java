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
public abstract class Guest {

   Guest() {
   }

   public abstract boolean osCustomization();

   public abstract OperatingSystem operatingSystem();

   /**
    * always null for OSImage, optional for CustomerImage and Server
    */
   @Nullable
   public abstract VmTools vmTools();

   public static Builder builder() {
      return new AutoValue_Guest.Builder();
   }

   @SerializedNames({ "osCustomization", "operatingSystem", "vmTools" })
   public static Guest create(boolean osCustomization, OperatingSystem operatingSystem, VmTools vmTools) {
      return builder().osCustomization(osCustomization).operatingSystem(operatingSystem).vmTools(vmTools).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder osCustomization(boolean osCustomization);

      public abstract Builder operatingSystem(OperatingSystem operatingSystem);

      public abstract Builder vmTools(VmTools vmTools);

      public abstract Guest build();
   }
}
