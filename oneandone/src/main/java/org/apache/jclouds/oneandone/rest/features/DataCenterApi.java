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
package org.apache.jclouds.oneandone.rest.features;

import java.io.Closeable;
import java.util.List;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.oneandone.rest.domain.DataCenter;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

@Path("/datacenters")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface DataCenterApi extends Closeable {

   @Named("datacenter:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<DataCenter> list();

   @Named("datacenter:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<DataCenter> list(GenericQueryOptions options);

   @Named("datacenter:get")
   @GET
   @Path("/{dataCenterId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   DataCenter get(@PathParam("dataCenterId") String dataCenterId);
}
