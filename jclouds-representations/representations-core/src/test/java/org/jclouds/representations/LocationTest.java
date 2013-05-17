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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


@Test
public class LocationTest {

   @Test
   void testToJson() {
      Location location = Location.builder().id("region-1a").scope("ZONE").parentId("region-1").iso3166Codes(ImmutableSet.of("IE")).build();
      Gson gson = new GsonBuilder().create();
      JsonElement json = gson.toJsonTree(location);
      assertNotNull(json);
      assertEquals("region-1a", json.getAsJsonObject().get("id").getAsString());
      assertEquals("ZONE", json.getAsJsonObject().get("scope").getAsString());
      assertEquals("region-1", json.getAsJsonObject().get("parentId").getAsString());
      assertEquals("IE", json.getAsJsonObject().get("iso3166Codes").getAsJsonArray().get(0).getAsString());
   }

   @Test
   void testFromJson() throws IOException {
      Gson gson = new GsonBuilder().create();
      String json = Resources.toString(getResource("Location-aws-ec2-eu-west-1a.json"), Charsets.UTF_8);
      Location location = gson.fromJson(json, Location.class);
      assertNotNull(location);
      assertEquals("eu-west-1a", location.getId());
      assertEquals("eu-west-1", location.getParentId());
      assertEquals("ZONE", location.getScope());
      assertEquals(1, location.getIso3166Codes().size());
      assertTrue(location.getIso3166Codes().contains("IE"));
   }
}
