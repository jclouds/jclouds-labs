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
package org.jclouds.openstack.heat.v1.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.openstack.heat.v1.domain.StackStatus;
import org.jclouds.openstack.heat.v1.options.ListStackOptions.SortDirection;
import org.jclouds.openstack.heat.v1.options.ListStackOptions.SortKey;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.globalTenant;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.limit;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.marker;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.name;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.sortDirection;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.sortKey;
import static org.jclouds.openstack.heat.v1.options.ListStackOptions.Builder.status;

/**
 * Tests behavior of {@code ListVirtualMachinesOptions}
 */
@Test(groups = "unit")
public class ListStackOptionsTest {

   public void testLimit() {
      ListStackOptions options = new ListStackOptions().limit(42);
      assertThat(options.buildQueryParameters().get("limit")).isEqualTo(ImmutableSet.of("42"));
   }

   public void testLimitStatic() {
      ListStackOptions options = limit(42);
      assertThat(options.buildQueryParameters().get("limit")).isEqualTo(ImmutableSet.of("42"));
   }

   public void testMarker() {
      ListStackOptions options = new ListStackOptions().marker("deploy");
      assertThat(options.buildQueryParameters().get("marker")).isEqualTo(ImmutableSet.of("deploy"));
   }

   public void testMarkerStatic() {
      ListStackOptions options = marker("deploy");
      assertThat(options.buildQueryParameters().get("marker")).isEqualTo(ImmutableSet.of("deploy"));
   }

   public void testStatus() {
      ListStackOptions options = new ListStackOptions().status(StackStatus.CREATE_COMPLETE);
      assertThat(options.buildQueryParameters().get("status"))
            .isEqualTo(ImmutableSet.of(StackStatus.CREATE_COMPLETE.toString()));
   }

   public void testStatusStatic() {
      ListStackOptions options = status(StackStatus.CREATE_COMPLETE);
      assertThat(options.buildQueryParameters().get("status"))
            .isEqualTo(ImmutableSet.of(StackStatus.CREATE_COMPLETE.toString()));
   }

   public void testName() {
      ListStackOptions options = new ListStackOptions().name("deployment");
      assertThat(options.buildQueryParameters().get("name")).isEqualTo(ImmutableSet.of("deployment"));
   }

   public void testNameStatic() {
      ListStackOptions options = name("deployment");
      assertThat(options.buildQueryParameters().get("name")).isEqualTo(ImmutableSet.of("deployment"));
   }

   public void testSortKey() {
      ListStackOptions options = new ListStackOptions().sortKey(SortKey.NAME);
      assertThat(options.buildQueryParameters().get("sort_keys")).isEqualTo(ImmutableSet.of(SortKey.NAME.toString()));
   }

   public void testSortKeyStatic() {
      ListStackOptions options = sortKey(SortKey.NAME);
      assertThat(options.buildQueryParameters().get("sort_keys")).isEqualTo(ImmutableSet.of(SortKey.NAME.toString()));
   }

   public void testSortDirection() {
      ListStackOptions options = new ListStackOptions().sortDirection(SortDirection.ASCENDING);
      assertThat(options.buildQueryParameters().get("sort_dir"))
            .isEqualTo(ImmutableSet.of(SortDirection.ASCENDING.toString()));
   }

   public void testSortDirectionStatic() {
      ListStackOptions options = sortDirection(SortDirection.DESCENDING);
      assertThat(options.buildQueryParameters().get("sort_dir"))
            .isEqualTo(ImmutableSet.of(SortDirection.DESCENDING.toString()));
   }

   public void testGlobalTenant() {
      ListStackOptions options = globalTenant(true);
      assertThat(options.buildQueryParameters().get("global_tenant"))
            .isEqualTo(ImmutableSet.of("true"));
   }
}
