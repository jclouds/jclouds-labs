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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.binders.StorageServiceParamsToXML;
import org.jclouds.azurecompute.domain.Availability;
import org.jclouds.azurecompute.domain.StorageService;
import org.jclouds.azurecompute.domain.StorageServiceParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.AvailabilityHandler;
import org.jclouds.azurecompute.xml.ListStorageServicesHandler;
import org.jclouds.azurecompute.xml.StorageServiceHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

@Path("/services/storageservices")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface StorageAccountApi {

   /**
    * The Create Storage Account asynchronous operation creates a new storage account in Microsoft Azure.
    *
    */
   @Named("CreateStorageAccount")
   @POST
   @Produces(APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String create(@BinderParam(StorageServiceParamsToXML.class) StorageServiceParams storageServiceParams);

   /**
    * https://management.core.windows.net/<subscription-id>/services/storageservices
    */
   @Named("GetStorageAccountDetails")
   @GET
   @Path("/{storageAccountName}")
   @XMLResponseParser(StorageServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   StorageService get(@PathParam("storageAccountName") String storageAccountName);

   /**
    * The List Storage Accounts operation lists the storage accounts that are available in the specified subscription.
    */
   @Named("ListStorageAccounts")
   @GET
   @XMLResponseParser(ListStorageServicesHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<StorageService> list();

   /**
    * The Check Storage Account Name Availability operation checks to see if the specified storage account name is available, or if it has already been taken.
    */
   @Named("CheckStorageAccountNameAvailability")
   @GET
   @Path("/operations/isavailable/{storageAccountName}")
   @XMLResponseParser(AvailabilityHandler.class)
   Availability checkAvailable(@PathParam("storageAccountName") String storageAccountName);

   @Named("DeleteStorageAccount")
   @DELETE
   @Path("/{serviceName}")
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(String serviceName);

}
