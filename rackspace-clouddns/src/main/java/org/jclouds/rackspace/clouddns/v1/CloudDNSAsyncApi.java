/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.jclouds.rackspace.clouddns.v1;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.features.DomainAsyncApi;
import org.jclouds.rackspace.clouddns.v1.features.LimitAsyncApi;
import org.jclouds.rackspace.clouddns.v1.features.RecordAsyncApi;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to the Rackspace Cloud DNS API.
 * <p/>
 * 
 * @see CloudDNSApi
 * @author Everett Toews
 */
public interface CloudDNSAsyncApi {
   /**
    * @see CloudDNSApi#getJob(String)
    */
   @Named("job:get")
   @Endpoint(CloudDNS.class)
   @RequestFilters(AuthenticateRequest.class)
   @GET
   @Consumes(APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "showDetails", values = "true")
   @Path("/status/{jobId}")
   <T> ListenableFuture<Job<T>> getJob(@PathParam("jobId") String jobId);

   /**
    * Provides asynchronous access to Limit features.
    */
   @Delegate
   LimitAsyncApi getLimitApi();

   /**
    * Provides asynchronous access to Domain features.
    */
   @Delegate
   DomainAsyncApi getDomainApi();

   /**
    * Provides asynchronous access to Record features.
    */
   @Delegate
   @Path("/domains/{domainId}")
   RecordAsyncApi getRecordApiForDomain(@PathParam("domainId") int domainId);
}
