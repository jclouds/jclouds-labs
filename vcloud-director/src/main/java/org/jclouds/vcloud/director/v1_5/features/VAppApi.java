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
import org.jclouds.javax.annotation.Nullable;
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

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppApi {

   /** Returns the vApp or null if not found. */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
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
