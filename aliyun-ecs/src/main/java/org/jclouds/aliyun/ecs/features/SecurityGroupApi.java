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
import org.jclouds.aliyun.ecs.domain.IpProtocol;
import org.jclouds.aliyun.ecs.domain.Permission;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.SecurityGroup;
import org.jclouds.aliyun.ecs.domain.SecurityGroupRequest;
import org.jclouds.aliyun.ecs.domain.internal.PaginatedCollection;
import org.jclouds.aliyun.ecs.domain.options.CreateSecurityGroupOptions;
import org.jclouds.aliyun.ecs.domain.options.ListSecurityGroupsOptions;
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
import org.jclouds.rest.annotations.SelectJson;
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
 * https://www.alibabacloud.com/help/doc-detail/25553.htm?spm=a2c63.p38356.b99.323.1a3b59abPkInRB
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = {"Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod"},
        values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface SecurityGroupApi {

   @Named("securityGroup:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeSecurityGroups")
   @ResponseParser(ParseSecurityGroups.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<SecurityGroup> list(@QueryParam("RegionId") String region, ListSecurityGroupsOptions options);

   @Named("securityGroup:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeSecurityGroups")
   @ResponseParser(ParseSecurityGroups.class)
   @Transform(ParseSecurityGroups.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<SecurityGroup> list(@QueryParam("RegionId") String region);

   @Singleton
   final class ParseSecurityGroups extends ParseJson<ParseSecurityGroups.SecurityGroups> {

      @Inject
      ParseSecurityGroups(final Json json) {
         super(json, TypeLiteral.get(SecurityGroups.class));
      }

      private static class SecurityGroups extends PaginatedCollection<SecurityGroup> {

         @ConstructorProperties({"SecurityGroups", "PageNumber", "TotalCount", "PageSize", "RegionId", "RequestId"})
         public SecurityGroups(Map<String, Iterable<SecurityGroup>> content, Integer pageNumber, Integer totalCount, Integer pageSize, String regionId, String requestId) {
            super(content, pageNumber, totalCount, pageSize, regionId, requestId);
         }
      }

      private static class ToPagedIterable extends ArgsToPagedIterable<SecurityGroup, ToPagedIterable> {

         private final ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<SecurityGroup>> markerToNextForArgs(List<Object> args) {
            if (args == null || args.isEmpty()) throw new IllegalStateException("Can't advance the PagedIterable");
            final String regionId = args.get(0).toString();
            final ListSecurityGroupsOptions original = (ListSecurityGroupsOptions) Iterables.tryFind(args, Predicates.instanceOf(ListSecurityGroupsOptions.class)).orNull();

            return new Function<Object, IterableWithMarker<SecurityGroup>>() {
               @Override
               public IterableWithMarker<SecurityGroup> apply(Object input) {
                  ListSecurityGroupsOptions options = original == null ?
                          ListSecurityGroupsOptions.Builder.paginationOptions(PaginationOptions.class.cast(input)) :
                          original.paginationOptions(PaginationOptions.class.cast(input));
                  return api.securityGroupApi().list(regionId, options);
               }
            };
         }
      }
   }

   @Named("securityGroup:get")
   @GET
   @QueryParams(keys = "Action", values = "DescribeSecurityGroupAttribute")
   @SelectJson("Permission")
   List<Permission> get(@QueryParam("RegionId") String region, @QueryParam("SecurityGroupId") String securityGroupId);

   @Named("securityGroup:create")
   @POST
   @QueryParams(keys = "Action", values = "CreateSecurityGroup")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   SecurityGroupRequest create(@QueryParam("RegionId") String region);

   @Named("securityGroup:create")
   @POST
   @QueryParams(keys = "Action", values = "CreateSecurityGroup")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   SecurityGroupRequest create(@QueryParam("RegionId") String region, CreateSecurityGroupOptions options);

   @Named("securityGroup:addInbound")
   @POST
   @QueryParams(keys = "Action", values = "AuthorizeSecurityGroup")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   Request addInboundRule(@QueryParam("RegionId") String region, @QueryParam("SecurityGroupId") String securityGroupId,
                          @QueryParam("IpProtocol") IpProtocol ipProtocol, @QueryParam("PortRange") String portRange,
                          @QueryParam("SourceCidrIp") String sourceCidrIp);

   @Named("securityGroup:delete")
   @POST
   @QueryParams(keys = "Action", values = "DeleteSecurityGroup")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   Request delete(@QueryParam("RegionId") String region, @QueryParam("SecurityGroupId") String securityGroupId);
}

