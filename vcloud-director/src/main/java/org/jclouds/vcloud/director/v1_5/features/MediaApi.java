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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CLONE_MEDIA_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;

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
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface MediaApi {

   /** Returns the media or null if not found. */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Media get(@EndpointParam URI mediaHref);

   /**
    * Creates a media (and present upload link for the floppy/iso file).
    * 
    * @return The response will return a link to transfer site to be able to continue with uploading
    *         the media.
    */
   @POST
   @Consumes(MEDIA)
   @Produces(MEDIA)
   @JAXBResponseParser
   Media add(@EndpointParam URI updateHref, @BinderParam(BindToXMLPayload.class) Media media);

   @POST
   @Path("/action/cloneMedia")
   @Consumes(MEDIA)
   @Produces(CLONE_MEDIA_PARAMS)
   @JAXBResponseParser
   Media clone(@EndpointParam URI mediaHref, @BinderParam(BindToXMLPayload.class) CloneMediaParams params);

   /** Updates the name/description of a media. */
   @PUT
   @Consumes(TASK)
   @Produces(MEDIA)
   @JAXBResponseParser
   Task edit(@EndpointParam URI mediaHref, @BinderParam(BindToXMLPayload.class) Media media);

   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   Task remove(@EndpointParam URI mediaHref);

   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   Owner getOwner(@EndpointParam URI mediaHref);
}
