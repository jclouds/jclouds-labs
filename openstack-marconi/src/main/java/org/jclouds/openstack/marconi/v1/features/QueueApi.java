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
package org.jclouds.openstack.marconi.v1.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.marconi.v1.domain.Queue;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.marconi.v1.domain.Queues;
import org.jclouds.openstack.marconi.v1.fallbacks.EmptyQueuesFallback;
import org.jclouds.openstack.marconi.v1.functions.ParseQueueStats;
import org.jclouds.openstack.marconi.v1.functions.ParseQueues;
import org.jclouds.openstack.marconi.v1.functions.QueuesToPagedIterable;
import org.jclouds.openstack.marconi.v1.options.ListQueuesOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

import static org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import static org.jclouds.Fallbacks.FalseOnNotFoundOr404;

/**
 * Provides access to Queues via their REST API.
 *
 * @author Everett Toews
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface QueueApi {
   /**
    * Create a queue.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    */
   @Named("queue:create")
   @PUT
   @Path("queues/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean create(@PathParam("name") String name);

   /**
    * Delete a queue.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    */
   @Named("queue:delete")
   @DELETE
   @Path("queues/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("name") String name);

   /**
    * Check for a queue's existence.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    */
   @Named("queue:get")
   @GET
   @Path("queues/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean exists(@PathParam("name") String name);

   /**
    * Lists the queues.
    *
    * @param detailed Determines whether queue metadata is included in the list.
    */
   @Named("queue:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseQueues.class)
   @Transform(QueuesToPagedIterable.class)
   @Path("queues")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Queue> list(@QueryParam("detailed") boolean detailed);

   /**
    * Use this method to manually page through the list of queues.
    */
   @Named("queue:list")
   @GET
   @ResponseParser(ParseQueues.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyQueuesFallback.class)
   @Path("queues")
   Queues list(ListQueuesOptions options);

   /**
    * Sets metadata for the specified queue.
    * <p/>
    * The request body has a limit of 256 KB, excluding whitespace.
    * <p/>
    * This operation replaces any existing metadata document in its entirety. Ensure that you do not accidentally
    * overwrite existing metadata that you want to retain.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    * @param metadata Metadata in key/value pairs.
    */
   @Named("queue:setMetadata")
   @PUT
   @Path("queues/{name}/metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean setMetadata(@PathParam("name") String name,
                       @BinderParam(BindToJsonPayload.class) Map<String, String> metadata);

   /**
    * Gets metadata for the specified queue.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    */
   @Named("queue:getMetadata")
   @GET
   @Path("queues/{name}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   Map<String, String> getMetadata(@PathParam("name") String name);


   /**
    * Gets stats for the specified queue.
    *
    * @param name Name of the queue. The name must not exceed 64 bytes in length, and it is limited to US-ASCII
    *             letters, digits, underscores, and hyphens.
    */
   @Named("queue:getStats")
   @GET
   @Path("queues/{name}/stats")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseQueueStats.class)
   @Fallback(FalseOnNotFoundOr404.class)
   QueueStats getStats(@PathParam("name") String name);
}
