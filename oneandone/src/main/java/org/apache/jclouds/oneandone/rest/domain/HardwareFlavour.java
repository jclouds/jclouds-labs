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
package org.apache.jclouds.oneandone.rest.domain;

import com.google.auto.value.AutoValue;
import java.util.List;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class HardwareFlavour {

   public abstract String id();

   public abstract String name();

   public abstract Hardware hardware();

   @SerializedNames({"id", "name", "hardware"})
   public static HardwareFlavour create(String id, String name, Hardware hardware) {
      return new AutoValue_HardwareFlavour(id, name, hardware);
   }

   @AutoValue
   public abstract static class Hardware {

      @Nullable
      public abstract String fixedInstanceSizeId();

      public abstract double vcore();

      public abstract double coresPerProcessor();

      public abstract double ram();

      public abstract List<Hdd> hdds();

      @SerializedNames({"fixed_instance_size_id", "vcore", "cores_per_processor", "ram", "hdds"})
      public static Hardware create(String fixedInstanceSizeId, double vcore, double coresPerProcessor, double ram, List<Hdd> hdds) {
         return new AutoValue_HardwareFlavour_Hardware(fixedInstanceSizeId, vcore, coresPerProcessor, ram, hdds);
      }

      @AutoValue
      public abstract static class Hdd {

         public abstract String unit();

         public abstract int size();

         public abstract boolean isMain();

         @SerializedNames({"unit", "size", "is_main"})
         public static Hdd create(String unit, int size, boolean isMain) {
            return new AutoValue_HardwareFlavour_Hardware_Hdd(unit, size, isMain);
         }
      }
   }

}
