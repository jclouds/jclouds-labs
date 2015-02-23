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
import org.jclouds.shipyard.domain.containers.ContainerInfo;
import org.jclouds.shipyard.domain.containers.DeployContainer;
import org.jclouds.shipyard.filters.ServiceKeyAuthentication;

@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters({ ServiceKeyAuthentication.class })
@Path("/api/containers")
public interface ContainersApi {

   @Named("containers:list")
   @GET
   List<ContainerInfo> listContainers();
   
   @Named("containers:info")
   @GET
   @Path("/{id}")
   ContainerInfo getContainer(@PathParam("id") String id);
   
   @Named("containers:delete")
   @DELETE
   @Path("/{id}")
   void deleteContainer(@PathParam("id") String id);
   
   @Named("containers:stop")
   @GET
   @Path("/{id}/stop")
   void stopContainer(@PathParam("id") String id);
   
   @Named("containers:restart")
   @GET
   @Path("/{id}/restart")
   void restartContainer(@PathParam("id") String id);
   
   @Named("containers:deploy")
   @POST
   List<ContainerInfo> deployContainer(@BinderParam(BindToJsonPayload.class) DeployContainer deployContainer);
}
