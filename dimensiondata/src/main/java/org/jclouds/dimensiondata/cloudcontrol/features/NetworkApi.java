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
package org.jclouds.dimensiondata.cloudcontrol.features;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRuleTarget.Port;
import org.jclouds.dimensiondata.cloudcontrol.domain.FirewallRules;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRule;
import org.jclouds.dimensiondata.cloudcontrol.domain.NatRules;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomain;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkDomains;
import org.jclouds.dimensiondata.cloudcontrol.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontrol.domain.Placement;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlock;
import org.jclouds.dimensiondata.cloudcontrol.domain.PublicIpBlocks;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlan;
import org.jclouds.dimensiondata.cloudcontrol.domain.Vlans;
import org.jclouds.dimensiondata.cloudcontrol.filters.OrganisationIdFilter;
import org.jclouds.dimensiondata.cloudcontrol.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontrol.utils.ParseResponse;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequestFilters({ BasicAuthentication.class, OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/caas/{jclouds.api-version}/network")
public interface NetworkApi {

   @Named("network:deployNetworkDomain")
   @POST
   @Path("/deployNetworkDomain")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParseNetworkDomainId.class)
   String deployNetworkDomain(@PayloadParam("datacenterId") String datacenterId, @PayloadParam("name") String name,
         @PayloadParam("description") String description, @PayloadParam("type") String type);

   @Named("networkDomain:get")
   @GET
   @Path("/networkDomain/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   NetworkDomain getNetworkDomain(@PathParam("id") String networkDomainId);

   @Named("networkDomain:listWithDatacenterIdAndName")
   @GET
   @Path("/networkDomain")
   @Transform(ParseNetworkDomains.ToPagedIterable.class)
   @ResponseParser(ParseNetworkDomains.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<NetworkDomain> listNetworkDomainsWithDatacenterIdAndName(
         @QueryParam("datacenterId") String datacenterId, @QueryParam("name") String name);

   @Named("networkDomain:list")
   @GET
   @Path("/networkDomain")
   @ResponseParser(ParseNetworkDomains.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<NetworkDomain> listNetworkDomains(PaginationOptions options);

   @Named("networkDomain:list")
   @GET
   @Path("/networkDomain")
   @Transform(ParseNetworkDomains.ToPagedIterable.class)
   @ResponseParser(ParseNetworkDomains.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<NetworkDomain> listNetworkDomains();

   @Named("networkDomain:delete")
   @POST
   @Path("/deleteNetworkDomain")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteNetworkDomain(@PayloadParam("id") String networkDomainId);

   @Named("vlan:deploy")
   @POST
   @Path("/deployVlan")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParseVlanId.class)
   String deployVlan(@PayloadParam("networkDomainId") String networkDomainId, @PayloadParam("name") String name,
         @PayloadParam("description") String description,
         @PayloadParam("privateIpv4BaseAddress") String privateIpv4BaseAddress,
         @PayloadParam("privateIpv4PrefixSize") Integer privateIpv4PrefixSize);

   @Named("vlan:get")
   @GET
   @Path("/vlan/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Vlan getVlan(@PathParam("id") String vlanId);

   @Named("vlan:list")
   @GET
   @Path("/vlan")
   @ResponseParser(ParseVlans.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<Vlan> listVlans(@QueryParam("networkDomainId") String networkDomainId,
         PaginationOptions options);

   @Named("vlan:list")
   @GET
   @Path("/vlan")
   @Transform(ParseVlans.ToPagedIterable.class)
   @ResponseParser(ParseVlans.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Vlan> listVlans(@QueryParam("networkDomainId") String networkDomainId);

   @Named("vlan:delete")
   @POST
   @Path("/deleteVlan")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteVlan(@PayloadParam("id") String vlanId);

   @Named("networkDomain:addPublicIpBlock")
   @POST
   @Path("/addPublicIpBlock")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParsePublicIpBlockId.class)
   String addPublicIpBlock(@PayloadParam("networkDomainId") String networkDomainId);

   @Named("networkDomain:listPublicIPv4AddressBlocks")
   @GET
   @Path("/publicIpBlock")
   @ResponseParser(ParsePublicIpBlocks.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<PublicIpBlock> listPublicIPv4AddressBlocks(@QueryParam("networkDomainId") String networkDomainId,
         PaginationOptions options);

   @Named("networkDomain:listPublicIPv4AddressBlock")
   @GET
   @Path("/publicIpBlock")
   @Transform(ParsePublicIpBlocks.ToPagedIterable.class)
   @ResponseParser(ParsePublicIpBlocks.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<PublicIpBlock> listPublicIPv4AddressBlocks(@QueryParam("networkDomainId") String networkDomainId);

   @Named("networkDomain:removePublicIpBlock")
   @POST
   @Path("/removePublicIpBlock")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void removePublicIpBlock(@PayloadParam("id") String publicIpBlockId);

   @Named("networkDomain:getPublicIPv4AddressBlock")
   @GET
   @Path("/publicIpBlock/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   PublicIpBlock getPublicIPv4AddressBlock(@PathParam("id") String publicIPv4AddressBlockId);

   @Named("network:createNatRule")
   @POST
   @Path("/createNatRule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @ResponseParser(ParseNatRuleId.class)
   String createNatRule(@PayloadParam("networkDomainId") String networkDomainId,
         @PayloadParam("internalIp") String internalIp, @PayloadParam("externalIp") String externalIp);

   @Named("networkDomain:listNatRules")
   @GET
   @Path("/natRule")
   @ResponseParser(ParseNatRules.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<NatRule> listNatRules(@QueryParam("networkDomainId") String networkDomainId,
         PaginationOptions options);

   @Named("networkDomain:listNatRules")
   @GET
   @Path("/natRule")
   @Transform(ParseNatRules.ToPagedIterable.class)
   @ResponseParser(ParseNatRules.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<NatRule> listNatRules(@QueryParam("networkDomainId") String networkDomainId);

   @Named("network:getNatRule")
   @GET
   @Path("/natRule/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   NatRule getNatRule(@PathParam("id") String natRuleId);

   @Named("network:deleteNatRule")
   @POST
   @Path("/deleteNatRule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteNatRule(@PayloadParam("id") String natRuleId);

   @Named("networkDomain:createFirewallRule")
   @POST
   @Path("/createFirewallRule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(FirewallRuleId.class)
   String createFirewallRule(@PayloadParam("networkDomainId") String networkDomainId, @PayloadParam("name") String name,
         @PayloadParam("action") String action, @PayloadParam("ipVersion") String ipVersion,
         @PayloadParam("protocol") String protocol, @PayloadParam("source") FirewallRuleTarget source,
         @PayloadParam("destination") FirewallRuleTarget destination, @PayloadParam("enabled") Boolean enabled,
         @PayloadParam("placement") Placement placement);

   @Named("networkDomain:listFirewallRules")
   @GET
   @Path("/firewallRule")
   @ResponseParser(ParseFirewallRules.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<FirewallRule> listFirewallRules(@QueryParam("networkDomainId") String networkDomainId,
         PaginationOptions options);

   @Named("networkDomain:listFirewallRules")
   @GET
   @Path("/firewallRule")
   @Transform(ParseFirewallRules.ToPagedIterable.class)
   @ResponseParser(ParseFirewallRules.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<FirewallRule> listFirewallRules(@QueryParam("networkDomainId") String networkDomainId);

   @Named("networkDomain:deleteFirewallRule")
   @POST
   @Path("/deleteFirewallRule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteFirewallRule(@PayloadParam("id") String firewallRuleId);

   @Named("networkDomain:createPortList")
   @POST
   @Path("/createPortList")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(PortListId.class)
   String createPortList(@PayloadParam("networkDomainId") String networkDomainId, @PayloadParam("name") String name,
         @PayloadParam("description") String description, @PayloadParam("port") List<Port> port,
         @PayloadParam("childPortListId") List<String> childPortListId);

   @Named("networkDomain:getPortList")
   @GET
   @Path("/portList/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   FirewallRuleTarget.PortList getPortList(@PathParam("id") String portListId);

   @Named("networkDomain:deletePortList")
   @POST
   @Path("/deletePortList")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deletePortList(@PayloadParam("id") String portListId);

   @Singleton
   final class ParseFirewallRules extends ParseJson<FirewallRules> {

      @Inject
      ParseFirewallRules(Json json) {
         super(json, TypeLiteral.get(FirewallRules.class));
      }

      private static class ToPagedIterable
            extends Arg0ToPagedIterable<FirewallRule, ParseFirewallRules.ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<FirewallRule>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<FirewallRule>>() {
               @Override
               public IterableWithMarker<FirewallRule> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getNetworkApi().listFirewallRules(getArgs(request).get(0).toString(), paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseNatRules extends ParseJson<NatRules> {

      @Inject
      ParseNatRules(Json json) {
         super(json, TypeLiteral.get(NatRules.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<NatRule, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<NatRule>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<NatRule>>() {
               @Override
               public IterableWithMarker<NatRule> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getNetworkApi().listNatRules(getArgs(request).get(0).toString(), paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseNetworkDomains extends ParseJson<NetworkDomains> {

      @Inject
      ParseNetworkDomains(Json json) {
         super(json, TypeLiteral.get(NetworkDomains.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<NetworkDomain, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<NetworkDomain>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<NetworkDomain>>() {
               @Override
               public IterableWithMarker<NetworkDomain> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getNetworkApi().listNetworkDomains(paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParsePublicIpBlocks extends ParseJson<PublicIpBlocks> {

      @Inject
      ParsePublicIpBlocks(Json json) {
         super(json, TypeLiteral.get(PublicIpBlocks.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<PublicIpBlock, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<PublicIpBlock>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<PublicIpBlock>>() {
               @Override
               public IterableWithMarker<PublicIpBlock> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getNetworkApi()
                        .listPublicIPv4AddressBlocks(getArgs(request).get(0).toString(), paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseVlans extends ParseJson<Vlans> {

      @Inject
      ParseVlans(Json json) {
         super(json, TypeLiteral.get(Vlans.class));
      }

      static class ToPagedIterable extends Arg0ToPagedIterable<Vlan, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Vlan>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<Vlan>>() {
               @Override
               public IterableWithMarker<Vlan> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getNetworkApi().listVlans(getArgs(request).get(0).toString(), paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseNetworkDomainId extends ParseResponse {

      @Inject
      ParseNetworkDomainId(Json json) {
         super(json, "networkDomainId");
      }
   }

   @Singleton
   final class ParseVlanId extends ParseResponse {

      @Inject
      ParseVlanId(Json json) {
         super(json, "vlanId");
      }
   }

   @Singleton
   final class PortListId extends ParseResponse {

      @Inject
      PortListId(Json json) {
         super(json, "portListId");
      }
   }

   @Singleton
   final class FirewallRuleId extends ParseResponse {

      @Inject
      FirewallRuleId(Json json) {
         super(json, "firewallRuleId");
      }
   }

   @Singleton
   final class ParsePublicIpBlockId extends ParseResponse {

      @Inject
      ParsePublicIpBlockId(Json json) {
         super(json, "ipBlockId");
      }
   }

   @Singleton
   final class ParseNatRuleId extends ParseResponse {

      @Inject
      ParseNatRuleId(Json json) {
         super(json, "natRuleId");
      }
   }

}

