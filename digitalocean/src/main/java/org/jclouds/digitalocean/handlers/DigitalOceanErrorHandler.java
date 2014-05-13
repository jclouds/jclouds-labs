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
package org.jclouds.digitalocean.handlers;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Parse the errors in the response and propagate an appropriate exception.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 * 
 * @see org.jclouds.digitalocean.http.ResponseStatusFromPayloadHttpCommandExecutorService
 */
@Singleton
public class DigitalOceanErrorHandler implements HttpErrorHandler {

   @Override
   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = null;

      try {
         // The response message is already properly populated by the
         // ResponseStatusFromPayloadHttpCommandExecutorService
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(response.getMessage(), exception);
               break;
            case 404:
               exception = new ResourceNotFoundException(response.getMessage(), exception);
               break;
            default:
               exception = new HttpResponseException(response.getMessage(), command, response);
               break;
         }
      } finally {
         command.setException(exception);
      }
   }
}
