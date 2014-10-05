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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.USER;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToAdminHref;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface UserApi {

   /**
    * Creates or imports a user in an organization. The user could be enabled or disabled.
    * 
    * <pre>
    * POST /admin/org/{id}/users
    * </pre>
    * 
    * @param orgUrn
    *           the urn for the org
    * @return the addd user
    */
   @POST
   @Path("/users")
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser User addUserToOrg(@BinderParam(BindToXMLPayload.class) User user,
         @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   @POST
   @Path("/users")
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser
   User addUserToOrg(@BinderParam(BindToXMLPayload.class) User user, @EndpointParam URI orgAdminHref);

   /**
    * Retrieves a user. This entity could be enabled or disabled.
    * 
    * <pre>
    * GET /admin/user/{id}
    * </pre>
    * 
    * @param userUrn
    *           the reference for the user
    * @return a user
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   User get(@EndpointParam(parser = URNToHref.class) String userUrn);

   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   User get(@EndpointParam URI userHref);

   /**
    * Modifies a user. The user object could be enabled or disabled. Note: the lock status cannot be
    * changed using this call: use unlockUser.
    * 
    * <pre>
    * PUT /admin/user/{id}
    * </pre>
    * 
    * @param userUrn
    *           the reference for the user
    * @return the modified user
    */
   @PUT
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser
   User edit(@EndpointParam(parser = URNToHref.class) String userUrn, @BinderParam(BindToXMLPayload.class) User user);

   @PUT
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser
   User edit(@EndpointParam URI userHref, @BinderParam(BindToXMLPayload.class) User user);

   /**
    * Deletes a user. Enabled and disabled users could be removed.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   void remove(@EndpointParam(parser = URNToHref.class) String userUrn);

   @DELETE
   @Consumes
   @JAXBResponseParser
   void remove(@EndpointParam URI userHref);

   /**
    * Unlocks a user.
    * 
    * <pre>
    * POST /admin/user/{id}/action/unlock
    * </pre>
    */
   @POST
   @Path("/action/unlock")
   @Consumes
   @JAXBResponseParser
   void unlock(@EndpointParam(parser = URNToHref.class) String userUrn);

   @POST
   @Path("/action/unlock")
   @Consumes
   @JAXBResponseParser
   void unlock(@EndpointParam URI userHref);
}
