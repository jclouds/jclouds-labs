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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import com.google.inject.Inject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.jclouds.json.Json;

public class ServerApplianceParser {

   final Json jsonBinder;

   @Inject
   ServerApplianceParser(Json jsonBinder) {
      this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
   }

   @SuppressWarnings("serial")
   public String parse(String json, String prefix, String key) {

      SingleServerAppliance result = null;
      Type mapType = new TypeToken<Map<String, Object>>() {
      }.getType();
      Map<String, Object> jsonMap = jsonBinder.fromJson(json, mapType);
      List<Object> dcs = cast(jsonMap.get("available_datacenters"));
      List<Object> categories = cast(jsonMap.get("categories"));
      Class<? extends Object> typeName = dcs.get(0).getClass();
      List<SingleServerAppliance.AvailableDataCenters> list = new ArrayList<SingleServerAppliance.AvailableDataCenters>();
      List<String> cats = null;

      if (typeName != String.class) {
         for (Object t : dcs) {
            LinkedTreeMap map = (LinkedTreeMap) t;
            list.add(SingleServerAppliance.AvailableDataCenters.create(map.get("id").toString(), map.get("name").toString()));
         }
         if (categories != null) {
            cats = new ArrayList<String>();
            for (Object t : categories) {
               cats.add(t.toString());
            }
         }

      } else {
         for (Object t : dcs) {
            list.add(SingleServerAppliance.AvailableDataCenters.create(t.toString(), ""));
         }
         if (categories != null) {
            cats = new ArrayList<String>();
            for (Object t : categories) {
               cats.add(t.toString());
            }
         }
      }
      String osInstallationBase = jsonMap.get("os_installation_base") != null ? jsonMap.get("os_installation_base").toString() : null;
      Types.OSFamliyType osFamily = jsonMap.get("os_family") != null ? Types.OSFamliyType.fromValue(jsonMap.get("os_family").toString()) : null;
      String os = jsonMap.get("os") != null ? jsonMap.get("os").toString() : null;
      String osVersion = jsonMap.get("os_version") != null ? jsonMap.get("os_version").toString() : null;
      Types.OSImageType imageType = jsonMap.get("os_image_type") != null ? Types.OSImageType.fromValue(jsonMap.get("os_image_type").toString()) : null;
      Types.ApplianceType type = jsonMap.get("type") != null ? Types.ApplianceType.fromValue(jsonMap.get("type").toString()) : null;
      String state = jsonMap.get("state") != null ? jsonMap.get("state").toString() : null;
      String version = jsonMap.get("version") != null ? jsonMap.get("version").toString() : null;
      String eula_url = jsonMap.get("eula_url") != null ? jsonMap.get("eula_url").toString() : null;

      result = SingleServerAppliance.builder().availableDataCenters(list)
              .categories(cats)
              .eulaUrl(eula_url)
              .id(jsonMap.get("id").toString())
              .minHddSize((int) Double.parseDouble(jsonMap.get("min_hdd_size").toString()))
              .os(os)
              .name(jsonMap.get("name").toString())
              .osArchitecture((int) Double.parseDouble(jsonMap.get("os_architecture").toString()))
              .osFamily(osFamily)
              .osImageType(imageType)
              .osInstallationBase(osInstallationBase)
              .osVersion(osVersion)
              .state(state)
              .type(type)
              .version(version)
              .build();

      return jsonBinder.toJson(result);
   }

   @SuppressWarnings("unchecked")
   public static <T extends List<?>> T cast(Object obj) {
      return (T) obj;
   }
}
