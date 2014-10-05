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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ANY_IMAGE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OVF_RASD_ITEM;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RELOCATE_VM_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VM;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VM_PENDING_ANSWER;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ReturnPayloadBytes;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VmApi {

   /**
    * Retrieves a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#get(String)
    */
   @GET
   @Consumes(VM)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Vm get(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Consumes(VM)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Vm get(@EndpointParam URI vmHref);

   /**
    * Modifies the name/description of a {@link Vm}.
    * 
    * @since 0.9
    */
   @PUT
   @Produces(VM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) Vm vApp);

   @PUT
   @Produces(VM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) Vm vApp);

   /**
    * Deletes a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#remove(String)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI vmHref);

   /**
    * Consolidates a {@link Vm}.
    * 
    * <pre>
    * POST /vApp/{id}/action/consolidate
    * </pre>
    * 
    * @since 1.5
    */
   @POST
   @Path("/action/consolidate")
   @Consumes(TASK)
   @JAXBResponseParser
   Task consolidate(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/action/consolidate")
   @Consumes(TASK)
   @JAXBResponseParser
   Task consolidate(@EndpointParam URI vmHref);

   /**
    * Deploys a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#deploy(String, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task deploy(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task deploy(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * Discard suspended state of a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#discardSuspendedState(String)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   Task discardSuspendedState(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   Task discardSuspendedState(@EndpointParam URI vmHref);

   /**
    * Installs VMware tools to the virtual machine.
    * 
    * It should be running in order for them to be installed.
    * 
    * <pre>
    * POST /vApp/{id}/action/installVMwareTools
    * </pre>
    * 
    * @since 1.5
    */
   @POST
   @Path("/action/installVMwareTools")
   @Consumes(TASK)
   @JAXBResponseParser
   Task installVMwareTools(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/action/installVMwareTools")
   @Consumes(TASK)
   @JAXBResponseParser
   Task installVMwareTools(@EndpointParam URI vmHref);

   /**
    * Relocates a {@link Vm}.
    * 
    * <pre>
    * POST /vApp/{id}/action/relocate
    * </pre>
    * 
    * @since 1.5
    */
   @POST
   @Path("/action/relocate")
   @Produces(RELOCATE_VM_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task relocate(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RelocateParams params);

   @POST
   @Path("/action/relocate")
   @Produces(RELOCATE_VM_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task relocate(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) RelocateParams params);

   /**
    * Undeploy a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#undeploy(String, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task undeploy(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task undeploy(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * Upgrade virtual hardware version of a VM to the highest supported virtual hardware version of
    * provider vDC where the VM locates.
    * 
    * <pre>
    * POST /vApp/{id}/action/upgradeHardwareVersion
    * </pre>
    * 
    * @since 1.5
    */
   @POST
   @Path("/action/upgradeHardwareVersion")
   @Consumes(TASK)
   @JAXBResponseParser
   Task upgradeHardwareVersion(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/action/upgradeHardwareVersion")
   @Consumes(TASK)
   @JAXBResponseParser
   Task upgradeHardwareVersion(@EndpointParam URI vmHref);

   /**
    * Powers off a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#powerOff(String)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOff(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOff(@EndpointParam URI vmHref);

   /**
    * Powers on a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#powerOn(String)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOn(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOn(@EndpointParam URI vmHref);

   /**
    * Reboots a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#reboot(String)
    */
   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reboot(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reboot(@EndpointParam URI vmHref);

   /**
    * Resets a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#reset(String)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reset(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reset(@EndpointParam URI vmHref);

   /**
    * Shuts down a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#shutdown(String)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   Task shutdown(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   Task shutdown(@EndpointParam URI vmHref);

   /**
    * Suspends a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#suspend(String)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   Task suspend(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   Task suspend(@EndpointParam URI vmHref);

   /**
    * Retrieves the guest customization section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/guestCustomizationSection
    * </pre>
    * 
    * @since 1.0
    */
   @GET
   @Path("/guestCustomizationSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   GuestCustomizationSection getGuestCustomizationSection(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/guestCustomizationSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   GuestCustomizationSection getGuestCustomizationSection(@EndpointParam URI vmHref);

   /**
    * Modifies the guest customization section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/guestCustomizationSection
    * </pre>
    * 
    * @since 1.0
    */
   @PUT
   @Path("/guestCustomizationSection")
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editGuestCustomizationSection(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   @PUT
   @Path("/guestCustomizationSection")
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editGuestCustomizationSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   /**
    * Ejects media from a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/media/action/ejectMedia
    * </pre>
    * 
    * @since 0.9
    */
   @POST
   @Path("/media/action/ejectMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task ejectMedia(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   @POST
   @Path("/media/action/ejectMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task ejectMedia(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * Insert media into a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/media/action/insertMedia
    * </pre>
    * 
    * @since 0.9
    */
   @POST
   @Path("/media/action/insertMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task insertMedia(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   @POST
   @Path("/media/action/insertMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task insertMedia(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   /**
    * Retrieves the network connection section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/networkConnectionSection
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/networkConnectionSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConnectionSection getNetworkConnectionSection(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/networkConnectionSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConnectionSection getNetworkConnectionSection(@EndpointParam URI vmHref);

   /**
    * Modifies the network connection section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/networkConnectionSection
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/networkConnectionSection")
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editNetworkConnectionSection(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   @PUT
   @Path("/networkConnectionSection")
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editNetworkConnectionSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   /**
    * Retrieves the operating system section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/operatingSystemSection
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/operatingSystemSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   OperatingSystemSection getOperatingSystemSection(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/operatingSystemSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   OperatingSystemSection getOperatingSystemSection(@EndpointParam URI vmHref);

   /**
    * Modifies the operating system section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/operatingSystemSection
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/operatingSystemSection")
   @Produces(OPERATING_SYSTEM_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editOperatingSystemSection(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) OperatingSystemSection section);

   @PUT
   @Path("/operatingSystemSection")
   @Produces(OPERATING_SYSTEM_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editOperatingSystemSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) OperatingSystemSection section);

   /**
    * Retrieves {@link Vm} product sections.
    * 
    * @since 1.5
    * @see VAppApi#getProductSections(String)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam URI vmHref);

   /**
    * Modifies the product section information of a {@link Vm}.
    * 
    * @since 1.5
    * @see VAppApi#editProductSections(String, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editProductSections(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editProductSections(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * Retrieves a pending question for a {@link Vm}.
    * 
    * The user should answer to the question by operation
    * {@link #answerQuestion(String, VmQuestionAnswer)}. Usually questions will be asked when the VM
    * is powering on.
    * 
    * <pre>
    * GET /vApp/{id}/question
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/question")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VmPendingQuestion getPendingQuestion(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/question")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VmPendingQuestion getPendingQuestion(@EndpointParam URI vmHref);

   /**
    * Answer a pending question on a {@link Vm}.
    * 
    * The answer IDs of choice and question should match the ones returned from operation
    * {@link #getPendingQuestion(String)}.
    * 
    * <pre>
    * POST /vApp/{id}/question/action/answer
    * </pre>
    * 
    * @since 0.9
    */
   @POST
   @Path("/question/action/answer")
   @Produces(VM_PENDING_ANSWER)
   @Consumes
   @JAXBResponseParser
   void answerQuestion(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) VmQuestionAnswer answer);

   @POST
   @Path("/question/action/answer")
   @Produces(VM_PENDING_ANSWER)
   @Consumes
   @JAXBResponseParser
   void answerQuestion(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) VmQuestionAnswer answer);

   /**
    * Retrieves the runtime info section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/runtimeInfoSection
    * </pre>
    * 
    * @since 1.5
    */
   @GET
   @Path("/runtimeInfoSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RuntimeInfoSection getRuntimeInfoSection(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/runtimeInfoSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RuntimeInfoSection getRuntimeInfoSection(@EndpointParam URI vmHref);

   /**
    * Retrieves the thumbnail of the screen of a {@link Vm}.
    * 
    * The content type of the response may vary (e.g. {@code image/png}, {@code image/gif}).
    * 
    * <pre>
    * GET /vApp/{id}/screen
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/screen")
   @Consumes(ANY_IMAGE)
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ReturnPayloadBytes.class)
   byte[] getScreenImage(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/screen")
   @Consumes(ANY_IMAGE)
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ReturnPayloadBytes.class)
   byte[] getScreenImage(@EndpointParam URI vmHref);

   /**
    * Retrieve a screen ticket for remote console connection to a {@link Vm}.
    * 
    * A screen ticket is a string that includes the virtual machine's IP address, its managed object
    * reference, and a string that has been encoded as described in RFC 2396. Each VM element in a
    * vApp includes a link where rel="screen:acquireTicket". You can use that link to request a
    * screen ticket that you can use with the vmware-vmrc utility to open a VMware Remote Console
    * for the virtual machine represented by that VM element. The vApp should be running to get a
    * valid screen ticket.
    * 
    * <pre>
    * GET /vApp/{id}/screen/action/acquireTicket
    * </pre>
    * 
    * @since 0.9
    */
   @POST
   @Path("/screen/action/acquireTicket")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ScreenTicket getScreenTicket(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @POST
   @Path("/screen/action/acquireTicket")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ScreenTicket getScreenTicket(@EndpointParam URI vmHref);

   /**
    * Retrieves the virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VirtualHardwareSection getVirtualHardwareSection(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VirtualHardwareSection getVirtualHardwareSection(@EndpointParam URI vmHref);

   /**
    * Modifies the virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/virtualHardwareSection")
   @Produces(VIRTUAL_HARDWARE_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSection(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) VirtualHardwareSection section);

   @PUT
   @Path("/virtualHardwareSection")
   @Produces(VIRTUAL_HARDWARE_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) VirtualHardwareSection section);

   /**
    * Retrieves the CPU properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection/cpu")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionCpu(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/cpu")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionCpu(@EndpointParam URI vmHref);

   /**
    * Modifies the CPU properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/virtualHardwareSection/cpu")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionCpu(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   @PUT
   @Path("/virtualHardwareSection/cpu")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionCpu(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * Retrieves a list of items for disks from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection/disks")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionDisks(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/disks")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionDisks(@EndpointParam URI vmHref);

   /**
    * Modifies the disks list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/virtualHardwareSection/disks")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionDisks(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   @PUT
   @Path("/virtualHardwareSection/disks")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionDisks(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * Retrieves the list of items that represents the floppies and CD/DVD drives in a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/media
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection/media")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionMedia(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/media")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionMedia(@EndpointParam URI vmHref);

   /**
    * Retrieves the item that contains memory information from virtual hardware section of a
    * {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection/memory")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionMemory(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/memory")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionMemory(@EndpointParam URI vmHref);

   /**
    * Modifies the memory properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/virtualHardwareSection/memory")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionMemory(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   @PUT
   @Path("/virtualHardwareSection/memory")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionMemory(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   /**
    * Retrieves a list of items for network cards from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    * 
    * @since 0.9
    */
   @GET
   @Path("/virtualHardwareSection/networkCards")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionNetworkCards(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/networkCards")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref);

   /**
    * Modifies the network cards list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    * 
    * @since 0.9
    */
   @PUT
   @Path("/virtualHardwareSection/networkCards")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionNetworkCards(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   @PUT
   @Path("/virtualHardwareSection/networkCards")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * Retrieves a list of items for serial ports from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    * 
    * @since 1.5
    */
   @GET
   @Path("/virtualHardwareSection/serialPorts")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionSerialPorts(@EndpointParam(parser = URNToHref.class) String vmUrn);

   @GET
   @Path("/virtualHardwareSection/serialPorts")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref);

   /**
    * Modifies the serial ports list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    * 
    * @since 1.5
    */
   @PUT
   @Path("/virtualHardwareSection/serialPorts")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionSerialPorts(@EndpointParam(parser = URNToHref.class) String vmUrn,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   @PUT
   @Path("/virtualHardwareSection/serialPorts")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);
}
