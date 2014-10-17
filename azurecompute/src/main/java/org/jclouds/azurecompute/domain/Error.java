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
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460801" >api</a>
 */
public final class Error {
   public static enum Code {
      MISSING_OR_INCORRECT_VERSION_HEADER,
      INVALID_XML_REQUEST,
      MISSING_OR_INVALID_REQUIRED_QUERY_PARAMETER,
      INVALID_HTTP_VERB,
      AUTHENTICATION_FAILED,
      RESOURCE_NOT_FOUND,
      INTERNAL_ERROR,
      OPERATION_TIMED_OUT,
      SERVER_BUSY,
      SUBSCRIPTION_DISABLED,
      BAD_REQUEST,
      CONFLICT_ERROR,
      UNRECOGNIZED;
   }

   /** Error code */
   public Code getCode() {
      return code;
   }

   /** User message */
   public String message() {
      return message;
   }

   public static Error create(Code code, String message) {
      return new Error(code, message);
   }

   // TODO: Remove from here down with @AutoValue.
   private Error(Code code, String message) {
      this.code = checkNotNull(code, "code");
      this.message = checkNotNull(message, "message");
   }

   private final Code code;
   private final String message;

   @Override
   public int hashCode() {
      return Objects.hashCode(code, message);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Error other = (Error) obj;
      return equal(this.code, other.code) && equal(this.message, other.message);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("code", code).add("message", message).toString();
   }
}
