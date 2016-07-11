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
package org.apache.jclouds.oneandone.rest.config;

import com.google.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_MAX_PERIOD;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_PERIOD;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_TIMEOUT;

@Singleton
public class OneAndOneConstants {

   @Inject
   @Named(POLL_TIMEOUT)
   private String pollTimeout;

   @Inject
   @Named(POLL_PERIOD)
   private String pollPeriod;

   @Inject
   @Named(POLL_MAX_PERIOD)
   private String pollMaxPeriod;

   public long pollTimeout() {
      return Long.parseLong(pollTimeout);
   }

   public long pollPeriod() {
      return Long.parseLong(pollPeriod);
   }

   public long pollMaxPeriod() {
      return Long.parseLong(pollMaxPeriod);
   }
}
