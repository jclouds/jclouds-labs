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
package org.jclouds.openstack.marconi.v1.options;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Options used to control the messages returned in the response.
 */
public class ListQueuesOptions extends PaginationOptions {

   public static final ListQueuesOptions NONE = new ListQueuesOptions();

   /**
    * {@inheritDoc}
    */
   @Override
   public ListQueuesOptions queryParameters(Multimap<String, String> queryParams) {
      checkNotNull(queryParams, "queryParams");
      queryParameters.putAll(queryParams);
      return this;
   }

   /**
    * @see Builder#marker(String)
    */
   @Override
   public ListQueuesOptions marker(String marker) {
      super.marker(marker);
      return this;
   }

   /**
    * @see Builder#limit(int)
    */
   @Override
   public ListQueuesOptions limit(int limit) {
      super.limit(limit);
      return this;

   }

   /**
    * @see Builder#detailed(boolean)
    */
   public ListQueuesOptions detailed(boolean detailed) {
      queryParameters.put("detailed", Boolean.toString(detailed));
      return this;
   }

   /**
    * @return The String representation of the marker for these StreamMessagesOptions.
    */
   public String getMarker() {
      return Iterables.getOnlyElement(queryParameters.get("marker"));
   }

   public static class Builder {
      /**
       * @see PaginationOptions#queryParameters(Multimap)
       */
      public static ListQueuesOptions queryParameters(Multimap<String, String> queryParams) {
         ListQueuesOptions options = new ListQueuesOptions();
         return options.queryParameters(queryParams);
      }

      /**
       * Specifies the name of the last queue received in a previous request, or none to get the first page of results.
       */
      public static ListQueuesOptions marker(String marker) {
         ListQueuesOptions options = new ListQueuesOptions();
         return options.marker(marker);
      }

      /**
       * Specifies the number of queues to return. The default value for the number of queues returned is 10. If you do
       * not specify this parameter, the default number of queues is returned.
       */
      public static ListQueuesOptions limit(int limit) {
         ListQueuesOptions options = new ListQueuesOptions();
         return options.limit(limit);
      }

      /**
       * Determines whether queue metadata is included in the list.
       */
      public static ListQueuesOptions detailed(boolean detailed) {
         ListQueuesOptions options = new ListQueuesOptions();
         return options.detailed(detailed);
      }
   }
}
