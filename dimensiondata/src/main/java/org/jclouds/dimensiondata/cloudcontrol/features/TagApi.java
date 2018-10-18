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
import org.jclouds.dimensiondata.cloudcontrol.domain.PaginatedCollection;
import org.jclouds.dimensiondata.cloudcontrol.domain.Tag;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagInfo;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagKey;
import org.jclouds.dimensiondata.cloudcontrol.domain.TagKeys;
import org.jclouds.dimensiondata.cloudcontrol.domain.Tags;
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
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequestFilters({ BasicAuthentication.class, OrganisationIdFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
@Path("/caas/{jclouds.api-version}/tag")
public interface TagApi {

   @Named("tag:createTagKey")
   @POST
   @Path("/createTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(TagKeyId.class)
   String createTagKey(@PayloadParam("name") String name, @PayloadParam("description") String description,
         @PayloadParam("valueRequired") boolean valueRequired,
         @PayloadParam("displayOnReport") boolean displayOnReport);

   @Named("tag:applyTags")
   @POST
   @Path("/applyTags")
   @MapBinder(BindToJsonPayload.class)
   void applyTags(@PayloadParam("assetId") String assetId, @PayloadParam("assetType") String assetType,
         @PayloadParam("tagById") List<TagInfo> tagById);

   @Named("tag:removeTags")
   @POST
   @Path("/removeTags")
   @MapBinder(BindToJsonPayload.class)
   void removeTags(@PayloadParam("assetId") String assetId, @PayloadParam("assetType") String assetType,
         @PayloadParam("tagKeyId") List<String> tagKeyName);

   @Named("tag:tagKey")
   @GET
   @Path("/tagKey")
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   @ResponseParser(ParseTagKeys.class)
   PaginatedCollection<TagKey> listTagKeys(PaginationOptions options);

   @Named("tag:tagKey")
   @GET
   @Path("/tagKey")
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   @ResponseParser(ParseTagKeys.class)
   @Transform(ParseTagKeys.ToPagedIterable.class)
   PagedIterable<TagKey> listTagKeys();

   @Named("tag:tagKeyById")
   @GET
   @Path("/tagKey/{tagKeyId}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   TagKey tagKeyById(@PathParam("tagKeyId") String tagKeyId);

   @Named("tag:editTagKey")
   @POST
   @Path("/editTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   void editTagKey(@PayloadParam("name") String name, @PayloadParam("id") String id,
         @PayloadParam("description") String description, @PayloadParam("valueRequired") Boolean valueRequired,
         @PayloadParam("displayOnReport") Boolean displayOnReport);

   @Named("tag:deleteTagKey")
   @POST
   @Path("/deleteTagKey")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteTagKey(@PayloadParam("id") String id);

   @Named("tag:tags")
   @GET
   @Path("/tag")
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   @ResponseParser(ParseTags.class)
   PaginatedCollection<Tag> listTags(PaginationOptions options);

   @Named("tag:tags")
   @GET
   @Path("/tag")
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   @ResponseParser(ParseTags.class)
   @Transform(ParseTags.ToPagedIterable.class)
   PagedIterable<Tag> listTags();

   @Singleton
   final class ParseTagKeys extends ParseJson<TagKeys> {

      @Inject
      ParseTagKeys(final Json json, final TypeLiteral<TagKeys> type) {
         super(json, type);
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<TagKey, ToPagedIterable> {

         private final DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(final DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<TagKey>> markerToNextForArg0(Optional<Object> arg0) {
            return new Function<Object, IterableWithMarker<TagKey>>() {
               @Override
               public IterableWithMarker<TagKey> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getTagApi().listTagKeys(paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseTags extends ParseJson<Tags> {

      @Inject
      ParseTags(final Json json, final TypeLiteral<Tags> type) {
         super(json, type);
      }

      private static class ToPagedIterable extends Arg0ToPagedIterable<Tag, ToPagedIterable> {

         private final DimensionDataCloudControlApi api;

         @Inject
         ToPagedIterable(final DimensionDataCloudControlApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Tag>> markerToNextForArg0(Optional<Object> optional) {
            return new Function<Object, IterableWithMarker<Tag>>() {
               @Override
               public IterableWithMarker<Tag> apply(Object input) {
                  PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
                  return api.getTagApi().listTags(paginationOptions);
               }
            };
         }
      }
   }

   @Singleton
   final class TagKeyId extends ParseResponse {

      @Inject
      TagKeyId(final Json json) {
         super(json, "tagKeyId");
      }
   }

}
