/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.compute.representations;

import com.google.common.base.Charsets;
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
public class NodeTest {

   @Test
   void testToJson() {
      NodeMetadata nodeMetadata = NodeMetadata.builder().id("1").name("testnode-1").group("test-group").imageId("myimage").locationId("mylocation")
                                                        .defaultCredentials(LoginCredentials.builder()
                                                                .username("root")
                                                                .password("password1")
                                                                .authenticateSudo(false)
                                                                .build())

                                                        .build();

      Gson gson = new GsonBuilder().create();
      JsonElement json = gson.toJsonTree(nodeMetadata);
      assertNotNull(json);
      assertEquals("1", json.getAsJsonObject().get("id").getAsString());
      assertEquals("testnode-1", json.getAsJsonObject().get("name").getAsString());
      assertEquals("root", json.getAsJsonObject().getAsJsonObject("defaultCredentials").get("username").getAsString());
      assertEquals("password1", json.getAsJsonObject().getAsJsonObject("defaultCredentials").get("password").getAsString());
   }

   @Test
   void testFromJson() throws IOException {
      Gson gson = new GsonBuilder().create();
      String json = Resources.toString(getResource("compute/Node-stub.json"), Charsets.UTF_8);
      NodeMetadata node = gson.fromJson(json, NodeMetadata.class);
      assertNotNull(node);
      assertEquals("1", node.getId());
      assertEquals("test-695", node.getName());
      assertEquals("root", node.getDefaultCredentials().getUsername());

   }
}
