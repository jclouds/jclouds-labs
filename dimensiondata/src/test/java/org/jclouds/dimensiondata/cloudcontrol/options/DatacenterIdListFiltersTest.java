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

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters.Builder.datacenterId;
import static org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters.Builder.paginationOptions;
import static org.jclouds.dimensiondata.cloudcontrol.options.PaginationOptions.Builder.pageNumber;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DatacenterIdListFiltersTest {

   @Test
   public void testDatacenterId_Varargs() {
      final Collection<String> queryIds = datacenterId("A", "B").buildQueryParameters().get("datacenterId");
      assertNotNull(queryIds, "Expected to have a value for query parameter for id");
      assertEquals(queryIds.size(), 2, "Query parameter count does not match");
      assertTrue(queryIds.contains("A"), "Expected query parameter value A not found");
      assertTrue(queryIds.contains("B"), "Expected query parameter value B not found");
   }

   @Test
   public void testDatacenterId_Collection() {
      final DatacenterIdListFilters datacenterIdListFilters = datacenterId(Collections.singletonList("value"));
      final Collection<String> id = datacenterIdListFilters.buildQueryParameters().get("datacenterId");
      assertNotNull(id, "Expected to have a value for query parameter for id");
      assertEquals("value", id.iterator().next());
   }

   @Test
   public void testPaginationOptions() {
      final PaginationOptions paginationOptions = pageNumber(1).pageSize(2).orderBy("orderBy");
      final DatacenterIdListFilters datacenterIdListFilters = paginationOptions(paginationOptions);
      assertEquals(paginationOptions.buildQueryParameters(), datacenterIdListFilters.buildQueryParameters(),
            "Query Parameters are not equal");
   }
}
