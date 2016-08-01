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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.oneandone.rest.domain.SharedStorage;
import org.apache.jclouds.oneandone.rest.domain.SharedStorage.Server;
import org.apache.jclouds.oneandone.rest.domain.SharedStorageAccess;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/shared_storages")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface SharedStorageApi extends Closeable {

   @Named("sharedstorages:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<SharedStorage> list();

   @Named("sharedstorages:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<SharedStorage> list(GenericQueryOptions options);

   @Named("sharedstorages:get")
   @GET
   @Path("/{sharedStorageId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   SharedStorage get(@PathParam("sharedStorageId") String sharedStorageId);

   @Named("sharedstorages:create")
   @POST
   SharedStorage create(@BinderParam(BindToJsonPayload.class) SharedStorage.CreateSharedStorage sharedStorage);

   @Named("sharedstorages:update")
   @PUT
   @Path("/{sharedStorageId}")
   SharedStorage update(@PathParam("sharedStorageId") String sharedStorageId, @BinderParam(BindToJsonPayload.class) SharedStorage.UpdateSharedStorage sharedStorage);

   @Named("sharedstorages:delete")
   @DELETE
   @Path("/{sharedStorageId}")
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   SharedStorage delete(@PathParam("sharedStorageId") String sharedStorageId);

   @Named("sharedstorages:servers:list")
   @GET
   @Path("/{sharedStorageId}/servers")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Server> listServers(@PathParam("sharedStorageId") String sharedStorageId);

   @Named("sharedstorages:servers:create")
   @POST
   @Path("/{sharedStorageId}/servers")
   SharedStorage attachServer(@PathParam("sharedStorageId") String sharedStorageId, @BinderParam(BindToJsonPayload.class) Server.CreateServer server);

   @Named("sharedstorages:servers:get")
   @GET
   @Path("/{sharedStorageId}/servers/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server getServer(@PathParam("sharedStorageId") String sharedStorageId, @PathParam("serverId") String serverId);

   @Named("sharedstorages:servers:delete")
   @DELETE
   @Path("/{sharedStorageId}/servers/{serverId}")
   @MapBinder(BindToJsonPayload.class)
   SharedStorage detachServer(@PathParam("sharedStorageId") String sharedStorageId, @PathParam("serverId") String serverId);

   @Named("sharedstorages:access:list")
   @GET
   @Path("/access")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<SharedStorageAccess> getAccessCredentials();

   @Named("sharedstorages:access:update")
   @PUT
   @Path("/access")
   List<SharedStorageAccess> changePassword(@BinderParam(BindToJsonPayload.class) SharedStorageAccess.UpdateSharedStorageAccess access);
}
