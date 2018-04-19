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
import org.jclouds.dimensiondata.cloudcontrol.domain.CustomerImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.CustomerImages;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImage;
import org.jclouds.dimensiondata.cloudcontrol.domain.OsImages;
import org.jclouds.dimensiondata.cloudcontrol.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontrol.filters.OrganisationIdFilter;
import org.jclouds.dimensiondata.cloudcontrol.options.DatacenterIdListFilters;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@RequestFilters({ BasicAuthentication.class, OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/{jclouds.api-version}/image")
public interface ServerImageApi {

   @Named("image:listOsImages")
   @GET
   @Path("/osImage")
   @ResponseParser(ParseOsImages.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<OsImage> listOsImages(DatacenterIdListFilters datacenterIdListFilters);

   @Named("image:listOsImages")
   @GET
   @Path("/osImage")
   @Transform(ParseOsImages.ToPagedIterable.class)
   @ResponseParser(ParseOsImages.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<OsImage> listOsImages();

   @Named("image:listCustomerImages")
   @GET
   @Path("/customerImage")
   @ResponseParser(ParseCustomerImages.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   PaginatedCollection<CustomerImage> listCustomerImages(DatacenterIdListFilters datacenterIdListFilters);

   @Named("image:listCustomerImages")
   @GET
   @Path("/customerImage")
   @Transform(ParseCustomerImages.ToPagedIterable.class)
   @ResponseParser(ParseCustomerImages.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<CustomerImage> listCustomerImages();

   @Named("image:getOsImage")
   @GET
   @Path("/osImage/{id}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   OsImage getOsImage(@PathParam("id") String id);

   @Named("image:getCustomerImage")
   @GET
   @Path("/customerImage/{id}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   CustomerImage getCustomerImage(@PathParam("id") String id);

   final class ParseOsImages extends ParseJson<OsImages> {

      @Inject
      ParseOsImages(Json json) {
         super(json, TypeLiteral.get(OsImages.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<OsImage, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(final DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<OsImage>> markerToNextForArg0(final Optional<Object> arg0) {
            return new Function<Object, IterableWithMarker<OsImage>>() {
               @Override
               public IterableWithMarker<OsImage> apply(Object input) {
                  DatacenterIdListFilters datacenterIdListFilters = arg0.isPresent() ?
                        ((DatacenterIdListFilters) arg0.get()).paginationOptions(PaginationOptions.class.cast(input)) :
                        DatacenterIdListFilters.Builder.paginationOptions(PaginationOptions.class.cast(input));
                  return api.getServerImageApi().listOsImages(datacenterIdListFilters);
               }
            };
         }
      }
   }

   final class ParseCustomerImages extends ParseJson<CustomerImages> {

      @Inject
      ParseCustomerImages(Json json) {
         super(json, TypeLiteral.get(CustomerImages.class));
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<CustomerImage, ToPagedIterable> {

         private DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(final DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<CustomerImage>> markerToNextForArg0(
               final Optional<Object> arg0) {
            return new Function<Object, IterableWithMarker<CustomerImage>>() {
               @Override
               public IterableWithMarker<CustomerImage> apply(Object input) {
                  DatacenterIdListFilters datacenterIdListFilters = arg0.isPresent() ?
                        ((DatacenterIdListFilters) arg0.get()).paginationOptions(PaginationOptions.class.cast(input)) :
                        DatacenterIdListFilters.Builder.paginationOptions(PaginationOptions.class.cast(input));
                  return api.getServerImageApi().listCustomerImages(datacenterIdListFilters);
               }
            };
         }
      }
   }

}


