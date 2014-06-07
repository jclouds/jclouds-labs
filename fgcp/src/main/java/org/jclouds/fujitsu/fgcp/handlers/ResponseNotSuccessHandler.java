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

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * This will parse the XML payload and set an appropriate exception on the
 * command object.
 */
@Singleton
public class ResponseNotSuccessHandler implements HttpErrorHandler {
   private static final Pattern ERROR_STATUS_PATTERN = Pattern.compile("<responseStatus>(.+)</responseStatus>");
   private static final Pattern ERROR_MESSAGE_PATTERN = Pattern.compile("<responseMessage>(.+)</responseMessage>");
   private static final Pattern SYSTEM_RECONFIGURING_PATTERN = Pattern.compile("ILLEGAL_STATE.+RECONFIG_ING.*");
   private static final Pattern AUTH_PATTERN = Pattern.compile(".*(AuthFailure|User not found in selectData).*");
   private static final Pattern RESOURCE_NOT_FOUND_PATTERN = Pattern.compile(".*RESOURCE_NOT_FOUND.*");
   private static final Pattern NOT_IMPLEMENTED_PATTERN = Pattern.compile(".*NOTFOUND: API to the Version.*");
   private static final Pattern ILLEGAL_STATE_PATTERN = Pattern
         .compile(".*(NEVER_BOOTED|ALREADY_STARTED|ALREADY_STOPPED|ILLEGAL_STATE).*");
   private static final Pattern ILLEGAL_ARGUMENT_PATTERN = Pattern
         .compile(".*(SERVER_NAME_ALREADY_EXISTS|VALIDATION_ERROR).*");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = null;
      try {
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data, "UTF-8") : null;
         if (message != null) {
            Matcher ms = ERROR_STATUS_PATTERN.matcher(message);
            Matcher mm = ERROR_MESSAGE_PATTERN.matcher(message);

            if (ms.find() && mm.find()) {
               String status = ms.group(1);
               String errorMessage = mm.group(1);
               // revert status code to 200 to match actual server's return status
               response = response.toBuilder().statusCode(200).build();
               exception = refineException(new HttpResponseException(command, response, status + ": " + errorMessage));
            }
         }
      } catch (UnsupportedEncodingException e) {
         // should never happen as UTF-8 is always supported
      } finally {
         if (exception == null) {
            exception = new HttpResponseException(command, response);
         }
         command.setException(exception);
         releasePayload(response);
      }
   }

   private Exception refineException(HttpResponseException exception) {
      Exception refinedException = exception;
      String error = exception.getContent();

      // Create custom exception for error messages we know about
      if (SYSTEM_RECONFIGURING_PATTERN.matcher(error).matches()) {
         refinedException = new IllegalStateException(exception);
      } else if (RESOURCE_NOT_FOUND_PATTERN.matcher(error).matches()) {
         refinedException = new ResourceNotFoundException(error, exception);
      } else if (ILLEGAL_STATE_PATTERN.matcher(error).matches()) {
         refinedException = new IllegalStateException(error, exception);
      } else if (ILLEGAL_ARGUMENT_PATTERN.matcher(error).matches()) {
         refinedException = new IllegalArgumentException(error, exception);
      } else if (AUTH_PATTERN.matcher(error).matches()) {
         refinedException = new AuthorizationException(error, exception);
      } else if (NOT_IMPLEMENTED_PATTERN.matcher(error).matches()) {
         refinedException = new UnsupportedOperationException(error, exception);
      }
      return refinedException;
   }

}
