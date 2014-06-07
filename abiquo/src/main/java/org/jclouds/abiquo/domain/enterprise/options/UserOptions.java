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

/**
 * Available options to query users.
 */
public class UserOptions extends BaseHttpRequestOptions implements Cloneable {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      UserOptions options = new UserOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder extends BaseFilterOptionsBuilder<Builder> {

      private final UserOptions options = new UserOptions();

      public Builder page(int page) {
         this.options.queryParameters.put("page", String.valueOf(page));
         return this;
      }

      @Override
      public Builder limit(int limit) {
         this.options.queryParameters.put("numResults", String.valueOf(limit));
         return this;
      }

      public UserOptions build() {
         return addFilterOptions(this.options);
      }
   }
}
