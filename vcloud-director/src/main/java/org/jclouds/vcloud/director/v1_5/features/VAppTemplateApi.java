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
import org.jclouds.javax.annotation.Nullable;
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

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppTemplateApi {

   /** Returns the vApp template or null if not found. */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VAppTemplate get(@EndpointParam URI reference);

   /**
    * Modifies only the name/description of a vApp template.
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   Task edit(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI templateUri);

   /**
    * Disables the download link to the ovf of a vApp template.
    */
   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   void disableDownload(@EndpointParam URI templateHref);

   /**
    * Enables downloading of the ovf of a vApp template.
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   Task enableDownload(@EndpointParam URI templateHref);

   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   CustomizationSection getCustomizationSection(@EndpointParam URI templateHref);

   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   LeaseSettingsSection getLeaseSettingsSection(@EndpointParam URI templateHref);

   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   Task editLeaseSettingsSection(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   NetworkConfigSection getNetworkConfigSection(@EndpointParam URI templateHref);

   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   NetworkSection getNetworkSection(@EndpointParam URI templateHref);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    * 
    * <p/> This OVF represents the vApp template as it is, with all vCloud specific information (like mac
    * address, parent networks, etc). The OVF which could be downloaded by enabling for download
    * will not contain this information. There are no specific states bound to this entity.
    */
   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   Envelope getOvf(@EndpointParam URI templateHref);

   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   Owner getOwner(@EndpointParam URI templateHref);

   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   ProductSectionList getProductSections(@EndpointParam URI templateHref);

   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   Task editProductSections(@EndpointParam URI templateHref,
         @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   References getShadowVms(@EndpointParam URI templateHref);
}
