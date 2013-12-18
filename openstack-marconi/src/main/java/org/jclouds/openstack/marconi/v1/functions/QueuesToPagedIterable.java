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
package org.jclouds.openstack.marconi.v1.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.ArgsToPagedIterable;
import org.jclouds.openstack.marconi.v1.MarconiApi;
import org.jclouds.openstack.marconi.v1.domain.Queue;
import org.jclouds.openstack.marconi.v1.features.QueueApi;
import org.jclouds.openstack.marconi.v1.options.ListQueuesOptions;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.marconi.v1.options.ListQueuesOptions.Builder.queryParameters;

/**
 * @author Everett Toews
 */
@Beta
public class QueuesToPagedIterable extends ArgsToPagedIterable.FromCaller<Queue, QueuesToPagedIterable> {

   private final MarconiApi api;

   @Inject
   protected QueuesToPagedIterable(MarconiApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<Queue>> markerToNextForArgs(List<Object> args) {
      String zone = String.class.cast(args.get(0));
      UUID clientId = UUID.class.cast(args.get(1));

      return new ListQueuesAtMarker(api.getQueueApiForZoneAndClient(zone, clientId));
   }

   private static class ListQueuesAtMarker implements Function<Object, IterableWithMarker<Queue>> {
      private final QueueApi api;

      @Inject
      protected ListQueuesAtMarker(QueueApi api) {
         this.api = checkNotNull(api, "api");
      }

      public PaginatedCollection<Queue> apply(Object input) {
         PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
         ListQueuesOptions listQueuesOptions = queryParameters(paginationOptions.buildQueryParameters());

         return api.list(listQueuesOptions);
      }

      public String toString() {
         return "ListRecordsAtMarker";
      }
   }
}
