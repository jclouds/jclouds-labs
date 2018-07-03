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
import org.jclouds.aliyun.ecs.domain.AllocatePublicIpAddressRequest;
import org.jclouds.aliyun.ecs.domain.AvailableZone;
import org.jclouds.aliyun.ecs.domain.Instance;
import org.jclouds.aliyun.ecs.domain.InstanceRequest;
import org.jclouds.aliyun.ecs.domain.InstanceStatus;
import org.jclouds.aliyun.ecs.domain.InstanceType;
import org.jclouds.aliyun.ecs.domain.Request;
import org.jclouds.aliyun.ecs.domain.internal.PaginatedCollection;
import org.jclouds.aliyun.ecs.domain.options.CreateInstanceOptions;
import org.jclouds.aliyun.ecs.domain.options.ListInstanceStatusOptions;
import org.jclouds.aliyun.ecs.domain.options.ListInstancesOptions;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

/**
 * https://www.alibabacloud.com/help/doc-detail/25500.htm?spm=a2c63.p38356.b99.287.129a44a8RBMBLH
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = { "Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod" },
             values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface InstanceApi {

   @Named("instance:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeInstances")
   @ResponseParser(ParseInstances.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Instance> list(@QueryParam("RegionId") String region, ListInstancesOptions options);

   @Named("instance:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeInstances")
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Instance> list(@QueryParam("RegionId") String region);

   @Named("instanceType:list")
   @GET
   @QueryParams(keys = "Action", values = "DescribeInstanceTypes")
   @SelectJson("InstanceType")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<InstanceType> listTypes();

   @Named("instanceType:list")
   @GET
   @QueryParams(keys = { "Action", "DestinationResource", "IoOptimized" },
                values = { "DescribeAvailableResource", "InstanceType", "optimized" })
   @SelectJson("AvailableZone")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<AvailableZone> listInstanceTypesByAvailableZone(@QueryParam("RegionId") String regionId);

   @Named("instance:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "CreateInstance")
   InstanceRequest create(@QueryParam("RegionId") String regionId,
                          @QueryParam("ImageId") String imageId,
                          @QueryParam("SecurityGroupId") String securityGroupId,
                          @QueryParam("HostName") String hostname,
                          @QueryParam("InstanceType") String instanceType);

   @Named("instance:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "CreateInstance")
   InstanceRequest create(@QueryParam("RegionId") String regionId,
                          @QueryParam("ImageId") String imageId,
                          @QueryParam("SecurityGroupId") String securityGroupId,
                          @QueryParam("HostName") String hostname,
                          @QueryParam("InstanceType") String instanceType,
                          CreateInstanceOptions options);

   @Named("instance:allocatePublicIpAddress")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "AllocatePublicIpAddress")
   AllocatePublicIpAddressRequest allocatePublicIpAddress(@QueryParam("RegionId") String regionId,
                                                          @QueryParam("InstanceId") String instanceId);

   @Named("instance:listInstanceStatus")
   @GET
   @QueryParams(keys = "Action", values = "DescribeInstanceStatus")
   @ResponseParser(ParseInstanceStatus.class)
   @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<InstanceStatus> listInstanceStatus(@QueryParam("RegionId") String region, ListInstanceStatusOptions options);

   @Named("instance:listInstanceStatus")
   @GET
   @QueryParams(keys = "Action", values = "DescribeInstanceStatus")
   @ResponseParser(ParseInstanceStatus.class)
   @Transform(ParseInstanceStatus.ToPagedIterable.class)
   @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<InstanceStatus> listInstanceStatus(@QueryParam("RegionId") String region);

   /**
    * You can only release an instance that is in the Stopped (Stopped) status.
    *
    * @param instanceId
    */
   @Named("instance:delete")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "DeleteInstance")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Request delete(@QueryParam("InstanceId") String instanceId);

   @Named("instance:powerOff")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "StopInstance")
   Request powerOff(@QueryParam("InstanceId") String instanceId);

   @Named("instance:powerOn")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "StartInstance")
   Request powerOn(@QueryParam("InstanceId") String instanceId);

   @Named("instance:reboot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "Action", values = "RebootInstance")
   Request reboot(@QueryParam("InstanceId") String instanceId);

   @Singleton
   final class ParseInstances extends ParseJson<ParseInstances.Instances> {

      @Inject
      ParseInstances(final Json json) {
         super(json, TypeLiteral.get(Instances.class));
      }

      private static class Instances extends PaginatedCollection<Instance> {

         @ConstructorProperties({ "Instances", "PageNumber", "TotalCount", "PageSize", "RegionId", "RequestId" })
         public Instances(Map<String, Iterable<Instance>> content, Integer pageNumber, Integer totalCount, Integer pageSize, String regionId, String requestId) {
            super(content, pageNumber, totalCount, pageSize, regionId, requestId);
         }
      }

      private static class ToPagedIterable extends ArgsToPagedIterable<Instance, ToPagedIterable> {

         private ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(final ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<Instance>> markerToNextForArgs(List<Object> args) {
            if (args == null || args.isEmpty()) throw new IllegalStateException("Can't advance the PagedIterable");
            final String regionId = args.get(0).toString();
            final ListInstancesOptions original = (ListInstancesOptions) Iterables.tryFind(args, Predicates.instanceOf(ListInstancesOptions.class)).orNull();

            return new Function<Object, IterableWithMarker<Instance>>() {
               @Override
               public IterableWithMarker<Instance> apply(Object input) {
                  ListInstancesOptions options = original == null ?
                          ListInstancesOptions.Builder.paginationOptions(PaginationOptions.class.cast(input)) :
                          original.paginationOptions(PaginationOptions.class.cast(input));
                  return api.instanceApi().list(regionId, options);
               }
            };
         }
      }
   }

   @Singleton
   final class ParseInstanceStatus extends ParseJson<ParseInstanceStatus.InstanceStatuses> {

      @Inject
      ParseInstanceStatus(final Json json) {
         super(json, TypeLiteral.get(InstanceStatuses.class));
      }

      private static class InstanceStatuses extends PaginatedCollection<InstanceStatus> {

         @ConstructorProperties({ "InstanceStatuses", "PageNumber", "TotalCount", "PageSize", "RegionId", "RequestId" })
         public InstanceStatuses(Map<String, Iterable<InstanceStatus>> content, Integer pageNumber, Integer totalCount, Integer pageSize, String regionId, String requestId) {
            super(content, pageNumber, totalCount, pageSize, regionId, requestId);
         }
      }

      private static class ToPagedIterable extends ArgsToPagedIterable<InstanceStatus, ToPagedIterable> {

         private ECSComputeServiceApi api;

         @Inject
         ToPagedIterable(final ECSComputeServiceApi api) {
            this.api = api;
         }

         @Override
         protected Function<Object, IterableWithMarker<InstanceStatus>> markerToNextForArgs(List<Object> args) {
            if (args == null || args.isEmpty()) throw new IllegalStateException("Can't advance the PagedIterable");
            final String regionId = args.get(0).toString();
            final ListInstanceStatusOptions original = (ListInstanceStatusOptions) Iterables.tryFind(args, Predicates.instanceOf(ListInstanceStatusOptions.class)).orNull();

            return new Function<Object, IterableWithMarker<InstanceStatus>>() {
               @Override
               public IterableWithMarker<InstanceStatus> apply(Object input) {
                  ListInstanceStatusOptions options = original == null ?
                          ListInstanceStatusOptions.Builder.paginationOptions(PaginationOptions.class.cast(input)) :
                          original.paginationOptions(PaginationOptions.class.cast(input));
                  return api.instanceApi().listInstanceStatus(regionId, options);
               }
            };
         }
      }
   }

}
