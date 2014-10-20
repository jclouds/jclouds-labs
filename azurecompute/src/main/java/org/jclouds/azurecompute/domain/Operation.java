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
package org.jclouds.azurecompute.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 *
 * Determines whether the asynchronous operation has succeeded, failed, or is still in progress.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 */
public final class Operation {
   public enum Status {
      IN_PROGRESS, SUCCEEDED, FAILED,
      UNRECOGNIZED;
   }

   public String id() {
      return id;
   }

   public Status status() {
      return status;
   }

   @Nullable public Integer httpStatusCode() {
      return httpStatusCode;
   }

   /** Present when the operation {@link Status#FAILED failed}. */
   @Nullable public Error error() {
      return error;
   }

   public static Operation create(String id, Status status, Integer httpStatusCode, Error error) {
      return new Operation(id, status, httpStatusCode, error);
   }

   // TODO: Remove from here down with @AutoValue.
   private Operation(String id, Status status, Integer httpStatusCode, Error error) {
      this.id = checkNotNull(id, "id");
      this.status = checkNotNull(status, "status");
      this.httpStatusCode = httpStatusCode;
      this.error = error;
   }

   private final String id;
   private final Status status;
   private final Integer httpStatusCode;
   private final Error error;

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, httpStatusCode, error);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Operation other = (Operation) obj;
      return equal(this.id, other.id) &&
            equal(this.status, other.status) &&
            equal(this.httpStatusCode, other.httpStatusCode) &&
            equal(this.error, other.error);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("id", id)
            .add("status", status)
            .add("httpStatusCode", httpStatusCode)
            .add("error", error).toString();
   }
}
