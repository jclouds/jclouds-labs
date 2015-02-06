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
package org.jclouds.openstack.poppy.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.openstack.poppy.v1.PoppyApi;
import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.openstack.poppy.v1.features.ServiceApi;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * Makes Services work as a PagedIterable.
 */
public class ServicesToPagedIterable extends Arg0ToPagedIterable.FromCaller<Service, ServicesToPagedIterable> {

   private final PoppyApi api;

   @Inject
   protected ServicesToPagedIterable(PoppyApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<Service>> markerToNextForArg0(Optional<Object> arg0) {
      final ServiceApi serviceApi = api.getServiceApi();
      return new Function<Object, IterableWithMarker<Service>>() {

         @SuppressWarnings("unchecked")
         @Override
         public IterableWithMarker<Service> apply(Object input) {
            PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
            return IterableWithMarker.class.cast(serviceApi.list(paginationOptions));
         }

         @Override
         public String toString() {
            return "listServices()";
         }
      };
   }

}
