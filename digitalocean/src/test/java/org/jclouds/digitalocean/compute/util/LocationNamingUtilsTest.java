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
package org.jclouds.digitalocean.compute.util;

import static org.jclouds.digitalocean.compute.util.LocationNamingUtils.encodeRegionIdAndName;
import static org.jclouds.digitalocean.compute.util.LocationNamingUtils.extractRegionId;
import static org.jclouds.digitalocean.compute.util.LocationNamingUtils.extractRegionName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.jclouds.digitalocean.domain.Region;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link LocationNamingUtils} class.
 */
@Test(groups = "unit", testName = "LocationNamingUtilsTest")
public class LocationNamingUtilsTest {

   @Test
   public void testExtractRegionId() {
      assertEquals(1, extractRegionId(location("1/foo")));
      assertEquals(1, extractRegionId(location("1///foo")));
      assertEquals(1, extractRegionId(location("1/2/3/foo")));
   }

   @Test
   public void testExtractRegionIdInvalidEncodedForms() {
      assertInvalidRegionIdFormat("/");
      assertInvalidRegionIdFormat("/foo");
      assertInvalidRegionIdFormat("/1/2/foo");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "location cannot be null")
   public void testExtractRegionIdNullLocation() {
      extractRegionId(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "location description should be in the form 'regionId/regionName' but was: foobar")
   public void testExtractRegionIdWithoutEncodedForm() {
      extractRegionId(location("foobar"));
   }

   @Test
   public void testExtractRegionName() {
      assertEquals("foo", extractRegionName(location("1/foo")));
      assertEquals("//foo", extractRegionName(location("1///foo")));
      assertEquals("2/3/foo", extractRegionName(location("1/2/3/foo")));
   }

   @Test
   public void testExtractRegionNameInvalidEncodedForms() {
      assertEquals("", extractRegionName(location("/")));
      assertEquals("foo", extractRegionName(location("/foo")));
      assertEquals("1/2/foo", extractRegionName(location("/1/2/foo")));
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "location cannot be null")
   public void testExtractRegionNameNullLocation() {
      extractRegionId(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "location description should be in the form 'regionId/regionName' but was: foobar")
   public void testExtractRegionNameWithoutEncodedForm() {
      extractRegionId(location("foobar"));
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "region cannot be null")
   public void testEncodeRegionAndNameNullRegion() {
      encodeRegionIdAndName(null);
   }

   @Test
   public void testEncodeRegionAndName() {
      assertEquals("1/foo", encodeRegionIdAndName(new Region(1, "foo", "bar")));
      assertEquals("1/1", encodeRegionIdAndName(new Region(1, "1", "1")));
      assertEquals("1///", encodeRegionIdAndName(new Region(1, "//", "1")));
   }

   private static void assertInvalidRegionIdFormat(String encoded) {
      try {
         extractRegionId(location(encoded));
         fail("Encoded form [" + encoded + "] shouldn't produce a valid region id");
      } catch (NumberFormatException ex) {
         // Success
      }
   }

   private static Location location(String description) {
      return new LocationBuilder().id("location").description(description).scope(LocationScope.REGION).build();
   }
}
