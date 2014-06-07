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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.Server;
import org.jclouds.cloudsigma2.domain.Subscription;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.VLANInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

@Test(groups = "unit")
public class VLANInfoToJsonTest {

   private static final VLANInfoToJson VLAN_INFO_TO_JSON = Guice.createInjector().getInstance(VLANInfoToJson.class);

   private VLANInfo input;
   private JsonObject expected;

   {
      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "test vlan");
      meta.put("test_key_1", "test_value_1");
      meta.put("test_key_2", "test_value_2");

      try {
         input = new VLANInfo.Builder()
               .meta(meta)
               .resourceUri(new URI("/api/2.0/vlans/96537817-f4b6-496b-a861-e74192d3ccb0/"))
               .servers(ImmutableList.of(
                     new Server.Builder()
                           .uuid("81f911f9-5152-4328-8671-02543bafbd0e")
                           .resourceUri(new URI("/api/2.0/servers/81f911f9-5152-4328-8671-02543bafbd0e/"))
                           .build()
                     , new Server.Builder()
                     .uuid("19163e1a-a6d6-4e73-8087-157dd302c373")
                     .resourceUri(new URI("/api/2.0/servers/19163e1a-a6d6-4e73-8087-157dd302c373/"))
                     .build()
               ))
               .subscription(new Subscription.Builder()
                     .id("7272")
                     .resourceUri(new URI("/api/2.0/subscriptions/7272/"))
                     .build())
               .tags(new ArrayList<Tag>())
               .uuid("96537817-f4b6-496b-a861-e74192d3ccb0")
               .build();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }

      expected = new JsonObject();
      expected.add("meta", new JsonParser().parse(new Gson().toJson(meta)));
   }

   public void test() {
      Assert.assertEquals(VLAN_INFO_TO_JSON.apply(input), expected);
   }
}
