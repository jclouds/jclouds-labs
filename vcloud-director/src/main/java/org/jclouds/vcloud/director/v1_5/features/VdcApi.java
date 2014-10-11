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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CAPTURE_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CLONE_MEDIA_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CLONE_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.INSTANTIATE_VAPP_TEMPLATE_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.UPLOAD_VAPP_TEMPLATE_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP_TEMPLATE;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.params.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VdcApi {

   /** Returns the virtual datacenter or null if not found. */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Vdc get(@EndpointParam URI vdcHref);
   
   /** Captures a vApp into vApp template. */
   @POST
   @Path("/action/captureVApp")
   @Consumes(VAPP_TEMPLATE)
   @Produces(CAPTURE_VAPP_PARAMS)
   @JAXBResponseParser
   VAppTemplate captureVApp(@EndpointParam URI vdcHref, @BinderParam(BindToXMLPayload.class) CaptureVAppParams params);

   /** Clones a media into new one. */
   @POST
   @Path("/action/cloneMedia")
   @Consumes(MEDIA)
   @Produces(CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   Media cloneMedia(@EndpointParam URI vdcHref, @BinderParam(BindToXMLPayload.class) CloneMediaParams params);

   /** Clones a vApp into new one. */
   @POST
   @Path("/action/cloneVApp")
   @Consumes(VAPP)
   @Produces(CLONE_VAPP_PARAMS)
   // TODO fix these etc.
   @JAXBResponseParser
   VApp cloneVApp(@EndpointParam URI vdcHref, @BinderParam(BindToXMLPayload.class) CloneVAppParams params);

   /** Clones a vApp template into new one. */
   @POST
   @Path("/action/cloneVAppTemplate")
   @Consumes(VAPP_TEMPLATE)
   @Produces(VCloudDirectorMediaType.CLONE_VAPP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   VAppTemplate cloneVAppTemplate(@EndpointParam URI vdcHref,
         @BinderParam(BindToXMLPayload.class) CloneVAppTemplateParams params);

   /**
    * Composes a new vApp using VMs from other vApps or vApp templates.
    *
    * <p /> The composed vApp must be deployed and powered on before it can be used.
    */
   @POST
   @Path("/action/composeVApp")
   @Consumes(VAPP)
   @Produces(VCloudDirectorMediaType.COMPOSE_VAPP_PARAMS)
   @JAXBResponseParser
   VApp composeVApp(@EndpointParam URI vdcHref,
         @BinderParam(BindToXMLPayload.class) ComposeVAppParams params);

   /** Instantiate a vApp template into a new vApp. */
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Consumes(VAPP)
   @Produces(INSTANTIATE_VAPP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   VApp instantiateVApp(@EndpointParam URI vdcHref,
         @BinderParam(BindToXMLPayload.class) InstantiateVAppParams params);

   /**
    * Uploading vApp template to a vDC.
    *
    * The operation is separate on several steps:
    * <ol>
    * <li>creating empty vApp template entity
    * <li>uploading an OVF of vApp template
    * <li>uploading disks described from the OVF
    * <li>finishing task for uploading
    * </ol>
    */
   @POST
   @Path("/action/uploadVAppTemplate")
   @Consumes(VAPP_TEMPLATE)
   @Produces(UPLOAD_VAPP_TEMPLATE_PARAMS)
   @JAXBResponseParser
   VAppTemplate uploadVAppTemplate(@EndpointParam URI vdcHref,
         @BinderParam(BindToXMLPayload.class) UploadVAppTemplateParams params);

   /**
    * Creates a media (and present upload link for the floppy/iso file).
    * 
    * @return The response will return a link to transfer site to be able to continue with uploading the media.
    */
   @POST
   @Path("/media")
   @Consumes(MEDIA)
   @Produces(MEDIA)
   @JAXBResponseParser
   Media addMedia(@EndpointParam URI vdcHref, @BinderParam(BindToXMLPayload.class) Media media);
}
