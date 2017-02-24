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
package org.apache.jclouds.oneandone.rest.exceptions;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import static org.apache.jclouds.oneandone.rest.handlers.OneAndOneRateLimitRetryHandler.millisUntilNextAvailableRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.RateLimitExceededException;

public class OneAndOneRateLimitExceededException extends RateLimitExceededException {

   private static final long serialVersionUID = 1L;
   private static final String RATE_LIMIT_HEADER_PREFIX = "X-Rate-Limit-";

   private Long timeToNextAvailableRequest;
   private Integer remainingRequests;
   private Integer averageRequestsAllowedPerMinute;

   public OneAndOneRateLimitExceededException(HttpResponse response) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response));
      parseRateLimitInfo(response);
   }

   public OneAndOneRateLimitExceededException(HttpResponse response, Throwable cause) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response), cause);
      parseRateLimitInfo(response);
   }

   public Long timeToNextAvailableRequest() {
      return timeToNextAvailableRequest;
   }

   public Integer remainingRequests() {
      return remainingRequests;
   }

   public Integer averageRequestsAllowedPerMinute() {
      return averageRequestsAllowedPerMinute;
   }

   private void parseRateLimitInfo(HttpResponse response) {
      String reset = response.getFirstHeaderOrNull("X-Rate-Limit-Reset");
      String remaining = response.getFirstHeaderOrNull("X-Rate-Limit-Remaining");
      String limit = response.getFirstHeaderOrNull("X-Rate-Limit-Limit");

      remainingRequests = remaining == null ? null : Integer.valueOf(remaining);
      averageRequestsAllowedPerMinute = limit == null ? null : Integer.valueOf(limit);
      timeToNextAvailableRequest = reset == null ? null : millisUntilNextAvailableRequest(Long.parseLong(reset));
   }

   private static Multimap<String, String> rateLimitHeaders(HttpResponse response) {
      return Multimaps.filterKeys(response.getHeaders(), new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            return input.startsWith(RATE_LIMIT_HEADER_PREFIX);
         }
      });
   }
}
