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
package org.apache.jclouds.oneandone.rest.features;

import java.util.List;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.oneandone.rest.domain.MonitoringPolicy;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/monitoring_policies")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface MonitoringPolicyApi {

   @Named("monitoringpolicy:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<MonitoringPolicy> list();

   @Named("monitoringpolicy:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<MonitoringPolicy> list(GenericQueryOptions options);

   @Named("monitoringpolicy:get")
   @GET
   @Path("/{policyId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   MonitoringPolicy get(@PathParam("policyId") String policyId);

   @Named("monitoringpolicy:create")
   @POST
   MonitoringPolicy create(@BinderParam(BindToJsonPayload.class) MonitoringPolicy.CreatePolicy policy);

   @Named("monitoringpolicy:update")
   @PUT
   @Path("/{policyId}")
   MonitoringPolicy update(@PathParam("policyId") String policyId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.UpdatePolicy policy);

   @Named("monitoringpolicy:delete")
   @DELETE
   @Path("/{policyId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   MonitoringPolicy delete(@PathParam("policyId") String policyId);

   @Named("monitoringpolicy:ports:list")
   @GET
   @Path("/{policyId}/ports")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<MonitoringPolicy.Port> listPorts(@PathParam("policyId") String policyId);

   @Named("monitoringpolicy:ports:get")
   @GET
   @Path("/{policyId}/ports/{portId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   MonitoringPolicy.Port getPort(@PathParam("policyId") String policyId, @PathParam("portId") String portId);

   @Named("monitoringpolicy:ports:create")
   @POST
   @Path("/{policyId}/ports")
   MonitoringPolicy addPort(@PathParam("policyId") String policyId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.Port.CreatePort port);

   @Named("monitoringpolicy:ports:update")
   @PUT
   @Path("/{policyId}/ports/{portId}")
   MonitoringPolicy updatePort(@PathParam("policyId") String policyId, @PathParam("portId") String portId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.Port.UpdatePort port);

   @Named("monitoringpolicy:ports:delete")
   @DELETE
   @Path("/{policyId}/ports/{portId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   MonitoringPolicy deletePort(@PathParam("policyId") String policyId, @PathParam("portId") String portId);

   @Named("monitoringpolicy:processes:list")
   @GET
   @Path("/{policyId}/processes")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<MonitoringPolicy.Process> listProcesses(@PathParam("policyId") String policyId);

   @Named("monitoringpolicy:processes:get")
   @GET
   @Path("/{policyId}/processes/{processId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   MonitoringPolicy.Process getProcess(@PathParam("policyId") String policyId, @PathParam("processId") String processId);

   @Named("monitoringpolicy:processes:create")
   @POST
   @Path("/{policyId}/processes")
   MonitoringPolicy addProcess(@PathParam("policyId") String policyId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.Process.CreateProcess process);

   @Named("monitoringpolicy:processes:update")
   @PUT
   @Path("/{policyId}/processes/{processId}")
   MonitoringPolicy updateProcess(@PathParam("policyId") String policyId, @PathParam("processId") String processId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.Process.UpdateProcess process);

   @Named("monitoringpolicy:processes:delete")
   @DELETE
   @Path("/{policyId}/processes/{processId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   MonitoringPolicy deleteProcess(@PathParam("policyId") String policyId, @PathParam("processId") String processId);

   @Named("monitoringpolicy:servers:list")
   @GET
   @Path("/{policyId}/servers")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<MonitoringPolicy.Server> listServers(@PathParam("policyId") String policyId);

   @Named("monitoringpolicy:servers:create")
   @POST
   @Path("/{policyId}/servers")
   MonitoringPolicy attachServer(@PathParam("policyId") String policyId, @BinderParam(BindToJsonPayload.class) MonitoringPolicy.Server.CreateServer server);

   @Named("monitoringpolicy:servers:get")
   @GET
   @Path("/{policyId}/servers/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   MonitoringPolicy.Server getServer(@PathParam("policyId") String policyId, @PathParam("serverId") String serverId);

   @Named("monitoringpolicy:servers:delete")
   @DELETE
   @Path("/{policyId}/servers/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   MonitoringPolicy detachServer(@PathParam("policyId") String policyId, @PathParam("serverId") String serverId);
}
