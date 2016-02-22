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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseIdTest")
public class ParseIdTest {

   @Test
   public void testIdExtraction() throws IOException {
      String serverResult = Resources.toString(getClass().getResource("/server/get.json"), Charsets.UTF_8);
      
      Injector injector = Guice.createInjector(new GsonModule());
      ParseId parseService = injector.getInstance(ParseId.class);
      Json json = injector.getInstance(Json.class);
      
      String result = parseService.parseId(serverResult, "datacenters", "dataCenterId");
      
      Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
      Map<String, String> jsonMap = json.fromJson(result, mapType);
      assertTrue(jsonMap.get("dataCenterId").equals("b0ac144e-e294-415f-ba39-6737d5a9d419"));
   }
}
