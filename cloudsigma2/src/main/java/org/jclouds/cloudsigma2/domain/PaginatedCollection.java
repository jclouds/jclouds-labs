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
package org.jclouds.cloudsigma2.domain;

import com.google.common.base.Optional;
import org.jclouds.cloudsigma2.options.PaginationOptions;
import org.jclouds.collect.IterableWithMarker;

import java.util.Iterator;

public class PaginatedCollection<T> extends IterableWithMarker<T> {

   private Iterable<T> objects;
   private PaginationOptions paginationOptions;

   public PaginatedCollection(Iterable<T> objects, PaginationOptions paginationOptions) {
      this.objects = objects;
      this.paginationOptions = paginationOptions;
   }

   @Override
   public Optional<Object> nextMarker() {
      if (paginationOptions.getLimit() == 0) {
         return Optional.absent();
      }

      if (paginationOptions.getTotalCount() - paginationOptions.getOffset() > paginationOptions.getLimit()) {
         return Optional.of((Object) new PaginationOptions.Builder()
               .limit(paginationOptions.getLimit())
               .offset(paginationOptions.getOffset() + paginationOptions.getLimit())
               .build());
      }

      return Optional.absent();
   }

   @Override
   public Iterator<T> iterator() {
      return objects.iterator();
   }
}
