/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.clouddns.v1.features;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.binders.FormatAndContentsToJSON;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateDomainToJSON;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateDomainsToJSON;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.DomainChange;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rackspace.clouddns.v1.functions.DomainsToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.functions.ParseDomain;
import org.jclouds.rackspace.clouddns.v1.functions.ParseDomains;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.functions.ParseSubdomains;
import org.jclouds.rackspace.clouddns.v1.functions.SubdomainsToPagedIterable;
import org.jclouds.rackspace.cloudidentity.v2_0.CloudIdentityFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.functions.DateParser;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see DomainApi
 * @author Everett Toews
 */
@Endpoint(CloudDNS.class)
@RequestFilters(AuthenticateRequest.class)
public interface DomainAsyncApi {

   /**
    * @see DomainApi#create(CreateDomain)
    */
   @Named("domain:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/domains")
   ListenableFuture<Job<Set<Domain>>> create(@WrapWith("domains") Iterable<CreateDomain> createDomains);

   /**
    * @see DomainApi#list()
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDomains.class)
   @Transform(DomainsToPagedIterable.class)
   @Path("/domains")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<Domain>> list();

   /**
    * @see DomainApi#listWithFilterByNamesMatching(String)
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDomains.class)
   @Transform(DomainsToPagedIterable.class)
   @Path("/domains")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<Domain>> listWithFilterByNamesMatching(@QueryParam("name") String nameFilter);

   /**
    * @see DomainApi#list(PaginationOptions)
    */
   @Named("domain:list")
   @GET
   @ResponseParser(ParseDomains.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/domains")
   ListenableFuture<PaginatedCollection<Domain>> list(PaginationOptions options);

   /**
    * @see DomainApi#listSubdomains(int)
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseSubdomains.class)
   @Transform(SubdomainsToPagedIterable.class)
   @Path("/domains/{domainId}/subdomains")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<Subdomain>> listSubdomains(@PathParam("domainId") int domainId);

   /**
    * @see DomainApi#listSubdomains(int, PaginationOptions)
    */
   @Named("domain:list")
   @GET
   @ResponseParser(ParseSubdomains.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/domains/{domainId}/subdomains")
   ListenableFuture<PaginatedCollection<Subdomain>> listSubdomains(@PathParam("domainId") int domainId,
         PaginationOptions options);

   /**
    * @see DomainApi#listChanges(int, Date)
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/domains/{id}/changes")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DomainChange> listChanges(@PathParam("id") int id,
         @ParamParser(DateParser.class) @QueryParam("changes") Date since);

   /**
    * @see DomainApi#get(int)
    */
   @Named("domain:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/domains/{id}")
   @QueryParams(keys = { "showRecords", "showSubdomains" }, values = { "true", "true" })
   @ResponseParser(ParseDomain.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Domain> get(@PathParam("id") int id);

   /**
    * @see DomainApi#update(int, UpdateDomain)
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/domains/{id}")
   @MapBinder(UpdateDomainToJSON.class)
   @SuppressWarnings("rawtypes")
   ListenableFuture<Job> update(@PathParam("id") int id, @PayloadParam("updateDomain") UpdateDomain updateDomain);

   /**
    * @see DomainApi#updateTTL(Iterable, int)
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/domains")
   @MapBinder(UpdateDomainsToJSON.class)
   @SuppressWarnings("rawtypes")
   ListenableFuture<Job> updateTTL(@PayloadParam("ids") Iterable<Integer> ids, @PayloadParam("ttl") int ttl);

   /**
    * @see DomainApi#updateEmail(Iterable, String)
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/domains")
   @MapBinder(UpdateDomainsToJSON.class)
   @SuppressWarnings("rawtypes")
   ListenableFuture<Job> updateEmail(@PayloadParam("ids") Iterable<Integer> ids, @PayloadParam("emailAddress") String email);

   /**
    * @see DomainApi#delete(Iterable, boolean)
    */
   @Named("domain:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/domains")
   @Consumes("*/*")
   @SuppressWarnings("rawtypes")
   ListenableFuture<Job> delete(@QueryParam("id") Iterable<Integer> ids,
         @QueryParam("deleteSubdomains") boolean deleteSubdomains);

   /**
    * @see DomainApi#exportFormat(int)
    */
   @Named("domain:export")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains/{id}/export")
   @Fallback(NullOnNotFoundOr404.class)
   // format is ignored because the Cloud DNS API doesn't use it but other formats (e.g. BIND 10) may be supported in
   // the future and we don't want this interface to change
   ListenableFuture<Job<List<String>>> exportFormat(@PathParam("id") int id, Domain.Format format);

   /**
    * @see DomainApi#importFormat(List, Domain.Format)
    */
   @Named("domain:import")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @MapBinder(FormatAndContentsToJSON.class)
   @Path("/domains/import")
   ListenableFuture<Job<Domain>> importFormat(
         @PayloadParam("contents") List<String> contents,
         @PayloadParam("format") Domain.Format format);
}
