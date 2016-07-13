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
package org.apache.jclouds.oneandone.rest.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Image;
import org.apache.jclouds.oneandone.rest.domain.Image.CreateImage;
import org.apache.jclouds.oneandone.rest.domain.Image.UpdateImage;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ImageApiMockTest", singleThreaded = true)
public class ImageApiMockTest extends BaseOneAndOneApiMockTest {

   private ImageApi imageApi() {
      return api.imageApi();
   }

   @Test
   public void testList() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/list.json"))
      );

      List<Image> images = imageApi().list();

      assertNotNull(images);
      assertEquals(images.size(), 2);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images");
   }

   @Test
   public void testList404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));

      List<Image> images = imageApi().list();

      assertNotNull(images);
      assertEquals(images.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images");
   }

   @Test
   public void testListWithOption() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/list.options.json"))
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "New", null);
      List<Image> images = imageApi().list(options);

      assertNotNull(images);
      assertEquals(images.size(), 1);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images?q=New");
   }

   @Test
   public void testListWithOption404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "test", null);
      List<Image> images = imageApi().list(options);

      assertNotNull(images);
      assertEquals(images.size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images?q=test");
   }

   public void testGetImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/get.json"))
      );
      Image result = imageApi().get("imageId");

      assertNotNull(result);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/imageId");
   }

   @Test
   public void testGetImage404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404)
      );
      Image result = imageApi().get("imageId");

      assertEquals(result, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/imageId");
   }

   @Test
   public void testCreateImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/get.json"))
      );
      Image response = imageApi().createImage(CreateImage.builder()
              .name("name")
              .description("desc")
              .numImages(1)
              .serverId("server-Id")
              .frequency(Types.ImageFrequency.ONCE)
              .datacenterId("datacenter-id")
              .build());

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/images", "{ \n"
              + "  \"server_id\": \"server-Id\",\n"
              + "  \"name\": \"name\",\n"
              + "  \"description\": \"desc\",\n"
              + "  \"frequency\": \"ONCE\",\n"
              + "  \"num_images\": 1,\n"
              + "  \"datacenter_id\": \"datacenter-id\"\n"
              + "}"
      );
   }

   @Test
   public void testUpdateImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/get.json"))
      );
      Image response = imageApi().update("imageId", UpdateImage.create("name", "desc", Types.ImageFrequency.ONCE));

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/images/imageId", "{\n"
              + "  \"name\": \"name\", \n"
              + "  \"description\": \"desc\",\n"
              + "  \"frequency\": \"ONCE\"\n"
              + "}"
      );
   }

   @Test
   public void testDeleteImage() throws InterruptedException {
      server.enqueue(
              new MockResponse().setBody(stringFromResource("/image/get.json"))
      );
      Image response = imageApi().delete("imageId");

      assertNotNull(response);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/images/imageId");
   }

   @Test
   public void testDeleteImage404() throws InterruptedException {
      server.enqueue(
              new MockResponse().setResponseCode(404));
      Image hdd = imageApi().delete("imageId");

      assertEquals(hdd, null);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/images/imageId");
   }

}
