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

import static org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.binders.NetworkSecurityGroupToXML;
import org.jclouds.azurecompute.binders.RuleToXML;
import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.ListNetworkSecurityGroupsHandler;
import org.jclouds.azurecompute.xml.NetworkSecurityGroupHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

@Path("/services/networking")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface NetworkSecurityGroupApi {

   /**
    * Lists all of the Network Security Groups for the subscription.
    *
    */
   @Named("ListNetworkSecurityGroups")
   @Path("/networksecuritygroups")
   @GET
   @XMLResponseParser(ListNetworkSecurityGroupsHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<NetworkSecurityGroup> list();

   @Named("CreateNetworkSecurityGroup")
   @Path("/networksecuritygroups")
   @POST
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String create(@BinderParam(NetworkSecurityGroupToXML.class) NetworkSecurityGroup networkSecurityGroup);

   /**
    * Deletes the pecified Network Security Group from your subscription.
    * If the Network Security group is still associated with some VM/Role/Subnet, the deletion will fail.
    * In order to successfully delete the Network Security, it needs to be not used.
    *
    * @param networkSecurityGroupName
    * @return request id
    */
   @Named("CreateNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}")
   @DELETE
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("networkSecurityGroupName") String networkSecurityGroupName);


   /**
    * Gets the Network Security Group applied to a specific subnet.
    *
    * @param virtualNetworkName
    * @param subnetName
    * @return
    */
   @Named("GetsNetworkSecurityGroupAppliedToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups")
   @GET
   @XMLResponseParser(NetworkSecurityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityGroup getNetworkSecurityGroupAppliedToSubnet(@PathParam("virtualNetworkName") String virtualNetworkName,
                            @PathParam("subnetName") String subnetName);

   /**
    * Gets the details for the specified Network Security Group in the subscription
    *
    * @param networkSecurityGroupName
    * @return
    */
   @Named("GetDetailsNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}")
   @GET
   @QueryParams(keys = "detaillevel", values = "Full")
   @XMLResponseParser(NetworkSecurityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityGroup getFullDetails(@PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    *  Adds a Network Security Group to a subnet.
    *
    * @param virtualNetworkName
    * @return
    */
   @Named("AddNetworkSecurityGroupToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups")
   @POST
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String addToSubnet(@PathParam("virtualNetworkName") String virtualNetworkName,
                      @PathParam("subnetName") String subnetName,
                      @BinderParam(NetworkSecurityGroupToXML.class) NetworkSecurityGroup networkSecurityGroup);

   /**
    * Removes a Network Security Group from a subnet
    */
   @Named("RemoveNetworkSecurityGroupToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups/{networkSecurityGroupName}")
   @DELETE
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String removeFromSubnet(@PathParam("virtualNetworkName") String virtualNetworkName,
                      @PathParam("subnetName") String subnetName,
                      @PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * Sets a new Network Security Rule to existing Network Security Group
    *
    *
    */
   @Named("SetNetworkSecurityRuleToNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}/rules/{ruleName}")
   @PUT
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String setRule(@PathParam("networkSecurityGroupName") String networkSecurityGroupName,
                      @PathParam("ruleName") String ruleName, @BinderParam(RuleToXML.class) Rule rule);

   /**
    * Deletes a rule from the specified Network Security Group.
    *
    */
   @Named("SetNetworkSecurityRuleToNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}/rules/{ruleName}")
   @DELETE
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String deleteRule(@PathParam("networkSecurityGroupName") String networkSecurityGroupName,
                  @PathParam("ruleName") String ruleName);
}
