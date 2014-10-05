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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP_TEMPLATE;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppTemplateApi {

   /**
    * Retrieves a vApp template (can be used also to retrieve a VM from a vApp Template).
    * 
    * The vApp could be in one of these statues:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION
    * FAILED_CREATION(-1)} - Transient entity state, e.g., model object is addd but the
    * corresponding VC backing does not exist yet. This is further sub-categorized in the respective
    * entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} - Entity is whole, e.g., VM creation is complete and all the required model
    * objects and VC backings are addd.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED
    * RESOLVED(1)} - Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN
    * UNKNOWN(6)} - Entity state could not be retrieved from the inventory, e.g., VM power state is
    * null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF
    * POWERED_OFF(8)} - All VMs of the vApp template are powered off.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)}
    * - vApp template status is set to {@code MIXED} when the VMs in the vApp are in different power
    * states.
    * </ul>
    * 
    * <pre>
    * GET /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the requested template
    */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VAppTemplate get(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VAppTemplate get(@EndpointParam URI reference);

   /**
    * Modifies only the name/description of a vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @param template
    *           the template containing the new name and/or description
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam(parser = URNToHref.class) String templateUrn,
         @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   /**
    * Deletes a vApp template.
    * 
    * <pre>
    * DELETE /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam String templateUrn);

   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI templateUri);

   /**
    * Disables the download link to the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/disableDownload
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    */
   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   void disableDownload(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   void disableDownload(@EndpointParam URI templateHref);

   /**
    * Enables downloading of the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/enableDownload
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   Task enableDownload(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   Task enableDownload(@EndpointParam URI templateHref);

   /**
    * Retrieves the customization section of a vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/customizationSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the customization section
    */
   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CustomizationSection getCustomizationSection(
         @EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CustomizationSection getCustomizationSection(@EndpointParam URI templateHref);

   /**
    * Retrieves the lease settings section of a vApp or vApp template
    * 
    * <pre>
    * GET /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the lease settings
    */
   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   LeaseSettingsSection getLeaseSettingsSection(
         @EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   LeaseSettingsSection getLeaseSettingsSection(@EndpointParam URI templateHref);

   /**
    * Modifies the lease settings section of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @param settingsSection
    *           the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   Task editLeaseSettingsSection(
         @EndpointParam(parser = URNToHref.class) String templateUrn,
         @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   Task editLeaseSettingsSection(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   /**
    * Retrieves the network config section of a vApp or vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/networkConfigSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the network config section requested
    */
   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConfigSection getNetworkConfigSection(
         @EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConfigSection getNetworkConfigSection(@EndpointParam URI templateHref);

   /**
    * Retrieves the network section of a vApp or vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/networkSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the network section requested
    */
   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSection getNetworkSection(
         @EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSection getNetworkSection(@EndpointParam URI templateHref);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    * 
    * This OVF represents the vApp template as it is, with all vCloud specific information (like mac
    * address, parent networks, etc). The OVF which could be downloaded by enabling for download
    * will not contain this information. There are no specific states bound to this entity.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/ovf
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the ovf envelope
    */
   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Envelope getOvf(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Envelope getOvf(@EndpointParam URI templateHref);

   /**
    * Retrieves vApp template owner.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/owner
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the owner of the vApp template
    */
   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Owner getOwner(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Owner getOwner(@EndpointParam URI templateHref);

   /**
    * Retrieves VAppTemplate/VM product sections
    * 
    * <pre>
    * GET /vAppTemplate/{id}/productSections
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the product sections
    */
   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(
         @EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam URI templateHref);

   /**
    * Modifies the product sections of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/productSections
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   Task editProductSections(@EndpointParam(parser = URNToHref.class) String templateUrn,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   Task editProductSections(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   /**
    * <pre>
    * GET /vAppTemplate/{id}/shadowVms
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return shadowVM references
    */
   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   References getShadowVms(@EndpointParam(parser = URNToHref.class) String templateUrn);

   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   References getShadowVms(@EndpointParam URI templateHref);
}
