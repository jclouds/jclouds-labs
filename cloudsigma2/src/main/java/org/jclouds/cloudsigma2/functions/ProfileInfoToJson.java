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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.cloudsigma2.domain.ProfileInfo;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Singleton;

@Singleton
public class ProfileInfoToJson implements Function<ProfileInfo, JsonObject> {
   @Override
   public JsonObject apply(@Nullable ProfileInfo input) {
      if (input == null) {
         return null;
      }

      JsonObject profileJson = new JsonObject();

      if (input.getAddress() != null) {
         profileJson.addProperty("address", input.getAddress());
      }

      if (input.getBankReference() != null) {
         profileJson.addProperty("bank_reference", input.getBankReference());
      }

      if (input.getCompany() != null) {
         profileJson.addProperty("company", input.getCompany());
      }

      if (input.getCountry() != null) {
         profileJson.addProperty("country", input.getCountry());
      }

      if (input.getEmail() != null) {
         profileJson.addProperty("email", input.getEmail());
      }

      if (input.getFirstName() != null) {
         profileJson.addProperty("first_name", input.getFirstName());
      }

      if (input.getLastName() != null) {
         profileJson.addProperty("last_name", input.getLastName());
      }

      if (input.getMeta() != null) {
         profileJson.add("meta", new JsonParser().parse(new Gson().toJson(input.getMeta())));
      }

      if (input.getMyNotes() != null) {
         profileJson.addProperty("my_notes", input.getMyNotes());
      }

      if (input.getNickname() != null) {
         profileJson.addProperty("nickname", input.getNickname());
      }

      if (input.getPhone() != null) {
         profileJson.addProperty("phone", input.getPhone());
      }

      if (input.getPostcode() != null) {
         profileJson.addProperty("postcode", input.getPostcode());
      }

      if (input.getTitle() != null) {
         profileJson.addProperty("title", input.getTitle());
      }

      if (input.getTown() != null) {
         profileJson.addProperty("town", input.getTown());
      }

      return profileJson;
   }
}
