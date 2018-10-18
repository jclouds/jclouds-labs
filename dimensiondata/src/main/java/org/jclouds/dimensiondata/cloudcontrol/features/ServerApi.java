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
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.dimensiondata.cloudcontrol.DimensionDataCloudControlApi;
import org.jclouds.dimensiondata.cloudcontrol.domain.Disk;
import org.jclouds.dimensiondata.cloudcontrol.domain.NetworkInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.dimensiondata.cloudcontrol.domain.Servers;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CloneServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.domain.options.CreateServerOptions;
import org.jclouds.dimensiondata.cloudcontrol.filters.OrganisationIdFilter;
import org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters;
import org.jclouds.dimensiondata.cloudcontrol.options.PaginationOptions;
import org.jclouds.dimensiondata.cloudcontrol.utils.ParseResponse;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
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
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequestFilters({ BasicAuthentication.class, OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/{jclouds.api-version}/server")
public interface ServerApi {

   @Named("server:list")
   @GET
   @Path("/server")
   @ResponseParser(ParseServers.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<Server> listServers(DatacenterIdListFilters datacenterIdListFilters);

   @Named("server:list")
   @GET
   @Path("/server")
   @Transform(ParseServers.ToPagedIterable.class)
   @ResponseParser(ParseServers.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Server> listServers();

   @Named("server:get")
   @GET
   @Path("/server/{id}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server getServer(@PathParam("id") String id);

   @Named("server:deploy")
   @POST
   @Path("/deployServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ServerId.class)
   String deployServer(@PayloadParam("name") String name, @PayloadParam("imageId") String imageId,
         @PayloadParam("start") Boolean start, @PayloadParam("networkInfo") NetworkInfo networkInfo,
         @PayloadParam("administratorPassword") String administratorPassword);

   @Named("server:deploy")
   @POST
   @Path("/deployServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateServerOptions.class)
   @ResponseParser(ServerId.class)
   String deployServer(@PayloadParam("name") String name, @PayloadParam("imageId") String imageId,
         @PayloadParam("start") Boolean start, @PayloadParam("networkInfo") NetworkInfo networkInfo,
         @Nullable @PayloadParam("administratorPassword") String administratorPassword,
         @Nullable @PayloadParam("disk") List<Disk> disks, @Nullable CreateServerOptions options);

   @Named("server:delete")
   @POST
   @Path("/deleteServer")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   void deleteServer(@PayloadParam("id") String id);

   @Named("server:powerOff")
   @POST
   @Path("/powerOffServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void powerOffServer(@PayloadParam("id") String id);

   @Named("server:reboot")
   @POST
   @Path("/rebootServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void rebootServer(@PayloadParam("id") String id);

   @Named("server:reconfigure")
   @POST
   @Path("/reconfigureServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void reconfigureServer(@PayloadParam("id") String id, @PayloadParam("cpuCount") int cpuCount,
         @PayloadParam("cpuSpeed") String cpuSpeed, @PayloadParam("coresPerSocket") int coresPerSocket);

   @Named("server:clone")
   @POST
   @Path("/cloneServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CloneServerOptions.class)
   @ResponseParser(ImageId.class)
   String cloneServer(@PayloadParam("id") String id, @PayloadParam("imageName") String imageName,
         CloneServerOptions cloneServerOptions);

   @Named("server:start")
   @POST
   @Path("/startServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void startServer(@PayloadParam("id") String id);

   /**
    * Operation for cleaning servers with FAILED_ADD state
    *
    * @see org.jclouds.dimensiondata.cloudcontrol.domain.State.FAILED_ADD
    */
   @Named("server:cleanServer")
   @POST
   @Path("/cleanServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void cleanServer(@PayloadParam("id") String id);

   @Named("server:shutdown")
   @POST
   @Path("/shutdownServer")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void shutdownServer(@PayloadParam("id") String id);

   @Singleton
   final class ParseServers extends ParseJson<Servers> {

      @Inject
      ParseServers(final Json json) {
         super(json, TypeLiteral.get(Servers.class));
      }

      static class ToPagedIterable extends Arg0ToPagedIterable<Server, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(final DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Server>> markerToNextForArg0(final Optional<Object> arg0) {
            return new Function<Object, IterableWithMarker<Server>>() {
               @Override
               public IterableWithMarker<Server> apply(Object input) {
                  DatacenterIdListFilters datacenterIdListFilters = arg0.isPresent() ?
                        ((DatacenterIdListFilters) arg0.get()).paginationOptions(PaginationOptions.class.cast(input)) :
                        DatacenterIdListFilters.Builder.paginationOptions(PaginationOptions.class.cast(input));
                  return api.getServerApi().listServers(datacenterIdListFilters);
               }
            };
         }
      }

   }

   @Singleton
   final class ServerId extends ParseResponse {

      @Inject
      ServerId(final Json json) {
         super(json, "serverId");
      }
   }

   @Singleton
   final class ImageId extends ParseResponse {

      @Inject
      ImageId(final Json json) {
         super(json, "imageId");
      }
   }
}
