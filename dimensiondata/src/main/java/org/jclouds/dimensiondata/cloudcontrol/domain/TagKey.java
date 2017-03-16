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
public abstract class TagKey {

   TagKey() {
   }

   public abstract String id();

   public abstract String name();

   @Nullable
   public abstract String description();

   public abstract boolean valueRequired();

   public abstract boolean displayOnReport();

   @SerializedNames({ "id", "name", "description", "valueRequired", "displayOnReport" })
   public static TagKey create(String id, String name, String description, boolean valueRequired,
         boolean displayOnReport) {
      return builder().id(id).name(name).description(description).valueRequired(valueRequired)
            .displayOnReport(displayOnReport).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);

      public abstract Builder name(String tagKeyName);

      public abstract Builder description(String description);

      public abstract Builder valueRequired(boolean valueRequired);

      public abstract Builder displayOnReport(boolean displayOnReport);

      public abstract TagKey build();

   }

   public static Builder builder() {
      return new AutoValue_TagKey.Builder();
   }
}
