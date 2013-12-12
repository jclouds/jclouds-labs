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
package org.jclouds.representations;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static com.google.common.io.Resources.getResource;


@Test
public class ApiMetadataTest {

   @Test
   void testToJson() {
      Map<String, String> props = Maps.newHashMap();
      props.put("key1", "value1");
      props.put("key2", "value2");
      props.put("key3", "value3");
      ApiMetadata apiMetadata = ApiMetadata.builder().id("test-api").defaultIdentity("identity")
                                           .credentialName("credential")
                                           .documentation("http://somehost.org/doc")
                                           .defaultProperties(props).build();

      Gson gson = new GsonBuilder().create();
      JsonElement json = gson.toJsonTree(apiMetadata);
      assertNotNull(json);
      assertEquals("test-api", json.getAsJsonObject().get("id").getAsString());
      assertEquals("value1", json.getAsJsonObject().getAsJsonObject("defaultProperties").get("key1").getAsString());
      assertEquals("value2", json.getAsJsonObject().getAsJsonObject("defaultProperties").get("key2").getAsString());
      assertEquals("value3", json.getAsJsonObject().getAsJsonObject("defaultProperties").get("key3").getAsString());
   }

   @Test
   void testFromJson() throws IOException {
      Gson gson = new GsonBuilder().create();
      String json = Resources.toString(getResource("ApiMetadata-stub.json"), Charsets.UTF_8);
      ApiMetadata apiMetadata = gson.fromJson(json, ApiMetadata.class);
      assertNotNull(apiMetadata);
      assertEquals("stub", apiMetadata.getId());
      assertEquals("stub", apiMetadata.getDefaultIdentity());
      assertEquals("stub", apiMetadata.getDefaultCredential());
   }
}
