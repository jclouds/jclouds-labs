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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.inject.name.Named;

/**
 * Information of an error.
 */
public class BaseResponse {

   public static enum Status {
      OK, ERROR;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   private final Status status;
   @Named("error_message")
   private final String message;
   @Named("message")
   private final String details;

   @ConstructorProperties({ "status", "error_message", "message" })
   public BaseResponse(Status status, @Nullable String message, @Nullable String details) {
      this.status = checkNotNull(status, "status cannot be null");
      this.message = message;
      this.details = details;
   }

   public Status getStatus() {
      return status;
   }

   public String getMessage() {
      return message;
   }

   public String getDetails() {
      return details;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (details == null ? 0 : details.hashCode());
      result = prime * result + (message == null ? 0 : message.hashCode());
      result = prime * result + (status == null ? 0 : status.hashCode());
      return result;
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
      BaseResponse other = (BaseResponse) obj;
      if (details == null) {
         if (other.details != null) {
            return false;
         }
      } else if (!details.equals(other.details)) {
         return false;
      }
      if (message == null) {
         if (other.message != null) {
            return false;
         }
      } else if (!message.equals(other.message)) {
         return false;
      }
      if (status != other.status) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "BaseResponse [status=" + status + ", message=" + message + ", details=" + details + "]";
   }

}
