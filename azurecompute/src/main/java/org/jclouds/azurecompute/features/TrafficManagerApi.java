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
package org.jclouds.azurecompute.features;

import java.util.List;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.binders.ProfileDefinitionParamsToXML;
import org.jclouds.azurecompute.binders.ProfileParamsToXML;
import org.jclouds.azurecompute.domain.Profile;
import org.jclouds.azurecompute.domain.ProfileDefinition;
import org.jclouds.azurecompute.domain.ProfileDefinitionParams;
import org.jclouds.azurecompute.domain.CreateProfileParams;
import org.jclouds.azurecompute.domain.UpdateProfileParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.ListProfileDefinitionsHandler;
import org.jclouds.azurecompute.xml.ListProfilesHandler;
import org.jclouds.azurecompute.xml.ProfileDefinitionHandler;
import org.jclouds.azurecompute.xml.ProfileHandler;
import org.jclouds.azurecompute.xml.ResultHandler;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for creating, updating, listing, and deleting Azure Traffic Manager
 * profiles and definitions.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758255.aspx">docs</a>
 */
@Path("/services/WATM")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
public interface TrafficManagerApi {

   /**
    * The List Definitions operation returns all definitions of a profile.
    *
    * @param profile profile name.
    * @return profile definitions.
    */
   @Named("ListProfileDefinitions")
   @GET
   @Path("/profiles/{profile}/definitions")
   @XMLResponseParser(ListProfileDefinitionsHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<ProfileDefinition> listDefinitions(@PathParam("profile") String profile);

   /**
    * The Get Definition operation returns an existing profile definition.
    *
    * @param profile profile name.
    * @return profile definition.
    */
   @Named("GetProfileDefinition")
   @GET
   @Path("/profiles/{profile}/definitions/1")
   @XMLResponseParser(ProfileDefinitionHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ProfileDefinition getDefinition(@PathParam("profile") String profile);

   /**
    * The Create Profile operation creates a new profile for a domain name, owned by the specified subscription.
    *
    * @return traffic manager profiles.
    */
   @Named("ListProfiles")
   @GET
   @Path("/profiles")
   @XMLResponseParser(ListProfilesHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Profile> listProfiles();

   /**
    * The Get Profile operation returns profile details, including all definition versions and their statuses.
    *
    * @param profile profile name.
    * @return traffic manager profile.
    */
   @Named("GetProfile")
   @GET
   @Path("/profiles/{profile}")
   @XMLResponseParser(ProfileHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   Profile getProfile(@PathParam("profile") String profile);

   /**
    * The Check DNS Prefix Availability operation checks whether the specified DNS prefix is available for creating a
    * profile.
    *
    * @param name DNS name that you want to use. You must include .trafficmanager.net in the name.
    * @return DNS name availability.
    */
   @Named("CheckDNSPrefixAvailability")
   @GET
   @Path("/operations/isavailable/{name}")
   @XMLResponseParser(ResultHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   boolean checkDNSPrefixAvailability(@PathParam("name") String name);

   /**
    * The Create Definition operation creates a new definition for a specified profile. This definition will be assigned
    * a version number by the service. For more information about creating a profile, see Create Profile.
    *
    * @param name profile name.
    * @param params profile definition details to be sent as request body.
    * @return request id.
    */
   @Named("CreateProfileDefinition")
   @POST
   @Path("/profiles/{name}/definitions")
   @ResponseParser(ParseRequestIdHeader.class)
   String createDefinition(
           @PathParam("name") String name,
           @BinderParam(ProfileDefinitionParamsToXML.class) ProfileDefinitionParams params);

   /**
    * The Delete Profile operation deletes a profile and all of its definitions. This operation cannot be reverted.
    *
    * @param profile traffic manager profile name.
    * @return request id.
    */
   @Named("DeleteProfile")
   @DELETE
   @Path("/profiles/{profile}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("profile") String profile);

   /**
    * The Create Profile operation creates a new profile for a domain name, owned by the specified subscription.
    *
    * @param params profile parameters.
    * @return request id.
    */
   @Named("CreateProfile")
   @POST
   @Path("/profiles")
   @ResponseParser(ParseRequestIdHeader.class)
   String createProfile(@BinderParam(ProfileParamsToXML.class) CreateProfileParams params);

   /**
    * The Update Profile operation enables or disables a profile.
    *
    * @param profile traffic manager profile name.
    * @param params update profile params.
    * @return request id.
    */
   @Named("UpdateProfile")
   @PUT
   @Path("/profiles/{profile}")
   @ResponseParser(ParseRequestIdHeader.class)
   String updateProfile(
           @PathParam("profile") String profile, @BinderParam(ProfileParamsToXML.class) UpdateProfileParams params);
}
