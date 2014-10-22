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
package org.jclouds.azurecompute.features;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.azurecompute.domain.HostedService;
import org.jclouds.azurecompute.functions.Base64EncodeLabel;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.HostedServiceHandler;
import org.jclouds.azurecompute.xml.ListHostedServicesHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing the hosted services beneath your
 * subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
 */
@Path("/services/hostedservices")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
public interface HostedServiceApi {

   /**
    * The List Hosted Services operation lists the hosted services available under the current
    * subscription.
    *
    * @return the response object
    */
   @Named("ListHostedServices")
   @GET
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(ListHostedServicesHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<HostedService> list();

   /**
    * The Create Hosted Service operation creates a new hosted service in Windows Azure.
    *
    * @param name
    *           A name for the hosted service that is unique within Windows Azure. This name is the
    *           DNS prefix name and can be used to access the hosted service.
    *
    *           For example: http://name.cloudapp.net//
    * @param label
    *           The name can be used identify the storage account for your tracking purposes. The
    *           name can be up to 100 characters in length.
    * @param location
    *           The location where the hosted service will be created.
    * @return the requestId to track this async request progress
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    */
   @Named("CreateHostedService")
   @POST
   @Produces(APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload("<CreateHostedService xmlns=\"http://schemas.microsoft.com/windowsazure\"><ServiceName>{name}</ServiceName><Label>{label}</Label><Location>{location}</Location></CreateHostedService>")
   String createWithLabelInLocation(@PayloadParam("name") String name,
         @PayloadParam("label") @ParamParser(Base64EncodeLabel.class) String label,
         @PayloadParam("location") String location);

   /**
    * The Get Hosted Service Properties operation retrieves system properties for the specified
    * hosted service. These properties include the service name and service type; the name of the
    * affinity group to which the service belongs, or its location if it is not part of an affinity
    * group.
    *
    * @param name
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   @Named("GetHostedServiceProperties")
   @GET
   @Path("/{name}")
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(HostedServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable HostedService get(@PathParam("name") String name);

   /**
    * The Delete Hosted Service operation deletes the specified hosted service from Windows Azure.
    *
    * @param name
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    *
    * @return request id or null, if not found
    */
   @Named("DeleteHostedService")
   @DELETE
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("name") String name);
}
