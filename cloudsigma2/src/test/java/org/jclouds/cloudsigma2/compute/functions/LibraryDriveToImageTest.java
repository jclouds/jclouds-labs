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
package org.jclouds.cloudsigma2.compute.functions;

import com.google.common.collect.ImmutableMap;
import org.jclouds.cloudsigma2.domain.DriveStatus;
import org.jclouds.cloudsigma2.domain.LibraryDrive;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jclouds.cloudsigma2.compute.config.CloudSigma2ComputeServiceContextModule.driveStatusToImageStatus;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "LibraryDriveToImageTest")
public class LibraryDriveToImageTest {

   private LibraryDrive input;
   private Image expected;

   @BeforeMethod
   public void setUp() throws Exception {
      input = new LibraryDrive.Builder()
            .uuid("0bc6b02c-7ea2-4c5c-bf07-41c4cec2797d")
            .name("Debian 7.3 Server")
            .description("Debian 7.3 Server - amd64 Pre-Installed English with Python, SSH and VirtIO support. " +
                  "Last update 2014/02/15.")
            .os("linux")
            .arch("64")
            .version("7.3")
            .status(DriveStatus.UNMOUNTED)
            .meta(ImmutableMap.of("test_key", "test_value",
                  "sample key", "sample value"))
            .build();

      expected = new ImageBuilder()
            .ids("0bc6b02c-7ea2-4c5c-bf07-41c4cec2797d")
            .userMetadata(ImmutableMap.of("test_key", "test_value",
                  "sample key", "sample value"))
            .name("Debian 7.3 Server")
            .description("Debian 7.3 Server - amd64 Pre-Installed English with Python, SSH and VirtIO support. " +
                  "Last update 2014/02/15.")
            .operatingSystem(OperatingSystem.builder()
                  .name("Debian 7.3 Server")
                  .arch("64")
                  .family(OsFamily.LINUX)
                  .version("7.3")
                  .is64Bit(true)
                  .description("Debian 7.3 Server - amd64 Pre-Installed English with Python, SSH and VirtIO support. " +
                        "Last update 2014/02/15.")
                  .build())
            .status(Image.Status.UNRECOGNIZED)
            .build();
   }

   public void testConvertLibraryDrive() {
      LibraryDriveToImage function = new LibraryDriveToImage(driveStatusToImageStatus);
      Image converted = function.apply(input);
      assertEquals(converted, expected);
      assertEquals(converted.getUserMetadata(), expected.getUserMetadata());
      assertEquals(converted.getName(), expected.getName());
      assertEquals(converted.getDescription(), expected.getDescription());
      assertEquals(converted.getStatus(), expected.getStatus());
      assertEquals(converted.getOperatingSystem(), expected.getOperatingSystem());
   }
}
