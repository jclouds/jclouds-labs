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
import org.apache.jclouds.oneandone.rest.domain.Hdd.CreateHdd;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Hardware {

   public abstract double vcore();

   public abstract double coresPerProcessor();

   public abstract double ram();

   public abstract List<Hdd> hdds();

   @SerializedNames({"vcore", "cores_per_processor", "ram", "hdds"})
   public static Hardware create(double vcore, double coresPerProcessor, double ram, List<Hdd> hdds) {
      return new AutoValue_Hardware(vcore, coresPerProcessor, ram, hdds);
   }

   @AutoValue
   public abstract static class CreateHardware {

      public abstract double vcore();

      public abstract double coresPerProcessor();

      public abstract double ram();

      public abstract List<CreateHdd> hdds();

      @SerializedNames({"vcore", "cores_per_processor", "ram", "hdds"})
      public static CreateHardware create(double vcore, double coresPerProcessor, double ram, List<CreateHdd> hdds) {
         return new AutoValue_Hardware_CreateHardware(vcore, coresPerProcessor, ram, hdds);
      }
   }

   @AutoValue
   public abstract static class UpdateHardware {

      public abstract double ram();

      public abstract double coresPerProcessor();

      public abstract double vcore();

      @SerializedNames({"ram", "cores_per_processor", "vcore"})
      public static UpdateHardware create(double vcore, double coresPerProcessor, double ram) {
         return new AutoValue_Hardware_UpdateHardware(vcore, coresPerProcessor, ram);
      }
   }

}
