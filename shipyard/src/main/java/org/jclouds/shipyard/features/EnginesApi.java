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
package org.jclouds.shipyard.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.shipyard.domain.engines.AddEngine;
import org.jclouds.shipyard.domain.engines.EngineInfo;
import org.jclouds.shipyard.filters.ServiceKeyAuthentication;

@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters({ ServiceKeyAuthentication.class })
@Path("/api/engines")
public interface EnginesApi {

   @Named("engines:list-info")
   @GET
   List<EngineInfo> listEngines();

   @Named("engines:info")
   @GET
   @Path("/{id}")
   EngineInfo getEngine(@PathParam("id") String engineID);
   
   @Named("engines:add")
   @POST
   void addEngine(@BinderParam(BindToJsonPayload.class) AddEngine addEngine);
   
   @Named("engines:remove")
   @DELETE
   @Path("/{id}")
   void removeEngine(@PathParam("id") String engineID);
}
