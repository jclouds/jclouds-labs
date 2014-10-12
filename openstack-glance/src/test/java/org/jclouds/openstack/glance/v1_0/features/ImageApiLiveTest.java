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
package org.jclouds.openstack.glance.v1_0.features;


import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.containerFormat;
import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.copyFrom;
import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.diskFormat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceApiLiveTest;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "ImageApiLiveTest", singleThreaded = true)
public class ImageApiLiveTest extends BaseGlanceApiLiveTest {

   public static final String IMAGE_NAME_PREFIX = "jclouds-live-test-";

   public static String imageName;
   public static String updatedImageName;

   @BeforeClass
   public void init() {
      Random random = new Random();
      imageName = IMAGE_NAME_PREFIX + random.nextInt();
      updatedImageName = IMAGE_NAME_PREFIX + random.nextInt();
   }

   @AfterTest
   public void tearDown() {
      for (String region : api.getConfiguredRegions()) {
         cleanImagesByRegionAndName(region, imageName, updatedImageName);
      }
   }

   @Test
   public void testList() throws Exception {
      for (String region : api.getConfiguredRegions()) {
         ImageApi imageApi = api.getImageApi(region);
         Set<? extends Image> response = imageApi.list().concat().toSet();
         assert null != response;
         for (Image image : response) {
            checkImage(image);
         }
      }
   }

   private void checkImage(Image image) {
      assert image.getId() != null : image;
      assert image.getName() != null : image;
      assert image.getLinks() != null : image;
   }

   @Test
   public void testListInDetail() throws Exception {
      for (String region : api.getConfiguredRegions()) {
         ImageApi imageApi = api.getImageApi(region);
         Set<? extends ImageDetails> response = imageApi.listInDetail().concat().toSet();
         assert null != response;
         for (ImageDetails image : response) {
            checkImage(image);
            ImageDetails newDetails = imageApi.get(image.getId());
            checkImageDetails(newDetails);
            checkImageDetailsEqual(image, newDetails);
         }
      }
   }

   private void checkImageDetails(ImageDetails image) {
      checkImage(image);
      assertTrue(image.getMinDisk() >= 0);
      assertTrue(image.getMinRam() >= 0);
   }

   private void checkImageDetailsEqual(ImageDetails image, ImageDetails newDetails) {
      assertEquals(newDetails.getId(), image.getId());
      assertEquals(newDetails.getName(), image.getName());
      assertEquals(newDetails.getLinks(), image.getLinks());
   }

   @Test
   public void testCreateUpdateAndDeleteImage() {
      StringPayload imageData = new StringPayload("This isn't really an image!");
      for (String region : api.getConfiguredRegions()) {
         ImageApi imageApi = api.getImageApi(region);
         ImageDetails details = imageApi.create(imageName, imageData, diskFormat(DiskFormat.RAW), containerFormat(ContainerFormat.BARE));
         assertEquals(details.getName(), imageName);
         assertEquals(details.getSize().get().longValue(), imageData.getRawContent().length());

         details = imageApi.update(details.getId(), UpdateImageOptions.Builder.name(updatedImageName), UpdateImageOptions.Builder.minDisk(10));
         assertEquals(details.getName(), updatedImageName);
         assertEquals(details.getMinDisk(), 10);

         Image fromListing = imageApi.list(
                  ListImageOptions.Builder.containerFormat(ContainerFormat.BARE).name(updatedImageName).limit(2))
                  .get(0);
         assertEquals(fromListing.getId(), details.getId());
         assertEquals(fromListing.getSize(), details.getSize());

         assertEquals(Iterables.getOnlyElement(imageApi.listInDetail(ListImageOptions.Builder.name(updatedImageName))), details);

         assertTrue(imageApi.delete(details.getId()));

         assertTrue(imageApi.list(ListImageOptions.Builder.name(updatedImageName)).isEmpty());
      }
   }

   @Test
   public void testReserveUploadAndDeleteImage() {
      StringPayload imageData = new StringPayload("This isn't an image!");
      for (String region : api.getConfiguredRegions()) {
         ImageApi imageApi = api.getImageApi(region);
         ImageDetails details = imageApi.reserve(imageName, diskFormat(DiskFormat.RAW), containerFormat(ContainerFormat.BARE));
         assertEquals(details.getName(), imageName);

         details = imageApi.upload(details.getId(), imageData, UpdateImageOptions.Builder.name(updatedImageName), UpdateImageOptions.Builder.minDisk(10));
         assertEquals(details.getName(), updatedImageName);
         assertEquals(details.getSize().get().longValue(), imageData.getRawContent().length());
         assertEquals(details.getMinDisk(), 10);

         Image fromListing = Iterables.getOnlyElement(imageApi.list(ListImageOptions.Builder.name(updatedImageName).limit(2).containerFormat(ContainerFormat.BARE)));
         assertEquals(fromListing.getId(), details.getId());
         assertEquals(fromListing.getSize(), details.getSize());

         assertEquals(Iterables.getOnlyElement(imageApi.listInDetail(ListImageOptions.Builder.name(updatedImageName))), details);

         assertTrue(imageApi.delete(details.getId()));

         assertTrue(imageApi.list(ListImageOptions.Builder.name(updatedImageName)).isEmpty());
      }
   }

   @Test
   public void testCreateImageCopyFromLocation() {
      StringPayload imageData = new StringPayload("");
      for (String region : api.getConfiguredRegions()) {
         ImageApi imageApi = api.getImageApi(region);
         ImageDetails details = imageApi.create(imageName, imageData, diskFormat(DiskFormat.RAW), containerFormat(ContainerFormat.BARE), copyFrom("http://10.5.5.121/Installs/Templates/tiny/tinylinux-v2.qcow2"));
         assertEquals(details.getName(), imageName);

         Image fromListing = imageApi.get(details.getId());

         assertEquals(fromListing.getId(), details.getId());
         assertEquals(fromListing.getSize(), details.getSize());
         assertEquals(fromListing.getContainerFormat(), details.getContainerFormat());
         assertEquals(fromListing.getDiskFormat(), details.getDiskFormat());
         assertEquals(fromListing.getSize(), details.getSize());

         assertTrue(imageApi.delete(details.getId()));
         assertTrue(imageApi.list(ListImageOptions.Builder.name(imageName)).isEmpty());
      }
   }

   private void cleanImagesByRegionAndName(String region, String... imageNames) {
      ImageApi imageApi = api.getImageApi(region);

      for (String imageName : imageNames) {
         ImmutableList<Image> images = imageApi.list(ListImageOptions.Builder.name(imageName)).toList();

         for (Image image : images) {
            imageApi.delete(image.getId());
         }
      }
   }
}
