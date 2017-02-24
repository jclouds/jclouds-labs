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
package org.apache.jclouds.oneandone.rest.handlers;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import javax.inject.Singleton;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.RateLimitRetryHandler;

@Beta
@Singleton
public class OneAndOneRateLimitRetryHandler extends RateLimitRetryHandler {

   public static final String RETRY_AFTER_CUSTOM = "X-Rate-Limit-Reset";

   @Override
   protected Optional<Long> millisToNextAvailableRequest(HttpCommand command, HttpResponse response) {

      String secondsToNextAvailableRequest = response.getFirstHeaderOrNull(RETRY_AFTER_CUSTOM);
      if (secondsToNextAvailableRequest == null) {
         return Optional.absent();
      }
      return Optional.of(millisUntilNextAvailableRequest(Long.parseLong(secondsToNextAvailableRequest)));

   }

   public static long millisUntilNextAvailableRequest(long secondsToNextAvailableRequest) {
      return secondsToNextAvailableRequest * 1000;
   }
}
