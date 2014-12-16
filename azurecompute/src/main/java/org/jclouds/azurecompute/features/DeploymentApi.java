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

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azurecompute.binders.DeploymentParamsToXML;
import org.jclouds.azurecompute.domain.Deployment;
import org.jclouds.azurecompute.domain.DeploymentParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.DeploymentHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

@Path("/services/hostedservices/{serviceName}/deployments")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface DeploymentApi {

   /**
    * The Get Deployment operation returns the specified deployment from Windows Azure.
    *
    * @param name
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   @Named("GetDeployment")
   @GET
   @Path("/{name}")
   @XMLResponseParser(DeploymentHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   Deployment get(@PathParam("name") String name);

   @Named("CreateVirtualMachineDeployment")
   @POST
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String create(@BinderParam(DeploymentParamsToXML.class) DeploymentParams params);

   /**
    * The Delete Deployment operation deletes the specified deployment from Windows Azure.
    *
    * @param name
    *           the unique DNS Prefix value in the Windows Azure Management Portal
    */
   @Named("DeleteDeployment")
   @DELETE
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("name") String name);
   
}
