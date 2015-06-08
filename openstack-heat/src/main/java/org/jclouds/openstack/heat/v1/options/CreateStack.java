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
 * Representation of create stack  options.
 */
@AutoValue
public abstract class CreateStack {

   /**
    * @see Builder#name(String)
    */
    public abstract String getName();

   /**
    * @see Builder#template(String)
    */
   @Nullable public abstract String getTemplate();

   /**
    * @see Builder#templateUrl(String)
    */
   @Nullable public abstract String getTemplateUrl();

   /**
    * @see Builder#parameters(Map<String, Object>)
    */
   @Nullable public abstract Map<String, Object> getParameters();

   /**
    * @see Builder#disableRollback(boolean)
    */
   public abstract boolean isDisableRollback();

   /**
    * @see Builder#files(Map<String, String>)
    */
   @Nullable public abstract Map<String, String> getFiles();

   /**
    * @see Builder#environment(String)
    */
   @Nullable public abstract String getEnvironment();

   public Builder toBuilder() {
      return builder()
            .name(getName())
            .template(getTemplate())
            .templateUrl(getTemplateUrl())
            .parameters(getParameters())
            .disableRollback(isDisableRollback())
            .files(getFiles())
            .environment(getEnvironment());
   }

   @SerializedNames({"stack_name", "template", "template_url", "parameters", "disable_rollback", "files", "environment" })
   private static CreateStack create(String name, @Nullable String template, @Nullable String templateUrl, @Nullable Map<String, Object> parameters, boolean disableRollback, @Nullable Map<String, String> files, @Nullable String environment) {
      return builder()
            .name(name)
            .template(template)
            .templateUrl(templateUrl)
            .parameters(parameters)
            .disableRollback(disableRollback)
            .files(files)
            .environment(environment).build();
   }

   public static Builder builder() {
      return new AutoValue_CreateStack.Builder().disableRollback(true).files(null).environment(null);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder name(String name);
      public abstract Builder template(String template);
      public abstract Builder templateUrl(String templateUrl);
      public abstract Builder parameters(Map<String, Object> parameters);
      public abstract Builder disableRollback(boolean disableRollback);
      public abstract Builder files(Map<String, String> files);
      public abstract Builder environment(String environment);

      abstract Map<String, Object> getParameters();
      abstract Map<String, String> getFiles();

      abstract CreateStack autoBuild();

      public CreateStack build() {
         parameters(getParameters() != null ? ImmutableMap.copyOf(getParameters()) : null);
         files(getFiles() != null ? ImmutableMap.copyOf(getFiles()) : null);
         return autoBuild();
      }
   }
}
