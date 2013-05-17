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
package org.jclouds.blobstore.representations;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Calendar;

import static com.google.common.io.Resources.getResource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class StorageMetadataTest {

   @Test
   void testToJson() {
      StorageMetadata storage = StorageMetadata.builder().creationDate(Calendar.getInstance().getTime()).name("file.txt")
                                                         .uri("https://somehost/file.txt")
                                                         .build();
      Gson gson = new GsonBuilder().create();
      JsonElement json = gson.toJsonTree(storage);
      assertNotNull(json);
      assertEquals("file.txt", json.getAsJsonObject().get("name").getAsString());
      assertEquals("https://somehost/file.txt", json.getAsJsonObject().get("uri").getAsString());
   }

   @Test
   void testFromJson() throws IOException {
      Gson gson = new GsonBuilder().create();
      String json = Resources.toString(getResource("blobstore/StorageMetadata-aws-s3-repo.json"), Charsets.UTF_8);
      StorageMetadata storage = gson.fromJson(json, StorageMetadata.class);
      assertNotNull(storage);
      assertEquals("file.txt", storage.getName());
      assertEquals("https://somecontainer.s3.amazonaws.com/file.txt", storage.getUri());
   }
}
