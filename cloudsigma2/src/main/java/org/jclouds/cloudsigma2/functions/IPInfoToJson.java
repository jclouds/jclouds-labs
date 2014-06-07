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
import org.jclouds.cloudsigma2.domain.IPInfo;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Singleton;

@Singleton
public class IPInfoToJson implements Function<IPInfo, JsonObject> {
   @Override
   public JsonObject apply(@Nullable IPInfo input) {
      if (input == null) {
         return null;
      }

      JsonObject ipObject = new JsonObject();

      if (input.getMeta() != null) {
         ipObject.add("meta", new JsonParser().parse(new Gson().toJson(input.getMeta())));
      }

      return ipObject;
   }
}
