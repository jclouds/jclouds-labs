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
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork.Server;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/private_networks")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface PrivateNetworkApi {

   @Named("privatenetwork:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<PrivateNetwork> list();

   @Named("privatenetwork:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<PrivateNetwork> list(GenericQueryOptions options);

   @Named("privatenetwork:get")
   @GET
   @Path("/{privateNetworkId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   PrivateNetwork get(@PathParam("privateNetworkId") String privateNetworkId);

   @Named("privatenetwork:create")
   @POST
   PrivateNetwork create(@BinderParam(BindToJsonPayload.class) PrivateNetwork.CreatePrivateNetwork privateNetwork);

   @Named("privatenetwork:update")
   @PUT
   @Path("/{privateNetworkId}")
   PrivateNetwork update(@PathParam("privateNetworkId") String privateNetworkId, @BinderParam(BindToJsonPayload.class) PrivateNetwork.UpdatePrivateNetwork privateNetwork);

   @Named("privatenetwork:delete")
   @DELETE
   @Path("/{privateNetworkId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   PrivateNetwork delete(@PathParam("privateNetworkId") String privateNetworkId);

   @Named("sharedstorages:servers:list")
   @GET
   @Path("/{privateNetworkId}/servers")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Server> listServers(@PathParam("privateNetworkId") String privateNetworkId);

   @Named("sharedstorages:servers:create")
   @POST
   @Path("/{privateNetworkId}/servers")
   PrivateNetwork attachServer(@PathParam("privateNetworkId") String privateNetworkId, @BinderParam(BindToJsonPayload.class) PrivateNetwork.Server.CreateServer server);

   @Named("sharedstorages:servers:get")
   @GET
   @Path("/{privateNetworkId}/servers/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server getServer(@PathParam("privateNetworkId") String privateNetworkId, @PathParam("serverId") String serverId);

   @Named("sharedstorages:servers:delete")
   @DELETE
   @Path("/{privateNetworkId}/servers/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   PrivateNetwork detachServer(@PathParam("privateNetworkId") String privateNetworkId, @PathParam("serverId") String serverId);
}
