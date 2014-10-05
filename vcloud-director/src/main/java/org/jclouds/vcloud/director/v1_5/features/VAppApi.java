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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CONTROL_ACCESS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RECOMPOSE_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.STARTUP_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppApi {

   /**
    * @see VAppApi#get(String)
    */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VApp get(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the name/description of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   @PUT
   @Produces(VAPP)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) VApp vApp);

   /**
    * Deletes a {@link VApp}.
    *
    * <pre>
    * DELETE /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the control access of a {@link VApp}.
    *
    * <pre>
    * POST /vApp/{id}/action/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ControlAccessParams editControlAccess(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * Deploys a {@link VApp}.
    *
    * Deployment means allocation of all resource for a vApp/VM like CPU and memory from a vDC
    * resource pool. Deploying a vApp automatically deploys all of the virtual machines it contains.
    * As of version 1.5 the operation supports force customization passed with
    * {@link DeployVAppParamsType#setForceCustomization(Boolean)} parameter.
    *
    * <pre>
    * POST /vApp/{id}/action/deploy
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task deploy(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * Discard suspended state of a {@link VApp}.
    *
    * Discarding suspended state of a vApp automatically discarded suspended states of all of the
    * virtual machines it contains.
    *
    * <pre>
    * POST /vApp/{id}/action/discardSuspendedState
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   Task discardSuspendedState(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Place the {@link VApp} into maintenance mode.
    *
    * While in maintenance mode, a system admin can operate on the vApp as usual, but end users are
    * restricted to read-only operations. Any user-initiated tasks running when the vApp enters
    * maintenance mode will continue.
    *
    * <pre>
    * POST /vApp/{id}/action/enterMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   @POST
   @Path("/action/enterMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   void enterMaintenanceMode(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Take the {@link VApp} out of maintenance mode.
    *
    * <pre>
    * POST /vApp/{id}/action/exitMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   @POST
   @Path("/action/exitMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   void exitMaintenanceMode(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Recompose a {@link VApp} by removing its own VMs and/or adding new ones from other vApps or
    * vApp templates.
    *
    * To remove VMs you should put their references in elements. The way you add VMs is the same as
    * described in compose vApp operation
    * {@link VdcApi#composeVApp(String, org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams)}.
    * The status of vApp will be in
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED} until the
    * recompose task is finished.
    *
    * <pre>
    * POST /vApp/{id}/action/recomposeVApp
    * </pre>
    *
    * @since 1.0
    */
   @POST
   @Path("/action/recomposeVApp")
   @Produces(RECOMPOSE_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task recompose(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) RecomposeVAppParams params);

   /**
    * Undeploy a {@link VApp}.
    *
    * Undeployment means deallocation of all resources for a vApp/VM like CPU and memory from a vDC
    * resource pool. Undeploying a vApp automatically undeploys all of the virtual machines it
    * contains.
    *
    * <pre>
    * POST /vApp/{id}/action/undeploy
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task undeploy(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * Retrieves the control access information for a {@link VApp}.
    *
    * The vApp could be shared to everyone or could be shared to specific user, by editing the
    * control access values.
    *
    * <pre>
    * GET /vApp/{id}/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   // TODO: revise
   @GET
   @Path("/controlAccess")
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ControlAccessParams getAccessControl(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Powers off a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are powered off. This operation is allowed
    * only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOff
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOff(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Powers on a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are powered on. This operation is allowed
    * only when the vApp/VM is powered off.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOn
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOn(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Reboots a {@link VApp}.
    *
    * The vApp/VM should be started in order to reboot it.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reboot
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reboot(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Resets a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are reset. This operation is allowed only
    * when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reset
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reset(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Shuts down a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are shutdown. This operation is allowed only
    * when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/shutdown
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   Task shutdown(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Suspends a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are suspended. This operation is allowed
    * only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/suspend
    * </pre>
    *
    * @since 0.9
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   Task suspend(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Retrieves the lease settings section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   @GET
   @Path("/leaseSettingsSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   LeaseSettingsSection getLeaseSettingsSection(
         @EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the lease settings section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   @PUT
   @Path("/leaseSettingsSection")
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editLeaseSettingsSection(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) LeaseSettingsSection section);

   /**
    * Retrieves the network config section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   @GET
   @Path("/networkConfigSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConfigSection getNetworkConfigSection(
         @EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the network config section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   @PUT
   @Path("/networkConfigSection")
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editNetworkConfigSection(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * Retrieves the network section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/networkSection
    * </pre>
    *
    * @since 0.9
    */
   @GET
   @Path("/networkSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSection getNetworkSection(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Retrieves the owner of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Owner getOwner(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Changes {@link VApp} owner.
    *
    * <pre>
    * PUT /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   @PUT
   @Path("/owner")
   @Produces(OWNER)
   @Consumes(TASK)
   @JAXBResponseParser
   void editOwner(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) Owner owner);

   /**
    * Retrieves {@link VApp} product sections.
    *
    * <pre>
    * GET /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the product section information of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editProductSections(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * Retrieves the startup section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   @GET
   @Path("/startupSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   StartupSection getStartupSection(@EndpointParam(parser = URNToHref.class) String vAppUrn);

   /**
    * Modifies the startup section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   @PUT
   @Path("/startupSection")
   @Produces(STARTUP_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editStartupSection(@EndpointParam(parser = URNToHref.class) String vAppUrn,
         @BinderParam(BindToXMLPayload.class) StartupSection section);

   /**
    * Retrieves a {@link VApp}.
    *
    * The {@link VApp} could be in one of these statuses:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION
    * FAILED_CREATION(-1)} - Transient entity state, e.g., model object is addd but the
    * corresponding VC backing does not exist yet. This is further sub-categorized in the respective
    * entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} - Entity is whole, e.g., VM creation is complete and all the required model
    * objects and VC backings are created.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED
    * RESOLVED(1)} - Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#DEPLOYED
    * DEPLOYED(2)} - Entity is deployed.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#SUSPENDED
    * SUSPENDED(3)} - All VMs of the vApp are suspended.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_ON
    * POWERED_ON(4)} - All VMs of the vApp are powered on.
    * <li>
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#WAITING_FOR_INPUT
    * WAITING_FOR_INPUT(5)} - VM is pending response on a question.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN
    * UNKNOWN(6)} - Entity state could not be retrieved from the inventory, e.g., VM power state is
    * null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRECOGNIZED
    * UNRECOGNIZED(7)} - Entity state was retrieved from the inventory but could not be mapped to an
    * internal state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF
    * POWERED_OFF(8)} - All VMs of the vApp are powered off.
    * <li>
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#INCONSISTENT_STATE
    * INCONSISTENT_STATE(9)} - Apply to VM status, if a vm is {@code POWERED_ON}, or
    * {@code WAITING_FOR_INPUT}, but is undeployed, it is in an inconsistent state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)}
    * - vApp status is set to {@code MIXED} when the VMs in the vApp are in different power states
    * </ul>
    *
    * <pre>
    * GET /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   VApp get(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#edit(URI, VApp)
    */
   @PUT
   @Produces(VAPP)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam URI vAppHref, @BinderParam(BindToXMLPayload.class) VApp vApp);

   /**
    * @see VAppApi#remove(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editControlAccess(URI, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ControlAccessParams editControlAccess(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see VAppApi#deploy(URI, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task deploy(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VAppApi#discardSuspendedState(URI)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   Task discardSuspendedState(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#enterMaintenanceMode(URI)
    */
   @POST
   @Path("/action/enterMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   void enterMaintenanceMode(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#exitMaintenanceMode(URI)
    */
   @POST
   @Path("/action/exitMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   void exitMaintenanceMode(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#recompose(URI, RecomposeVAppParams)
    */
   @POST
   @Path("/action/recomposeVApp")
   @Produces(RECOMPOSE_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task recompose(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) RecomposeVAppParams params);

   /**
    * @see VAppApi#undeploy(URI, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   Task undeploy(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VAppApi#getAccessControl(URI)
    */
   @GET
   @Path("/controlAccess")
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ControlAccessParams getAccessControl(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#powerOff(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOff(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#powerOn(URI)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   Task powerOn(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#reboot(URI)
    */
   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reboot(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#reset(URI)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   Task reset(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#shutdown(URI)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   Task shutdown(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#suspend(URI)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   Task suspend(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#getLeaseSettingsSection(URI)
    */
   @GET
   @Path("/leaseSettingsSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   LeaseSettingsSection getLeaseSettingsSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editLeaseSettingsSection(URI, LeaseSettingsSection)
    */
   @PUT
   @Path("/leaseSettingsSection")
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editLeaseSettingsSection(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) LeaseSettingsSection section);

   /**
    * @see VAppApi#getNetworkConfigSection(URI)
    */
   @GET
   @Path("/networkConfigSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConfigSection getNetworkConfigSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editNetworkConfigSection(URI, NetworkConfigSection)
    */
   @PUT
   @Path("/networkConfigSection")
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editNetworkConfigSection(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppApi#getNetworkSection(URI)
    */
   @GET
   @Path("/networkSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSection getNetworkSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   Owner getOwner(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editOwner(URI, Owner)
    */
   @PUT
   @Path("/owner")
   @Produces(OWNER)
   @Consumes(TASK)
   @JAXBResponseParser
   void editOwner(@EndpointParam URI vAppHref, @BinderParam(BindToXMLPayload.class) Owner owner);

   /**
    * @see VAppApi#getProductSections(URI)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ProductSectionList getProductSections(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editProductSections(URI, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editProductSections(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * @see VAppApi#getStartupSection(URI)
    */
   @GET
   @Path("/startupSection")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   StartupSection getStartupSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editStartupSection(URI, StartupSection)
    */
   @PUT
   @Path("/startupSection")
   @Produces(STARTUP_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   Task editStartupSection(@EndpointParam URI vAppHref,
         @BinderParam(BindToXMLPayload.class) StartupSection section);
}
