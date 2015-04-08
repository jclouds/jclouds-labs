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


import autovalue.shaded.com.google.common.common.collect.ImmutableMap;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Map;

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
      return new AutoValue_UpdateStack.Builder().parameters(null);
   }

   public Builder toBuilder() {
      return builder()
            .template(getTemplate())
            .templateUrl(getTemplateUrl())
            .parameters(getParameters());
   }

   @SerializedNames({"template", "template_url", "parameters"})
   private static UpdateStack create(@Nullable String template, @Nullable String templateUrl, @Nullable Map<String, Object> parameters) {
      return builder()
            .template(template)
            .templateUrl(templateUrl)
            .parameters(parameters).build();
   }


   public static final class Builder {

      private String template;
      private String templateUrl;
      private Map<String, Object> parameters;

      Builder() {
      }

      Builder(CreateStack source) {
         template(source.getTemplate());
         templateUrl(source.getTemplateUrl());
         parameters(source.getParameters());
      }

      /**
       * @see UpdateStack#getTemplate()
       */
      public Builder template(String template) {
         this.template = template;
         return this;
      }

      /**
       * @see UpdateStack#getTemplateUrl()
       */
      public Builder templateUrl(String templateUrl) {
         this.templateUrl = templateUrl;
         return this;
      }

      /**
       * @see UpdateStack#getParameters()
       */
      public Builder parameters(Map<String, Object> parameters) {
         this.parameters = parameters;
         return this;
      }

      public UpdateStack build() {
         UpdateStack result = new AutoValue_UpdateStack(
               this.template,
               this.templateUrl,
               parameters != null ? ImmutableMap.copyOf(this.parameters) : null);
         return result;
      }
   }
}

