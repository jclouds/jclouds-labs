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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CATALOG_ITEM;

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
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface CatalogApi {

   /** Returns the catalog or null if not found. */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Catalog get(@EndpointParam URI catalogHref);

   @POST
   @Path("/catalogItems")
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem addItem(@EndpointParam URI catalogHref, @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /** Returns the item or null if not found. */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CatalogItem getItem(@EndpointParam URI catalogItemHref);

   @PUT
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem editItem(@EndpointParam URI catalogItemHref,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   @DELETE
   @Consumes
   @JAXBResponseParser
   void removeItem(@EndpointParam URI catalogItemHref);
}
