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

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 *
 * Determines whether the asynchronous operation has succeeded, failed, or is still in progress.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 */
@AutoValue
public abstract class Operation {

   public enum Status {
      IN_PROGRESS, SUCCEEDED, FAILED,
      UNRECOGNIZED;
   }

   Operation() {} // For AutoValue only!

   public abstract String id();

   public abstract Status status();

   @Nullable public abstract Integer httpStatusCode();

   /** Present when the operation {@link Status#FAILED failed}. */
   @Nullable public abstract Error error();

   public static Operation create(String id, Status status, Integer httpStatusCode, Error error) {
      return new AutoValue_Operation(id, status, httpStatusCode, error);
   }
}
