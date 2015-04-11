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
package org.jclouds.shipyard.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.shipyard.domain.roles.RoleInfo;
import org.jclouds.shipyard.fallbacks.ShipyardFallbacks.BooleanOnRoleNotFoundAnd500;
import org.jclouds.shipyard.fallbacks.ShipyardFallbacks.NullOnRoleNotFoundAnd500;
import org.jclouds.shipyard.filters.ServiceKeyAuthentication;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestFilters({ ServiceKeyAuthentication.class })
@Path("/api/roles")
public interface RolesApi {

   @Named("roles:list")
   @GET
   List<RoleInfo> listRoles();
   
   @Named("roles:info")
   @Fallback(NullOnRoleNotFoundAnd500.class)
   @GET
   @Path("/{name}")
   RoleInfo getRole(@PathParam("name") String name);
   
   @Named("roles:create")
   @POST
   void createRole(@WrapWith("name") String name);
   
   @Named("roles:delete")
   @Fallback(BooleanOnRoleNotFoundAnd500.class)
   @DELETE
   boolean deleteRole(@WrapWith("name") String name);
}
