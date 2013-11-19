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

import org.jclouds.Fallbacks;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.marconi.v1.domain.QueueStats;
import org.jclouds.openstack.marconi.v1.functions.ParseQueueStats;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

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
    */
   @Named("queue:create")
   @PUT
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean create();

   /**
    * Delete a queue.
    */
   @Named("queue:delete")
   @DELETE
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete();

   /**
    * Check for a queue's existence.
    */
   @Named("queue:get")
   @GET
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean exists();

   // TODO stream method!

   /**
    * Sets metadata for the specified queue.
    * <p/>
    * The request body has a limit of 256 KB, excluding whitespace.
    * <p/>
    * This operation replaces any existing metadata document in its entirety. Ensure that you do not accidentally
    * overwrite existing metadata that you want to retain.
    *
    * @param metadata Metadata in key/value pairs.
    */
   @Named("queue:setMetadata")
   @PUT
   @Path("/metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean setMetadata(@BinderParam(BindToJsonPayload.class) Map<String, String> metadata);

   /**
    * Gets metadata for the specified queue.
    */
   @Named("queue:getMetadata")
   @GET
   @Path("/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   Map<String, String> getMetadata();


   /**
    * Gets stats for the specified queue.
    */
   @Named("queue:getStats")
   @GET
   @Path("/stats")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseQueueStats.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   QueueStats getStats();
}
