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
package org.apache.jclouds.oneandone.rest.compute.function;

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.internal.BaseOneAndOneApiMockTest;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerApplianceToImageTest", singleThreaded = true)
public class SingleServerApplianceToImageTest extends BaseOneAndOneApiMockTest {

   private SingleServerApplianceToImage fnImage;

   @BeforeTest
   public void setup() {
      this.fnImage = new SingleServerApplianceToImage();
   }

   @Test
   public void testImageToImage() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/image.json"))
      );

      SingleServerAppliance image = api.serverApplianceApi().get("some-id");

      Image actual = fnImage.apply(image);

      Image expected = new ImageBuilder()
              .ids(image.id())
              .name(image.name())
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description("Windows 2008R2 - 64 bits (Standard) + SQL Server 2012 (Standard)")
                      .family(OsFamily.WINDOWS)
                      .version("Windows 2008R2")
                      .is64Bit(true)
                      .build())
              .build();

      assertEquals(actual, expected);
   }
}
