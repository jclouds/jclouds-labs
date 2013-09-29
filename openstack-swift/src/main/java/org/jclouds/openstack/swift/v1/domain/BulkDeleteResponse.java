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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;

/**
 * @see <a
 *      href="http://docs.openstack.org/developer/swift/misc.html#module-swift.common.middleware.bulk">
 *      Swift Bulk Middleware</a>
 */
public class BulkDeleteResponse {
   public static BulkDeleteResponse create(int deleted, int notFound, Map<String, String> errors) {
      return new BulkDeleteResponse(deleted, notFound, errors);
   }

   private final int deleted;
   private final int notFound;
   private final Map<String, String> errors;

   private BulkDeleteResponse(int deleted, int notFound, Map<String, String> errors) {
      this.deleted = deleted;
      this.notFound = notFound;
      this.errors = checkNotNull(errors, "errors");
   }

   /** number of files deleted. */
   public int deleted() {
      return deleted;
   }

   /** number of files not found. */
   public int notFound() {
      return notFound;
   }

   /** For each path that failed to delete, a corresponding error response. */
   public Map<String, String> errors() {
      return errors;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof BulkDeleteResponse) {
         BulkDeleteResponse that = BulkDeleteResponse.class.cast(object);
         return equal(deleted(), that.deleted()) //
               && equal(notFound(), that.notFound()) //
               && equal(errors(), that.errors());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(deleted(), notFound(), errors());
   }

   @Override
   public String toString() {
      return toStringHelper("") //
            .add("deleted", deleted()) //
            .add("notFound", notFound()) //
            .add("errors", errors()).toString();
   }
}
