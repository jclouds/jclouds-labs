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

import org.apache.jclouds.oneandone.rest.domain.BlockStorage;
import org.apache.jclouds.oneandone.rest.domain.BlockStorage.Server;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import java.io.Closeable;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/block_storages")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface BlockStorageApi extends Closeable {

   @Named("blockstorages:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<BlockStorage> list();

   @Named("blockstorages:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<BlockStorage> list(GenericQueryOptions options);

   @Named("blockstorages:get")
   @GET
   @Path("/{blockStorageId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   BlockStorage get(@PathParam("blockStorageId") String blockStorageId);

   @Named("blockstorages:create")
   @POST
   BlockStorage create(@BinderParam(BindToJsonPayload.class) BlockStorage.CreateBlockStorage blockStorage);

   @Named("blockstorages:update")
   @PUT
   @Path("/{blockStorageId}")
   BlockStorage update(@PathParam("blockStorageId") String blockStorageId, @BinderParam(BindToJsonPayload.class) BlockStorage.UpdateBlockStorage blockStorage);

   @Named("blockstorages:delete")
   @DELETE
   @Path("/{blockStorageId}")
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   void delete(@PathParam("blockStorageId") String blockStorageId);

   @Named("blockstorages:attachServer")
   @POST
   @Path("/{blockStorageId}/server")
   BlockStorage attachServer(@PathParam("blockStorageId") String blockStorageId, @BinderParam(BindToJsonPayload.class) Server.AttachServer server);

   @Named("blockstorages:detachServer")
   @DELETE
   @Path("/{blockStorageId}/server")
   @MapBinder(BindToJsonPayload.class)
   BlockStorage detachServer(@PathParam("blockStorageId") String blockStorageId);
}
