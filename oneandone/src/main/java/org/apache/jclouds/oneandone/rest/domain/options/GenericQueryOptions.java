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
package org.apache.jclouds.oneandone.rest.domain.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class GenericQueryOptions extends BaseHttpRequestOptions {

   public static final String PAGE = "page";
   public static final String PERPAGE = "per_page";
   public static final String SORT = "sort";
   public static final String QUERY = "q";
   public static final String FIELDS = "fields";

   public GenericQueryOptions options(int page, int perPage, String sort, String query, String fields) {

      if (page != 0) {
         queryParameters.put(PAGE, String.valueOf(page));
      }
      if (perPage != 0) {
         queryParameters.put(PERPAGE, String.valueOf(perPage));
      }
      if (sort != null && !sort.isEmpty()) {
         queryParameters.put(SORT, sort);
      }
      if (query != null && !query.isEmpty()) {
         queryParameters.put(QUERY, query);
      }
      if (fields != null && !fields.isEmpty()) {
         queryParameters.put(FIELDS, fields);
      }
      return this;
   }

}
