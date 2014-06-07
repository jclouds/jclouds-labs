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
package org.jclouds.abiquo.features;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.binders.cloud.BindHardDiskRefsToPayload;
import org.jclouds.abiquo.binders.cloud.BindMoveVolumeToPath;
import org.jclouds.abiquo.binders.cloud.BindNetworkConfigurationRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindNetworkRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindVirtualDatacenterRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindVolumeRefsToPayload;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.options.VirtualApplianceOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.fallbacks.MovedVolume;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.abiquo.functions.enterprise.ParseEnterpriseId;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.functions.pagination.ParsePrivateIps;
import org.jclouds.abiquo.functions.pagination.ParsePublicIps;
import org.jclouds.abiquo.functions.pagination.ParseVirtualMachineTemplates;
import org.jclouds.abiquo.functions.pagination.ParseVirtualMachines;
import org.jclouds.abiquo.functions.pagination.ParseVolumes;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.MovedVolumeDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;

/**
 * Provides synchronous access to Abiquo Cloud API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/cloud")
public interface CloudApi extends Closeable {
   /*********************** Virtual Datacenter ***********************/

   /**
    * List all virtual datacenters.
    * 
    * @param options
    *           Optional query params.
    * @return The list of Datacenters.
    */
   @Named("vdc:list")
   @GET
   @Path("/virtualdatacenters")
   @Consumes(VirtualDatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualDatacentersDto listVirtualDatacenters(VirtualDatacenterOptions options);

   /**
    * Get the given virtual datacenter.
    * 
    * @param virtualDatacenterId
    *           The id of the virtual datacenter.
    * @return The virtual datacenter or <code>null</code> if it does not exist.
    */
   @Named("vdc:get")
   @GET
   @Path("/virtualdatacenters/{virtualdatacenter}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualDatacenterDto getVirtualDatacenter(@PathParam("virtualdatacenter") Integer virtualDatacenterId);

   /**
    * Create a new virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter to be created.
    * @param datacenter
    *           Datacenter where the virtualdatacenter will be deployed.
    * @param enterprise
    *           Enterprise of the virtual datacenter.
    * @return The created virtual datacenter.
    */
   @Named("vdc:create")
   @POST
   @Path("/virtualdatacenters")
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualDatacenterDto createVirtualDatacenter(
         @BinderParam(BindToXMLPayload.class) final VirtualDatacenterDto virtualDatacenter,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter,
         @QueryParam("enterprise") @ParamParser(ParseEnterpriseId.class) final EnterpriseDto enterprise);

   /**
    * Updates an existing virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The new attributes for the virtual datacenter.
    * @return The updated virtual datacenter.
    */
   @Named("vdc:update")
   @PUT
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualDatacenterDto updateVirtualDatacenter(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Deletes an existing virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter to delete.
    */
   @Named("vdc:delete")
   @DELETE
   void deleteVirtualDatacenter(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * List all available templates for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of available templates.
    */
   @Named("vdc:listtemplates")
   @GET
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachineTemplates.class)
   @Transform(ParseVirtualMachineTemplates.ToPagedIterable.class)
   PagedIterable<VirtualMachineTemplateDto> listAvailableTemplates(
         @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * List all available templates for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of available templates.
    */
   @Named("vdc:listtemplates")
   @GET
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachineTemplates.class)
   PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> listAvailableTemplates(
         @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         VirtualMachineTemplateOptions options);

   /**
    * List all available ips to purchase in the datacenter by the virtual
    * datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of available ips.
    */
   @Named("vdc:listavailablepublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   @Transform(ParsePublicIps.ToPagedIterable.class)
   PagedIterable<PublicIpDto> listAvailablePublicIps(
         @EndpointLink("topurchase") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * List all available ips to purchase in the datacenter by the virtual
    * datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of available ips.
    */
   @Named("vdc:listavailablepublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   PaginatedCollection<PublicIpDto, PublicIpsDto> listAvailablePublicIps(
         @EndpointLink("topurchase") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         IpOptions options);

   /**
    * List all purchased public ip addresses in the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of purchased ips.
    */
   @Named("vdc:listpurchasedpublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   @Transform(ParsePublicIps.ToPagedIterable.class)
   PagedIterable<PublicIpDto> listPurchasedPublicIps(
         @EndpointLink("purchased") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * List all purchased public ip addresses in the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of purchased ips.
    */
   @Named("vdc:listpurchasedpublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   PaginatedCollection<PublicIpDto, PublicIpsDto> listPurchasedPublicIps(
         @EndpointLink("purchased") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         IpOptions options);

   /**
    * Purchase a public IP.
    * 
    * @param ip
    *           The public ip address to purchase.
    * @return The purchased public ip.
    */
   @Named("vdc:purchasepublicip")
   @PUT
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PublicIpDto purchasePublicIp(@EndpointLink("purchase") @BinderParam(BindToPath.class) PublicIpDto publicIp);

   /**
    * Release a public IP.
    * 
    * @param ip
    *           The public ip address to purchase.
    * @return The release public ip.
    */
   @Named("vdc:releasepublicip")
   @PUT
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PublicIpDto releasePublicIp(@EndpointLink("release") @BinderParam(BindToPath.class) PublicIpDto publicIp);

   /**
    * List the storage tiers available for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The storage tiers available to the given virtual datacenter.
    */
   @Named("vdc:listtiers")
   @GET
   @Consumes(TiersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TiersDto listStorageTiers(
         @EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the storage tier from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param The
    *           id of the storage tier.
    * @return The storage tiers available to the given virtual datacenter.
    */
   @Named("vdc:gettier")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TierDto getStorageTier(@EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer tierId);

   /*********************** Private Network ***********************/

   /**
    * Get the default network of the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The default network of the virtual datacenter.
    */
   @Named("vdc:getdefaultnetwork")
   @GET
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto getDefaultNetwork(
         @EndpointLink("defaultnetwork") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Set the default network of the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param network
    *           The default network.
    */
   @Named("vdc:setdefaultnetwork")
   @PUT
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   void setDefaultNetwork(
         @EndpointLink("defaultvlan") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindNetworkRefToPayload.class) VLANNetworkDto network);

   /**
    * List all private networks for a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of private networks for the virtual datacenter.
    */
   @Named("privatenetwork:list")
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworksDto listPrivateNetworks(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the given private network from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualApplianceId
    *           The id of the private network.
    * @return The private network or <code>null</code> if it does not exist.
    */
   @Named("privatenetwork:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto getPrivateNetwork(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer privateNetworkId);

   /**
    * Create a new private network in a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param privateNetwork
    *           The private network to be created.
    * @return The created private network.
    */
   @Named("privatenetwork:create")
   @POST
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto createPrivateNetwork(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VLANNetworkDto privateNetwork);

   /**
    * Updates an existing private network from the given virtual datacenter.
    * 
    * @param privateNetwork
    *           The new attributes for the private network.
    * @return The updated private network.
    */
   @Named("privatenetwork:update")
   @PUT
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto updatePrivateNetwork(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VLANNetworkDto privateNetwork);

   /**
    * Deletes an existing private network.
    * 
    * @param privateNetwork
    *           The private network to delete.
    */
   @Named("privatenetwork:delete")
   @DELETE
   void deletePrivateNetwork(@EndpointLink("edit") @BinderParam(BindToPath.class) VLANNetworkDto privateNetwork);

   /*********************** Private Network IPs ***********************/

   /**
    * List all ips for a private network.
    * 
    * @param network
    *           The private network.
    * @return The list of ips for the private network.
    */
   @Named("privatenetwork:listips")
   @GET
   @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePrivateIps.class)
   @Transform(ParsePrivateIps.ToPagedIterable.class)
   PagedIterable<PrivateIpDto> listPrivateNetworkIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * List all ips for a private network with options.
    * 
    * @param network
    *           The private network.
    * @param options
    *           Filtering options.
    * @return The list of ips for the private network.
    */
   @Named("privatenetwork:listips")
   @GET
   @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePrivateIps.class)
   PaginatedCollection<PrivateIpDto, PrivateIpsDto> listPrivateNetworkIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * Get the requested ip from the given private network.
    * 
    * @param network
    *           The private network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    */
   @Named("privatenetwork:getip")
   @GET
   @Consumes(PrivateIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PrivateIpDto getPrivateNetworkIp(@EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /*********************** Virtual Appliance ***********************/

   /**
    * List all virtual appliance for a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of virtual appliances for the virtual datacenter.
    */
   @Named("vapp:list")
   @GET
   @Consumes(VirtualAppliancesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualAppliancesDto listVirtualAppliances(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the given virtual appliance from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualApplianceId
    *           The id of the virtual appliance.
    * @return The virtual appliance or <code>null</code> if it does not exist.
    */
   @Named("vapp:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualApplianceDto getVirtualAppliance(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer virtualApplianceId);

   /**
    * Create a new virtual appliance in a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualAppliance
    *           The virtual appliance to be created.
    * @return The created virtual appliance.
    */
   @Named("vapp:create")
   @POST
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualApplianceDto createVirtualAppliance(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VirtualApplianceDto virtualAppliance);

   /**
    * Updates an existing virtual appliance from the given virtual datacenter.
    * 
    * @param virtualAppliance
    *           The new attributes for the virtual appliance.
    * @return The updated virtual appliance.
    */
   @Named("vapp:update")
   @PUT
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualApplianceDto updateVirtualAppliance(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * Deletes an existing virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to delete.
    */
   @Named("vapp:delete")
   @DELETE
   void deleteVirtualAppliance(@EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * Deletes an existing virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to delete.
    * @param options
    *           The options to customize the delete operation (e.g. Force
    *           delete).
    */
   @Named("vapp:delete")
   @DELETE
   void deleteVirtualAppliance(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         VirtualApplianceOptions options);

   /**
    * Deploy a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to deploy
    * @param options
    *           the extra options for the deploy process.
    * @return Response message to the deploy request.
    */
   @Named("vapp:deploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> deployVirtualAppliance(
         @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /**
    * Undeploy a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to undeploy
    * @param options
    *           the extra options for the undeploy process.
    * @return Response message to the undeploy request.
    */
   @Named("vapp:undeploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> undeployVirtualAppliance(
         @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /**
    * Get the state of the given virtual appliance.
    * 
    * @param virtualAppliance
    *           The given virtual appliance.
    * @return The state of the given virtual appliance.
    */
   @Named("vapp:getstate")
   @GET
   @Consumes(VirtualApplianceStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualApplianceStateDto getVirtualApplianceState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * Gets the price of the given virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to get the price of.
    * @return A <code>String</code> representation of the price of the virtual
    *         appliance.
    */
   @Named("vapp:getprice")
   @GET
   @Consumes(MediaType.TEXT_PLAIN)
   @ResponseParser(ReturnStringIf2xx.class)
   String getVirtualAppliancePrice(
         @EndpointLink("price") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /*********************** Virtual Machine ***********************/

   /**
    * List all virtual machines available to the current user.
    * 
    * @return The list of all virtual machines available to the current user.
    */
   @SinceApiVersion("2.4")
   @Named("vm:listall")
   @GET
   @Path("/virtualmachines")
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachines.class)
   @Transform(ParseVirtualMachines.ToPagedIterable.class)
   PagedIterable<VirtualMachineWithNodeExtendedDto> listAllVirtualMachines();

   /**
    * List all virtual machines available to the current user.
    * 
    * @param options
    *           The filter options.
    * @return The list of all virtual machines available to the current user.
    */
   @SinceApiVersion("2.4")
   @Named("vm:listall")
   @GET
   @Path("/virtualmachines")
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachines.class)
   PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> listAllVirtualMachines(
         VirtualMachineOptions options);

   /**
    * List all virtual machines for a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @return The list of virtual machines for the virtual appliance.
    */
   @Named("vm:list")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachines.class)
   @Transform(ParseVirtualMachines.ToPagedIterable.class)
   PagedIterable<VirtualMachineWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * List all virtual machines for a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param options
    *           The options to filter the list of virtual machines.
    * @return The list of virtual machines for the virtual appliance.
    */
   @Named("vm:list")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachines.class)
   PaginatedCollection<VirtualMachineWithNodeExtendedDto, VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         VirtualMachineOptions options);

   /**
    * Get the given virtual machine from the given virtual machine.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param virtualMachineId
    *           The id of the virtual machine.
    * @return The virtual machine or <code>null</code> if it does not exist.
    */
   @Named("vm:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineWithNodeExtendedDto getVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(AppendToPath.class) Integer virtualMachineId);

   /**
    * Create a new virtual machine in a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param virtualMachine
    *           The virtual machine to be created.
    * @return The created virtual machine.
    */
   @Named("vm:create")
   @POST
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineWithNodeExtendedDto createVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * Deletes an existing virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine to delete.
    */
   @Named("vm:delete")
   @DELETE
   void deleteVirtualMachine(@EndpointLink("edit") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Updates an existing virtual machine from the given virtual appliance.
    * 
    * @param virtualMachine
    *           The new attributes for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:update")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> updateVirtualMachine(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * Updates an existing virtual machine from the given virtual appliance.
    * 
    * @param virtualMachine
    *           The new attributes for the virtual machine.
    * @param options
    *           The update options.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:update")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> updateVirtualMachine(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine,
         VirtualMachineOptions options);

   /**
    * Changes the state an existing virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @param state
    *           The new state.
    * @return The task reference.
    */
   @Named("vm:changestate")
   @PUT
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> changeVirtualMachineState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineStateDto state);

   /**
    * Get the state of the given virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @return The state of the given virtual machine.
    */
   @Named("vm:getstate")
   @GET
   @Consumes(VirtualMachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineStateDto getVirtualMachineState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Deploy a virtual machine with task options.
    * 
    * @param virtualMachine
    *           The virtual machine to deploy.
    * @param options
    *           extra deploy options.
    * @return Response message to the deploy request.
    */
   @Named("vm:deploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> deployVirtualMachine(
         @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto options);

   /**
    * Undeploy a virtual machine with task options.
    * 
    * @param virtualMachine
    *           The virtual machine to undeploy.
    * @param options
    *           extra undeploy options.
    * @return Response message to the undeploy request.
    */
   @Named("vm:undeploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> undeployVirtualMachine(
         @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto options);

   /**
    * List all available network configurations for a virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The list of network configurations.
    */
   @Named("vm:listnetworkconfigurations")
   @GET
   @Consumes(VMNetworkConfigurationsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VMNetworkConfigurationsDto listNetworkConfigurations(
         @EndpointLink("configurations") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Sets the gateway network to be used by this virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param network
    *           The gateway network to use.
    */
   @Named("vm:setgateway")
   @PUT
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   void setGatewayNetwork(
         @EndpointLink("configurations") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindNetworkConfigurationRefToPayload.class) VLANNetworkDto network);

   /**
    * Reboot a virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine to reboot.
    * @return Response message to the reset request.
    */
   @Named("vm:reboot")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> rebootVirtualMachine(
         @EndpointLink("reset") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Take a snapshot of the given virtual machine.
    * <p>
    * This will create a new virtual machine template in the appliance library
    * based on the given virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine to snapshot.
    * @param snapshotConfig
    *           The configuration of the snapshot.
    * @return The task reference to the snapshot process.
    */
   @Named("vm:snapshot")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineInstanceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   AcceptedRequestDto<String> snapshotVirtualMachine(
         @EndpointLink("instance") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineInstanceDto snapshotConfig);

   /******************* Virtual Machine Template ***********************/

   /**
    * Get the template of a virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @return The template of the given virtual machine.
    */
   @Named("vm:gettemplate")
   @GET
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineTemplateDto getVirtualMachineTemplate(
         @EndpointLink("virtualmachinetemplate") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Get the volumes attached to the given virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The volumes attached to the given virtual machine.
    */
   @Named("vm:listvolumes")
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VolumesManagementDto listAttachedVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Detach all volumes from the given virtual machine.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:detachvolumes")
   @DELETE
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> detachAllVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Replaces the current volumes attached to the virtual machine with the
    * given ones.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param options
    *           virtual machine parameters
    * @param volumes
    *           The new volumes for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:changevolumes")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> replaceVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         VirtualMachineOptions options, @BinderParam(BindVolumeRefsToPayload.class) VolumeManagementDto... volumes);

   /**
    * List all hard disks attached to the given virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The hard disks attached to the virtual machine.
    */
   @Named("vm:listharddisks")
   @GET
   @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DisksManagementDto listAttachedHardDisks(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Detach all hard disks from the given virtual machine.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:detachharddisks")
   @DELETE
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> detachAllHardDisks(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * Replaces the current hard disks attached to the virtual machine with the
    * given ones.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param hardDisks
    *           The new hard disks for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Named("vm:changeharddisks")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> replaceHardDisks(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindHardDiskRefsToPayload.class) DiskManagementDto... hardDisks);

   /*********************** Hard disks ***********************/

   /**
    * List all hard disks in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The hard disks in the virtual datacenter.
    */
   @Named("harddisk:list")
   @GET
   @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DisksManagementDto listHardDisks(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the hard disk with the given id in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param diskId
    *           The id of the hard disk to get.
    * @return The requested hard disk or <code>null</code> if it does not exist.
    */
   @Named("harddisk:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DiskManagementDto getHardDisk(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer diskId);

   /**
    * Creates a new hard disk in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter where the hard disk will be created.
    * @param hardDisk
    *           The hard disk to create.
    * @return The created hard disk.
    */
   @Named("harddisk:create")
   @POST
   @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
   @Produces(DiskManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DiskManagementDto createHardDisk(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) DiskManagementDto hardDisk);

   /**
    * Deletes the given hard disk.
    * 
    * @param hardDisk
    *           The hard disk to delete.
    */
   @Named("harddisk:delete")
   @DELETE
   void deleteHardDisk(@EndpointLink("edit") @BinderParam(BindToPath.class) DiskManagementDto hardDisk);

   /*********************** Volumes ***********************/

   /**
    * List all volumes in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The volumes in the virtual datacenter.
    */
   @Named("volume:list")
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVolumes.class)
   @Transform(ParseVolumes.ToPagedIterable.class)
   PagedIterable<VolumeManagementDto> listVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * List all volumes in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Optional parameters to filter the volume list.
    * @return The volumes in the virtual datacenter.
    */
   @Named("volume:list")
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVolumes.class)
   PaginatedCollection<VolumeManagementDto, VolumesManagementDto> listVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         VolumeOptions options);

   /**
    * Get a volume from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param volumeId
    *           The id of the volume to get.
    * @return The volume or <code>null</code> if it does not exist.
    */
   @Named("volume:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VolumeManagementDto getVolume(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer volumeId);

   /**
    * Creates a volume in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter where the volume will be created.
    * @param volume
    *           The volume to create. This volume dto must contain a link to the
    *           tier where the volume should be created.
    * @return The created volume.
    */
   @Named("volume:create")
   @POST
   @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
   @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VolumeManagementDto createVolume(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VolumeManagementDto volume);

   /**
    * Modifies the given volume.
    * <p>
    * If the virtual machine is deployed and the size of the volume is changed,
    * then an asynchronous task will be generated to refresh the resources of
    * the virtual machine in the hypervisor.
    * 
    * @param volume
    *           The volume to modify.
    * @return The task reference or <code>null</code> if no task was generated.
    */
   @Named("volume:update")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> updateVolume(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VolumeManagementDto volume);

   /**
    * Delete the given volume.
    * 
    * @param volume
    *           The volume to delete.
    */
   @Named("volume:delete")
   @DELETE
   void deleteVolume(@EndpointLink("edit") @BinderParam(BindToPath.class) VolumeManagementDto volume);

   /**
    * Moves the given volume to a new virtual datacenter.
    * <p>
    * The Abiquo API will return a 301 (Moved Permanently), so redirects must be
    * enabled to make this method succeed.
    * 
    * @param volume
    *           The volume to move.
    * @param newVirtualDatacenter
    *           The destination virtual datacenter.
    * @return The reference to the volume in the new virtual datacenter.
    */
   @Named("volume:move")
   @POST
   @Fallback(MovedVolume.class)
   @Consumes(MovedVolumeDto.BASE_MEDIA_TYPE)
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VolumeManagementDto moveVolume(@BinderParam(BindMoveVolumeToPath.class) VolumeManagementDto volume,
         @BinderParam(BindVirtualDatacenterRefToPayload.class) VirtualDatacenterDto newVirtualDatacenter);

}
