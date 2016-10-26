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
package org.apache.jclouds.profitbricks.rest.exceptions;

import org.jclouds.http.HttpResponse;
import org.jclouds.rest.RateLimitExceededException;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Provides detailed information for rate limit exceptions.
 */
@Beta
public class ProfitBricksRateLimitExceededException extends RateLimitExceededException {
   private static final long serialVersionUID = 1L;
   private static final String RATE_LIMIT_HEADER_PREFIX = "X-RateLimit-";
   
   private Integer maxConcurrentRequestsAllowed;
   private Integer remainingRequests;
   private Integer averageRequestsAllowedPerMinute;

   public ProfitBricksRateLimitExceededException(HttpResponse response) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response));
      parseRateLimitInfo(response);
   }

   public ProfitBricksRateLimitExceededException(HttpResponse response, Throwable cause) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response), cause);
      parseRateLimitInfo(response);
   }
   
   public Integer maxConcurrentRequestsAllowed() {
      return maxConcurrentRequestsAllowed;
   }

   public Integer remainingRequests() {
      return remainingRequests;
   }

   public Integer averageRequestsAllowedPerMinute() {
      return averageRequestsAllowedPerMinute;
   }

   private void parseRateLimitInfo(HttpResponse response) {
      String burst = response.getFirstHeaderOrNull("X-RateLimit-Burst");
      String remaining = response.getFirstHeaderOrNull("X-RateLimit-Remaining");
      String limit = response.getFirstHeaderOrNull("X-RateLimit-Limit");

      maxConcurrentRequestsAllowed = burst == null ? null : Integer.valueOf(burst);
      remainingRequests = remaining == null ? null : Integer.valueOf(remaining);
      averageRequestsAllowedPerMinute = limit == null ? null : Integer.valueOf(limit);
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
