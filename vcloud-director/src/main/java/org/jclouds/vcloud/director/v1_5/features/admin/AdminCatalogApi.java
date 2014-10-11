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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CONTROL_ACCESS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS;

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
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.features.CatalogApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminCatalogApi extends CatalogApi {

   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   AdminCatalog get(@EndpointParam URI orgHref);

   /**
    * Creates a catalog in an organization. The catalog will always be added in unpublished state.
    */
   @POST
   @Path("/catalogs")
   @Consumes(ADMIN_CATALOG)
   @Produces(ADMIN_CATALOG)
   @JAXBResponseParser
   AdminCatalog addCatalogToOrg(@BinderParam(BindToXMLPayload.class) AdminCatalog catalog,
         @EndpointParam URI orgHref);

   /**
    * Modifies a catalog. A catalog could be published or unpublished. The IsPublished property is
    * treated as a read only value by the server. In order to control publishing settings use the
    * 'publish' action must be used.
    */
   @PUT
   @Consumes(ADMIN_CATALOG)
   @Produces(ADMIN_CATALOG)
   @JAXBResponseParser
   AdminCatalog edit(@EndpointParam URI catalogAdminHref,
         @BinderParam(BindToXMLPayload.class) AdminCatalog catalog);

   /**
    * Deletes a catalog. The catalog could be removed if it is either published or unpublished.
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   void remove(@EndpointParam URI catalogAdminHref);

   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   Owner getOwner(@EndpointParam URI catalogAdminHref);

   @PUT
   @Path("/owner")
   @Consumes
   @Produces(OWNER)
   @JAXBResponseParser
   void setOwner(@EndpointParam URI catalogAdminHref, @BinderParam(BindToXMLPayload.class) Owner newOwner);
   
   /**
    * Publish a catalog. Publishing a catalog makes the catalog visible to all organizations in a
    * vCloud.
    */
   @POST
   @Path("/action/publish")
   @Consumes
   @Produces(PUBLISH_CATALOG_PARAMS)
   @JAXBResponseParser
   void publish(@EndpointParam URI catalogAdminHref, @BinderParam(BindToXMLPayload.class) PublishCatalogParams params);

   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ControlAccessParams editAccessControl(@EndpointParam URI catalogAdminHref,
         @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   @GET
   @Path("/controlAccess")
   @Consumes
   @JAXBResponseParser
   ControlAccessParams getAccessControl(@EndpointParam URI catalogAdminHref);
}
