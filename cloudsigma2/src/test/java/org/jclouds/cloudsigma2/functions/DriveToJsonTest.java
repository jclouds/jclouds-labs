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

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.cloudsigma2.domain.DriveInfo;
import org.jclouds.cloudsigma2.domain.DriveLicense;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.jclouds.cloudsigma2.domain.Job;
import org.jclouds.cloudsigma2.domain.License;
import org.jclouds.cloudsigma2.domain.MediaType;
import org.jclouds.cloudsigma2.domain.Server;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;

@Test(groups = "unit")
public class DriveToJsonTest {

   private static final DriveToJson DRIVE_TO_JSON = Guice.createInjector().getInstance(DriveToJson.class);

   private static final JsonObject result = new JsonObject();
   private DriveInfo input;

   @BeforeMethod
   public void setUp() throws Exception {
      List<String> affinities = ImmutableList.of("ssd", "sample");
      List<String> tags = ImmutableList.of("tag_uuid_1", "tag_uuid_2");

      Map<String, String> metaMap = Maps.newHashMap();
      metaMap.put("description", "test_description");
      metaMap.put("install_notes", "test_install_notes");

      result.addProperty("name", "test");
      result.addProperty("size", "1024000000");
      result.addProperty("media", "disk");
      result.add("affinities", new JsonParser().parse(new Gson().toJson(affinities)));
      result.add("meta", new JsonParser().parse(new Gson().toJson(metaMap)));
      result.add("tags", new JsonParser().parse(new Gson().toJson(tags)));
      result.addProperty("allow_multimount", false);

      input = new DriveInfo.Builder()
            .affinities(ImmutableList.of("ssd", "sample"))
            .allowMultimount(false)
            .jobs(ImmutableList.of(new Job.Builder()
            		.resourceUri("/api/2.0/jobs/")
            		.uuid("933133a2-4ee2-4310-9a63-c8d5e705233")
            		.build()))
            .licenses(ImmutableList.of(new DriveLicense.Builder()
                  .amount(1)
                  .license(new License.Builder()
                        .isBurstable(true)
                        .longName("sample_longname")
                        .name("sample_name")
                        .resourceUri(new URI("/api/2.0/samples/"))
                        .type("sample_type")
                        .userMetric("sample")
                        .build())
                  .build()))
            .media(MediaType.DISK)
            .meta(metaMap)
            .mountedOn(ImmutableList.of(new Server.Builder()
                  .uuid("81f911f9-5152-4328-8671-02543bafbd0e")
                  .build(),
                  new Server.Builder()
                        .uuid("19163e1a-a6d6-4e73-8087-157dd302c373")
                        .build()))
            .name("test")
            .size(new BigInteger("1024000000"))
            .status(DriveStatus.UNMOUNTED)
            .tags(ImmutableList.of("tag_uuid_1", "tag_uuid_2"))
            .uuid("e96f3c63-6f50-47eb-9401-a56c5ccf6b32")
            .build();
   }

   public void test() {
      Assert.assertEquals(DRIVE_TO_JSON.apply(input), result);
   }
}

