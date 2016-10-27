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
package org.apache.jclouds.profitbricks.rest.util;

import static com.google.common.base.Preconditions.checkState;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.jclouds.profitbricks.rest.ProfitBricksApi;
import org.apache.jclouds.profitbricks.rest.domain.RequestStatus;
import org.apache.jclouds.profitbricks.rest.domain.Trackable;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;

@Singleton
public class Trackables {
   private final ProfitBricksApi api;
   private final Predicate<URI> requestCompletedPredicate;

   @Inject
   Trackables(ProfitBricksApi api, Predicate<URI> requestCompletedPredicate) {
      this.api = api;
      this.requestCompletedPredicate = requestCompletedPredicate;
   }

   public void waitUntilRequestCompleted(Trackable trackable) {
      if (trackable.requestStatusUri().isPresent()) {
         requestCompletedPredicate.apply(trackable.requestStatusUri().get());
         RequestStatus status = api.getRequestStatus(trackable.requestStatusUri().get());

         String entityName = trackable.getClass().getSimpleName();
         if (entityName.contains("AutoValue")) {
            entityName = entityName.substring(entityName.lastIndexOf('_') + 1);
         }

         checkState(RequestStatus.Status.DONE == status.metadata().status(), "%s creation failed: %s", entityName,
               status.metadata().message());
      }
   }

   public void waitUntilRequestCompleted(@Nullable URI uri) {
      if (uri != null) {
         requestCompletedPredicate.apply(uri);
         RequestStatus status = api.getRequestStatus(uri);
         checkState(RequestStatus.Status.DONE == status.metadata().status(), "Request %s failed: %s", uri, status
               .metadata().message());
      }
   }
}
