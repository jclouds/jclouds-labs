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

import java.net.URL;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class StorageService {

   @AutoValue
   public abstract static class StorageServiceProperties {

      StorageServiceProperties() {} // For AutoValue only!

      @Nullable public abstract String description();

      public abstract String status();

      public abstract String location();

      public abstract String accountType();

      public static StorageServiceProperties create(String description, String status, String location, String accountType) {
         return new AutoValue_StorageService_StorageServiceProperties(description, status, location, accountType);
      }
   }

   StorageService() {} // For AutoValue only!

   public abstract URL url();

   public abstract String serviceName();

   public abstract StorageServiceProperties storageServiceProperties();

   public static StorageService create(URL url, String serviceName, StorageServiceProperties storageServiceProperties) {
      return new AutoValue_StorageService(url, serviceName, storageServiceProperties);
   }
}
