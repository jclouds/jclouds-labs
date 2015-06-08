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
package org.jclouds.openstack.heat.v1.options;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

/**
 * Representation of updatable options
 */
@AutoValue
public abstract class UpdateStack {

   /**
    * @see Builder#template(String)
    */
   @Nullable public abstract String getTemplate();

   /**
    * @see Builder#templateUrl(String)
    */
   @Nullable public abstract String getTemplateUrl();

   /**
    * @see Builder#parameters(java.util.Map)
    */
   @Nullable public abstract Map<String, Object> getParameters();

   public static Builder builder() {
      return new AutoValue_UpdateStack.Builder();
   }

   public abstract Builder toBuilder();

   @SerializedNames({"template", "template_url", "parameters"})
   private static UpdateStack create(@Nullable String template, @Nullable String templateUrl, @Nullable Map<String, Object> parameters) {
      return builder()
            .template(template)
            .templateUrl(templateUrl)
            .parameters(parameters).build();
   }


   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder template(String template);
      public abstract Builder templateUrl(String templateUrl);
      public abstract Builder parameters(Map<String, Object> parameters);

      abstract Map<String, Object> getParameters();

      abstract UpdateStack autoBuild();

      public UpdateStack build() {
         parameters(getParameters() != null ? ImmutableMap.copyOf(getParameters()) : null);
         return autoBuild();
      }
   }
}
