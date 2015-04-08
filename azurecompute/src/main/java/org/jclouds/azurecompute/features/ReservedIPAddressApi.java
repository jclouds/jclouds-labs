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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.binders.ReservedIPAddressParamsToXML;
import org.jclouds.azurecompute.domain.ReservedIPAddress;
import org.jclouds.azurecompute.domain.ReservedIPAddressParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;

import org.jclouds.azurecompute.xml.ListReservedIPAddressHandler;
import org.jclouds.azurecompute.xml.ReservedIPAddressHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing the reserved IP addresses in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722420.aspx">docs</a>
 */
@Path("/services/networking/reservedips")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
public interface ReservedIPAddressApi {

   /**
    * The List Reserved IP Addresses operation lists the IP addresses that have been reserved for the specified
    * subscription.
    *
    * @return list of reserved IPs.
    */
   @Named("ListReservedIPAddress")
   @GET
   @XMLResponseParser(ListReservedIPAddressHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<ReservedIPAddress> list();

   /**
    * The Get Reserved IP Address operation retrieves information about the specified reserved IP address.
    *
    * @param name reserver IP address name.
    * @return reserved IP.
    */
   @Named("GetReservedIPAddress")
   @GET
   @Path("/{name}")
   @XMLResponseParser(ReservedIPAddressHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ReservedIPAddress get(@PathParam("name") String name);

   /**
    * The Get Reserved IP Address operation retrieves information about the specified reserved IP address.
    *
    * @param name reserver IP address name.
    * @return request id.
    */
   @Named("DeleteReservedIPAddress")
   @DELETE
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("name") String name);

   /**
    * The Create Reserved IP Address operation reserves an IPv4 address for the specified subscription. For more
    * information, see Reserved IP Addresses. You can use a small number of reserved IP addresses for free, but unused
    * reserved IP addresses and a number of addresses above the limit will incur charges.
    *
    * @param params reserved IP details to be sent as request body.
    * @return request id.
    */
   @Named("CreateReservedIPAddress")
   @POST
   @ResponseParser(ParseRequestIdHeader.class)
   String create(@BinderParam(ReservedIPAddressParamsToXML.class) ReservedIPAddressParams params);
}
