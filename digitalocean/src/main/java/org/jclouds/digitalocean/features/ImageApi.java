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
package org.jclouds.digitalocean.features;

import java.io.Closeable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.name.Named;

/**
 * Provides access to the Image management features.
 */
@RequestFilters(AuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/images")
public interface ImageApi extends Closeable {

   /**
    * Lists all available images.
    * 
    * @return The list of all available images.
    */
   @Named("image:list")
   @GET
   @SelectJson("images")
   List<Image> list();

   /**
    * Gets the details of the given image.
    * <p>
    * Note that Image IDs can change. The recommended way to get an image is using the {@link #get(String)} method.
    * 
    * @param id The id of the image to get.
    * @return The details of the image or <code>null</code> if no image exists with the given id.
    * 
    * @see #get(String)
    */
   @Named("image:get")
   @GET
   @Path("/{id}")
   @SelectJson("image")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("id") int id);

   /**
    * Gets the details of the given image.
    * 
    * @param slug The slug of the image to get.
    * @return The details of the image or <code>null</code> if no image exists with the given slug.
    * 
    * @see #get(int)
    */
   @Named("image:get")
   @GET
   @Path("/{slug}")
   @SelectJson("image")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("slug") String slug);

   /**
    * Deletes an existing image.
    * 
    * @param id The id of the key pair.
    */
   @Named("image:delete")
   @GET
   @Path("/{id}/destroy")
   void delete(@PathParam("id") int id);

   /**
    * Deletes an existing image.
    * 
    * @param slug The slug of the key pair.
    */
   @Named("image:delete")
   @GET
   @Path("/{slug}/destroy")
   void delete(@PathParam("slug") String slug);

   /**
    * Transfers the image to the given region.
    * 
    * @param id The id of the image to transfer.
    * @param regionId The id of the region to which the image will be transferred.
    * @return The id of the event to track the transfer process.
    * 
    * @see #transfer(String, int)
    */
   @Named("image:transfer")
   @GET
   @Path("/{id}/transfer")
   @SelectJson("event_id")
   int transfer(@PathParam("id") int id, @QueryParam("region_id") int regionId);

   /**
    * Transfers the image to the given region.
    * 
    * @param slug The slug of the image to transfer.
    * @param regionId The id of the region to which the image will be transferred.
    * @return The id of the event to track the transfer process.
    * 
    * @see #transfer(int, int)
    */
   @Named("image:transfer")
   @GET
   @Path("/{slug}/transfer")
   @SelectJson("event_id")
   int transfer(@PathParam("slug") String slug, @QueryParam("region_id") int regionId);
}
