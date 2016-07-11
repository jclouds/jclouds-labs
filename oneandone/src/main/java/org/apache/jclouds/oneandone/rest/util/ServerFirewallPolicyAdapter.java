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
package org.apache.jclouds.oneandone.rest.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.TypeLiteral;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jclouds.oneandone.rest.domain.ServerFirewallPolicy;

public class ServerFirewallPolicyAdapter<T> extends TypeAdapter<List<T>> {

   private com.google.gson.reflect.TypeToken<T> adapterclass;
   private Gson gson;

   public ServerFirewallPolicyAdapter(com.google.gson.reflect.TypeToken<T> adapterclass) {
      this.adapterclass = adapterclass;
      gson = new Gson();

   }

   static final TypeLiteral<List<ServerFirewallPolicy>> list = new TypeLiteral<List<ServerFirewallPolicy>>() {
   };

   @Override
   public List<T> read(JsonReader reader) throws IOException {
      List<ServerFirewallPolicy> list = new ArrayList<ServerFirewallPolicy>();
      if (reader.peek() == JsonToken.BEGIN_OBJECT) {
         Type mapType = new TypeToken<Map<String, Object>>() {
         }.getType();
         Map<String, String> jsonMap = gson.fromJson(reader, mapType);
         ServerFirewallPolicy inning = ServerFirewallPolicy.create(jsonMap.get("id"), jsonMap.get("name"));
         list.add(inning);

      } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {

         reader.beginArray();
         while (reader.hasNext()) {
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, String> jsonMap = gson.fromJson(reader, mapType);
            ServerFirewallPolicy inning = ServerFirewallPolicy.create(jsonMap.get("id"), jsonMap.get("name"));
            list.add(inning);
         }
         reader.endArray();

      } else {
         reader.skipValue();
      }
      return (List<T>) list;
   }

   @Override
   public void write(JsonWriter writer, List<T> t) throws IOException {
   }

}
