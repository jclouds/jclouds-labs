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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface UserApi {

   /** Returns the user, even if disabled, or null if not found. */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   User get(@EndpointParam URI userHref);

   /**
    * Creates or imports a user in an organization. The user could be enabled or disabled.
    */
   @POST
   @Path("/users")
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser
   User addUserToOrg(@BinderParam(BindToXMLPayload.class) User user, @EndpointParam URI orgAdminHref);


   /**
    * Modifies a user. The user object could be enabled or disabled. Note: the lock status cannot be
    * changed using this call: use {@link #unlock(URI)}.
    */
   @PUT
   @Consumes(USER)
   @Produces(USER)
   @JAXBResponseParser
   User edit(@EndpointParam URI userHref, @BinderParam(BindToXMLPayload.class) User user);

   @DELETE
   @Consumes
   @JAXBResponseParser
   void remove(@EndpointParam URI userHref);

   @POST
   @Path("/action/unlock")
   @Consumes
   @JAXBResponseParser
   void unlock(@EndpointParam URI userHref);
}
