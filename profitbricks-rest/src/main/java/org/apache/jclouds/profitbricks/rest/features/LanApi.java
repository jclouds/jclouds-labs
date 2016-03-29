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
import org.apache.jclouds.profitbricks.rest.binder.lan.CreateLanRequestBinder;
import org.apache.jclouds.profitbricks.rest.binder.lan.UpdateLanRequestBinder;
import org.apache.jclouds.profitbricks.rest.domain.Lan;
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

@Path("/datacenters/{dataCenterId}/lans")
@RequestFilters(BasicAuthentication.class)
public interface LanApi extends Closeable {
   
   @Named("lan:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Lan> list(@PathParam("dataCenterId") String dataCenterId);

   @Named("lan:list")
   @GET
   @SelectJson("items")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Lan> list(@PathParam("dataCenterId") String dataCenterId, DepthOptions options);
   
   @Named("lan:get")
   @GET   
   @Path("/{lanId}")
   @ResponseParser(LanApi.LanParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Lan get(@PathParam("dataCenterId") String dataCenterId, @PathParam("lanId") String lanId);

   @Named("lan:get")
   @GET   
   @Path("/{lanId}")
   @ResponseParser(LanApi.LanParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Lan get(@PathParam("dataCenterId") String dataCenterId, @PathParam("lanId") String lanId, DepthOptions options);
   
   @Named("lan:create")
   @POST
   @MapBinder(CreateLanRequestBinder.class)
   @ResponseParser(LanApi.LanParser.class)
   Lan create(@PayloadParam("lan") Lan.Request.CreatePayload payload);
   
   @Named("lan:update")
   @PATCH
   @Path("/{lanId}")
   @MapBinder(UpdateLanRequestBinder.class)
   @ResponseParser(LanApi.LanParser.class)
   @Produces("application/vnd.profitbricks.partial-properties+json")
   Lan update(@PayloadParam("lan") Lan.Request.UpdatePayload payload);
   
   @Named("lan:delete")
   @DELETE
   @Path("/{lanId}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void delete(@PathParam("dataCenterId") String dataCenterId, @PathParam("lanId") String lanId);
   
   static final class LanParser extends ParseJson<Lan> {
            
      final ParseId parseService;
      
      @Inject LanParser(Json json, ParseId parseId) {
         super(json, TypeLiteral.get(Lan.class));
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
