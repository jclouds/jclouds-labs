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
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.PerformanceInfo;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;


/**
 * API relating to virtual servers.
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface VirtualServerApi extends Closeable {

   @Named("StartVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StartVServer")
   void start(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("StopVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StopVServer")
   void stop(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("StopVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = { "Action", "force" }, values = { "StopVServer", "true" })
   void stopForcefully(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("DestroyVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyVServer")
   void destroy(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("GetVServerAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerAttributes")
   @Transform(SingleElementResponseToElement.class)
   VServer get(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("GetVServerConfiguration")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerConfiguration")
   @Transform(SingleElementResponseToElement.class)
   VServerWithDetails getDetails(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("UpdateVServerAttribute")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateVServerAttribute")
   void update(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @Named("GetVServerStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerStatus")
   // @Transform(StringToVServerStatus.class)
   @Transform(SingleElementResponseToElement.class)
   VServerStatus getStatus(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("GetVServerInitialPassword")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerInitialPassword")
   @Transform(SingleElementResponseToElement.class)
   String getInitialPassword(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @Named("AttachVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "AttachVDisk")
   void attachDisk(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String serverId,
         @QueryParam("vdiskId") String diskId);

   @Named("GetPerformanceInformation")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPerformanceInformation")
   Set<PerformanceInfo> getPerformanceInformation(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("serverId") String id,
         @QueryParam("interval") String interval);

   @Named("GetPerformanceInformation")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPerformanceInformation")
   Set<PerformanceInfo> getPerformanceInformation(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("serverId") String id,
         @QueryParam("dataType") String dataType,
         @QueryParam("interval") String interval);

   @Named("RegisterPrivateDiskImage")
   @POST
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RegisterPrivateDiskImage")
   void registerAsPrivateDiskImage(String xml);
}
