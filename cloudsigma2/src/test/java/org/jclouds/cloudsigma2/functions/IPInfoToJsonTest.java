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
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.IPInfo;
import org.jclouds.cloudsigma2.domain.Owner;
import org.jclouds.cloudsigma2.domain.Subscription;
import org.jclouds.cloudsigma2.domain.Tag;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

@Test(groups = "unit")
public class IPInfoToJsonTest {

   private static final IPInfoToJson IP_INFO_TO_JSON = Guice.createInjector().getInstance(IPInfoToJson.class);

   private IPInfo input;
   private JsonObject expected;

   @BeforeMethod
   public void setUp() throws Exception {
      Owner owner = new Owner.Builder()
            .resourceUri(new URI("/api/2.0/user/5b4a69a3-8e78-4c45-a8ba-8b13f0895e23/"))
            .uuid("5b4a69a3-8e78-4c45-a8ba-8b13f0895e23")
            .build();

      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "test ip");
      meta.put("test_key_1", "test_value_1");
      meta.put("test_key_2", "test_value_2");

      input = new IPInfo.Builder()
            .gateway("185.12.6.1")
            .meta(meta)
            .nameservers(ImmutableList.of(
                  "69.194.139.62",
                  "178.22.66.167",
                  "178.22.71.56"))
            .netmask(24)
            .owner(owner)
            .resourceUri(new URI("/api/2.0/ips/185.12.6.183/"))
            .server(null)
            .subscription(new Subscription.Builder()
                  .id("7273")
                  .resourceUri(new URI("/api/2.0/subscriptions/7273/"))
                  .build())
            .tags(new ArrayList<Tag>())
            .uuid("185.12.6.183")
            .build();

      expected = new JsonObject();

      JsonObject metaObject = new JsonObject();
      metaObject.addProperty("description", "test ip");
      metaObject.addProperty("test_key_1", "test_value_1");
      metaObject.addProperty("test_key_2", "test_value_2");

      expected.add("meta", metaObject);
   }

   public void test() {
      Assert.assertEquals(IP_INFO_TO_JSON.apply(input), expected);
   }
}
