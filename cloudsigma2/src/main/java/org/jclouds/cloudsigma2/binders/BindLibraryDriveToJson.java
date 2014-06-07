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
package org.jclouds.cloudsigma2.binders;

import com.google.gson.JsonObject;
import com.google.inject.Singleton;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.functions.LibraryDriveToJson;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class BindLibraryDriveToJson implements Binder {

   private final LibraryDriveToJson createDriveJsonObjectFunction;

   @Inject
   public BindLibraryDriveToJson(LibraryDriveToJson createDriveJsonObjectFunction) {
      this.createDriveJsonObjectFunction = createDriveJsonObjectFunction;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof LibraryDrive, "this binder is only valid for LibraryDrive!");
      LibraryDrive create = LibraryDrive.class.cast(input);
      JsonObject profileInfoJsonObject = createDriveJsonObjectFunction.apply(create);
      request.setPayload(profileInfoJsonObject.toString());
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_JSON);
      return request;
   }
}
