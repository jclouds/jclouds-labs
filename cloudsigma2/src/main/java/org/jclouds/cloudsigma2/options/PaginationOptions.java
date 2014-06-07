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
package org.jclouds.cloudsigma2.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import javax.inject.Named;
import java.beans.ConstructorProperties;

public class PaginationOptions extends BaseHttpRequestOptions {

   public static class Builder {
      private int limit = PaginationOptions.DEFAULT_LIMIT;
      private int offset = 0;

      public Builder limit(int limit) {
         this.limit = limit;
         return this;
      }

      public Builder offset(int offset) {
         this.offset = offset;
         return this;
      }

      public PaginationOptions build() {
         if (limit < 0) {
            limit = PaginationOptions.DEFAULT_LIMIT;
         }

         if (offset < 0) {
            offset = 0;
         }

         return new PaginationOptions(limit, offset, 0);
      }
   }

   public static final int DEFAULT_LIMIT = 20;

   private final int limit;
   private final int offset;
   @Named("total_count")
   private final int totalCount;

   @ConstructorProperties({"limit", "offset", "total_count"})
   public PaginationOptions(int limit, int offset, int totalCount) {
      this.limit = limit;
      this.offset = offset;
      this.totalCount = totalCount;
      queryParameters.put("limit", String.valueOf(limit));
      queryParameters.put("offset", String.valueOf(offset));
   }

   public int getTotalCount() {
      return totalCount;
   }

   public int getOffset() {
      return offset;
   }

   public int getLimit() {
      return limit;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PaginationOptions)) return false;
      if (!super.equals(o)) return false;

      PaginationOptions that = (PaginationOptions) o;

      if (limit != that.limit) return false;
      if (offset != that.offset) return false;
      if (totalCount != that.totalCount) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + limit;
      result = 31 * result + offset;
      result = 31 * result + totalCount;
      return result;
   }

   @Override
   public String toString() {
      return "PaginationOptions{" +
            "limit=" + limit +
            ", offset=" + offset +
            ", totalCount=" + totalCount +
            "} " + super.toString();
   }
}
