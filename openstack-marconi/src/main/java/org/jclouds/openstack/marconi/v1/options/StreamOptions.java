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
public class StreamOptions extends PaginationOptions {

   public static final StreamOptions NONE = new StreamOptions();

   /**
    * {@inheritDoc}
    */
   @Override
   public StreamOptions queryParameters(Multimap<String, String> queryParams) {
      checkNotNull(queryParams, "queryParams");
      queryParameters.putAll(queryParams);
      return this;
   }

   /**
    * @see Builder#marker(String)
    */
   @Override
   public StreamOptions marker(String marker) {
      super.marker(marker);
      return this;
   }

   /**
    * @see Builder#limit(int)
    */
   @Override
   public StreamOptions limit(int limit) {
      super.limit(limit);
      return this;

   }

   /**
    * @see Builder#echo(boolean)
    */
   public StreamOptions echo(boolean echo) {
      queryParameters.put("echo", Boolean.toString(echo));
      return this;
   }

   /**
    * @return The String representation of the marker for these StreamOptions.
    */
   public String getMarker() {
      return Iterables.getOnlyElement(queryParameters.get("marker"));
   }

   public static class Builder {
      /**
       * @see PaginationOptions#queryParameters(Multimap)
       */
      public static StreamOptions queryParameters(Multimap<String, String> queryParams) {
         StreamOptions options = new StreamOptions();
         return options.queryParameters(queryParams);
      }

      /**
       * Specifies an opaque string that the client can use to request the next batch of messages. The marker parameter
       * communicates to the server which messages the client has already received. If you do not specify a value, the
       * API returns all messages at the head of the queue (up to the limit).
       * </p>
       * Clients should make no assumptions about the format or length of the marker. Furthermore, clients should assume
       * that there is no relationship between markers and message IDs.
       */
      public static StreamOptions marker(String marker) {
         StreamOptions options = new StreamOptions();
         return options.marker(marker);
      }

      /**
       * When more messages are available than can be returned in a single request, the client can pick up the next
       * batch of messages by simply using the {@see StremOptions} returned from the previous call in {@code
       * MessageStream#nextStreamOptions()}. Specifies up to 10 messages (the default value) to return. If you do not
       * specify a value for the limit parameter, the default value of 10 is used.
       */
      public static StreamOptions limit(int limit) {
         StreamOptions options = new StreamOptions();
         return options.limit(limit);
      }

      /**
       * The echo parameter determines whether the API returns a client's own messages, as determined by the clientId
       * (UUID) portion of the client. If you do not specify a value, echo uses the default value of false. If you are
       * experimenting with the API, you might want to set echo=true in order to see the messages that you posted.
       */
      public static StreamOptions echo(boolean echo) {
         StreamOptions options = new StreamOptions();
         return options.echo(echo);
      }
   }
}
