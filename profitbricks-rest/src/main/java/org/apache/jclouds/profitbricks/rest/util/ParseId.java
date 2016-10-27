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
package org.apache.jclouds.profitbricks.rest.util;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jclouds.json.Json;

public class ParseId {
   
   final Json jsonBinder;
   
   @Inject
   ParseId(Json jsonBinder) {
      this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
   }
   
   @SuppressWarnings("serial")
   public String parseId (String json, String prefix, String key) {
         
      Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
      Map<String, String> jsonMap = jsonBinder.fromJson(json, mapType);

      Pattern p = Pattern.compile(String.format(".*%s\\/([a-z|\\-|\\d]*).*", prefix));
      Matcher m = p.matcher(jsonMap.get("href"));

      if (m.matches()) {
         String dataCenterId = m.group(1);
         jsonMap = new ImmutableMap.Builder<String, String>()
                 .putAll(jsonMap)
                 .put(key, dataCenterId)
                 .build();
         json = jsonBinder.toJson(jsonMap);
      }

      return json;
   }
   
}
