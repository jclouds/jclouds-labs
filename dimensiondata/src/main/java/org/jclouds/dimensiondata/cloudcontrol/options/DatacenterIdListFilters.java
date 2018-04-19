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

public class DatacenterIdListFilters extends BaseHttpRequestOptions {

   private DatacenterIdListFilters() {
   }

   public DatacenterIdListFilters datacenterIds(final String... datacenterIds) {
      for (String datacenterId : datacenterIds) {
         this.queryParameters.put("datacenterId", checkNotNull(datacenterId, "datacenterId"));
      }
      return this;
   }

   public DatacenterIdListFilters datacenterIds(final Collection<String> datacenterIds) {
      for (String datacenterId : datacenterIds) {
         this.queryParameters.put("datacenterId", checkNotNull(datacenterId, "datacenterId"));
      }
      return this;
   }

   public DatacenterIdListFilters paginationOptions(final PaginationOptions paginationOptions) {
      this.queryParameters.putAll(paginationOptions.buildQueryParameters());
      return this;
   }

   public static class Builder {

      /**
       * @see DatacenterIdListFilters#datacenterIds(Collection<String>)
       */
      public static DatacenterIdListFilters datacenterId(final Collection<String> datacenterIds) {
         DatacenterIdListFilters options = new DatacenterIdListFilters();
         return options.datacenterIds(datacenterIds);
      }

      /**
       * @see DatacenterIdListFilters#datacenterIds(String...)
       */
      public static DatacenterIdListFilters datacenterId(final String... ids) {
         DatacenterIdListFilters options = new DatacenterIdListFilters();
         return options.datacenterIds(ids);
      }

      /**
       * @see DatacenterIdListFilters#paginationOptions(PaginationOptions)
       */
      public static DatacenterIdListFilters paginationOptions(PaginationOptions paginationOptions) {
         DatacenterIdListFilters options = new DatacenterIdListFilters();
         return options.paginationOptions(paginationOptions);
      }
   }
}
