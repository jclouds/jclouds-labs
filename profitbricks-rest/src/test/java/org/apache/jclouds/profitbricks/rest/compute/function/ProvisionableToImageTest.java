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
package org.apache.jclouds.profitbricks.rest.compute.function;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import java.util.Set;
import org.apache.jclouds.profitbricks.rest.internal.BaseProfitBricksApiMockTest;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ProvisionableToImageTest", singleThreaded = true)
public class ProvisionableToImageTest extends BaseProfitBricksApiMockTest {

   private ProvisionableToImage fnImage;

   private final Location location = new LocationBuilder().id("us/las").description("us/las").scope(LocationScope.ZONE)
           .parent(new LocationBuilder().id("us").description("us").scope(LocationScope.REGION).build()).build();

   @BeforeTest
   public void setup() {
      this.fnImage = new ProvisionableToImage(Suppliers.<Set<? extends Location>>ofInstance(ImmutableSet.of(location)));
   }

   @Test
   public void testImageToImage() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/image.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Image image = api.imageApi().getImage("some-id");

      Image actual = fnImage.apply(image);

      Image expected = new ImageBuilder()
              .ids(image.id())
              .name(image.properties().name())
              .location(location)
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description("UBUNTU")
                      .family(OsFamily.UBUNTU)
                      .version("14.04")
                      .is64Bit(false)
                      .build())
              .build();

      assertEquals(actual, expected);
   }

   @Test
   public void testImageDescriptionParsing() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/image1.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Image image1 = api.imageApi().getImage("some-id");

      Image actual1 = fnImage.apply(image1);

      Image expected1 = new ImageBuilder()
              .ids(image1.id())
              .name(image1.properties().name())
              .location(location)
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description("FEDORA")
                      .family(OsFamily.FEDORA)
                      .version("7")
                      .is64Bit(true)
                      .build())
              .build();

      assertEquals(actual1, expected1);

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/image2.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Image image2 = api.imageApi().getImage("some-id");

      Image actual2 = fnImage.apply(image2);

      Image expected2 = new ImageBuilder()
              .ids(image2.id())
              .name(image2.properties().name())
              .location(location)
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description("UNRECOGNIZED")
                      .family(OsFamily.UNRECOGNIZED)
                      .version("6.5.0")
                      .is64Bit(true)
                      .build())
              .build();

      assertEquals(actual2, expected2);

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/image3.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Image image3 = api.imageApi().getImage("some-id");

      Image actual3 = fnImage.apply(image3);

      Image expected3 = new ImageBuilder()
              .ids(image3.id())
              .name(image3.properties().name())
              .location(location)
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description("WINDOWS")
                      .family(OsFamily.WINDOWS)
                      .version("2008")
                      .is64Bit(false)
                      .build())
              .build();

      assertEquals(actual3, expected3);
   }

   @Test
   public void testSnapshotToImage() {

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/snapshot1.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Snapshot snapshot1 = api.snapshotApi().get("some-id");

      Image actual1 = fnImage.apply(snapshot1);

      Image expected1 = new ImageBuilder()
              .ids(snapshot1.id())
              .name(snapshot1.properties().name())
              .location(location)
              .status(Image.Status.AVAILABLE)
              .operatingSystem(OperatingSystem.builder()
                      .description(snapshot1.properties().description())
                      .family(OsFamily.LINUX)
                      .is64Bit(true)
                      .build())
              .build();

      assertEquals(actual1, expected1);

      server.enqueue(
              new MockResponse().setBody(stringFromResource("/compute/snapshot2.json"))
      );

      org.apache.jclouds.profitbricks.rest.domain.Snapshot snapshot2 = api.snapshotApi().get("some-id");

      Image actual2 = fnImage.apply(snapshot2);

      Image expected2 = new ImageBuilder()
              .ids(snapshot2.id())
              .name(snapshot2.properties().name())
              .location(location)
              .status(Image.Status.PENDING)
              .operatingSystem(OperatingSystem.builder()
                      .description("ubuntu")
                      .family(OsFamily.UBUNTU)
                      .is64Bit(true)
                      .version("00.00")
                      .build())
              .build();

      assertEquals(actual2, expected2);
      assertEquals(actual2.getOperatingSystem(), expected2.getOperatingSystem());

   }
}
