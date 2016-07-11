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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Hdd {

   public abstract String id();

   public abstract double size();

   public abstract boolean isMain();

   @SerializedNames({"id", "size", "is_main"})
   public static Hdd create(String id, double size, boolean isMain) {
      return new AutoValue_Hdd(id, size, isMain);
   }

   @AutoValue
   public abstract static class CreateHddList {

      public abstract List<CreateHdd> hdds();

      @SerializedNames({"hdds"})
      public static CreateHddList create(List<CreateHdd> hdds) {
         return new AutoValue_Hdd_CreateHddList(hdds);
      }
   }

   @AutoValue
   public abstract static class CreateHdd {

      public abstract double size();

      public abstract boolean isMain();

      @SerializedNames({"size", "is_main"})
      public static CreateHdd create(double size, boolean isMain) {
         return new AutoValue_Hdd_CreateHdd(size, isMain);
      }
   }
}
