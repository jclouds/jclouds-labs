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

import org.jclouds.javax.annotation.Nullable;
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

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VmApi {

   /** Returns the virtual machine or null if not found. */
   @GET
   @Consumes(VM)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Vm get(@EndpointParam URI vmHref);

   /** Modifies the name/description of a {@link Vm}. */
   @PUT
   @Produces(VM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) Vm vApp);

   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI vmHref);

   @POST
   @Path("/action/consolidate")
   @Consumes(TASK)
   @JAXBResponseParser
   Task consolidate(@EndpointParam URI vmHref);

   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task deploy(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   Task discardSuspendedState(@EndpointParam URI vmHref);

   /**
    * Installs VMware tools to the virtual machine.
    * 
    * <p />It should be running in order for them to be installed.
    */
   @POST
   @Path("/action/installVMwareTools")
   @Consumes(TASK)
   @JAXBResponseParser
   Task installVMwareTools(@EndpointParam URI vmHref);

   @POST
   @Path("/action/relocate")
   @Produces(RELOCATE_VM_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task relocate(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) RelocateParams params);

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
    */
   @POST
   @Path("/action/upgradeHardwareVersion")
   @Consumes(TASK)
   @JAXBResponseParser
   Task upgradeHardwareVersion(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOff(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOn(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reboot(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reset(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   Task shutdown(@EndpointParam URI vmHref);

   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   Task suspend(@EndpointParam URI vmHref);

   @GET
   @Path("/guestCustomizationSection")
   @Consumes
   @JAXBResponseParser
   GuestCustomizationSection getGuestCustomizationSection(@EndpointParam URI vmHref);

   @PUT
   @Path("/guestCustomizationSection")
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editGuestCustomizationSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   @POST
   @Path("/media/action/ejectMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task ejectMedia(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   @POST
   @Path("/media/action/insertMedia")
   @Produces(MEDIA_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task insertMedia(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) MediaInsertOrEjectParams mediaParams);

   @GET
   @Path("/networkConnectionSection")
   @Consumes
   @JAXBResponseParser
   NetworkConnectionSection getNetworkConnectionSection(@EndpointParam URI vmHref);

   @PUT
   @Path("/networkConnectionSection")
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editNetworkConnectionSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   @GET
   @Path("/operatingSystemSection")
   @Consumes
   @JAXBResponseParser
   OperatingSystemSection getOperatingSystemSection(@EndpointParam URI vmHref);

   @PUT
   @Path("/operatingSystemSection")
   @Produces(OPERATING_SYSTEM_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editOperatingSystemSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) OperatingSystemSection section);

   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam URI vmHref);

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
    * <p/> The user should answer to the question by operation
    * {@link #answerQuestion(URI, VmQuestionAnswer)}. Usually questions will be asked when the VM
    * is powering on.
    */
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
    * {@link #getPendingQuestion(URI)}.
    */
   @POST
   @Path("/question/action/answer")
   @Produces(VM_PENDING_ANSWER)
   @Consumes
   @JAXBResponseParser
   void answerQuestion(@EndpointParam URI vmHref, @BinderParam(BindToXMLPayload.class) VmQuestionAnswer answer);

   @GET
   @Path("/runtimeInfoSection")
   @Consumes
   @JAXBResponseParser
   RuntimeInfoSection getRuntimeInfoSection(@EndpointParam URI vmHref);

   @GET
   @Path("/screen")
   @Consumes(ANY_IMAGE)
   @ResponseParser(ReturnPayloadBytes.class)
   byte[] getScreenImage(@EndpointParam URI vmHref);

   @POST
   @Path("/screen/action/acquireTicket")
   @Consumes
   @JAXBResponseParser
   ScreenTicket getScreenTicket(@EndpointParam URI vmHref);

   @GET
   @Path("/virtualHardwareSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VirtualHardwareSection getVirtualHardwareSection(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection")
   @Produces(VIRTUAL_HARDWARE_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSection(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) VirtualHardwareSection section);

   @GET
   @Path("/virtualHardwareSection/cpu")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionCpu(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection/cpu")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionCpu(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   @GET
   @Path("/virtualHardwareSection/disks")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionDisks(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection/disks")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionDisks(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   /**
    * Retrieves the list of items that represents the floppies and CD/DVD drives in a {@link Vm}.
    */
   @GET
   @Path("/virtualHardwareSection/media")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionMedia(@EndpointParam URI vmHref);

   @GET
   @Path("/virtualHardwareSection/memory")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItem getVirtualHardwareSectionMemory(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection/memory")
   @Produces(OVF_RASD_ITEM)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionMemory(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItem rasd);

   @GET
   @Path("/virtualHardwareSection/networkCards")
   @Consumes
   @JAXBResponseParser
   RasdItemsList getVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection/networkCards")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionNetworkCards(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);

   @GET
   @Path("/virtualHardwareSection/serialPorts")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   RasdItemsList getVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref);

   @PUT
   @Path("/virtualHardwareSection/serialPorts")
   @Produces(OVF_RASD_ITEMS_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editVirtualHardwareSectionSerialPorts(@EndpointParam URI vmHref,
         @BinderParam(BindToXMLPayload.class) RasdItemsList rasdItemsList);
}
