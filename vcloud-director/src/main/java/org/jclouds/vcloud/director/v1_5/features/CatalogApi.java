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

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface CatalogApi {

   /**
    * Retrieves a catalog.
    * 
    * <pre>
    * GET /catalog/{id}
    * </pre>
    * 
    * @param catalogUrn
    *           the reference for the catalog
    * @return a catalog
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Catalog get(@EndpointParam(parser = URNToHref.class) String catalogUrn);

   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Catalog get(@EndpointParam URI catalogHref);

   /**
    * Creates a catalog item in a catalog.
    * 
    * <pre>
    * POST /catalog/{id}/catalogItems
    * </pre>
    * 
    * @param catalogUrn
    *           the URI of the catalog
    * @param catalogItem
    *           the catalog item to add
    * @return the added catalog item
    */
   @POST
   @Path("/catalogItems")
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem addItem(@EndpointParam(parser = URNToHref.class) String catalogUrn,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   @POST
   @Path("/catalogItems")
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem addItem(@EndpointParam URI catalogHref, @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * Retrieves a catalog item.
    * 
    * <pre>
    * GET /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemUrn
    *           the reference for the catalog item
    * @return the catalog item
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CatalogItem getItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn);

   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CatalogItem getItem(@EndpointParam URI catalogItemHref);

   /**
    * Modifies a catalog item.
    * 
    * <pre>
    * PUT /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemUrn
    *           the reference for the catalog item
    * @param catalogItem
    *           the catalog item
    * @return the edited catalog item
    */
   @PUT
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem editItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   @PUT
   @Consumes(CATALOG_ITEM)
   @Produces(CATALOG_ITEM)
   @JAXBResponseParser
   CatalogItem editItem(@EndpointParam URI catalogItemHref,
         @BinderParam(BindToXMLPayload.class) CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    * 
    * <pre>
    * DELETE /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemUrn
    *           the reference for the catalog item
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   void removeItem(@EndpointParam(parser = URNToHref.class) String catalogItemUrn);

   @DELETE
   @Consumes
   @JAXBResponseParser
   void removeItem(@EndpointParam URI catalogItemHref);
}
