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
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenter;
import org.jclouds.dimensiondata.cloudcontrol.domain.Datacenters;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystem;
import org.jclouds.dimensiondata.cloudcontrol.domain.OperatingSystems;
import org.jclouds.dimensiondata.cloudcontrol.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontrol.filters.DatacenterIdListDatacentersFilter;
import org.jclouds.dimensiondata.cloudcontrol.filters.OrganisationIdFilter;
import org.jclouds.dimensiondata.cloudcontrol.options.PaginationOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@RequestFilters({ BasicAuthentication.class, OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/{jclouds.api-version}/infrastructure")
public interface InfrastructureApi {

   @Named("infrastructure:datacenter")
   @GET
   @Path("/datacenter")
   @ResponseParser(ParseDatacenters.class)
   @RequestFilters(DatacenterIdListDatacentersFilter.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<Datacenter> listDatacenters(PaginationOptions options);

   @Named("infrastructure:datacenter")
   @GET
   @Path("/datacenter")
   @Transform(ParseDatacenters.ToPagedIterable.class)
   @ResponseParser(ParseDatacenters.class)
   @RequestFilters(DatacenterIdListDatacentersFilter.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Datacenter> listDatacenters();

   @Named("infrastructure:operatingSystem")
   @GET
   @Path("/operatingSystem")
   @ResponseParser(ParseOperatingSystems.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<OperatingSystem> listOperatingSystems(@QueryParam("datacenterId") Set<String> datacenterId,
         PaginationOptions options);

   @Named("infrastructure:operatingSystem")
   @GET
   @Path("/operatingSystem")
   @Transform(ParseOperatingSystems.ToPagedIterable.class)
   @ResponseParser(ParseOperatingSystems.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<OperatingSystem> listOperatingSystems(@QueryParam("datacenterId") Set<String> datacenterId);

   final class ParseDatacenters extends ParseJson<Datacenters> {

      @Inject
      ParseDatacenters(Json json) {
         super(json, TypeLiteral.get(Datacenters.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<Datacenter, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Datacenter>> markerToNextForArg0(Optional<Object> arg) {
            return new Function<Object, IterableWithMarker<Datacenter>>() {
               @Override
               public IterableWithMarker<Datacenter> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getInfrastructureApi().listDatacenters(paginationOptions);
               }
            };
         }
      }
   }

   final class ParseOperatingSystems extends ParseJson<OperatingSystems> {

      @Inject
      ParseOperatingSystems(Json json) {
         super(json, TypeLiteral.get(OperatingSystems.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<OperatingSystem, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<OperatingSystem>> markerToNextForArg0(
               final Optional<Object> arg) {
            return new Function<Object, IterableWithMarker<OperatingSystem>>() {
               @Override
               public IterableWithMarker<OperatingSystem> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getInfrastructureApi().listOperatingSystems((Set<String>) arg.get(), paginationOptions);
               }
            };
         }
      }
   }

}
