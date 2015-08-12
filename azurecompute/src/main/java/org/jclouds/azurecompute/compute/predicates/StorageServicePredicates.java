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
package org.jclouds.azurecompute.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import org.jclouds.azurecompute.domain.StorageService;

import com.google.common.base.Predicate;

/**
 * Predicates for working with {@link StorageService} collections.
 */
public class StorageServicePredicates {


   public static Predicate<StorageService> sameLocation(final String location) {
      checkNotNull(location, "location must be defined");

      return new Predicate<StorageService>() {
         @Override
         public boolean apply(StorageService storageService) {
            return storageService.storageServiceProperties().location().equals(location);
         }

         @Override
         public String toString() {
            return "sameLocation(" + location + ")";
         }
      };
   }

   public static Predicate<StorageService> status(final StorageService.Status status) {
      checkNotNull(status, "status must be defined");

      return new Predicate<StorageService>() {
         @Override
         public boolean apply(StorageService storageService) {
            return storageService.storageServiceProperties().status() == status;
         }

         @Override
         public String toString() {
            return "status(" + status + ")";
         }
      };
   }

   public static Predicate<StorageService> matchesName(final String defaultStorageAccountPrefix) {
      checkNotNull(defaultStorageAccountPrefix, "defaultStorageAccountPrefix must be defined");

      return new Predicate<StorageService>() {
         @Override
         public boolean apply(StorageService storageService) {
            return storageService.serviceName().matches(format("^%s[a-z]{10}$", defaultStorageAccountPrefix));
         }

         @Override
         public String toString() {
            return "matchesName(" + defaultStorageAccountPrefix + ")";
         }
      };
   }

}
