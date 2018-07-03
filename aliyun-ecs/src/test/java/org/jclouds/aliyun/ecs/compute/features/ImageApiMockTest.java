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
package org.jclouds.aliyun.ecs.compute.features;

import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiMockTest;
import org.jclouds.aliyun.ecs.domain.Image;
import org.jclouds.aliyun.ecs.domain.Regions;
import org.jclouds.aliyun.ecs.domain.options.ListImagesOptions;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
import org.jclouds.collect.IterableWithMarker;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "ImageApiMockTest", singleThreaded = true)
public class ImageApiMockTest extends BaseECSComputeServiceApiMockTest {

   public void testListImages() throws InterruptedException {
      server.enqueue(jsonResponse("/images-first.json"));
      server.enqueue(jsonResponse("/images-second.json"));
      server.enqueue(jsonResponse("/images-last.json"));

      Iterable<Image> images = api.imageApi().list(Regions.EU_CENTRAL_1.getName()).concat();
      assertEquals(size(images), 28); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 3);
      assertSent(server, "GET", "DescribeImages");
      assertSent(server, "GET", "DescribeImages", 2);
      assertSent(server, "GET", "DescribeImages", 3);
   }

   public void testListImagesReturns404() {
      server.enqueue(response404());
      Iterable<Image> images = api.imageApi().list(Regions.EU_CENTRAL_1.getName()).concat();
      assertTrue(isEmpty(images));
      assertEquals(server.getRequestCount(), 1);
   }

   public void testListImagesWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/images-first.json"));

      IterableWithMarker<Image> images = api.imageApi().list(Regions.EU_CENTRAL_1.getName(), ListImagesOptions.Builder
              .paginationOptions(PaginationOptions.Builder.pageNumber(1)));

      assertEquals(size(images), 10);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "DescribeImages", 1);
   }

   public void testListImagesWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      IterableWithMarker<Image> images = api.imageApi().list(Regions.EU_CENTRAL_1.getName(), ListImagesOptions.Builder
              .paginationOptions(PaginationOptions.Builder.pageNumber(2)));

      assertTrue(isEmpty(images));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "DescribeImages", 2);
   }

}
