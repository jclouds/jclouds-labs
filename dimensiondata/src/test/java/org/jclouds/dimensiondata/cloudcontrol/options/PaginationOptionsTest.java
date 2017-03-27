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
package org.jclouds.dimensiondata.cloudcontrol.options;

import com.google.common.collect.Multimap;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "PaginationOptionsTest")
public class PaginationOptionsTest {

   @Test()
   public void orderBy() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.orderBy("someField");
      assertQueryParameter(paginationOptions, "orderBy", "someField");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void orderByNullSupplied() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.orderBy(null);
   }

   @Test()
   public void pageNumber() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.pageNumber(2);
      assertQueryParameter(paginationOptions, "pageNumber", "2");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void pageSizeTooSmall() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.pageSize(-1);
   }

   @Test()
   public void pageSizeMinimum() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.pageSize(0);
      assertQueryParameter(paginationOptions, "pageSize", "0");
   }

   @Test()
   public void pageSizeMaximum() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.pageSize(10000);
      assertQueryParameter(paginationOptions, "pageSize", "10000");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void pageSizeTooBig() {
      PaginationOptions paginationOptions = new PaginationOptions();
      paginationOptions.pageSize(10001);
   }

   private void assertQueryParameter(PaginationOptions paginationOptions, String paramaterName, String expectedValue) {
      Multimap queryParameters = paginationOptions.buildQueryParameters();
      assertTrue(queryParameters.containsKey(paramaterName));
      Collection<String> values = queryParameters.get(paramaterName);
      assertEquals(values.size(), 1);
      assertEquals(values.iterator().next(), expectedValue);
   }
}
