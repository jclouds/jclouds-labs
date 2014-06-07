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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Guice;
import org.jclouds.cloudsigma2.domain.Owner;
import org.jclouds.cloudsigma2.domain.Tag;
import org.jclouds.cloudsigma2.domain.TagResource;
import org.jclouds.cloudsigma2.domain.TagResourceType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;

@Test(groups = "unit")
public class TagToJsonTest {

   private static final TagToJson TAG_TO_JSON = Guice.createInjector().getInstance(TagToJson.class);

   private Tag input;
   private JsonObject expected;

   @BeforeMethod
   public void setUp() throws Exception {
      Owner owner = new Owner.Builder()
            .resourceUri(new URI("/api/2.0/user/5b4a69a3-8e78-4c45-a8ba-8b13f0895e23/"))
            .uuid("5b4a69a3-8e78-4c45-a8ba-8b13f0895e23")
            .build();

      Map<String, String> meta = Maps.newHashMap();
      meta.put("description", "test tag");

      input = new Tag.Builder()
            .meta(meta)
            .name("TagCreatedWithResource")
            .resources(ImmutableList.of(
                  new TagResource.Builder()
                        .uuid("96537817-f4b6-496b-a861-e74192d3ccb0")
                        .build()
                  , new TagResource.Builder()
                  .uuid("61bcc398-c034-42f1-81c9-f6d7f62c4ea0")
                  .build()
                  , new TagResource.Builder()
                  .uuid("3610d935-514a-4552-acf3-a40dd0a5f961")
                  .build()
                  , new TagResource.Builder()
                  .resourceType(TagResourceType.IPS)
                  .resourceUri(new URI("/api/2.0/ips/185.12.6.183/"))
                  .uuid("185.12.6.183")
                  .owner(owner)
                  .build()
            ))
            .build();

      expected = new JsonObject();

      JsonObject metaObject = new JsonObject();
      metaObject.addProperty("description", "test tag");

      expected.add("meta", metaObject);
      expected.addProperty("name", "TagCreatedWithResource");

      JsonArray resourcesArray = new JsonArray();
      resourcesArray.add(new JsonPrimitive("96537817-f4b6-496b-a861-e74192d3ccb0"));
      resourcesArray.add(new JsonPrimitive("61bcc398-c034-42f1-81c9-f6d7f62c4ea0"));
      resourcesArray.add(new JsonPrimitive("3610d935-514a-4552-acf3-a40dd0a5f961"));
      resourcesArray.add(new JsonPrimitive("185.12.6.183"));

      expected.add("resources", resourcesArray);
   }

   public void test() {
      Assert.assertEquals(TAG_TO_JSON.apply(input), expected);
   }
}
