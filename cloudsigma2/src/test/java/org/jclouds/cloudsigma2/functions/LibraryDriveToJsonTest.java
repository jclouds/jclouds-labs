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
import java.net.URISyntaxException;

import org.jclouds.cloudsigma2.domain.DriveLicense;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.jclouds.cloudsigma2.domain.Job;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.cloudsigma2.domain.MediaType;
import org.jclouds.cloudsigma2.domain.Server;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Guice;

@Test(groups = "unit")
public class LibraryDriveToJsonTest {

   private static final DriveToJson DRIVE_TO_JSON = Guice.createInjector().getInstance(DriveToJson.class);
   private static final LibraryDriveToJson LIBRARY_DRIVE_TO_JSON = new LibraryDriveToJson(DRIVE_TO_JSON);

   private JsonObject expected = new JsonObject();
   private LibraryDrive input;

   {
      expected.addProperty("name", "Vyatta-6.5-32bit-Virtualization-ISO");
      expected.addProperty("size", "1000000000");
      expected.addProperty("media", "cdrom");
      expected.add("meta", new JsonObject());
      expected.add("tags", new JsonArray());
      expected.addProperty("allow_multimount", false);
      expected.addProperty("favourite", true);
      expected.addProperty("description", "test_description");

      try {
         input = new LibraryDrive.Builder()
               .allowMultimount(false)
               .arch("32")
               .category(ImmutableList.of("general"))
               .description("test_description")
               .isFavorite(true)
               .imageType("install")
               .installNotes("test_install_notes")
               .jobs(Lists.<Job>newArrayList())
               .licenses(Lists.<DriveLicense>newArrayList())
               .media(MediaType.CDROM)
               .meta(Maps.<String, String>newHashMap())
               .mountedOn(Lists.<Server>newArrayList())
               .name("Vyatta-6.5-32bit-Virtualization-ISO")
               .os("linux")
               .owner(null)
               .isPaid(false)
               .resourceUri(new URI("/api/2.0/libdrives/6d53b92c-42dc-472b-a7b6-7021f45f377a/"))
               .size(new BigInteger("1000000000"))
               .status(DriveStatus.MOUNTED)
               .tags(Lists.<String>newArrayList())
               .url("http://www.vyatta.org/")
               .uuid("6d53b92c-42dc-472b-a7b6-7021f45f377a")
               .build();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
   }

   public void test() {
      Assert.assertEquals(LIBRARY_DRIVE_TO_JSON.apply(input), expected);
   }
}
