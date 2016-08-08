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

import java.io.Closeable;
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
import org.apache.jclouds.oneandone.rest.domain.LoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/load_balancers")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface LoadBalancerApi extends Closeable {

   @Named("loadbalancers:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancer> list();

   @Named("loadbalancers:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancer> list(GenericQueryOptions options);

   @Named("loadbalancers:get")
   @GET
   @Path("/{loadbalancerId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   LoadBalancer get(@PathParam("loadbalancerId") String loadbalancerId);

   @Named("loadbalancers:create")
   @POST
   LoadBalancer create(@BinderParam(BindToJsonPayload.class) LoadBalancer.CreateLoadBalancer loadBalancer);

   @Named("loadbalancers:update")
   @PUT
   @Path("/{loadbalancerId}")
   LoadBalancer update(@PathParam("loadbalancerId") String loadbalancerId, @BinderParam(BindToJsonPayload.class) LoadBalancer.UpdateLoadBalancer loadBalancer);

   @Named("loadbalancers:delete")
   @DELETE
   @Path("/{loadbalancerId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   LoadBalancer delete(@PathParam("loadbalancerId") String loadbalancerId);

   @Named("loadbalancers:serverips:list")
   @GET
   @Path("/{loadbalancerId}/server_ips")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancer.ServerIp> listServerIps(@PathParam("loadbalancerId") String loadbalancerId);

   @Named("loadbalancers:serverips:create")
   @POST
   @Path("/{loadbalancerId}/server_ips")
   LoadBalancer assignServerIp(@PathParam("loadbalancerId") String loadbalancerId, @BinderParam(BindToJsonPayload.class) LoadBalancer.ServerIp.CreateServerIp serverIp);

   @Named("loadbalancers:serverips:get")
   @GET
   @Path("/{loadbalancerId}/server_ips/{serverIpId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   LoadBalancer.ServerIp getServerIp(@PathParam("loadbalancerId") String loadbalancerId, @PathParam("serverIpId") String serverIpId);

   @Named("loadbalancers:serverips:delete")
   @DELETE
   @Path("/{loadbalancerId}/server_ips/{serverIpId}")
   @MapBinder(BindToJsonPayload.class)
   LoadBalancer unassignServerIp(@PathParam("loadbalancerId") String loadbalancerId, @PathParam("serverIpId") String serverIpId);

   @Named("loadbalancers:rules:list")
   @GET
   @Path("/{loadbalancerId}/rules")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancer.Rule> listRules(@PathParam("loadbalancerId") String loadbalancerId);

   @Named("loadbalancers:rules:create")
   @POST
   @Path("/{loadbalancerId}/rules")
   LoadBalancer addRules(@PathParam("loadbalancerId") String loadbalancerId, @BinderParam(BindToJsonPayload.class) LoadBalancer.Rule.AddRule rule);

   @Named("loadbalancers:rules:get")
   @GET
   @Path("/{loadbalancerId}/rules/{ruleId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   LoadBalancer.Rule getRule(@PathParam("loadbalancerId") String loadbalancerId, @PathParam("ruleId") String ruleId);

   @Named("loadbalancers:rules:delete")
   @DELETE
   @Path("/{loadbalancerId}/rules/{ruleId}")
   @MapBinder(BindToJsonPayload.class)
   LoadBalancer removeRule(@PathParam("loadbalancerId") String firewallPolicyId, @PathParam("ruleId") String ruleId);
}
