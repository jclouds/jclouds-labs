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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.domain.enterprise.options.UserOptions;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.functions.pagination.ParseEnterprises;
import org.jclouds.abiquo.functions.pagination.ParseUsers;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.am.model.TemplatesStateDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;

/**
 * Provides synchronous access to Abiquo Enterprise API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin")
public interface EnterpriseApi extends Closeable {

   /*********************** Enterprise ********************** */

   /**
    * List all enterprises.
    * 
    * @return The list of Enterprises.
    */
   @Named("enterprise:list")
   @GET
   @Path("/enterprises")
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseEnterprises.class)
   @Transform(ParseEnterprises.ToPagedIterable.class)
   PagedIterable<EnterpriseDto> listEnterprises();

   /**
    * List enterprises with options.
    * 
    * @param options
    *           Filtering options.
    * @return The list of Enterprises.
    */
   @Named("enterprise:list")
   @GET
   @Path("/enterprises")
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseEnterprises.class)
   PaginatedCollection<EnterpriseDto, EnterprisesDto> listEnterprises(EnterpriseOptions options);

   /**
    * List filtered enterprises by datacenter.
    * 
    * @param datacenter
    *           The given datacenter.
    * @param options
    *           Filtering options.
    * @return The list of Enterprises.
    */
   @Named("enterprise:list")
   @GET
   @Consumes(EnterprisesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseEnterprises.class)
   PaginatedCollection<EnterpriseDto, EnterprisesDto> listEnterprises(
         @EndpointLink("enterprises") @BinderParam(BindToPath.class) DatacenterDto datacenter, EnterpriseOptions options);

   /**
    * Create a new enterprise.
    * 
    * @param enterprise
    *           The enterprise to be created.
    * @return The created enterprise.
    */
   @Named("enterprise:create")
   @POST
   @Path("/enterprises")
   @Produces(EnterpriseDto.BASE_MEDIA_TYPE)
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   EnterpriseDto createEnterprise(@BinderParam(BindToXMLPayload.class) EnterpriseDto enterprise);

   /**
    * Get the given enterprise.
    * 
    * @param enterpriseId
    *           The id of the enterprise.
    * @return The enterprise or <code>null</code> if it does not exist.
    */
   @Named("enterprise:get")
   @GET
   @Path("/enterprises/{enterprise}")
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   EnterpriseDto getEnterprise(@PathParam("enterprise") Integer enterpriseId);

   /**
    * Updates an existing enterprise.
    * 
    * @param enterprise
    *           The new attributes for the enterprise.
    * @return The updated enterprise.
    */
   @Named("enterprise:update")
   @PUT
   @Produces(EnterpriseDto.BASE_MEDIA_TYPE)
   @Consumes(EnterpriseDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   EnterpriseDto updateEnterprise(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) EnterpriseDto enterprise);

   /**
    * Deletes an existing enterprise.
    * 
    * @param enterprise
    *           The enterprise to delete.
    */
   @Named("enterprise:delete")
   @DELETE
   void deleteEnterprise(@EndpointLink("edit") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * List the allowed datacenters to the given enterprise.
    * 
    * @param enterpriseId
    *           The id of the enterprise.
    * @return The allowed datacenters to the given enterprise.
    */
   @Named("enterprise:listalloweddatacenters")
   @GET
   @Path("/datacenters")
   @Consumes(DatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacentersDto listAllowedDatacenters(@QueryParam("idEnterprise") Integer enterpriseId);

   /**
    * List all virtual datacenters of an enterprise.
    * 
    * @param enterprise
    *           The given enterprise.
    * @return The list of Datacenters.
    */
   @Named("enterprise:listvirtualdatacenters")
   @GET
   @Consumes(VirtualDatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualDatacentersDto listVirtualDatacenters(
         @EndpointLink("cloud/virtualdatacenters") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Enterprise Properties ***********************/

   /**
    * Get defined properties of the given enterprise.
    * 
    * @param enterpriseId
    *           The enterprise id.
    * @return Set of enterprise properties.
    */
   @Named("enterprise:getproperties")
   @GET
   @Consumes(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   EnterprisePropertiesDto getEnterpriseProperties(
         @EndpointLink("properties") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * Updates the given enterprise properties set.
    * 
    * @param properties
    *           The properties set.
    * @return The updated properties.
    */
   @Named("enterprse:setproperties")
   @PUT
   @Produces(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @Consumes(EnterprisePropertiesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   EnterprisePropertiesDto updateEnterpriseProperties(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) EnterprisePropertiesDto properties);

   /*********************** Enterprise Limits ***********************/

   /**
    * Allows the given enterprise to use the given datacenter with the given
    * limits.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenter
    *           The datacenter to allow to the given enterprise.
    * @param limits
    *           The usage limits for the enterprise in the given datacenter.
    * @return The usage limits for the enterprise in the given datacenter.
    */
   @Named("limit:create")
   @POST
   @Produces(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacenterLimitsDto createLimits(
         @EndpointLink("limits") @BinderParam(BindToPath.class) final EnterpriseDto enterprise,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter,
         @BinderParam(BindToXMLPayload.class) DatacenterLimitsDto limits);

   /**
    * Retrieves the limits for the given enterprise and datacenter.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenter
    *           The datacenter.
    * @return The usage limits for the enterprise in the given datacenter.
    */
   @Named("limit:get")
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   DatacentersLimitsDto getLimits(
         @EndpointLink("limits") @BinderParam(BindToPath.class) final EnterpriseDto enterprise,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter);

   /**
    * Retrieves limits for the given enterprise and any datacenter.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The usage limits for the enterprise on any datacenter.
    */
   @Named("limit:list")
   @GET
   @Consumes(DatacentersLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacentersLimitsDto listLimits(@EndpointLink("limits") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * Updates an existing enterprise-datacenter limits.
    * 
    * @param limits
    *           The new set of limits.
    * @return The updated limits.
    */
   @Named("limit:update")
   @PUT
   @Produces(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @Consumes(DatacenterLimitsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   DatacenterLimitsDto updateLimits(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) DatacenterLimitsDto limits);

   /**
    * Deletes existing limits for a pair enterprise-datacenter.
    * 
    * @param limits
    *           The limits to delete.
    */
   @Named("limit:delete")
   @DELETE
   void deleteLimits(@EndpointLink("edit") @BinderParam(BindToPath.class) DatacenterLimitsDto limits);

   /*********************** User ********************** */

   /**
    * Retrieves users of the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The users of the enterprise.
    */
   @Named("user:list")
   @GET
   @Consumes(UsersDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseUsers.class)
   @Transform(ParseUsers.ToPagedIterable.class)
   PagedIterable<UserDto> listUsers(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * List filtered users by enterprise.
    * 
    * @param enterprise
    *           The given enterprise.
    * @param options
    *           Filtering options.
    * @return The list of Users.
    */
   @Named("user:list")
   @GET
   @Consumes(UsersDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseUsers.class)
   PaginatedCollection<UserDto, UsersDto> listUsers(
         @EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise, UserOptions options);

   /**
    * Create a new user in the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param user
    *           The user to be created.
    * @return The created user.
    */
   @Named("user:create")
   @POST
   @Produces(UserDto.BASE_MEDIA_TYPE)
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   UserDto createUser(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) UserDto user);

   /**
    * Get the given user from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param userId
    *           The id of the user.
    * @return The user or <code>null</code> if it does not exist.
    */
   @Named("user:get")
   @GET
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   UserDto getUser(@EndpointLink("users") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer userId);

   /**
    * Updates an existing user.
    * 
    * @param enterprise
    *           The new attributes for the user.
    * @return The updated user.
    */
   @Named("user:update")
   @PUT
   @Produces(UserDto.BASE_MEDIA_TYPE)
   @Consumes(UserDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   UserDto updateUser(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) UserDto user);

   /**
    * Deletes existing user.
    * 
    * @param user
    *           The user to delete.
    */
   @Named("user:delete")
   @DELETE
   void deleteUser(@EndpointLink("edit") @BinderParam(BindToPath.class) UserDto user);

   /**
    * Retrieves list of virtual machines by user.
    * 
    * @param user
    *           The user.
    * @return The list of virtual machines of the user.
    */
   @Named("user:listvms")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) final UserDto user);

   /*********************** Datacenter Repository ***********************/

   /**
    * Get the given datacenter repository from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenterRepositoryId
    *           The id of the datacenter repository.
    * @return The datacenter repository or <code>null</code> if it does not
    *         exist.
    */
   @Named("repository:get")
   @GET
   @Consumes(DatacenterRepositoryDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   UserDto getDatacenterRepository(
         @EndpointLink("datacenterrepositories") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer datacenterRepositoryId);

   /**
    * Refreshes database with virtual machine templates existing in the
    * repository filesystem.
    * 
    * @param enterpriseId
    *           Id of the enterprise which information will be refreshed.
    * @param datacenterRepositoryId
    *           Id of the datacenter repository containing the templates.
    */
   @Named("repository:refresh")
   @PUT
   @Path("/enterprises/{enterprise}/datacenterrepositories/{datacenterrepository}/actions/refresh")
   void refreshTemplateRepository(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId);

   /*********************** Network ***********************/

   /**
    * List external networks of the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of external networks created and assigned.
    */
   @Named("enterprise:listexternalnetworks")
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VLANNetworksDto listExternalNetworks(
         @EndpointLink("externalnetworks") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /*********************** Cloud ***********************/

   /**
    * Retrieves list of virtual appliances by the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of virtual appliances of the enterprise.
    */
   @Named("enterprise:listvapps")
   @GET
   @Consumes(VirtualAppliancesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualAppliancesDto listVirtualAppliances(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) final EnterpriseDto enterprise);

   /**
    * List virtual machines for the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of virtual machines by the enterprise.
    */
   @Named("enterprise:listvms")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * List reserved machines for the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of reserved machines by the enterprise.
    */
   @Named("enterprise:listreservedmachines")
   @GET
   @Consumes(MachinesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   MachinesDto listReservedMachines(
         @EndpointLink("reservedmachines") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * List all template definitions in apps library.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of template definitions by the enterprise.
    */
   @Named("templatedefinitionlist:list")
   @GET
   @Consumes(TemplateDefinitionListsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TemplateDefinitionListsDto listTemplateDefinitionLists(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise);

   /**
    * Create a new template definition list in apps library in the given
    * enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param template
    *           The template to be created.
    * @return The created template.
    */
   @Named("templatedefinitionlist:create")
   @POST
   @Produces(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TemplateDefinitionListDto createTemplateDefinitionList(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(BindToXMLPayload.class) TemplateDefinitionListDto templateList);

   /**
    * Update an existing template definition list in apps library.
    * 
    * @param template
    *           The template to be update.
    * @return The updated template.
    */
   @Named("templatedefinitionlist:update")
   @PUT
   @Produces(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TemplateDefinitionListDto updateTemplateDefinitionList(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) TemplateDefinitionListDto templateList);

   /**
    * Deletes existing user.
    * 
    * @param user
    *           The user to delete.
    */
   @Named("templatedefinitionlist:delete")
   @DELETE
   void deleteTemplateDefinitionList(
         @EndpointLink("edit") @BinderParam(BindToPath.class) TemplateDefinitionListDto templateList);

   /**
    * Get the given template definition list from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param templateListId
    *           The id of the template definition list.
    * @return The list or <code>null</code> if it does not exist.
    */
   @Named("templatedefinitionlist:get")
   @GET
   @Consumes(TemplateDefinitionListDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   TemplateDefinitionListDto getTemplateDefinitionList(
         @EndpointLink("appslib/templateDefinitionLists") @BinderParam(BindToPath.class) EnterpriseDto enterprise,
         @BinderParam(AppendToPath.class) Integer templateListId);

   /**
    * Get the list of status of a template definition list in a datacenter.
    * 
    * @param templateList
    *           The template definition list.
    * @param datacenter
    *           The given datacenter.
    * @return The list of states.
    */
   @Named("templatedefinitionlist:status")
   @GET
   @Consumes(TemplatesStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   TemplatesStateDto listTemplateListStatus(
         @EndpointLink("repositoryStatus") @BinderParam(BindToPath.class) TemplateDefinitionListDto templateList,
         @QueryParam("datacenterId") @ParamParser(ParseDatacenterId.class) DatacenterDto datacenter);
}
