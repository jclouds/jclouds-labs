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
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing the network security groups in your subscription.
 *
 * @see https://msdn.microsoft.com/en-us/library/azure/dn913824.aspx.
 */
@Path("/services/networking")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
public interface NetworkSecurityGroupApi {

   /**
    * The List Network Security Groups operation returns a list of the network security groups in the specified
    * subscription.
    *
    * @return network security group list.
    */
   @Named("ListNetworkSecurityGroups")
   @Path("/networksecuritygroups")
   @GET
   @XMLResponseParser(ListNetworkSecurityGroupsHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<NetworkSecurityGroup> list();

   /**
    * The Create Network Security Group operation creates a new network security group within the context of the
    * specified subscription. For more information, see
    * <a href="https://msdn.microsoft.com/en-us/library/azure/dn848316.aspx">About Network Security Groups</a>.
    *
    * @param networkSecurityGroup network security group.
    * @return request id.
    */
   @Named("CreateNetworkSecurityGroup")
   @Path("/networksecuritygroups")
   @POST
   @ResponseParser(ParseRequestIdHeader.class)
   String create(@BinderParam(NetworkSecurityGroupToXML.class) NetworkSecurityGroup networkSecurityGroup);

   /**
    * Deletes the pecified Network Security Group from your subscription. If the Network Security group is still
    * associated with some VM/Role/Subnet, the deletion will fail. In order to successfully delete the Network Security,
    * it needs to be not used.
    *
    * @param networkSecurityGroupName
    * @return request id
    */
   @Named("CreateNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}")
   @Fallback(NullOnNotFoundOr404.class)
   @DELETE
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * The Get Network Security Group for Subnet operation returns information about the network security group
    * associated with a subnet.
    *
    * @param virtualNetworkName virtual network name.
    * @param subnetName subnet name.
    * @return network security group.
    */
   @Named("GetsNetworkSecurityGroupAppliedToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups")
   @GET
   @XMLResponseParser(NetworkSecurityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityGroup getNetworkSecurityGroupAppliedToSubnet(
           @PathParam("virtualNetworkName") String virtualNetworkName, @PathParam("subnetName") String subnetName);

   /**
    * The Get Network Security Group operation returns information about the specified network security group and rules.
    *
    * @param networkSecurityGroupName network security group name.
    * @return network security group.
    */
   @Named("GetDetailsNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}")
   @GET
   @QueryParams(keys = "detaillevel", values = "Full")
   @XMLResponseParser(NetworkSecurityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityGroup getFullDetails(@PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * The Get Network Security Group operation returns information about the specified network security group.
    *
    * @param networkSecurityGroupName network security group name.
    * @return network security group.
    */
   @Named("GetDetailsNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}")
   @GET
   @XMLResponseParser(NetworkSecurityGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityGroup get(@PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * The Add Network Security Group to Subnet operation associates the network security group with specified subnet in
    * a virtual network. For more information, see
    * <a href="https://msdn.microsoft.com/en-us/library/azure/dn848316.aspx">About Network Security Groups</a>.
    *
    * @param virtualNetworkName virtual network name.
    * @param subnetName subnet name.
    * @param networkSecurityGroupName network security group name.
    * @return request id.
    */
   @Named("AddNetworkSecurityGroupToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups")
   @Payload("<NetworkSecurityGroup xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<Name>{networkSecurityGroupName}</Name></NetworkSecurityGroup>")
   @POST
   @ResponseParser(ParseRequestIdHeader.class)
   String addToSubnet(@PathParam("virtualNetworkName") String virtualNetworkName,
           @PathParam("subnetName") String subnetName,
           @PayloadParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * The Remove Network Security Group from Subnet operation removes the association of the specified network security
    * group from the specified subnet.
    *
    * @param virtualNetworkName virtual network name.
    * @param subnetName subnet name.
    * @param networkSecurityGroupName network security group name.
    * @return request id.
    */
   @Named("RemoveNetworkSecurityGroupToSubnet")
   @Path("/virtualnetwork/{virtualNetworkName}/subnets/{subnetName}/networksecuritygroups/{networkSecurityGroupName}")
   @Fallback(NullOnNotFoundOr404.class)
   @DELETE
   @ResponseParser(ParseRequestIdHeader.class)
   String removeFromSubnet(@PathParam("virtualNetworkName") String virtualNetworkName,
           @PathParam("subnetName") String subnetName,
           @PathParam("networkSecurityGroupName") String networkSecurityGroupName);

   /**
    * The Set Network Security Rule operation adds or updates a network security rule that is associated with the
    * specified network security group.
    *
    * @param networkSecurityGroupName network security group name.
    * @param ruleName rule name.
    * @param rule rule.
    * @return request id.
    */
   @Named("SetNetworkSecurityRuleToNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}/rules/{ruleName}")
   @PUT
   @ResponseParser(ParseRequestIdHeader.class)
   String setRule(@PathParam("networkSecurityGroupName") String networkSecurityGroupName,
           @PathParam("ruleName") String ruleName, @BinderParam(RuleToXML.class) Rule rule);

   /**
    * The Delete Network Security Rule operation deletes a network security group rule from the specified network
    * security group.
    *
    * @param networkSecurityGroupName network security group name.
    * @param ruleName name of the rule to be deleted.
    * @return request id.
    */
   @Named("SetNetworkSecurityRuleToNetworkSecurityGroup")
   @Path("/networksecuritygroups/{networkSecurityGroupName}/rules/{ruleName}")
   @Fallback(NullOnNotFoundOr404.class)
   @DELETE
   @ResponseParser(ParseRequestIdHeader.class)
   String deleteRule(@PathParam("networkSecurityGroupName") String networkSecurityGroupName,
           @PathParam("ruleName") String ruleName);
}
