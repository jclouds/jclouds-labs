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
package org.jclouds.fujitsu.fgcp.handlers;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.Logger;

import com.google.inject.Singleton;

/**
 * Defines which requests should be retried.
 */
@Singleton
public class FGCPServerErrorRetryHandler implements HttpRetryHandler {
   private final HttpRetryHandler backoffHandler;

   @Inject
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 10;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public FGCPServerErrorRetryHandler(FGCPBackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (command.getFailureCount() > retryCountLimit)
         return false;

      if (response.getStatusCode() == 500) {
         byte[] content = HttpUtils.closeClientButKeepContentStream(response);
         if (content != null) {
            String message = new String(content);

            if (message.contains("<responseStatus>ILLEGAL_STATE</responseStatus>")
                  && message.contains("RECONFIG_ING")) {
               return backoffHandler.shouldRetryRequest(command, response);
            }
            if (message.contains("<responseStatus>VALIDATION_ERROR</responseStatus>")
                  && message.contains("verify error")) {
               return backoffHandler.shouldRetryRequest(command, response);
            }
         }
      }
      return false;
   }
}
