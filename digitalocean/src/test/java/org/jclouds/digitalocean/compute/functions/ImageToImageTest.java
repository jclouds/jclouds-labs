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
package org.jclouds.digitalocean.compute.functions;

import static org.jclouds.compute.domain.Image.Status.AVAILABLE;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.digitalocean.domain.Image;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Unit tests for the {@link ImageToImage} class.
 */
@Test(groups = "unit", testName = "ImageToImageTest")
public class ImageToImageTest {

   @Test
   public void testConvertImage() {
      Image image = new Image(1, "Ubuntu 14.04 x64", "Ubuntu 14.04 x64", true, "ubuntu-1404-x86");
      org.jclouds.compute.domain.Image expected = new ImageBuilder()
            .id("ubuntu-1404-x86")
            .providerId("1")
            .name("Ubuntu 14.04 x64")
            .status(AVAILABLE)
            .operatingSystem(
                  OperatingSystem.builder().name("Ubuntu 14.04 x64").description("Ubuntu 14.04 x64")
                        .family(OsFamily.UBUNTU).version("14.04").arch("x64").is64Bit(true).build())
            .userMetadata(ImmutableMap.of("publicImage", "true")).build();

      ImageToImage function = new ImageToImage();
      assertEquals(function.apply(image), expected);
   }
}
