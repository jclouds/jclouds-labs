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
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.name.Named;

/**
 * Provides access to the Droplet management features.
 */
@RequestFilters(AuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/droplets")
public interface DropletApi extends Closeable {

   /**
    * Lists all existing droplets.
    * 
    * @return The list of all existing droplets.
    */
   @Named("droplet:list")
   @GET
   @SelectJson("droplets")
   List<Droplet> list();

   /**
    * Gets the details of the given droplet.
    * 
    * @param id The id of the droplet to get.
    * @return The details of the droplet or <code>null</code> if no droplet exists with the given id.
    */
   @Named("droplet:get")
   @GET
   @Path("/{id}")
   @SelectJson("droplet")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Droplet get(@PathParam("id") int id);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageId The id of the image to use to create the droplet.
    * @param sizeId The size to use to create the droplet.
    * @param regionId The region where the droplet must be created.
    * @return The created droplet.
    * 
    * @see #create(String, String, String, String)
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @SelectJson("droplet")
   DropletCreation create(@QueryParam("name") String name, @QueryParam("image_id") int imageId,
         @QueryParam("size_id") int sizeId, @QueryParam("region_id") int regionId);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageId The id of the image to use to create the droplet.
    * @param sizeId The size to use to create the droplet.
    * @param regionId The region where the droplet must be created.
    * @param options Custom options to create the droplet.
    * @return The created droplet.
    * 
    * @see #create(String, String, String, String, CreateDropletOptions)
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @SelectJson("droplet")
   DropletCreation create(@QueryParam("name") String name, @QueryParam("image_id") int imageId,
         @QueryParam("size_id") int sizeId, @QueryParam("region_id") int regionId, CreateDropletOptions options);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageSlug The slug of the image to use to create the droplet.
    * @param sizeSlug The slug of the size to use to create the droplet.
    * @param regionSlug The slug region where the droplet must be created.
    * @return The created droplet.
    * 
    * @see #create(String, int, int, int)
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @SelectJson("droplet")
   DropletCreation create(@QueryParam("name") String name, @QueryParam("image_slug") String imageSlug,
         @QueryParam("size_slug") String sizeSlug, @QueryParam("region_slug") String regionSlug);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageSlug The slug of the image to use to create the droplet.
    * @param sizeSlug The slug of the size to use to create the droplet.
    * @param regionSlug The slug region where the droplet must be created.
    * @param options Custom options to create the droplet.
    * @return The created droplet.
    * 
    * @see #create(String, int, int, int, CreateDropletOptions)
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @SelectJson("droplet")
   DropletCreation create(@QueryParam("name") String name, @QueryParam("image_slug") String imageSlug,
         @QueryParam("size_slug") String sizeSlug, @QueryParam("region_slug") String regionSlug,
         CreateDropletOptions options);

   /**
    * Reboots the given droplet.
    * 
    * @param id The id of the droplet to reboot.
    * @return The id of the event to track the reboot process.
    */
   @Named("droplet:reboot")
   @GET
   @Path("/{id}/reboot")
   @SelectJson("event_id")
   int reboot(@PathParam("id") int id);

   /**
    * Power cycles the given droplet.
    * 
    * @param id The id of the droplet to power cycle.
    * @return The id of the event to track the power cycle process.
    */
   @Named("droplet:powercycle")
   @GET
   @Path("/{id}/power_cycle")
   @SelectJson("event_id")
   int powerCycle(@PathParam("id") int id);

   /**
    * Shuts down the given droplet.
    * 
    * @param id The id of the droplet to shutdown.
    * @return The id of the event to track the shutdown process.
    */
   @Named("droplet:shutdown")
   @GET
   @Path("/{id}/shutdown")
   @SelectJson("event_id")
   int shutdown(@PathParam("id") int id);

   /**
    * Powers off the given droplet.
    * 
    * @param id The id of the droplet to power off.
    * @return The id of the event to track the power off process.
    */
   @Named("droplet:poweroff")
   @GET
   @Path("/{id}/power_off")
   @SelectJson("event_id")
   int powerOff(@PathParam("id") int id);

   /**
    * Powers on the given droplet.
    * 
    * @param id The id of the droplet to power on.
    * @return The id of the event to track the power on process.
    */
   @Named("droplet:poweron")
   @GET
   @Path("/{id}/power_on")
   @SelectJson("event_id")
   int powerOn(@PathParam("id") int id);

   /**
    * Resets the password for the given droplet.
    * 
    * @param id The id of the droplet to reset the password to.
    * @return The id of the event to track the password reset process.
    */
   @Named("droplet:resetpassword")
   @GET
   @Path("/{id}/password_reset")
   @SelectJson("event_id")
   int resetPassword(@PathParam("id") int id);

   /**
    * Changes the size for the given droplet.
    * 
    * @param id The id of the droplet to change the size to.
    * @param sizeId The id of the new size for the droplet.
    * @return The id of the event to track the resize process.
    */
   @Named("droplet:resize")
   @GET
   @Path("/{id}/resize")
   @SelectJson("event_id")
   int resize(@PathParam("id") int id, @QueryParam("size_id") int sizeId);

   /**
    * Takes a snapshot of the droplet once it has been powered off.
    * 
    * @param id The id of the droplet to take the snapshot of.
    * @return The id of the event to track the snapshot process.
    */
   @Named("droplet:snapshot")
   @GET
   @Path("/{id}/snapshot")
   @SelectJson("event_id")
   int snapshot(@PathParam("id") int id);

   /**
    * Takes a snapshot of the droplet once it has been powered off.
    * 
    * @param id The id of the droplet to take the snapshot of.
    * @param name The name for the snapshot.
    * @return The id of the event to track the snapshot process.
    */
   @Named("droplet:snapshot")
   @GET
   @Path("/{id}/snapshot")
   @SelectJson("event_id")
   int snapshot(@PathParam("id") int id, @QueryParam("name") String name);

   /**
    * Restores a droplet with a previous image or snapshot.
    * <p>
    * This will be a mirror copy of the image or snapshot to your droplet. Be sure you have backed up any necessary
    * information prior to restore.
    * 
    * @param id The id of the droplet to restore.
    * @param imageId The id of the image or snapshot to use to restore the droplet.
    * @return The id of the event to track the restore process.
    */
   @Named("droplet:restore")
   @GET
   @Path("/{id}/restore")
   @SelectJson("event_id")
   int restore(@PathParam("id") int id, @QueryParam("image_id") int imageId);

   /**
    * Rebuilds a droplet with a default image.
    * <p>
    * This is useful if you want to start again but retain the same IP address for your droplet.
    * 
    * @param id The id of the droplet to rebuild.
    * @param imageId The id of the image or snapshot to use to restore the droplet.
    * @return The id of the event to track the restore process.
    */
   @Named("droplet:rebuild")
   @GET
   @Path("/{id}/rebuild")
   @SelectJson("event_id")
   int rebuild(@PathParam("id") int id, @QueryParam("image_id") int imageId);

   /**
    * Renames a droplet to the specified name.
    * 
    * @param id The id of the droplet to rename.
    * @param name The new name for the droplet.
    * @return The id of the event to track the rename process.
    */
   @Named("droplet:rename")
   @GET
   @Path("/{id}/rename")
   @SelectJson("event_id")
   int rename(@PathParam("id") int id, @QueryParam("name") String name);

   /**
    * Destroys the given droplet.
    * 
    * @param id The id of the droplet to destroy.
    * @return The id of the event to track the destroy process.
    */
   @Named("droplet:destroy")
   @GET
   @Path("/{id}/destroy")
   @SelectJson("event_id")
   int destroy(@PathParam("id") int id);

   /**
    * Destroys the given droplet.
    * 
    * @param id The id of the droplet to destroy.
    * @param scrubData If true this will strictly write 0s to your prior partition to ensure that all data is completely
    *           erased.
    * @return The id of the event to track the destroy process.
    */
   @Named("droplet:destroy")
   @GET
   @Path("/{id}/destroy")
   @SelectJson("event_id")
   int destroy(@PathParam("id") int id, @QueryParam("scrub_data") boolean scrubData);
}
