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
import org.jclouds.abiquo.AbiquoFallbacks.FalseIfNotAvailable;
import org.jclouds.abiquo.AbiquoFallbacks.PropagateAbiquoExceptionOnNotFoundOr4xx;
import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.binders.infrastructure.AppendMachineIdToPath;
import org.jclouds.abiquo.binders.infrastructure.AppendRemoteServiceTypeToPath;
import org.jclouds.abiquo.binders.infrastructure.BindSupportedDevicesLinkToPath;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.infrastructure.options.DatacenterOptions;
import org.jclouds.abiquo.domain.infrastructure.options.IpmiOptions;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.domain.network.options.NetworkOptions;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.functions.pagination.ParseExternalIps;
import org.jclouds.abiquo.functions.pagination.ParsePublicIps;
import org.jclouds.abiquo.functions.pagination.ParseUnmanagedIps;
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

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypesDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VlanTagAvailabilityDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesMetadataDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolsDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;

/**
 * Provides synchronous access to Abiquo Infrastructure API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin")
public interface InfrastructureApi extends Closeable {
   /*********************** Datacenter ***********************/

   /**
    * List all datacenters.
    * 
    * @return The list of Datacenters.
    */
   @Named("datacenter:list")
   @GET
   @Path("/datacenters")
   @Consumes(DatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacentersDto listDatacenters();

   /**
    * Create a new datacenter.
    * 
    * @param datacenter
    *           The datacenter to be created.
    * @return The created datacenter.
    */
   @Named("datacenter:create")
   @POST
   @Path("/datacenters")
   @Produces(DatacenterDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacenterDto createDatacenter(@BinderParam(BindToXMLPayload.class) DatacenterDto datacenter);

   /**
    * Get the given datacenter.
    * 
    * @param datacenterId
    *           The id of the datacenter.
    * @return The datacenter or <code>null</code> if it does not exist.
    */
   @Named("datacenter:get")
   @GET
   @Path("/datacenters/{datacenter}")
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   DatacenterDto getDatacenter(@PathParam("datacenter") Integer datacenterId);

   /**
    * Updates an existing datacenter.
    * 
    * @param datacenter
    *           The new attributes for the datacenter.
    * @return The updated datacenter.
    */
   @Named("datacenter:update")
   @PUT
   @Produces(DatacenterDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacenterDto updateDatacenter(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) DatacenterDto datacenter);

   /**
    * Deletes an existing datacenter.
    * 
    * @param datacenter
    *           The datacenter to delete.
    */
   @Named("datacenter:delete")
   @DELETE
   void deleteDatacenter(@EndpointLink("edit") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Retrieve remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine.
    */
   @Named("machine:discover")
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineDto discoverSingleMachine(
         @EndpointLink("discoversingle") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * Retrieve remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine.
    */
   @Named("machine:discover")
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineDto discoverSingleMachine(
         @EndpointLink("discoversingle") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password, MachineOptions options);

   /**
    * Retrieve a list of remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine list.
    */
   @Named("machine:discover")
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineDto discoverMultipleMachines(
         @EndpointLink("discovermultiple") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ipFrom") String ipFrom, @QueryParam("ipTo") String ipTo,
         @QueryParam("hypervisor") HypervisorType hypervisorType, @QueryParam("user") String user,
         @QueryParam("password") String password);

   /**
    * Retrieve a list of remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine list.
    */
   @Named("machine:discover")
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachinesDto discoverMultipleMachines(
         @EndpointLink("discovermultiple") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ipFrom") String ipFrom, @QueryParam("ipTo") String ipTo,
         @QueryParam("hypervisor") HypervisorType hypervisorType, @QueryParam("user") String user,
         @QueryParam("password") String password, MachineOptions options);

   /**
    * Retrieves limits for the given datacenter and any enterprise.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The usage limits for the datacenter on any enterprise.
    */
   @Named("limit:list")
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacentersLimitsDto listLimits(@EndpointLink("getLimits") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Check the state of a remote machine. This machine does not need to be
    * managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine state information.
    */
   @Named("machine:checkstate")
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineStateDto checkMachineState(
         @EndpointLink("checkmachinestate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * Check the state of a remote machine. This machine does not need to be
    * managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine state information.
    */
   @Named("machine:checkstate")
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineStateDto checkMachineState(
         @EndpointLink("checkmachinestate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("hypervisor") HypervisorType hypervisorType,
         @QueryParam("user") String user, @QueryParam("password") String password, MachineOptions options);

   /**
    * Check the ipmi configuration state of a remote machine. This machine does
    * not need to be managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The ipmi configuration state information
    */
   @Named("machine:checkipmi")
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineIpmiStateDto checkMachineIpmiState(
         @EndpointLink("checkmachineipmistate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("user") String user, @QueryParam("password") String password);

   /**
    * Check the ipmi configuration state of a remote machine. This machine does
    * not need to be managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The ipmi configuration state information
    */
   @Named("machine:checkipmi")
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(PropagateAbiquoExceptionOnNotFoundOr4xx.class)
   MachineIpmiStateDto checkMachineIpmiState(
         @EndpointLink("checkmachineipmistate") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @QueryParam("ip") String ip, @QueryParam("user") String user, @QueryParam("password") String password,
         IpmiOptions options);

   /*********************** Hypervisor ***********************/

   /**
    * Retrieves the hypervisor type of a remote a machine.
    * 
    * @param datacenter
    *           The datacenter.
    * @param options
    *           Optional query params.
    * @return The hypervisor type.
    */
   @Named("hypervisortype:getfrommachine")
   @GET
   @Consumes(MediaType.TEXT_PLAIN)
   @ResponseParser(ReturnStringIf2xx.class)
   String getHypervisorTypeFromMachine(
         @EndpointLink("hypervisor") @BinderParam(BindToPath.class) DatacenterDto datacenter, DatacenterOptions options);

   /**
    * Retrieves the hypervisor types in the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The hypervisor types.
    */
   @Named("hypervisortype:list")
   @GET
   @Consumes(HypervisorTypesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   HypervisorTypesDto getHypervisorTypes(
         @EndpointLink("hypervisors") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /*********************** Unmanaged Rack ********************** */

   /**
    * List all not managed racks for a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of not managed racks for the datacenter.
    */
   @Named("rack:list")
   @GET
   @Consumes(RacksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RacksDto listRacks(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Create a new not managed rack in a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rack
    *           The rack to be created.
    * @return The created rack.
    */
   @Named("rack:create")
   @POST
   @Produces(RackDto.BASE_MEDIA_TYPE)
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RackDto createRack(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) RackDto rack);

   /**
    * Get the given rack from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rackId
    *           The id of the rack.
    * @return The rack or <code>null</code> if it does not exist.
    */
   @Named("rack:get")
   @GET
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RackDto getRack(@EndpointLink("racks") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer rackId);

   /**
    * Updates an existing rack from the given datacenter.
    * 
    * @param rack
    *           The new attributes for the rack.
    * @return The updated rack.
    */
   @Named("rack:update")
   @PUT
   @Consumes(RackDto.BASE_MEDIA_TYPE)
   @Produces(RackDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RackDto updateRack(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RackDto rack);

   /**
    * Deletes an existing rack.
    * 
    * @param rack
    *           The rack to delete.
    */
   @Named("rack:delete")
   @DELETE
   void deleteRack(@EndpointLink("edit") @BinderParam(BindToPath.class) RackDto rack);

   /*********************** Remote Service ********************** */

   /**
    * List all remote services of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of remote services for the datacenter.
    */
   @Named("rs:list")
   @GET
   @Consumes(RemoteServicesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RemoteServicesDto listRemoteServices(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Create a new remote service in a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param remoteService
    *           The remote service to be created.
    * @return The created remote service.
    */
   @Named("rs:create")
   @POST
   @Produces(RemoteServiceDto.BASE_MEDIA_TYPE)
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RemoteServiceDto createRemoteService(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) RemoteServiceDto remoteService);

   /**
    * Get the given remote service from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param remoteServiceType
    *           The type of the remote service.
    * @return The remote service or <code>null</code> if it does not exist.
    */
   @Named("rs:get")
   @GET
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RemoteServiceDto getRemoteService(
         @EndpointLink("remoteservices") @BinderParam(BindToPath.class) final DatacenterDto datacenter,
         @BinderParam(AppendRemoteServiceTypeToPath.class) final RemoteServiceType remoteServiceType);

   /**
    * Updates an existing remote service from the given datacenter.
    * 
    * @param remoteService
    *           The new attributes for the remote service.
    * @return The updated remote service.
    */
   @Named("rs:update")
   @PUT
   @Consumes(RemoteServiceDto.BASE_MEDIA_TYPE)
   @Produces(RemoteServiceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   RemoteServiceDto updateRemoteService(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) RemoteServiceDto remoteService);

   /**
    * Deletes an existing remote service.
    * 
    * @param remoteService
    *           The remote service to delete.
    */
   @Named("rs:delete")
   @DELETE
   void deleteRemoteService(@EndpointLink("edit") @BinderParam(BindToPath.class) RemoteServiceDto remoteService);

   /**
    * Check if the given remote service is available and properly configured.
    * 
    * @param remoteService
    *           The remote service to check.
    * @return A Boolean indicating if the remote service is available.
    */
   @Named("rs:available")
   @GET
   @Fallback(FalseIfNotAvailable.class)
   boolean isAvailable(@EndpointLink("check") @BinderParam(BindToPath.class) RemoteServiceDto remoteService);

   /*********************** Machine ********************** */

   /**
    * Create a new physical machine in a rack.
    * 
    * @param rack
    *           The rack.
    * @param machine
    *           The physical machine to be created.
    * @return The created physical machine.
    */
   @Named("machine:create")
   @POST
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachineDto createMachine(@EndpointLink("machines") @BinderParam(BindToPath.class) RackDto rack,
         @BinderParam(BindToXMLPayload.class) MachineDto machine);

   /**
    * Get the given machine from the given rack.
    * 
    * @param rack
    *           The rack.
    * @param machineId
    *           The id of the machine.
    * @return The machine or <code>null</code> if it does not exist.
    */
   @Named("machine:get")
   @GET
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   MachineDto getMachine(@EndpointLink("machines") @BinderParam(BindToPath.class) final RackDto rack,
         @BinderParam(AppendToPath.class) Integer machineId);

   /**
    * Checks the real infrastructure state for the given physical machine. The
    * machine is updated with the result state.
    * 
    * @param machine
    *           The machine to check
    * @param sync
    *           boolean that indicates a database synchronization
    * @return A machineStateDto with a machine state value from enum
    *         MachineState
    */
   @Named("machine:checkstate")
   @GET
   @Consumes(MachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachineStateDto checkMachineState(
         @EndpointLink("checkstate") @BinderParam(BindToPath.class) final MachineDto machine,
         @QueryParam("sync") boolean sync);

   /**
    * Checks the ipmi configuration state for the given physical machine.
    * 
    * @param machine
    *           The machine to check
    * @return A machineIpmiStateDto with a machine ipmi configuration state
    *         value from enum MachineState
    */
   @Named("machine:checkipmi")
   @GET
   @Consumes(MachineIpmiStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachineIpmiStateDto checkMachineIpmiState(
         @EndpointLink("checkipmistate") @BinderParam(BindToPath.class) final MachineDto machine);

   /**
    * Updates an existing physical machine.
    * 
    * @param machine
    *           The new attributes for the physical machine.
    * @return The updated machine.
    */
   @Named("machine:update")
   @PUT
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachineDto updateMachine(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) MachineDto machine);

   /**
    * Deletes an existing physical machine.
    * 
    * @param machine
    *           The physical machine to delete.
    */
   @Named("machine:delete")
   @DELETE
   void deleteMachine(@EndpointLink("edit") @BinderParam(BindToPath.class) MachineDto machine);

   /**
    * Reserve the given machine for the given enterprise.
    * 
    * @param enterprise
    *           The enterprise reserving the machine.
    * @param machine
    *           The machine to reserve.
    * @return The reserved machine.
    */
   @Named("machine:reserve")
   @POST
   @Consumes(MachineDto.BASE_MEDIA_TYPE)
   @Produces(MachineDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachineDto reserveMachine(@EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) MachineDto machine);

   /**
    * Cancels the reservation of the given machine.
    * 
    * @param enterprise
    *           The enterprise to cancel reservation.
    * @param machine
    *           The machine to release.
    */
   @Named("machine:cancelreservation")
   @DELETE
   void cancelReservation(@EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendMachineIdToPath.class) MachineDto machine);

   /**
    * List all machines racks for a rack.
    * 
    * @param rack
    *           The rack.
    * @return The list of physical machines for the rack.
    */
   @Named("machine:list")
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachinesDto listMachines(@EndpointLink("machines") @BinderParam(BindToPath.class) RackDto rack);

   /**
    * List all virtual machines in a physical machine.
    * 
    * @param machine
    *           The physical machine.
    * @return The list of virtual machines in the physical machine.
    */
   @Named("machine:listvms")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachinesWithNodeExtendedDto listVirtualMachinesByMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) MachineDto machine, MachineOptions options);

   /**
    * Get the given virtual machine
    * 
    * @param machine
    * @param virtualMachineId
    * @return
    */
   @Named("machine:getvm")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineWithNodeExtendedDto getVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) MachineDto machine,
         @BinderParam(AppendToPath.class) Integer virtualMachineId);

   /*********************** Storage Device ***********************/

   /**
    * List all storage devices of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of storage devices in the datacenter.
    */
   @Named("storagedevice:list")
   @GET
   @Consumes(StorageDevicesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StorageDevicesDto listStorageDevices(@EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * List all supported storage devices.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of supported storage devices.
    */
   @Named("storagedevice:listsupported")
   @GET
   @Consumes(StorageDevicesMetadataDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StorageDevicesMetadataDto listSupportedStorageDevices(
         @EndpointLink("devices") @BinderParam(BindSupportedDevicesLinkToPath.class) DatacenterDto datacenter);

   /**
    * Get the storage device.
    * 
    * @param storageDeviceId
    *           The id of the storage device.
    * @return The storage device or <code>null</code> if it does not exist.
    */
   @Named("storagedevice:get")
   @GET
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   StorageDeviceDto getStorageDevice(@EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer storageDeviceId);

   /**
    * Create a new storage device.
    * 
    * @param datacenter
    *           The datacenter.
    * @param storageDevice
    *           The storage device to be created.
    * @return The created storage device.
    */
   @Named("storagedevice:create")
   @POST
   @Produces(StorageDeviceDto.BASE_MEDIA_TYPE)
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StorageDeviceDto createStorageDevice(
         @EndpointLink("devices") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) StorageDeviceDto storageDevice);

   /**
    * Deletes an existing storage device.
    * 
    * @param storageDevice
    *           The storage device to delete.
    */
   @Named("storagedevice:delete")
   @DELETE
   void deleteStorageDevice(@EndpointLink("edit") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice);

   /**
    * Updates an existing storage device.
    * 
    * @param storageDevice
    *           The new attributes for the storage device.
    * @return The updated storage device.
    */
   @Named("storagedevice:update")
   @PUT
   @Produces(StorageDeviceDto.BASE_MEDIA_TYPE)
   @Consumes(StorageDeviceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StorageDeviceDto updateStorageDevice(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) StorageDeviceDto storageDevice);

   /*********************** Tier ***********************/
   /**
    * List all tiers of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of tiers in the datacenter.
    */
   @Named("tier:list")
   @GET
   @Consumes(TiersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TiersDto listTiers(@EndpointLink("tiers") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Updates a tier.
    * 
    * @param tier
    *           The new attributes for the tier.
    * @return The updated tier.
    */
   @Named("tier:update")
   @PUT
   @Produces(TierDto.BASE_MEDIA_TYPE)
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TierDto updateTier(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) TierDto tier);

   /**
    * Get the tier.
    * 
    * @param tierId
    *           The id of the tier.
    * @return The tier or <code>null</code> if it does not exist.
    */
   @Named("tier:get")
   @GET
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   TierDto getTier(@EndpointLink("tiers") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer tierId);

   /*********************** Storage Pool ***********************/

   /**
    * List storage pools on a storage device.
    * 
    * @param storageDevice
    *           The storage device.
    * @param options
    *           Optional query params.
    * @return The list of storage pools in the storage device.
    */
   @Named("storagepool:list")
   @GET
   @Consumes(StoragePoolsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StoragePoolsDto listStoragePools(
         @EndpointLink("pools") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice,
         StoragePoolOptions options);

   /**
    * List storage pools on a tier.
    * 
    * @param tier
    *           The tier device.
    * @return The list of storage pools in the tier.
    */
   @Named("storagepool:list")
   @GET
   @Consumes(StoragePoolsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StoragePoolsDto listStoragePools(@EndpointLink("pools") @BinderParam(BindToPath.class) TierDto tier);

   /**
    * Create a new storage pool in a storage device.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePool
    *           The storage pool to be created.
    * @return The created storage pool.
    */
   @Named("storagepool:create")
   @POST
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @Produces(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StoragePoolDto createStoragePool(
         @EndpointLink("pools") @BinderParam(BindToPath.class) StorageDeviceDto storageDevice,
         @BinderParam(BindToXMLPayload.class) StoragePoolDto storagePool);

   /**
    * Updates a storage pool.
    * 
    * @param storagePool
    *           The new attributes for the storage pool.
    * @return The updated tier.
    */
   @Named("storagepool:update")
   @PUT
   // For the most strangest reason in world, compiler does not accept
   // constants StoragePoolDto.BASE_MEDIA_TYPE for this method.
   @Consumes("application/vnd.abiquo.storagepool+xml")
   @Produces("application/vnd.abiquo.storagepool+xml")
   @JAXBResponseParser
   StoragePoolDto updateStoragePool(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) StoragePoolDto StoragePoolDto);

   /**
    * Deletes an existing storage pool.
    * 
    * @param storagePool
    *           The storage pool to delete.
    */
   @Named("storagepool:delete")
   @DELETE
   void deleteStoragePool(@EndpointLink("edit") @BinderParam(BindToPath.class) StoragePoolDto storagePool);

   /**
    * Get the storage pool.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePoolId
    *           The id of the storage pool.
    * @return The storage pool or <code>null</code> if it does not exist.
    */
   @Named("storagepool:get")
   @GET
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   StoragePoolDto getStoragePool(
         @EndpointLink("pools") @BinderParam(BindToPath.class) final StorageDeviceDto storageDevice,
         @BinderParam(AppendToPath.class) final String storagePoolId);

   /**
    * Refresh the given storage pool data.
    * 
    * @param storagePool
    *           The storage pool to refresh.
    * @param options
    *           The options to query the storage pool.
    * @return The updated storage pool.
    */
   @Named("storagepool:refresh")
   @GET
   @Consumes(StoragePoolDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   StoragePoolDto refreshStoragePool(@EndpointLink("edit") @BinderParam(BindToPath.class) StoragePoolDto storagePool,
         StoragePoolOptions options);

   /*********************** Network ***********************/

   /**
    * List all public, external and not managed networks of a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of not public, external and not managed for the
    *         datacenter.
    */
   @Named("network:list")
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworksDto listNetworks(@EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * List networks of a datacenter with options.
    * 
    * @param datacenter
    *           The datacenter.
    * @param options
    *           Optional query params.
    * @return The list of not public, external and not managed for the
    *         datacenter.
    */
   @Named("network:list")
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworksDto listNetworks(@EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         NetworkOptions options);

   /**
    * Get the given network from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param networkId
    *           The id of the network.
    * @return The rack or <code>null</code> if it does not exist.
    */
   @Named("network:get")
   @GET
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VLANNetworkDto getNetwork(@EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer networkId);

   /**
    * Create a new public network.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePool
    *           The storage pool to be created.
    * @return The created storage pool.
    */
   @Named("network:create")
   @POST
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto createNetwork(@EndpointLink("network") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) VLANNetworkDto network);

   /**
    * Updates a network.
    * 
    * @param network
    *           The new attributes for the network.
    * @return The updated tier.
    */
   @Named("network:update")
   @PUT
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworkDto updateNetwork(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VLANNetworkDto network);

   /**
    * Deletes an existing network.
    * 
    * @param network
    *           The network to delete.
    */
   @Named("network:delete")
   @DELETE
   void deleteNetwork(@EndpointLink("edit") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * Check the availability of a tag.
    * 
    * @param datacenter
    *           The datacenter.
    * @param tag
    *           Tag to check.
    * @return A tag availability object.
    */
   @Named("network:checktag")
   @GET
   @Path("/datacenters/{datacenter}/network/action/checkavailability")
   @Consumes(VlanTagAvailabilityDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VlanTagAvailabilityDto checkTagAvailability(
         @PathParam("datacenter") @ParamParser(ParseDatacenterId.class) DatacenterDto datacenter,
         @QueryParam("tag") Integer tag);

   /*********************** Network IPs ***********************/

   /**
    * List all the IPs in the given public network.
    * 
    * @param network
    *           The public network.
    * @return The IPs in the given public network.
    */
   @SinceApiVersion("2.3")
   @Named("publicnetwork:listips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   @Transform(ParsePublicIps.ToPagedIterable.class)
   PagedIterable<PublicIpDto> listPublicIps(@EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * List all the IPs in the given public network.
    * 
    * @param network
    *           The public network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given public network.
    */
   @SinceApiVersion("2.3")
   @Named("publicnetwork:listips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParsePublicIps.class)
   PaginatedCollection<PublicIpDto, PublicIpsDto> listPublicIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * Get the given public ip.
    * 
    * @param network
    *           The public network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    */
   @SinceApiVersion("2.3")
   @Named("publicnetwork:getip")
   @GET
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PublicIpDto getPublicIp(@EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /**
    * List all the IPs in the given external network.
    * 
    * @param network
    *           The external network.
    * @return The IPs in the given external network.
    */
   @SinceApiVersion("2.3")
   @Named("externalnetwork:listips")
   @GET
   @Consumes(ExternalIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseExternalIps.class)
   @Transform(ParseExternalIps.ToPagedIterable.class)
   PagedIterable<ExternalIpDto> listExternalIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * List all the IPs in the given external network.
    * 
    * @param network
    *           The external network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given external network.
    */
   @SinceApiVersion("2.3")
   @Named("externalnetwork:listips")
   @GET
   @Consumes(ExternalIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseExternalIps.class)
   PaginatedCollection<ExternalIpDto, ExternalIpsDto> listExternalIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * Get the given external ip.
    * 
    * @param network
    *           The external network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    */
   @SinceApiVersion("2.3")
   @Named("externalnetwork:getip")
   @GET
   @Consumes(ExternalIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ExternalIpDto getExternalIp(@EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /**
    * List all the IPs in the given unmanaged network.
    * 
    * @param network
    *           The unmanaged network.
    * @return The IPs in the given unmanaged network.
    */
   @SinceApiVersion("2.3")
   @Named("unmanagednetwork:listips")
   @GET
   @Consumes(UnmanagedIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseUnmanagedIps.class)
   @Transform(ParseUnmanagedIps.ToPagedIterable.class)
   PagedIterable<UnmanagedIpDto> listUnmanagedIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * List all the IPs in the given unmanaged network.
    * 
    * @param network
    *           The unmanaged network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given unmanaged network.
    */
   @SinceApiVersion("2.3")
   @Named("unmanagednetwork:listips")
   @GET
   @Consumes(UnmanagedIpsDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseUnmanagedIps.class)
   PaginatedCollection<UnmanagedIpDto, UnmanagedIpsDto> listUnmanagedIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * Get the given unmanaged ip.
    * 
    * @param network
    *           The unmanaged network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    */
   @SinceApiVersion("2.3")
   @Named("unmanagednetwork:getip")
   @GET
   @Consumes(UnmanagedIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   UnmanagedIpDto getUnmanagedIp(@EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /**
    * List all the Network Service types definied into a datacenter.
    * 
    * @param datacenter
    *           The datacenter
    * @return The list of Network Service Types in the datacenter.
    */
   @Named("networkservicetype:list")
   @GET
   @Consumes(NetworkServiceTypesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   NetworkServiceTypesDto listNetworkServiceTypes(
         @EndpointLink("networkservicetypes") @BinderParam(BindToPath.class) DatacenterDto datacenter);

   /**
    * Create a new Network Service Type Dto.
    * 
    * @param datacenter
    *           the datacenter where the network service type will belong to
    * @param nst
    *           {@link NetworkServiceTypeDto} instance to create
    * @return the created {@link NetworkServiceTypeDto}
    */
   @Named("networkservicetype:create")
   @POST
   @Produces(NetworkServiceTypeDto.BASE_MEDIA_TYPE)
   @Consumes(NetworkServiceTypeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   NetworkServiceTypeDto createNetworkServiceType(
         @EndpointLink("networkservicetypes") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) NetworkServiceTypeDto nst);

   /**
    * Get a single instance of a {@link NetworkServiceTypeDto}
    * 
    * @param datacenter
    *           datacenter where search into
    * @param nstId
    *           identifier of the {@link NetworkServiceTypeDto}
    * @return the found entity
    */
   @Named("networkservicetype:get")
   @GET
   @Consumes(NetworkServiceTypeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkServiceTypeDto getNetworkServiceType(
         @EndpointLink("networkservicetypes") @BinderParam(BindToPath.class) DatacenterDto datacenter,
         @BinderParam(AppendToPath.class) Integer nstId);

   /**
    * Update the value of a {@link NetworkServiceTypeDto}
    * 
    * @param nstDto
    *           the instance to update with the new values.
    * @return the updated entity.
    */
   @Named("networkservicetype:update")
   @PUT
   @Produces(NetworkServiceTypeDto.BASE_MEDIA_TYPE)
   @Consumes(NetworkServiceTypeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   NetworkServiceTypeDto updateNetworkServiceType(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) NetworkServiceTypeDto nstDto);

   /**
    * Remove a {@link NetworkServiceTypeDto} entity.
    * 
    * @param nstDto
    *           the entity to delete
    */
   @Named("networkservicetype:delete")
   @DELETE
   void deleteNetworkServiceType(@EndpointLink("edit") @BinderParam(BindToPath.class) NetworkServiceTypeDto nstDto);

}
