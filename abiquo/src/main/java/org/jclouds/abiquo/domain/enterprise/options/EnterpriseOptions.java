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
package org.jclouds.abiquo.domain.enterprise.options;

import org.jclouds.abiquo.domain.options.FilterOptions.BaseFilterOptionsBuilder;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.rest.annotations.SinceApiVersion;

/**
 * Available options to query enterprises.
 */
public class EnterpriseOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      EnterpriseOptions options = new EnterpriseOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder extends BaseFilterOptionsBuilder<Builder> {
      private String idPricingTemplate;

      private String idScope;

      private Boolean included;

      public Builder pricingTemplate(final String idPricingTemplate) {
         this.idPricingTemplate = idPricingTemplate;
         return this;
      }

      @SinceApiVersion("2.3")
      public Builder scope(final String scope) {
         this.idScope = scope;
         return this;
      }

      public Builder included(final boolean included) {
         this.included = included;
         return this;
      }

      public EnterpriseOptions build() {
         EnterpriseOptions options = new EnterpriseOptions();

         if (idPricingTemplate != null) {
            options.queryParameters.put("idPricingTemplate", String.valueOf(idPricingTemplate));
         }

         if (idScope != null) {
            options.queryParameters.put("idScope", String.valueOf(idScope));
         }

         if (included != null) {
            options.queryParameters.put("included", String.valueOf(included));
         }

         return addFilterOptions(options);
      }
   }
}
