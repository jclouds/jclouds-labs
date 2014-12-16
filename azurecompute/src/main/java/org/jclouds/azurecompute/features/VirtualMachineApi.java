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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azurecompute.binders.RoleToXML;
import org.jclouds.azurecompute.domain.Role;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * The Service Management API includes operations for managing the virtual
 * machines in your subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
 */
@Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
// NOTE: MS Docs refer to the commands as Role, but in the description, it is always Virtual Machine.
public interface VirtualMachineApi {

   @Named("RestartRole")
   @POST
   // Warning : the url in the documentation is WRONG ! @see
   // http://social.msdn.microsoft.com/Forums/pl-PL/WAVirtualMachinesforWindows/thread/7ba2367b-e450-49e0-89e4-46c240e9d213
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<RestartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\"><OperationType>RestartRoleOperation</OperationType></RestartRoleOperation>")
   String restart(@PathParam("name") String name);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157201
    */
   @Named("CaptureRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<CaptureRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\"><OperationType>CaptureRoleOperation</OperationType><PostCaptureAction>Delete</PostCaptureAction><TargetImageLabel>{imageLabel}</TargetImageLabel><TargetImageName>{imageName}</TargetImageName></CaptureRoleOperation>")
   String capture(@PathParam("name") String name, @PayloadParam("imageName") String imageName,
         @PayloadParam("imageLabel") String imageLabel);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157195
    */
   @Named("ShutdownRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<ShutdownRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\"><OperationType>ShutdownRoleOperation</OperationType></ShutdownRoleOperation>")
   String shutdown(@PathParam("name") String name);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157189
    */
   @Named("StartRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<StartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\"><OperationType>StartRoleOperation</OperationType></StartRoleOperation>")
   String start(@PathParam("name") String name);

   /**
    * https://msdn.microsoft.com/en-us/library/azure/jj157193.aspx
    */
   @Named("GetRole")
   @GET
   @Path("/roles/{roleName}")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Fallback(NullOnNotFoundOr404.class)
   Role getRole(@PathParam("roleName") String roleName);

   /**
    * https://msdn.microsoft.com/library/azure/jj157187.aspx
    */
   @Named("UpdateRole")
   @PUT
   @Path("/roles/{roleName}")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String updateRole(@PathParam("roleName") String roleName, @BinderParam(RoleToXML.class) Role role);

}
