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
package org.jclouds.aliyun.ecs.domain.internal;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;

import java.util.Iterator;
import java.util.Map;

/**
 * Base class for a paginated collection in Aliyun ECS.
 */
public class PaginatedCollection<T> extends IterableWithMarker<T> {

   private final Map<String, Iterable<T>> resources;

   /**
    * The page number requested by the user or the default page (1) if none supplied.
    */
   private final int pageNumber;

   /**
    * The total number of result records matching request criteria.
    */
   private final int totalCount;

   /**
    * The page size used; either specified in the request or the default for the API function being requested.
    */
   private final int pageSize;

   private final String regionId;

   private final String requestId;

   public PaginatedCollection(@Nullable Map<String, Iterable<T>> resources, int pageNumber, int totalCount,
                              int pageSize, String regionId, String requestId) {
      this.resources = resources != null ? resources : ImmutableMap.<String, Iterable<T>>of();
      this.pageNumber = pageNumber;
      this.totalCount = totalCount;
      this.pageSize = pageSize;
      this.regionId = regionId;
      this.requestId = requestId;
   }

   public Iterable<T> getResources() {
      return resources.entrySet().iterator().next().getValue();
   }

   public int getPageNumber() {
      return pageNumber;
   }

   public int getTotalCount() {
      return totalCount;
   }

   public int getPageSize() {
      return pageSize;
   }

   public String getRegionId() {
      return regionId;
   }

   public String getRequestId() {
      return requestId;
   }

   @Override
   public Iterator<T> iterator() {
      return resources.entrySet().iterator().next().getValue().iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      if (totalCount < pageSize) {
         return Optional.absent();
      }

      if ((float) pageNumber < ((float) totalCount / (float) pageSize)) {
         return Optional.of(toPaginationOptions(pageNumber + 1));
      }
      return Optional.absent();
   }

   private Object toPaginationOptions(Integer pageNumber) {
      return PaginationOptions.Builder.pageNumber(pageNumber);
   }
}

