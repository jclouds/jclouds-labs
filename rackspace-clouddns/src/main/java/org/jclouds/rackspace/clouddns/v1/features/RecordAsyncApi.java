/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.features;

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.binders.BindRecordsToJsonPayload;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.functions.ParseOnlyRecord;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecord;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecords;
import org.jclouds.rackspace.clouddns.v1.functions.RecordsToPagedIterable;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see RecordAsyncApi
 * @author Everett Toews
 */
@Endpoint(CloudDNS.class)
@RequestFilters(AuthenticateRequest.class)
public interface RecordAsyncApi {

   /**
    * @see RecordApi#create(int, Iterable)
    */
   @Named("record:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/records")
   ListenableFuture<Job<Set<RecordDetail>>> create(@WrapWith("records") Iterable<Record> createRecords);

   /**
    * @see RecordApi#list(int)
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<RecordDetail>> list();

   /**
    * @see RecordApi#listByType(String)
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<RecordDetail>> listByType(
         @QueryParam("type") String typeFilter);

   /**
    * @see RecordApi#listByTypeAndData(String, String)
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<RecordDetail>> listByTypeAndData(
         @QueryParam("type") String typeFilter,
         @QueryParam("data") String dataFilter);

   /**
    * @see RecordApi#listByNameAndType(String, String)
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<RecordDetail>> listByNameAndType(
         @QueryParam("name") String nameFilter,
         @QueryParam("type") String typeFilter);

   /**
    * @see RecordApi#list(int, PaginationOptions)
    */
   @Named("record:list")
   @GET
   @ResponseParser(ParseRecords.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/records")
   ListenableFuture<PaginatedCollection<RecordDetail>> list(PaginationOptions options);

   /**
    * @see RecordApi#getByNameAndTypeAndData(String, String, String)
    */
   @Named("record:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseOnlyRecord.class)
   @Path("/records")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<RecordDetail> getByNameAndTypeAndData(
         @QueryParam("name") String nameFilter,
         @QueryParam("type") String typeFilter,
         @QueryParam("data") String dataFilter);

   /**
    * @see RecordApi#get(int, int)
    */
   @Named("record:get")
   @GET
   @ResponseParser(ParseRecord.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/records/{recordId}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<RecordDetail> get(@PathParam("recordId") String recordId);

   /**
    * @see RecordApi#update(int, String, UpdateRecord)
    */
   @Named("record:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/records/{recordId}")
   ListenableFuture<Job<Void>> update(
         @PathParam("recordId") String recordId,
         @BinderParam(BindToJsonPayload.class) Record record);

   /**
    * @see RecordApi#update(int, Iterable)
    */
   @Named("record:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/records")
   ListenableFuture<Job<Void>> update(
         @BinderParam(BindRecordsToJsonPayload.class) Map<String, Record> idsToRecords);

   /**
    * @see RecordApi#delete(int, String)
    */
   @Named("record:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/records/{recordId}")
   @Consumes("*/*")
   ListenableFuture<Job<Void>> delete(@PathParam("recordId") String recordId);

   /**
    * @see RecordApi#delete(int, String)
    */
   @Named("record:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/records")
   @Consumes("*/*")
   ListenableFuture<Job<Void>> delete(@QueryParam("id") Iterable<String> recordId);
}
