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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServer;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerBackup;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerConfiguration;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerStatus;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;


/**
 * API relating to built-in servers, also called extended function
 * module (EFM), such as a firewall or load balancer (SLB).
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface BuiltinServerApi extends Closeable {

   @Named("StartEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StartEFM")
   void start(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("StopEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StopEFM")
   void stop(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("DestroyEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyEFM")
   void destroy(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("BackupEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "BackupEFM")
   void backup(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("RestoreEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RestoreEFM")
   void restore(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("backupId") String backupId);

   @Named("ListEFMBackup")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListEFMBackup")
   Set<BuiltinServerBackup> listBackups(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("DestroyEFMBackup")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyEFMBackup")
   void destroyBackup(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("backupId") String backupId);

   @Named("GetEFMAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMAttributes")
   @Transform(SingleElementResponseToElement.class)
   BuiltinServer get(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("UpdateEFMAttribute")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateEFMAttribute")
   void update(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @Named("GetEFMStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMStatus")
   @Transform(SingleElementResponseToElement.class)
   BuiltinServerStatus getStatus(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id);

   @Named("GetEFMConfiguration")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEFMConfiguration")
   @Transform(SingleElementResponseToElement.class)
   BuiltinServer getConfiguration(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("efmId") String id,
         @QueryParam("configurationName") BuiltinServerConfiguration configuration);

//  @Named("GetEFMConfiguration")
//  @POST
//  @JAXBResponseParser
//  @QueryParams(keys = "Action", values = "GetEFMConfiguration")
//  @Transform(SingleElementResponseToElement.class)
//  Set<Rule> getUpdateDetails(String id);

   // Void
   // updateConfiguration(@BinderParam(BindAlsoToSystemId.class)
   // @QueryParam("efmId") String id, xml?);
//   EFM_UPDATE,       getUpdateStatus(String id);
}
