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
package org.jclouds.fujitsu.fgcp.services;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.PublicIPStatus;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;


/**
 * API relating to public IP addresses.
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface PublicIPAddressApi extends Closeable {

   @Named("AttachPublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "AttachPublicIP")
   void attach(@QueryParam("vsysId") String systemId,
         @QueryParam("publicIp") String ip);

   @Named("DetachPublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DetachPublicIP")
   void detach(@QueryParam("vsysId") String systemId,
         @QueryParam("publicIp") String ip);

   @Named("GetPublicIPStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPublicIPStatus")
   @Transform(SingleElementResponseToElement.class)
   PublicIPStatus getStatus(
         @QueryParam("publicIp") String ip);

   @Named("GetPublicIPAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPublicIPAttributes")
   @Transform(SingleElementResponseToElement.class)
   PublicIP get(@QueryParam("publicIp") String ip);

   @Named("FreePublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "FreePublicIP")
   void free(@QueryParam("vsysId") String systemId,
         @QueryParam("publicIp") String ip);

}
