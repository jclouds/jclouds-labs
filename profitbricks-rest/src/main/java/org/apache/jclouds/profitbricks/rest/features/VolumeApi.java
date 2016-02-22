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
import org.apache.jclouds.profitbricks.rest.binder.volume.CreateSnapshotRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.volume.CreateVolumeRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.volume.RestoreSnapshotRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.volume.UpdateVolumeRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Snapshot;
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

@Path("/datacenters/{dataCenterId}/volumes")
@RequestFilters(BasicAuthentication.class)
public interface VolumeApi extends Closeable {
   
   @Named("volume:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Volume> getList(@PathParam("dataCenterId") String dataCenterId);

   @Named("volume:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Volume> getList(@PathParam("dataCenterId") String dataCenterId, DepthOptions options);
   
   @Named("volume:get")
   @GET   
   @Path("/{volumeId}")
   @ResponseParser(VolumeApi.VolumeParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Volume getVolume(@PathParam("dataCenterId") String dataCenterId, @PathParam("volumeId") String volumeId);

   @Named("volume:get")
   @GET   
   @Path("/{volumeId}")
   @ResponseParser(VolumeApi.VolumeParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Volume getVolume(@PathParam("dataCenterId") String dataCenterId, @PathParam("volumeId") String volumeId, DepthOptions options);
   
   @Named("volume:create")
   @POST
   @MapBinder(CreateVolumeRequestBinder.class)
   @ResponseParser(VolumeApi.VolumeParser.class)
   Volume createVolume(@PayloadParam("volume") Volume.Request.CreatePayload payload);
   
   @Named("volume:update")
   @PATCH
   @Path("/{volumeId}")
   @MapBinder(UpdateVolumeRequestBinder.class)
   @ResponseParser(VolumeApi.VolumeParser.class)
   @Produces("application/vnd.profitbricks.partial-properties+json")
   Volume updateVolume(@PayloadParam("volume") Volume.Request.UpdatePayload payload);
   
   @Named("volume:delete")
   @DELETE
   @Path("/{volumeId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteVolume(@PathParam("dataCenterId") String dataCenterId, @PathParam("volumeId") String volumeId);
   
   @Named("volume:snapshot:create")
   @POST
   @MapBinder(CreateSnapshotRequestBinder.class)
   @ResponseParser(SnapshotApi.SnapshotParser.class)
   Snapshot createSnapshot(@PayloadParam("snapshot") Volume.Request.CreateSnapshotPayload payload);
   
   @Named("volume:snapshot:restore")
   @POST
   @MapBinder(RestoreSnapshotRequestBinder.class)
   void restoreSnapshot(@PayloadParam("snapshot") Volume.Request.RestoreSnapshotPayload payload);   
   
   static final class VolumeParser extends ParseJson<Volume> {
      
      final ParseId parseService;
      
      @Inject VolumeParser(Json json, ParseId parseId) {
         super(json, TypeLiteral.get(Volume.class));
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
