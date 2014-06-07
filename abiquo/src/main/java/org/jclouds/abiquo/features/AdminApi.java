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
package org.jclouds.abiquo.features;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.functions.enterprise.ParseEnterpriseId;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;

/**
 * Provides synchronous access to Abiquo Admin API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
public interface AdminApi extends Closeable {
   /* ********************** User ********************** */

   /**
    * Get the information of the current user.
    * 
    * @return The information of the current user.
    */
   @Named("user:get")
   @GET
   @Path("/login")
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   UserDto getCurrentUser();

   /* ********************** Role ********************** */

   /**
    * List global roles.
    * 
    * @return The list of global Roles.
    */
   @Named("role:list")
   @GET
   @Path("/admin/roles")
   @Consumes(RolesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RolesDto listRoles();

   /**
    * List enterprise roles.
    * 
    * @return The list of Roles for the given enterprise.
    */
   @Named("role:list")
   @GET
   @Path("/admin/roles")
   @Consumes(RolesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RolesDto listRoles(@QueryParam("identerprise") @ParamParser(ParseEnterpriseId.class) final EnterpriseDto enterprise);

   /**
    * Retrieves the role of the given user.
    * 
    * @param user
    *           The user.
    * @return The role of the user.
    */
   @Named("role:get")
   @GET
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RoleDto getRole(@EndpointLink("role") @BinderParam(BindToPath.class) UserDto user);

   /**
    * Get the given role.
    * 
    * @param roleId
    *           The id of the role.
    * @return The role or <code>null</code> if it does not exist.
    */
   @Named("role:get")
   @GET
   @Path("/admin/roles/{role}")
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RoleDto getRole(@PathParam("role") Integer roleId);

   /**
    * Deletes an existing role.
    * 
    * @param role
    *           The role to delete.
    */
   @Named("role:delete")
   @DELETE
   void deleteRole(@EndpointLink("edit") @BinderParam(BindToPath.class) RoleDto role);

   /**
    * Updates an existing role.
    * 
    * @param role
    *           The new attributes for the role.
    * @return The updated role.
    */
   @Named("role:update")
   @PUT
   @Produces(RoleDto.BASE_MEDIA_TYPE)
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RoleDto updateRole(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RoleDto role);

   /**
    * Create a new role.
    * 
    * @param role
    *           The role to be created.
    * @return The created role.
    */
   @Named("role:create")
   @POST
   @Path("/admin/roles")
   @Produces(RoleDto.BASE_MEDIA_TYPE)
   @Consumes(RoleDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RoleDto createRole(@BinderParam(BindToXMLPayload.class) RoleDto role);

   /**
    * Get privileges of the given role.
    * 
    * @param role
    *           The role.
    * @return The list of privileges.
    */
   @Named("privilege:list")
   @GET
   @Consumes(PrivilegesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PrivilegesDto listPrivileges(@EndpointLink("privileges") @BinderParam(BindToPath.class) RoleDto role);
}
