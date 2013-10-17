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
package org.jclouds.openstack.marconi.v1.handlers;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

public class MarconiErrorHandler implements HttpErrorHandler {

   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);
      Exception exception;

      if (data == null) {
         exception = new HttpResponseException(command, response);
      }
      else {
         exception = new HttpResponseException(command, response, new String(data));
      }

      switch (response.getStatusCode()) {
         case 401:
            exception = new AuthorizationException(exception.getMessage(), exception);
            break;
         case 409:
            exception = new IllegalStateException(exception.getMessage(), exception);
            break;
      }

      command.setException(exception);
   }
}
