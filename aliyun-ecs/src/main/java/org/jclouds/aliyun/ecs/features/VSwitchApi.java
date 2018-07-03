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
import org.jclouds.aliyun.ecs.domain.VSwitch;
import org.jclouds.aliyun.ecs.domain.VSwitchRequest;
import org.jclouds.aliyun.ecs.domain.internal.PaginatedCollection;
import org.jclouds.aliyun.ecs.domain.options.CreateVSwitchOptions;
import org.jclouds.aliyun.ecs.domain.options.ListVSwitchesOptions;
import org.jclouds.aliyun.ecs.domain.options.PaginationOptions;
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

/**
 * https://www.alibabacloud.com/help/doc-detail/35745.htm
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = { "Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod" },
             values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface VSwitchApi {

   @Named("vswitch:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeVSwitches")
   @ResponseParser(ParseVSwitches.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<VSwitch> list(@QueryParam("RegionId") String region, ListVSwitchesOptions options);

   @Named("vswitch:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeVSwitches")
   @ResponseParser(ParseVSwitches.class)
   @Transform(ParseVSwitches.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<VSwitch> list(@QueryParam("RegionId") String region);

   @Singleton
   final class ParseVSwitches extends ParseJson<ParseVSwitches.VSwitches> {

      private static class VSwitches extends PaginatedCollection<VSwitch> {

         @ConstructorProperties({ "VSwitches", "PageNumber", "TotalCount", "PageSize", "RegionId", "RequestId" })
         public VSwitches(Map<String, Iterable<VSwitch>> content, Integer pageNumber, Integer totalCount, Integer pageSize, String regionId, String requestId) {
            super(content, pageNumber, totalCount, pageSize, regionId, requestId);
         }
      }
      @Inject
      ParseVSwitches(final Json json) {
         super(json, TypeLiteral.get(VSwitches.class));
      }

      static class ToPagedIterable extends ArgsToPagedIterable<VSwitch, ParseVSwitches.ToPagedIterable> {

         private final ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<VSwitch>> markerToNextForArgs(List<Object> args) {
            if (args == null || args.isEmpty()) throw new IllegalStateException("Can't advance the PagedIterable");
            final String regionId = args.get(0).toString();
            final ListVSwitchesOptions original = (ListVSwitchesOptions) Iterables.tryFind(args, Predicates.instanceOf(ListVSwitchesOptions.class)).orNull();

            return new Function<Object, IterableWithMarker<VSwitch>>() {
               @Override
               public IterableWithMarker<VSwitch> apply(Object input) {
                  ListVSwitchesOptions options = original == null ?
                          ListVSwitchesOptions.Builder.paginationOptions(PaginationOptions.class.cast(input)) :
                          original.paginationOptions(PaginationOptions.class.cast(input));
                  return api.vSwitchApi().list(regionId, options);
               }
            };
         }
      }
   }

   @Named("vswitch:create")
   @POST
   @QueryParams(keys = "Action", values = "CreateVSwitch")
   VSwitchRequest create(@QueryParam("ZoneId") String zone,
                         @QueryParam("CidrBlock") String cidrBlock,
                         @QueryParam("VpcId") String vpcId);

   @Named("vswitch:create")
   @POST
   @QueryParams(keys = "Action", values = "CreateVSwitch")
   VSwitchRequest create(@QueryParam("ZoneId") String zone,
                         @QueryParam("CidrBlock") String cidrBlock,
                         @QueryParam("VpcId") String vpcId,
                         CreateVSwitchOptions vSwitchOptions);

   @Named("vswitch:delete")
   @POST
   @QueryParams(keys = "Action", values = "DeleteVSwitch")
   Request delete(@QueryParam("RegionId") String region,
                  @QueryParam("VSwitchId") String vSwitchId);
}

