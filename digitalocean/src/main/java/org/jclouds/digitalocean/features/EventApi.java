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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.digitalocean.domain.Event;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.name.Named;

/**
 * Provides access to the Event API.
 */
@RequestFilters(AuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/events")
public interface EventApi extends Closeable {

   /**
    * Gets the details of a given event.
    * 
    * @param The id of the event to get.
    * @return The details of the event or <code>null</code> if no event exists with the given id.
    */
   @Named("event:get")
   @GET
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("event")
   @Nullable
   Event get(@PathParam("id") int id);
}
