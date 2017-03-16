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
package org.jclouds.dimensiondata.cloudcontrol.domain;

import com.google.common.collect.Multimap;
import org.assertj.core.api.Assertions;
import org.jclouds.dimensiondata.cloudcontrol.options.PaginationOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "PaginatedCollectionTest")
public class PaginatedCollectionTest {

   public void testHasNextPage() {
      int pageNumber = 1;
      int pageCount = 0;
      int totalCount = 7;
      int pageSize = 5;
      final PaginatedCollection<Tags> collection = new PaginatedCollection<Tags>(null, pageNumber, pageCount,
            totalCount, pageSize);
      final Multimap<String, String> queryParameters = ((PaginationOptions) collection.nextMarker().get())
            .buildQueryParameters();
      final String nextPageNumber = queryParameters.get("pageNumber").iterator().next();
      Assertions.assertThat(nextPageNumber).isEqualTo("2");
   }

   public void testAllResultsViewed() {
      int pageNumber = 2;
      int pageCount = 0;
      int totalCount = 10;
      int pageSize = 5;
      final PaginatedCollection<Tags> collection = new PaginatedCollection<Tags>(null, pageNumber, pageCount,
            totalCount, pageSize);
      Assert.assertFalse(collection.nextMarker().isPresent());
   }
}
