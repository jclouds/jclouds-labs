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
import java.util.zip.ZipInputStream;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.oneandone.rest.domain.Vpn;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.apache.jclouds.oneandone.rest.util.VPNConfigParser;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/vpns")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface VpnApi {

   @Named("vpn:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Vpn> list();

   @Named("vpn:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Vpn> list(GenericQueryOptions options);

   @Named("vpn:get")
   @GET
   @Path("/{vpnId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Vpn get(@PathParam("vpnId") String vpnId);

   @Named("vpn:configurations:get")
   @GET
   @Path("/{vpnId}/configuration_file")
   @ResponseParser(VPNConfigParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Transform(VPNConfigParser.ToZipStream.class)
   ZipInputStream getConfiguration(@PathParam("vpnId") String vpnId);

   @Named("vpn:create")
   @POST
   Vpn create(@BinderParam(BindToJsonPayload.class) Vpn.CreateVpn vpn);

   @Named("vpn:update")
   @PUT
   @Path("/{vpnId}")
   Vpn update(@PathParam("vpnId") String vpnId, @BinderParam(BindToJsonPayload.class) Vpn.UpdateVpn vpn);

   @Named("vpn:delete")
   @DELETE
   @Path("/{vpnId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Vpn delete(@PathParam("vpnId") String vpnId);
}
