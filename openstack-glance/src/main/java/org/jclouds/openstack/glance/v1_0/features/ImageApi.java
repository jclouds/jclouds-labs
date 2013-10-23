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
package org.jclouds.openstack.glance.v1_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.functions.ParseImageDetailsFromHeaders;
import org.jclouds.openstack.glance.v1_0.functions.internal.ParseImageDetails;
import org.jclouds.openstack.glance.v1_0.functions.internal.ParseImages;
import org.jclouds.openstack.glance.v1_0.options.CreateImageOptions;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

/**
 * Image Services
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 * @see <a href="http://glance.openstack.org/glanceapi.html">api doc</a>
 * @see <a
 *      href="https://github.com/openstack/glance/blob/master/glance/api/v1/images.py">api
 *      src</a>
 */
@RequestFilters(AuthenticateRequest.class)
public interface ImageApi {

   /**
    * List all images (IDs, names, links)
    * 
    * @return all images (IDs, names, links)
    */
   @GET
   @Consumes(APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<? extends Image> list();

   @GET
   @Consumes(APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<? extends Image> list(ListImageOptions options);

   /**
    * List all images (all details)
    * 
    * @return all images (all details)
    */
   @GET
   @Consumes(APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Transform(ParseImageDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<? extends ImageDetails> listInDetail();

   @GET
   @Consumes(APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<? extends ImageDetails> listInDetail(ListImageOptions options);

   /**
    * Return metadata about an image with id
    */
   @HEAD
   @Path("/images/{id}")
   @ResponseParser(ParseImageDetailsFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ImageDetails get(@PathParam("id") String id);

   /**
    * Return image data for image with id
    */
   @GET
   @Path("/images/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   InputStream getAsStream(@PathParam("id") String id);

   /**
    * Create a new image
    * 
    * @return detailed metadata about the newly stored image
    */
   @POST
   @Path("/images")
   @Produces(APPLICATION_OCTET_STREAM)
   @SelectJson("image")
   @Consumes(APPLICATION_JSON)
   ImageDetails create(@HeaderParam("x-image-meta-name") String name, Payload payload, CreateImageOptions... options);

   /**
    * Reserve a new image to be uploaded later
    * 
    * @return detailed metadata about the newly stored image
    * @see #upload
    */
   @POST
   @Path("/images")
   @SelectJson("image")
   @Consumes(APPLICATION_JSON)
   ImageDetails reserve(@HeaderParam("x-image-meta-name") String name, CreateImageOptions... options);

   /**
    * Upload image data for a previously-reserved image
    * <p/>
    * If an image was previously reserved, and thus is in the queued state, then
    * image data can be added using this method. If the image already as data
    * associated with it (e.g. not in the queued state), then you will receive a
    * 409 Conflict exception.
    * 
    * @param imageData
    *           the new image to upload
    * @param options
    *           can be used to adjust the metadata stored for the image in the
    *           same call
    * @return detailed metadata about the updated image
    * @see #reserve
    */
   @PUT
   @Path("/images/{id}")
   @Produces(APPLICATION_OCTET_STREAM)
   @SelectJson("image")
   @Consumes(APPLICATION_JSON)
   ImageDetails upload(@PathParam("id") String id, Payload imageData, UpdateImageOptions... options);

   /**
    * Adjust the metadata stored for an existing image
    * 
    * @return detailed metadata about the updated image
    */
   @PUT
   @Path("/images/{id}")
   @SelectJson("image")
   @Consumes(APPLICATION_JSON)
   ImageDetails update(@PathParam("id") String id, UpdateImageOptions... options);

   /**
    * Delete the image with the specified id
    * 
    * @return true if successful
    */
   @DELETE
   @Path("/images/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
}
