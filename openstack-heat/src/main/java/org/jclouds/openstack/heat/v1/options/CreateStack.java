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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Map;

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

   public static Builder builder() {
      return new AutoValue_CreateStack.Builder().disableRollback(true).files(null).environment(null);
   }

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

   public static final class Builder {

      private String name;
      private String template;
      private String templateUrl;
      private Map<String, Object> parameters;
      private boolean disableRollback = true;
      private Map<String, String> files;
      private String environment;

      Builder() {
      }

      Builder(CreateStack source) {
         name(source.getName());
         template(source.getTemplate());
         templateUrl(source.getTemplateUrl());
         parameters(source.getParameters());
         disableRollback(source.isDisableRollback());
         files(source.getFiles());
         environment(source.getEnvironment());
      }

      /**
       * @param name - The name of the stack
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param template - The stack template to instantiate.
       */
      public Builder template(String template) {
         this.template = template;
         return this;
      }

      /**
       * @param templateUrl - A URI to the location containing the stack template to instantiate.
       */
      public Builder templateUrl(String templateUrl) {
         this.templateUrl = templateUrl;
         return this;
      }

      /**
       * @param parameters - The properties for the template
       */
      public Builder parameters(Map<String, Object> parameters) {
         this.parameters = parameters;
         return this;
      }

      /**
       * @param disableRollback - Controls whether a failure during stack creation causes deletion of all previously-created resources in that stack. The default is True
       */
      public Builder disableRollback(boolean disableRollback) {
         this.disableRollback = disableRollback;
         return this;
      }

      /**
       * @param files - The properties for the template
       */
      public Builder files(Map<String, String> files) {
         this.files = files;
         return this;
      }

      /**
       * @param environment - used to affect the runtime behaviour of the template
       */
      public Builder environment(String environment) {
         this.environment = environment;
         return this;
      }

      public CreateStack build() {
         CreateStack result = new AutoValue_CreateStack(
               this.name,
               this.template,
               this.templateUrl,
               this.parameters != null ? ImmutableMap.copyOf(this.parameters) : null,
               this.disableRollback,
               this.files != null ? ImmutableMap.copyOf(this.files) : null,
               this.environment);
         return result;
      }
   }
}
