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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.domain.CloudService;
import org.jclouds.azurecompute.domain.CloudServiceProperties;
import org.jclouds.azurecompute.functions.Base64EncodeLabel;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.CloudServiceHandler;
import org.jclouds.azurecompute.xml.CloudServicePropertiesHandler;
import org.jclouds.azurecompute.xml.ListCloudServicesHandler;
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
 * The Service Management API includes operations for managing the cloud services beneath your subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
 */
@Path("/services/hostedservices")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
public interface CloudServiceApi {

   /**
    * The List Cloud Services operation lists the cloud services available under the current subscription.
    *
    * @return the response object
    */
   @Named("ListCloudServices")
   @GET
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(ListCloudServicesHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<CloudService> list();

   /**
    * The Create Cloud Service operation creates a new cloud service in Windows Azure.
    *
    * @param name A name for the cloud service that is unique within Windows Azure. This name is the DNS prefix name and
    * can be used to access the cloud service.
    *
    * For example: http://name.cloudapp.net//
    * @param label The name can be used identify the storage account for your tracking purposes. The name can be up to
    * 100 characters in length.
    * @param location The location where the cloud service will be created.
    * @return the requestId to track this async request progress
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    */
   @Named("CreateCloudService")
   @POST
   @Produces(APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload("<CreateHostedService xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<ServiceName>{name}</ServiceName><Label>{label}</Label>"
           + "<Location>{location}</Location></CreateHostedService>")
   String createWithLabelInLocation(@PayloadParam("name") String name,
           @PayloadParam("label") @ParamParser(Base64EncodeLabel.class) String label,
           @PayloadParam("location") String location);

   /**
    * The Get Cloud Service Properties operation retrieves system properties for the specified cloud service. These
    * properties include the service name and service type; the name of the affinity group to which the service belongs,
    * or its location if it is not part of an affinity group.
    *
    * @param name the unique DNS Prefix value in the Windows Azure Management Portal
    */
   @Named("GetCloudServiceProperties")
   @GET
   @Path("/{name}")
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(CloudServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   CloudService get(@PathParam("name") String name);

   /**
    * The Delete Cloud Service operation deletes the specified cloud service from Windows Azure.
    *
    * @param name the unique DNS Prefix value in the Windows Azure Management Portal
    *
    * @return request id or null, if not found
    */
   @Named("DeleteCloudService")
   @DELETE
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("name") String name);

   /*
   * The Get Cloud Service Properties operation retrieves properties for the specified cloud service.
   *
   * These properties include the following values:
   *  The name and the description of the cloud service.
   *
   *  The name of the affinity group to which the cloud service belongs, or its location if it is not part of an affinity group.
   *
   *  The label that can be used to track the cloud service.
   *
   *  The date and time that the cloud service was created or modified.
   *
   *  If details are requested, information about deployments in the cloud service is returned.
   *
   * */

   @Named("CloudServiceProperties")
   @GET
   @Path("/{name}")
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(CloudServicePropertiesHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable CloudServiceProperties getProperties(@PathParam("name") String name);
}
