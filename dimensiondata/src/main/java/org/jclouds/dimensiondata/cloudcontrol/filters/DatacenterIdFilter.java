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
package org.jclouds.dimensiondata.cloudcontrol.filters;

import com.google.common.base.Supplier;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.location.Zone;

import javax.inject.Inject;
import java.util.Set;

/**
 * Adds set of Datacenter IDs as set in jclouds.zones JVM property.
 */
public class DatacenterIdFilter implements HttpRequestFilter {

   protected final Supplier<Set<String>> datacenterIdsSupplier;

   @Inject
   DatacenterIdFilter(@Zone Supplier<Set<String>> datacenterIdsSupplier) {
      this.datacenterIdsSupplier = datacenterIdsSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Set<String> datacenterIds = datacenterIdsSupplier.get();
      if (datacenterIds != null && !datacenterIds.isEmpty()) {
         return request.toBuilder().addQueryParam("datacenterId", datacenterIds).build();
      } else {
         return request;
      }
   }
}
