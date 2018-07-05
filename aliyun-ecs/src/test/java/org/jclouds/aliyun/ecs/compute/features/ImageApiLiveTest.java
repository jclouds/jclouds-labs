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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.aliyun.ecs.compute.internal.BaseECSComputeServiceApiLiveTest;
import org.jclouds.aliyun.ecs.domain.Image;
import org.jclouds.aliyun.ecs.domain.internal.Regions;
import org.jclouds.aliyun.ecs.features.ImageApi;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jclouds.aliyun.ecs.domain.options.ListImagesOptions.Builder.imageIds;
import static org.jclouds.aliyun.ecs.domain.options.PaginationOptions.Builder.pageNumber;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseECSComputeServiceApiLiveTest {

   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(Regions.EU_CENTRAL_1.getName()).concat(), new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All images must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some image to be returned");
   }

   public void testListWithOptions() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(Regions.EU_CENTRAL_1.getName(),
              imageIds("debian_8_09_64_20G_alibase_20170824.vhd")
              .paginationOptions(pageNumber(3)))
              .firstMatch(new Predicate<Image>() {
                 @Override
                 public boolean apply(Image input) {
                    found.incrementAndGet();
                    return !isNullOrEmpty(input.id());
                 }
              }).isPresent(), "All images must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some image to be returned");
   }

   private ImageApi api() {
      return api.imageApi();
   }
}
