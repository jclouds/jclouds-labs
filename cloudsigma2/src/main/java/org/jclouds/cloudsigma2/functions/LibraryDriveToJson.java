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
package org.jclouds.cloudsigma2.functions;

import com.google.common.base.Function;
import com.google.gson.JsonObject;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LibraryDriveToJson implements Function<LibraryDrive, JsonObject> {

   private final DriveToJson infoJsonObjectFunction;

   @Inject
   public LibraryDriveToJson(DriveToJson infoJsonObjectFunction) {
      this.infoJsonObjectFunction = infoJsonObjectFunction;
   }

   @Override
   public JsonObject apply(@Nullable LibraryDrive input) {
      JsonObject libraryDriveObject = infoJsonObjectFunction.apply(input);

      if (libraryDriveObject == null) {
         libraryDriveObject = new JsonObject();
      }

      libraryDriveObject.addProperty("favourite", input.isFavorite());

      if (input.getDescription() != null) {
         libraryDriveObject.addProperty("description", input.getDescription());
      }
      return libraryDriveObject;
   }
}
