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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.cloud.options.ConversionOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.abiquo.functions.pagination.ParseVirtualMachineTemplates;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.ConversionDto;
import com.abiquo.server.core.appslibrary.ConversionsDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatePersistentDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;

/**
 * Provides synchronous access to Abiquo Apps library API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/admin/enterprises")
public interface VirtualMachineTemplateApi extends Closeable {
   /*********************** Virtual Machine Template ***********************/

   /**
    * List all virtual machine templates for an enterprise in a datacenter
    * repository.
    * 
    * @param enterpriseId
    *           Id of the enterprise.
    * @param datacenterRepositoryId
    *           Id of the datacenter repository containing the templates.
    * @return The list of virtual machine templates for the enterprise in the
    *         datacenter repository.
    */
   @Named("template:list")
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachineTemplates.class)
   @Transform(ParseVirtualMachineTemplates.ToPagedIterable.class)
   PagedIterable<VirtualMachineTemplateDto> listVirtualMachineTemplates(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId);

   /**
    * List all virtual machine templates for an enterprise in a datacenter
    * repository.
    * 
    * @param enterpriseId
    *           Id of the enterprise.
    * @param datacenterRepositoryId
    *           Id of the datacenter repository containing the templates.
    * @param options
    *           The options to query the virtual machine templates.
    * @return The filtered list of virtual machine templates for the enterprise
    *         in the datacenter repository.
    */
   @Named("template:list")
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @ResponseParser(ParseVirtualMachineTemplates.class)
   PaginatedCollection<VirtualMachineTemplateDto, VirtualMachineTemplatesDto> listVirtualMachineTemplates(
         @PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId, VirtualMachineTemplateOptions options);

   /**
    * Get the given virtual machine template.
    * 
    * @param enterpriseId
    *           Id of the enterprise.
    * @param datacenterRepositoryId
    *           Id of the datacenter repository containing the templates.
    * @param enterpriseId
    *           The id of the virtual machine template.
    * @return The virtual machine template or <code>null</code> if it does not
    *         exist.
    */
   @Named("template:get")
   @GET
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates/{virtualmachinetemplate}")
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VirtualMachineTemplateDto getVirtualMachineTemplate(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId,
         @PathParam("virtualmachinetemplate") Integer virtualMachineTemplateId);

   /**
    * Updates an existing virtual machine template.
    * 
    * @param template
    *           The new attributes for the template.
    * @return The updated template.
    */
   @Named("template:update")
   @PUT
   @Produces(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   VirtualMachineTemplateDto updateVirtualMachineTemplate(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineTemplateDto template);

   /**
    * Deletes an existing virtual machine template.
    * 
    * @param template
    *           The virtual machine template to delete.
    */
   @Named("template:delete")
   @DELETE
   void deleteVirtualMachineTemplate(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualMachineTemplateDto template);

   /**
    * Creates a persistent virtual machine template from other virtual machine
    * template.
    * 
    * @param dcRepository
    *           The repository where the persistent virtual machine template
    *           will be created.
    * @param options
    *           The persistent options like name, volume/tier, virtual
    *           datacenter and original template.
    * @return Response message to the persistent request.
    */
   @Named("template:createpersistent")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTemplatePersistentDto.BASE_MEDIA_TYPE)
   @Path("/{enterprise}/datacenterrepositories/{datacenterrepository}/virtualmachinetemplates")
   @JAXBResponseParser
   AcceptedRequestDto<String> createPersistentVirtualMachineTemplate(@PathParam("enterprise") Integer enterpriseId,
         @PathParam("datacenterrepository") Integer datacenterRepositoryId,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTemplatePersistentDto persistentOptions);

   /**
    * List all the conversions for a virtual machine template.
    * 
    * @param template
    *           , The virtual machine template of the conversions.
    * @return The list of conversions for the virtual machine template.
    */
   @Named("conversion:list")
   @GET
   @Consumes(ConversionsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ConversionsDto listConversions(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) VirtualMachineTemplateDto template);

   /**
    * List conversions for a virtual machine template.
    * 
    * @param template
    *           , The virtual machine template of the conversions
    * @param options
    *           , Optionally filter compatible conversions with a provided
    *           hypervisor or with the desired state.
    * @return The list of conversions for the virtual machine template with the
    *         applied constrains.
    */
   @Named("conversion:list")
   @GET
   @Consumes(ConversionsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ConversionsDto listConversions(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         ConversionOptions options);

   /**
    * Get the conversions for a virtual machine template and the desired target
    * format.
    * 
    * @param template
    *           , The virtual machine template of the conversion
    * @param targetFormat
    *           The disk format type of the requested conversion
    * @return The conversions for the virtual machine template with the desired
    *         target disk format type.
    */
   @Named("conversion:get")
   @GET
   @Consumes(ConversionDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ConversionDto getConversion(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         @BinderParam(AppendToPath.class) DiskFormatType targetFormat);

   /**
    * Starts a V2V conversion of the current virtual machine template, or
    * updates a failed conversion.
    * 
    * @param template
    *           The virtual machine template to convert
    * @param targetFormat
    *           The requested target {@link DiskFormatType} of the conversion.
    * @param conversion
    *           , the dto representing the conversion
    * @return an accepted request with a link to track the progress of the
    *         conversion tasks.
    */
   @Named("conversion:request")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(ConversionDto.BASE_MEDIA_TYPE)
   AcceptedRequestDto<String> requestConversion(
         @EndpointLink("conversions") @BinderParam(BindToPath.class) final VirtualMachineTemplateDto template,
         @BinderParam(AppendToPath.class) DiskFormatType targetFormat,
         @BinderParam(BindToXMLPayload.class) ConversionDto conversion);
}
