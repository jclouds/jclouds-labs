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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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
import org.apache.jclouds.oneandone.rest.domain.Dvd;
import org.apache.jclouds.oneandone.rest.domain.Hardware;
import org.apache.jclouds.oneandone.rest.domain.HardwareFlavour;
import org.apache.jclouds.oneandone.rest.domain.Hdd;
import org.apache.jclouds.oneandone.rest.domain.Image;
import org.apache.jclouds.oneandone.rest.domain.PrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Server;
import org.apache.jclouds.oneandone.rest.domain.Server.CreateFixedInstanceServer;
import org.apache.jclouds.oneandone.rest.domain.Server.CreateServer;
import org.apache.jclouds.oneandone.rest.domain.ServerFirewallPolicy;
import org.apache.jclouds.oneandone.rest.domain.ServerIp;
import org.apache.jclouds.oneandone.rest.domain.ServerLoadBalancer;
import org.apache.jclouds.oneandone.rest.domain.ServerPrivateNetwork;
import org.apache.jclouds.oneandone.rest.domain.Snapshot;
import org.apache.jclouds.oneandone.rest.domain.Status;
import org.apache.jclouds.oneandone.rest.domain.Types;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.apache.jclouds.oneandone.rest.util.ServerFirewallPolicyAdapter;
import org.apache.jclouds.oneandone.rest.util.SnapshotAdapter;
import org.jclouds.Fallbacks;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.util.Strings2;

@Path("servers")
@Produces("application/json")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface ServerApi extends Closeable {

   @Named("servers:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Server> list();

   @Named("servers:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)

   List<Server> list(GenericQueryOptions options);

   @Named("servers:flavours:list")
   @GET
   @Path("/fixed_instance_sizes")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)

   List<HardwareFlavour> listHardwareFlavours();

   @Named("servers:flavours:get")
   @GET
   @Path("fixed_instance_sizes/{id}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   HardwareFlavour getHardwareFlavour(@PathParam("id") String flavourId);

   @Named("servers:get")
   @GET
   @Path("/{serverId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server get(@PathParam("serverId") String serverId);

   @Named("servers:status:get")
   @GET
   @Path("/{serverId}/status")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Status getStatus(@PathParam("serverId") String serverId);

   @Named("servers:create")
   @POST
   Server create(@BinderParam(BindToJsonPayload.class) CreateServer server);

   @Named("servers:fixedinstace:create")
   @POST
   Server createFixedInstanceServer(@BinderParam(BindToJsonPayload.class) CreateFixedInstanceServer server);

   @Named("server:update")
   @PUT
   @Path("/{serverId}")
   Server update(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Server.UpdateServer server);

   @Named("server:Status:update")
   @PUT
   @Path("/{serverId}/status/action")
   Server updateStatus(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Server.UpdateStatus server);

   @Named("server:delete")
   @DELETE
   @Path("/{serverId}")
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server delete(@PathParam("serverId") String serverId);

   @Named("servers:hardware:get")
   @GET
   @Path("/{serverId}/hardware")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Hardware getHardware(@PathParam("serverId") String serverId);

   @Named("server:hardware:update")
   @PUT
   @Path("/{serverId}/hardware")
   Server updateHardware(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Hardware.UpdateHardware server);

   @Named("servers:hardware:hdd:list")
   @GET
   @Path("/{serverId}/hardware/hdds")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Hdd> listHdds(@PathParam("serverId") String serverId);

   @Named("servers:hardware:hdds:create")
   @POST
   @Path("/{serverId}/hardware/hdds")
   Server addHdd(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Hdd.CreateHddList hdds);

   @Named("servers:hardware:hdds:get")
   @GET
   @Path("/{serverId}/hardware/hdds/{hddId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Hdd getHdd(@PathParam("serverId") String serverId, @PathParam("hddId") String hddId);

   @Named("server:hardware:hdds:update")
   @PUT
   @Path("/{serverId}/hardware/hdds/{hddId}")
   @MapBinder(BindToJsonPayload.class)
   Server updateHdd(@PathParam("serverId") String serverId, @PathParam("hddId") String hddId, @PayloadParam("size") double size);

   @Named("server:hardware:hdds:delete")
   @DELETE
   @Path("/{serverId}/hardware/hdds/{hddId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server deleteHdd(@PathParam("serverId") String serverId, @PathParam("hddId") String hddId);

   @Named("servers:image:get")
   @GET
   @Path("/{serverId}/image")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Image getImage(@PathParam("serverId") String serverId);

   @Named("server:image:update")
   @PUT
   @Path("/{serverId}/image")
   Server.UpdateServerResponse updateImage(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Server.UpdateImage server);

   @Named("servers:ip:list")
   @GET
   @Path("/{serverId}/ips")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ServerIp> listIps(@PathParam("serverId") String serverId);

   @Named("servers:ip:create")
   @POST
   @Path("/{serverId}/ips")
   @MapBinder(BindToJsonPayload.class)
   Server addIp(@PathParam("serverId") String serverId, @PayloadParam("type") Types.IPType type);

   @Named("servers:ip:get")
   @GET
   @Path("/{serverId}/ips/{ipId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ServerIp getIp(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId);

   @Named("server:ip:delete")
   @DELETE
   @Path("/{serverId}/ips/{ipId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server deleteIp(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId);

   @Named("servers:ip:firewallPolicy:list")
   @GET
   @Path("/{serverId}/ips/{ipId}/firewall_policy")
   @ResponseParser(ServerApi.FirewallPolicyListParser.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ServerFirewallPolicy> listIpFirewallPolicies(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId);

   @Named("servers:ip:firewallPolicy:update")
   @PUT
   @Path("/{serverId}/ips/{ipId}/firewall_policy")
   @MapBinder(BindToJsonPayload.class)
   Server addFirewallPolicy(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId, @PayloadParam("id") String policyId);

   @Named("servers:ip:loadBalancer:list")
   @GET
   @Path("/{serverId}/ips/{ipId}/load_balancers")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ServerLoadBalancer> listIpLoadBalancer(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId);

   @Named("servers:ip:loadBalancer:create")
   @POST
   @Path("/{serverId}/ips/{ipId}/load_balancers")
   @MapBinder(BindToJsonPayload.class)
   Server addIpLoadBalancer(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId, @PayloadParam("load_balancer_id") String loadBalancerId);

   @Named("servers:ip:loadBalancer:delete")
   @DELETE
   @Path("/{serverId}/ips/{ipId}/load_balancers/{loadBalancerId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server deleteIpLoadBalancer(@PathParam("serverId") String serverId, @PathParam("ipId") String ipId, @PathParam("loadBalancerId") String loadBalancerId);

   @Named("servers:dvd:get")
   @GET
   @Path("/{serverId}/dvd")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Dvd getDvd(@PathParam("serverId") String serverId);

   @Named("servers:dvd:delete")
   @DELETE
   @Path("/{serverId}/dvd")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server unloadDvd(@PathParam("serverId") String serverId);

   @Named("servers:dvd:update")
   @PUT
   @Path("/{serverId}/dvd")
   @MapBinder(BindToJsonPayload.class)
   Server loadDvd(@PathParam("serverId") String serverId, @PayloadParam("id") String dvdId);

   @Named("servers:privatenetwork:list")
   @GET
   @Path("/{serverId}/private_networks")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   List<ServerPrivateNetwork> listPrivateNetworks(@PathParam("serverId") String serverId);

   @Named("servers:privatenetwork:create")
   @POST
   @Path("/{serverId}/private_networks")
   @MapBinder(BindToJsonPayload.class)
   Server assignPrivateNetwork(@PathParam("serverId") String serverId, @PayloadParam("id") String privateNetworkId);

   @Named("servers:privatenetwork:get")
   @GET
   @Path("/{serverId}/private_networks/{privateNetworkId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   PrivateNetwork getPrivateNetwork(@PathParam("serverId") String serverId, @PathParam("privateNetworkId") String privateNetworkId);

   @Named("servers:privatenetwork:delete")
   @DELETE
   @Path("/{serverId}/private_networks/{privateNetworkId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server deletePrivateNetwork(@PathParam("serverId") String serverId, @PathParam("privateNetworkId") String privateNetworkId);

   @Named("servers:snapshot:list")
   @GET
   @Path("/{serverId}/snapshots")
   @ResponseParser(ServerApi.SnapshotListParser.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Snapshot> listSnapshots(@PathParam("serverId") String serverId);

   @Named("servers:snapshot:create")
   @POST
   @Path("/{serverId}/snapshots")
   Server createSnapshot(@PathParam("serverId") String serverId);

   @Named("servers:snapshot:update")
   @PUT
   @Path("/{serverId}/snapshots/{snapshotId}")
   @MapBinder(BindToJsonPayload.class)
   Server restoreSnapshot(@PathParam("serverId") String serverId, @PathParam("snapshotId") String snapshotId);

   @Named("servers:snapshot:delete")
   @DELETE
   @Path("/{serverId}/snapshots/{snapshotId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Server deleteSnapshot(@PathParam("serverId") String serverId, @PathParam("snapshotId") String snapshotId);

   @Named("servers:clone:create")
   @POST
   @Path("/{serverId}/clone")
   Server clone(@PathParam("serverId") String serverId, @BinderParam(BindToJsonPayload.class) Server.Clone clone);

   static final class FirewallPolicyListParser extends ParseJson<List<ServerFirewallPolicy>> {

      static final TypeLiteral<List<ServerFirewallPolicy>> list = new TypeLiteral<List<ServerFirewallPolicy>>() {
      };

      @Inject
      FirewallPolicyListParser(Json json) {
         super(json, list);
      }

      @Override
      public <V> V apply(InputStream stream, Type type) throws IOException {
         try {
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {
               @Override
               public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tt) {
                  return new ServerFirewallPolicyAdapter(tt);
               }
            });
            Gson gson = gsonBuilder.create();
            return (V) gson.fromJson(Strings2.toStringAndClose(stream), type);
         } finally {
            if (stream != null) {
               stream.close();
            }
         }
      }
   }

   static final class SnapshotListParser extends ParseJson<List<Snapshot>> {

      static final TypeLiteral<List<Snapshot>> list = new TypeLiteral<List<Snapshot>>() {
      };

      @Inject
      SnapshotListParser(Json json) {
         super(json, list);
      }

      @Override
      public <V> V apply(InputStream stream, Type type) throws IOException {
         try {
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {
               @Override
               public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tt) {
                  return new SnapshotAdapter(tt);
               }
            });
            Gson gson = gsonBuilder.create();
            return (V) gson.fromJson(Strings2.toStringAndClose(stream), type);
         } finally {
            if (stream != null) {
               stream.close();
            }
         }
      }
   }
}
