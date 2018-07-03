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
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;
import org.jclouds.Constants;
import org.jclouds.Fallbacks;
import org.jclouds.aliyun.ecs.ECSComputeServiceApi;
import org.jclouds.aliyun.ecs.domain.Image;
import org.jclouds.aliyun.ecs.domain.Images;
import org.jclouds.aliyun.ecs.domain.options.ListImagesOptions;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
import org.jclouds.aliyun.ecs.filters.FormSign;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * https://www.alibabacloud.com/help/doc-detail/25534.htm?spm=a2c63.p38356.b99.330.79eb59abhmnMDE
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = {"Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod"},
             values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface ImageApi {

   @Named("image:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeImages")
   @ResponseParser(ParseImages.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Image> list(@QueryParam("RegionId") String region, ListImagesOptions options);

   @Named("image:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeImages")
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Image> list(@QueryParam("RegionId") String region);

   @Singleton
   final class ParseImages extends ParseJson<Images> {

      @Inject
      ParseImages(final Json json) {
         super(json, TypeLiteral.get(Images.class));
      }

      static class ToPagedIterable extends Arg0ToPagedIterable<Image, ToPagedIterable> {

         private final ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Image>> markerToNextForArg0(final Optional<Object> arg0) {
            return new Function<Object, IterableWithMarker<Image>>() {
               @Override
               public IterableWithMarker<Image> apply(Object input) {
                  String regionId = arg0.get().toString();
                  ListImagesOptions listImagesOptions = ListImagesOptions.Builder.paginationOptions(PaginationOptions.class.cast(input));
                  return api.imageApi().list(regionId, listImagesOptions);
               }
            };
         }
      }
   }

}
