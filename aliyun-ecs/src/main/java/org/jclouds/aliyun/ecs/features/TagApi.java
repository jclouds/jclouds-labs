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
package org.jclouds.aliyun.ecs.features;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;
import org.jclouds.Constants;
import org.jclouds.Fallbacks;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.Tag;
import org.jclouds.aliyun.ecs.domain.internal.PaginatedCollection;
import org.jclouds.aliyun.ecs.domain.options.ListTagsOptions;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
import org.jclouds.aliyun.ecs.domain.options.TagOptions;
import org.jclouds.aliyun.ecs.filters.FormSign;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.internal.ArgsToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = { "Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod" },
             values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface TagApi {

   @Named("tag:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeTags")
   @ResponseParser(ParseTags.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Tag> list(@QueryParam("RegionId") String region, ListTagsOptions options);

   @Named("tag:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeTags")
   @ResponseParser(ParseTags.class)
   @Transform(ParseTags.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Tag> list(@QueryParam("RegionId") String region);

   @Singleton
   final class ParseTags extends ParseJson<ParseTags.Tags> {

      @Inject
      ParseTags(final Json json) {
         super(json, TypeLiteral.get(Tags.class));
      }

      private static class Tags extends PaginatedCollection<Tag> {

         @ConstructorProperties({ "Tags", "PageNumber", "TotalCount", "PageSize", "RegionId", "RequestId" })
         public Tags(Map<String, Iterable<Tag>> content, Integer pageNumber, Integer totalCount, Integer pageSize, String regionId, String requestId) {
            super(content, pageNumber, totalCount, pageSize, regionId, requestId);
         }
      }

      private static class ToPagedIterable extends ArgsToPagedIterable<Tag, ToPagedIterable> {

         private final ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Tag>> markerToNextForArgs(List<Object> args) {
            if (args == null || args.isEmpty()) throw new IllegalStateException("Can't advance the PagedIterable");
            final String regionId = args.get(0).toString();
            final ListTagsOptions original = (ListTagsOptions) Iterables.tryFind(args, Predicates.instanceOf(ListTagsOptions.class)).orNull();

            return new Function<Object, IterableWithMarker<Tag>>() {
               @Override
               public IterableWithMarker<Tag> apply(Object input) {
                  ListTagsOptions options = original == null ?
                          ListTagsOptions.Builder.paginationOptions(PaginationOptions.class.cast(input)) :
                          original.paginationOptions(PaginationOptions.class.cast(input));
                  return api.tagApi().list(regionId, options);
               }
            };
         }
      }
   }

   @Named("tag:add")
   @POST
   @QueryParams(keys = "Action", values = "AddTags")
   Request add(@QueryParam("RegionId") String region, @QueryParam("ResourceId") String resourceId,
                            @QueryParam("ResourceType") String resourceType,
                            TagOptions tagOptions);

   @Named("tag:remove")
   @POST
   @QueryParams(keys = "Action", values = "RemoveTags")
   Request remove(@QueryParam("RegionId") String region,
                  @QueryParam("ResourceId") String resourceId,
                  @QueryParam("ResourceType") String resourceType);

   @Named("tag:remove")
   @POST
   @QueryParams(keys = "Action", values = "RemoveTags")
   Request remove(@QueryParam("RegionId") String region,
                  @QueryParam("ResourceId") String resourceId,
                  @QueryParam("ResourceType") String resourceType,
                  TagOptions options);
}

