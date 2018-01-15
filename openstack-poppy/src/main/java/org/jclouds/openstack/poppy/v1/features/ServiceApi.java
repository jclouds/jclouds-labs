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
package org.jclouds.openstack.poppy.v1.features;

import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.auth.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.poppy.v1.config.CDN;
import org.jclouds.openstack.poppy.v1.domain.CreateService;
import org.jclouds.openstack.poppy.v1.domain.Service;
import org.jclouds.openstack.poppy.v1.domain.UpdateService;
import org.jclouds.openstack.poppy.v1.functions.ParseServiceURIFromHeaders;
import org.jclouds.openstack.poppy.v1.functions.ParseServices;
import org.jclouds.openstack.poppy.v1.functions.ServicesToPagedIterable;
import org.jclouds.openstack.poppy.v1.mapbinders.JSONPatchUpdate;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;

/**
 * Provides access to OpenStack Poppy Service features.
 */
@Beta
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Endpoint(CDN.class)
@Path("/services")
public interface ServiceApi {
   /**
    * Returns all Services currently defined in Poppy for the tenant.
    *
    * @return the list of all networks configured for the tenant in a a paged collection.
    */
   @Named("service:list")
   @GET
   @ResponseParser(ParseServices.class)
   @Transform(ServicesToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Service> list();

   /**
    * Lists Services by providing a specific set of listing options.
    * @param options Describes how services should be listed.
    */
   @Named("service:list")
   @GET
   @ResponseParser(ParseServices.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Service> list(PaginationOptions options);

   /**
    * Gets a specific Service by id (UUID).
    *
    * @param id  the id of the {@code Service}
    * @return the {@code Service} for the specified id, otherwise {@code null}
    */
   @Named("service:get")
   @GET
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Service get(@PathParam("id") String id);

   /**
    * Creates a Service.
    *
    * @param createService  Describes the new {@code Service} to be created.
    * @return a URI to the created {@code Service}.
    */
   @Named("service:create")
   @POST
   @ResponseParser(ParseServiceURIFromHeaders.class)
   @Produces(MediaType.APPLICATION_JSON)
   URI create(@BinderParam(BindToJsonPayload.class) CreateService createService);

   /**
    * Deletes the specified {@code Service}
    *
    * @param id the id of the {@code Service} to delete.
    * @return true if delete was successful, false if not.
    */
   @Named("service:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   /**
    * Updates a service by applying JSONPatch internally.
    * This requires providing your updatable {@code Service} and the target {@code UpdateService}.
    * They will be converted to JSON, diffed, and a JSON patch will be generated by jclouds automatically.
    *
    * @param service Source JSON
    * @param updateService Target JSON
    * @return a URI to the created service
    * @see <a href="https://tools.ietf.org/html/rfc6902">JSONPatch RFC</a>
    */
   @Named("service:update")
   @PATCH
   @Path("/{id}")
   @ResponseParser(ParseServiceURIFromHeaders.class)
   @MapBinder(JSONPatchUpdate.class)
   URI update(@PathParam("id") String id, @PayloadParam("service") Service service, @PayloadParam("updateService") UpdateService updateService);

   /**
    * Delete a cached service asset.
    *
    * @param id the id of the {@code Service} to delete.
    * @return true if delete was successful, false if not.
    */
   @Named("service:deleteAsset")
   @DELETE
   //@Path("/{id}/assets?url={url : .+}")
   @Path("/{id}/assets")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteAsset(@PathParam("id") String id, @QueryParam("url") String url);

   /**
    * Delete all cached service assets.
    *
    * @param id the id of the {@code Service} to delete.
    * @return true if delete was successful, false if not.
    */
   @Named("service:deleteAssets")
   @DELETE
   @Path("/{id}/assets")
   @QueryParams(keys = "all", values = "true")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteAssets(@PathParam("id") String id);
}
