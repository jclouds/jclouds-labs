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
package org.jclouds.dimensiondata.cloudcontrol.suppliers;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdToURISupplier;

import javax.inject.Inject;
import java.net.URI;
import java.util.Map;
import java.util.Set;

public class RegionsToApiEndpoints implements RegionIdToURISupplier {

   private static final String DIMENSION_DATA_API_URL_TEMPLATE = "https://api-%s.dimensiondata.com";
   private final Supplier<Set<String>> regionIds;

   @Inject
   RegionsToApiEndpoints(@Region Supplier<Set<String>> regionIds) {
      this.regionIds = regionIds;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Builder<String, Supplier<URI>> regionToEndpoint = ImmutableMap.builder();
      for (String region : regionIds.get()) {
         URI endpoint = URI.create(String.format(DIMENSION_DATA_API_URL_TEMPLATE, region));
         regionToEndpoint.put(region, Suppliers.ofInstance(endpoint));
      }
      return regionToEndpoint.build();
   }
}
