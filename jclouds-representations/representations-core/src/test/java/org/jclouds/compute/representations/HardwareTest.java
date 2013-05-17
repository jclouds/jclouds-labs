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
package org.jclouds.compute.representations;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


@Test
public class HardwareTest {

   @Test
   void testToJson() {
      Hardware hardware = Hardware.builder().id("test-hardware-profile")
                                   .processors(ImmutableList.of(Processor.builder().cores(4).speed(2).build()))
                                   .volumes(ImmutableList.of(Volume.builder().device("/dev/tst1").size(100f).bootDevice(true).build()))
                                   .build();

      Gson gson = new GsonBuilder().create();
      JsonElement json = gson.toJsonTree(hardware);
      assertNotNull(json);
      assertEquals("test-hardware-profile", json.getAsJsonObject().get("id").getAsString());
      assertEquals("4.0", json.getAsJsonObject().get("processors").getAsJsonArray().get(0).getAsJsonObject().get("cores").getAsString());
      assertEquals("2.0", json.getAsJsonObject().get("processors").getAsJsonArray().get(0).getAsJsonObject().get("speed").getAsString());
   }

   @Test
   void testFromJson() throws IOException {
      Gson gson = new GsonBuilder().create();
      String json = Resources.toString(getResource("compute/Hardware-stub-large.json"), Charsets.UTF_8);
      Hardware hardware = gson.fromJson(json, Hardware.class);
      assertNotNull(hardware);
      assertEquals("large", hardware.getId());
      assertEquals(1, hardware.getProcessors().size());
      assertEquals(8.0, hardware.getProcessors().get(0).getCores());
      assertEquals(1.0, hardware.getProcessors().get(0).getSpeed());
   }
}
