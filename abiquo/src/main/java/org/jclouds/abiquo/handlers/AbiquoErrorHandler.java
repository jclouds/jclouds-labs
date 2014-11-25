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
package org.jclouds.abiquo.handlers;

import static org.jclouds.util.Closeables2.closeQuietly;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Parse Abiquo API errors and set the appropriate exception.
 */
@Singleton
public class AbiquoErrorHandler implements HttpErrorHandler {

   @Override
   public void handleError(final HttpCommand command, final HttpResponse response) {
      Exception exception = null;
      String defaultMessage = String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
            response.getStatusLine());

      try {
         switch (response.getStatusCode()) {
            case 401:
            case 403:
               // Authorization exceptions do not return an errors DTO, so we
               // encapsulate a generic exception
               exception = new AuthorizationException(defaultMessage, new HttpResponseException(command, response,
                     defaultMessage));
               break;
            case 404:
               // TODO: get the exception to encapsulate from the returned error
               // object
               exception = new ResourceNotFoundException(defaultMessage);
               break;
            case 301:
               // Moved resources in Abiquo should be handled with the
               // ReturnMovedResource exception parser to return the moved
               // entity.
               exception = new HttpResponseException(command, response, defaultMessage);
               break;
            default:
               // TODO: get the exception to encapsulate from the returned error
               // object
               exception = new HttpResponseException(response.getMessage(), command, response);
               break;
         }
      } finally {
         closeQuietly(response.getPayload());
         command.setException(exception);
      }
   }
}
