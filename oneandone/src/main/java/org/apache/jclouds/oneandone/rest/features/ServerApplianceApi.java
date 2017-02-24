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

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.apache.jclouds.oneandone.rest.domain.ServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.SingleServerAppliance;
import org.apache.jclouds.oneandone.rest.domain.options.GenericQueryOptions;
import org.apache.jclouds.oneandone.rest.filters.AuthenticateRequest;
import org.apache.jclouds.oneandone.rest.util.ServerApplianceParser;
import org.jclouds.Fallbacks;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.util.Strings2;

@Path("/server_appliances")
@Consumes("application/json")
@RequestFilters(AuthenticateRequest.class)
public interface ServerApplianceApi extends Closeable {

   @Named("serverappliance:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ServerAppliance> list();

   @Named("serverappliance:list")
   @GET
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ServerAppliance> list(GenericQueryOptions options);

   @Named("serverappliance:get")
   @GET
   @Path("/{serverApplianceId}")
   @ResponseParser(ServerApplianceApi.SingleServerApplianceParser.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   SingleServerAppliance get(@PathParam("serverApplianceId") String serverApplianceId);

   static final class SingleServerApplianceParser extends ParseJson<SingleServerAppliance> {

      static final TypeLiteral<SingleServerAppliance> single = new TypeLiteral<SingleServerAppliance>() {
      };
      final ServerApplianceParser parseService;

      @Inject
      SingleServerApplianceParser(Json json, ServerApplianceParser parseId) {
         super(json, single);
         this.parseService = parseId;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <V> V apply(InputStream stream, Type type) throws IOException {
         try {
            return (V) json.fromJson(this.parseService.parse(Strings2.toStringAndClose(stream), "datacenters", "dataCenterId"), type);
         } finally {
            if (stream != null) {
               stream.close();
            }
         }
      }
   }

}
