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
import org.apache.jclouds.oneandone.rest.domain.FirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/firewall_policies")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface FirewallPolicyApi extends Closeable {

   @Named("firewallpolicies:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<FirewallPolicy> list();

   @Named("firewallpolicies:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<FirewallPolicy> list(GenericQueryOptions options);

   @Named("firewallpolicies:get")
   @GET
   @Path("/{firewallPolicyId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallPolicy get(@PathParam("firewallPolicyId") String firewallPolicyId);

   @Named("firewallpolicies:create")
   @POST
   FirewallPolicy create(@BinderParam(BindToJsonPayload.class) FirewallPolicy.CreateFirewallPolicy firewallPolicy);

   @Named("firewallpolicies:update")
   @PUT
   @Path("/{firewallPolicyId}")
   FirewallPolicy update(@PathParam("firewallPolicyId") String firewallPolicyId, @BinderParam(BindToJsonPayload.class) FirewallPolicy.UpdateFirewallPolicy firewallPolicy);

   @Named("firewallpolicies:delete")
   @DELETE
   @Path("/{firewallPolicyId}")
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallPolicy delete(@PathParam("firewallPolicyId") String firewallPolicyId);

   @Named("firewallpolicies:serverips:list")
   @GET
   @Path("/{firewallPolicyId}/server_ips")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<FirewallPolicy.ServerIp> listServerIps(@PathParam("firewallPolicyId") String firewallPolicyId);

   @Named("firewallpolicies:serverips:create")
   @POST
   @Path("/{firewallPolicyId}/server_ips")
   FirewallPolicy assignServerIp(@PathParam("firewallPolicyId") String firewallPolicyId, @BinderParam(BindToJsonPayload.class) FirewallPolicy.ServerIp.CreateServerIp serverIp);

   @Named("firewallpolicies:serverips:get")
   @GET
   @Path("/{firewallPolicyId}/server_ips/{serverIpId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallPolicy.ServerIp getServerIp(@PathParam("firewallPolicyId") String firewallPolicyId, @PathParam("serverIpId") String serverIpId);

   @Named("firewallpolicies:rules:list")
   @GET
   @Path("/{firewallPolicyId}/rules")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<FirewallPolicy.Rule> listRules(@PathParam("firewallPolicyId") String firewallPolicyId);

   @Named("firewallpolicies:rules:create")
   @POST
   @Path("/{firewallPolicyId}/rules")
   FirewallPolicy addRules(@PathParam("firewallPolicyId") String firewallPolicyId, @BinderParam(BindToJsonPayload.class) FirewallPolicy.Rule.AddRule rule);

   @Named("firewallpolicies:rules:get")
   @GET
   @Path("/{firewallPolicyId}/rules/{ruleId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   FirewallPolicy.Rule getRule(@PathParam("firewallPolicyId") String firewallPolicyId, @PathParam("ruleId") String ruleId);

   @Named("firewallpolicies:rules:delete")
   @DELETE
   @Path("/{firewallPolicyId}/rules/{ruleId}")
   @MapBinder(BindToJsonPayload.class)
   FirewallPolicy removeRule(@PathParam("firewallPolicyId") String firewallPolicyId, @PathParam("ruleId") String ruleId);
}
