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

import java.util.List;
import org.apache.jclouds.oneandone.rest.domain.Image;
import org.apache.jclouds.oneandone.rest.domain.Image.CreateImage;
import org.apache.jclouds.oneandone.rest.domain.Image.UpdateImage;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneLiveTest;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseOneAndOneLiveTest {

   private Image currentImage;
   private Server currentServer;
   private List<Image> images;

   private ImageApi imageApi() {
      return api.imageApi();
   }

   @BeforeClass
   public void setupTest() {
      currentServer = createServer("image jclouds server");
      assertNodeAvailable(currentServer);
      currentImage = imageApi().createImage(CreateImage.builder()
              .name("jcloudsimage")
              .numImages(1)
              .frequency(Types.ImageFrequency.WEEKLY)
              .serverId(currentServer.id())
              .build());
      Image checkImage = imageApi().get(currentImage.id());

      assertNotNull(currentImage);
      assertNotNull(checkImage);

   }

   @Test
   public void testList() {
      images = imageApi().list();

      assertNotNull(images);
      assertFalse(images.isEmpty());
      Assert.assertTrue(images.size() > 0);
   }

   @Test
   public void testListWithOption() {
      GenericQueryOptions options = new GenericQueryOptions();
      options.options(0, 0, null, "jcloudsimage", null);
      List<Image> imageWithQuery = imageApi().list(options);

      assertNotNull(imageWithQuery);
      assertFalse(imageWithQuery.isEmpty());
      Assert.assertTrue(imageWithQuery.size() > 0);
   }

   @Test
   public void testGetImage() {
      Image result = imageApi().get(currentImage.id());

      assertNotNull(result);
      assertEquals(result.id(), currentImage.id());
   }

   @Test(dependsOnMethods = "testGetImage")
   public void testUpdateImage() throws InterruptedException {
      String updatedName = "Updatedjava";

      Image updateResult = imageApi().update(currentImage.id(), UpdateImage.create(updatedName, "description", Types.ImageFrequency.ONCE));

      assertNotNull(updateResult);

      assertEquals(updateResult.name(), updatedName);

   }

   @AfterClass(alwaysRun = true)
   public void teardownTest() throws InterruptedException {
      if (currentImage != null) {
         imageApi().delete(currentImage.id());
      }
      if (currentServer != null) {
         assertNodeAvailable(currentServer);
         deleteServer(currentServer.id());
      }
   }

}
