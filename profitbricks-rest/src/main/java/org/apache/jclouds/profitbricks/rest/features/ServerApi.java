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
package org.apache.jclouds.profitbricks.rest.features;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.jclouds.profitbricks.rest.binder.server.AttachCdromRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.server.AttachVolumeRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.server.CreateServerRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.server.UpdateServerRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Image;
import org.apache.jclouds.profitbricks.rest.domain.Server;
import org.apache.jclouds.profitbricks.rest.domain.Volume;
import org.apache.jclouds.profitbricks.rest.domain.options.DepthOptions;
import org.apache.jclouds.profitbricks.rest.util.ParseId;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.util.Strings2;

@Path("/datacenters/{dataCenterId}/servers")
@RequestFilters(BasicAuthentication.class)
public interface ServerApi extends Closeable {
   
   @Named("server:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Server> getList(@PathParam("dataCenterId") String dataCenterId);

   @Named("server:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Server> getList(@PathParam("dataCenterId") String dataCenterId, DepthOptions options);
   
   @Named("server:get")
   @GET   
   @Path("/{serverId}")
   @ResponseParser(ServerApi.ServerParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server getServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);

   @Named("server:get")
   @GET   
   @Path("/{serverId}")
   @ResponseParser(ServerApi.ServerParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server getServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, DepthOptions options);
   
   @Named("server:create")
   @POST
   @MapBinder(CreateServerRequestBinder.class)
   @ResponseParser(ServerApi.ServerParser.class)
   Server createServer(@PayloadParam("server") Server.Request.CreatePayload payload);
   
   @Named("server:update")
   @PATCH
   @Path("/{serverId}")
   @MapBinder(UpdateServerRequestBinder.class)
   @ResponseParser(ServerApi.ServerParser.class)
   @Produces("application/vnd.profitbricks.partial-properties+json")
   Server updateServer(@PayloadParam("server") Server.Request.UpdatePayload payload);
   
   @Named("server:delete")
   @DELETE
   @Path("/{serverId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   @Named("server:volume:list")
   @GET
   @Path("/{serverId}/volumes")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @SelectJson("items")
   List<Volume> listAttachedVolumes(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   @Named("server:volume:attach")
   @POST
   @MapBinder(AttachVolumeRequestBinder.class)
   @ResponseParser(VolumeApi.VolumeParser.class)
   Volume attachVolume(@PayloadParam("volume") Server.Request.AttachVolumePayload payload);
   
   @Named("server:volume:delete")
   @DELETE
   @Path("/{serverId}/volumes/{volumeId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void detachVolume(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("volumeId") String volumeId);
   
   @Named("server:volume:get")
   @GET
   @Path("/{serverId}/volumes/{volumeId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @ResponseParser(VolumeApi.VolumeParser.class)
   Volume getVolume(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("volumeId") String volumeId);
   
   @Named("server:cdrom:list")
   @GET
   @Path("/{serverId}/cdroms")
   @Fallback(EmptyListOnNotFoundOr404.class)
   @SelectJson("items")
   List<Image> listAttachedCdroms(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   @Named("server:cdrom:attach")
   @POST
   @MapBinder(AttachCdromRequestBinder.class)
   @ResponseParser(ImageApi.ImageParser.class)
   Image attachCdrom(@PayloadParam("cdrom") Server.Request.AttachCdromPayload payload);
   
   @Named("server:cdrom:delete")
   @DELETE
   @Path("/{serverId}/cdroms/{cdRomId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void detachCdrom(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("cdRomId") String cdRomId);
   
   @Named("server:cdrom:get")
   @GET
   @Path("/{serverId}/cdroms/{cdRomId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @ResponseParser(ImageApi.ImageParser.class)
   Image getCdrom(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId, @PathParam("cdRomId") String cdRomId);
   
   @Named("server:reboot")
   @POST
   @Path("/{serverId}/reboot")
   void rebootServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   @Named("server:start")
   @POST
   @Path("/{serverId}/start")
   void startServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   @Named("server:stop")
   @POST
   @Path("/{serverId}/stop")
   void stopServer(@PathParam("dataCenterId") String dataCenterId, @PathParam("serverId") String serverId);
   
   static final class ServerParser extends ParseJson<Server> {
      
      final ParseId parseService;
      
      @Inject ServerParser(Json json, ParseId parseId) {
         super(json, TypeLiteral.get(Server.class));
         this.parseService = parseId;
      }

      @Override
      public <V> V apply(InputStream stream, Type type) throws IOException {
         try {
            return (V) json.fromJson(this.parseService.parseId(Strings2.toStringAndClose(stream), "datacenters", "dataCenterId"), type);
         } finally {
            if (stream != null)
               stream.close();
         }
      }
   }
   
}
