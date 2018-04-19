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
package org.jclouds.dimensiondata.cloudcontrol.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class IdListFilters extends BaseHttpRequestOptions {

   private IdListFilters() {
   }

   public IdListFilters ids(final String... ids) {
      for (String id : ids) {
         this.queryParameters.put("id", checkNotNull(id, "id"));
      }
      return this;
   }

   public IdListFilters ids(final Collection<String> ids) {
      for (String id : ids) {
         this.queryParameters.put("id", checkNotNull(id, "id"));
      }
      return this;
   }

   public IdListFilters paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static class Builder {

      /**
       * @see IdListFilters#ids(Collection<String>)
       */
      public static IdListFilters ids(final Collection<String> ids) {
         IdListFilters options = new IdListFilters();
         return options.ids(ids);
      }

      /**
       * @see IdListFilters#ids(String...)
       */
      public static IdListFilters ids(final String... ids) {
         IdListFilters options = new IdListFilters();
         return options.ids(ids);
      }

      /**
       * @see IdListFilters#paginationOptions(PaginationOptions)
       */
      public static IdListFilters paginationOptions(PaginationOptions paginationOptions) {
         IdListFilters options = new IdListFilters();
         return options.paginationOptions(paginationOptions);
      }
   }
}

