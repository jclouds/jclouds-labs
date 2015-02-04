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

import com.google.auto.value.AutoValue;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460801" >api</a>
 */
@AutoValue
public abstract class Error {

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

   Error() {} // For AutoValue only!

   /** Error code */
   public abstract Code code();

   /** User message */
   public abstract String message();

   public static Error create(Code code, String message) {
      return new AutoValue_Error(code, message);
   }
}
