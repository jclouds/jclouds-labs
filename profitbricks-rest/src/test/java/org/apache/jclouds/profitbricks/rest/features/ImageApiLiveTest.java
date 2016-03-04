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
package org.apache.jclouds.profitbricks.rest.features;

import java.util.List;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksLiveTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseProfitBricksLiveTest {
   
   private String testImageId;
   private String testImageName;
   
   @Test(dependsOnMethods = "testList")
   public void testGetImage() {
      Image image = imageApi().getImage(testImageId);

      assertNotNull(image);
      assertEquals(image.id(), testImageId);
      assertEquals(image.properties().name(), testImageName);
   }
   
   @Test
   public void testList() {
      List<Image> images = imageApi().getList(new DepthOptions().depth(5));

      assertNotNull(images);
      assertFalse(images.isEmpty());
      assertTrue(images.size() > 1);
      testImageId = images.get(0).id();
      testImageName = images.get(0).properties().name();
   }
   
   private ImageApi imageApi() {
      return api.imageApi();
   }
   
}
