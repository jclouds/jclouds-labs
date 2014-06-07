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
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Distribution;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ImageApi} class.
 */
@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseDigitalOceanMockTest {

   public void testListImages() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/images.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         List<Image> images = imageApi.list();

         assertRequestHasCommonFields(server.takeRequest(), "/images");
         assertEquals(images.size(), 3);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetImage() throws Exception {
      MockWebServer server = mockWebServer();
      String[] imageJsons = new String[] { "/image1.json", "/image2.json", "/image3.json", "/image2.json" };

      for (String imageJson : imageJsons) {
         server.enqueue(new MockResponse().setBody(payloadFromResource(imageJson)));
      }

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         Image image = imageApi.get(1);

         assertRequestHasCommonFields(server.takeRequest(), "/images/1");
         assertNotNull(image);
         assertEquals(image.getId(), 1);
         assertEquals(image.getOs().getDistribution(), Distribution.ARCHLINUX);
         assertEquals(image.getOs().getVersion(), "2013.05");
         assertEquals(image.getOs().getArch(), "x32");
         assertEquals(image.getName(), "Arch Linux 2013.05 x32");
         assertTrue(image.isPublicImage());
         assertEquals(image.getSlug(), "arch-linux-x32");

         image = imageApi.get(2);

         assertRequestHasCommonFields(server.takeRequest(), "/images/2");
         assertNotNull(image);
         assertEquals(image.getId(), 2);
         assertEquals(image.getOs().getDistribution(), Distribution.FEDORA);
         assertEquals(image.getOs().getVersion(), "17");
         assertEquals(image.getOs().getArch(), "x64");
         assertEquals(image.getName(), "Fedora 17 x64 Desktop");
         assertTrue(image.isPublicImage());
         assertEquals(image.getSlug(), "fedora-17-x64");

         image = imageApi.get(3);

         assertRequestHasCommonFields(server.takeRequest(), "/images/3");
         assertNotNull(image);
         assertNull(image.getSlug());
         assertEquals(image.getId(), 3);
         assertEquals(image.getOs().getDistribution(), Distribution.UBUNTU);
         assertEquals(image.getOs().getVersion(), "13.04");
         assertEquals(image.getOs().getArch(), "");
         assertEquals(image.getName(), "Dokku on Ubuntu 13.04 0.2.0rc3");
         assertTrue(image.isPublicImage());
         assertNull(image.getSlug());

         image = imageApi.get("fedora-17-x64");

         assertRequestHasCommonFields(server.takeRequest(), "/images/fedora-17-x64");
         assertNotNull(image);
         assertEquals(image.getId(), 2);
         assertEquals(image.getOs().getDistribution(), Distribution.FEDORA);
         assertEquals(image.getOs().getVersion(), "17");
         assertEquals(image.getOs().getArch(), "x64");
         assertEquals(image.getName(), "Fedora 17 x64 Desktop");
         assertTrue(image.isPublicImage());
         assertEquals(image.getSlug(), "fedora-17-x64");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         Image image = imageApi.get(15);

         assertRequestHasCommonFields(server.takeRequest(), "/images/15");
         assertNull(image);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDeleteImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse());

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.delete(15);

         assertRequestHasCommonFields(server.takeRequest(), "/images/15/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDeleteImageUsingSlug() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse());

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.delete("img-15");

         assertRequestHasCommonFields(server.takeRequest(), "/images/img-15/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDeleteUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         try {
            imageApi.delete(15);
            fail("Delete image should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/images/15/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testTransferUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         try {
            imageApi.transfer(47, 23);
            fail("Transfer image should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/images/47/transfer",
               ImmutableMultimap.of("region_id", "23"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testTransferImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         int eventId = imageApi.transfer(47, 23);

         assertRequestHasParameters(server.takeRequest(), "/images/47/transfer",
               ImmutableMultimap.of("region_id", "23"));
         assertEquals(eventId, 7499);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testTransferImageUsingSlug() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         int eventId = imageApi.transfer("img-47", 23);

         assertRequestHasParameters(server.takeRequest(), "/images/img-47/transfer",
               ImmutableMultimap.of("region_id", "23"));
         assertEquals(eventId, 7499);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
